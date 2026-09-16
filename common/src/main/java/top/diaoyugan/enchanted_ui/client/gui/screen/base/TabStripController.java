package top.diaoyugan.enchanted_ui.client.gui.screen.base;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import top.diaoyugan.enchanted_ui.api.client.gui.UITabLayout;
import top.diaoyugan.enchanted_ui.client.gui.widget.WidgetConditions;
import top.diaoyugan.enchanted_ui.client.gui.widget.button.TabButtonWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Owns tab-strip measurement, overflow navigation, and selection state. */
final class TabStripController {
    private static final int MIN_WIDTH = 60;
    private static final int PADDING = 10;

    private final List<Button> buttons = new ArrayList<>();
    private List<TabDefinition> tabs = List.of();
    private @Nullable UITabLayout layout;
    private @Nullable Button previousButton;
    private @Nullable Button nextButton;
    private boolean visible = true;
    private int windowStart;
    private int visibleCount;
    private int computedWidth = MIN_WIDTH;
    private boolean overflow;
    private int stripLeft;
    private int stripTop;
    private int stripRight;
    private int stripBottom;
    private int screenWidth;
    private int screenHeight;
    private int selectedIndex;

    void layout(UITabLayout layout) {
        this.layout = layout;
    }

    void visible(boolean visible) {
        this.visible = visible;
    }

    void build(
            List<TabDefinition> definitions,
            @Nullable Component sidebarTitle,
            Font font,
            int screenWidth,
            int screenHeight,
            Consumer<Button> register
    ) {
        clearWidgets();
        tabs = List.copyOf(definitions);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        if (!visible) {
            return;
        }

        int measuredWidth = MIN_WIDTH;
        for (TabDefinition tab : tabs) {
            measuredWidth = Math.max(measuredWidth, font.width(tab.label()) + PADDING);
        }
        if (sidebarTitle != null) {
            measuredWidth = Math.max(measuredWidth, font.width(sidebarTitle) + PADDING);
        }
        computedWidth = Math.min(measuredWidth, Math.max(MIN_WIDTH, screenWidth - 20));

        for (TabDefinition tab : tabs) {
            Button button = new TabButtonWidget(
                    tab.x(), tab.y(), computedWidth, tab.height(), tab.label(),
                    ignored -> tab.onSelect().run()
            );
            buttons.add(button);
            register.accept(button);
        }

        if (layout != null && !buttons.isEmpty()) {
            boolean vertical = layout.orientation() == UITabLayout.Orientation.VERTICAL;
            previousButton = Button.builder(
                    Component.literal(vertical ? "▲" : "◀"),
                    ignored -> shiftWindow(-1)
            ).bounds(0, 0, 20, 16).build();
            nextButton = Button.builder(
                    Component.literal(vertical ? "▼" : "▶"),
                    ignored -> shiftWindow(1)
            ).bounds(0, 0, 20, 16).build();
            register.accept(previousButton);
            register.accept(nextButton);
            layoutAutomatic();
        }
        updateSelection();
    }

    int effectiveContentLeft(int configuredLeft) {
        if (layout != null && layout.reserveContentSpace()
                && layout.orientation() == UITabLayout.Orientation.VERTICAL) {
            return Math.max(configuredLeft, layout.startX() + computedWidth + 8);
        }
        return configuredLeft;
    }

    int effectiveContentTop(int configuredTop) {
        if (layout != null && layout.reserveContentSpace()
                && layout.orientation() == UITabLayout.Orientation.HORIZONTAL) {
            int tabHeight = tabs.stream().mapToInt(TabDefinition::height).max().orElse(20);
            return Math.max(configuredTop, layout.startY() + tabHeight + 8);
        }
        return configuredTop;
    }

    boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!overflow || mouseX < stripLeft || mouseX >= stripRight
                || mouseY < stripTop || mouseY >= stripBottom) {
            return false;
        }
        double amount = horizontalAmount != 0 ? horizontalAmount : verticalAmount;
        if (amount == 0) {
            return false;
        }
        shiftWindow(amount > 0 ? -1 : 1);
        return true;
    }

    void select(int index) {
        selectedIndex = index;
        updateSelection();
    }

    @Nullable Button firstButton() {
        return buttons.isEmpty() ? null : buttons.getFirst();
    }

    private void clearWidgets() {
        buttons.clear();
        previousButton = null;
        nextButton = null;
        visibleCount = 0;
        overflow = false;
        stripLeft = 0;
        stripTop = 0;
        stripRight = 0;
        stripBottom = 0;
    }

    private void layoutAutomatic() {
        if (layout == null || previousButton == null || nextButton == null || buttons.isEmpty()) {
            return;
        }
        if (layout.orientation() == UITabLayout.Orientation.VERTICAL) {
            layoutVertical();
        } else {
            layoutHorizontal();
        }
    }

    private void layoutVertical() {
        int tabHeight = tabs.stream().mapToInt(TabDefinition::height).max().orElse(20);
        int availableBottom = Math.max(layout.startY() + tabHeight, screenHeight - layout.endMargin());
        int availableHeight = availableBottom - layout.startY();
        int fullHeight = buttons.size() * tabHeight + Math.max(0, buttons.size() - 1) * layout.gap();
        overflow = fullHeight > availableHeight;

        int contentStart = layout.startY();
        int contentBottom = availableBottom;
        if (overflow) {
            contentStart += 20;
            contentBottom -= 20;
        }
        visibleCount = Math.max(1, (contentBottom - contentStart + layout.gap()) / (tabHeight + layout.gap()));
        windowStart = Math.max(0, Math.min(windowStart, Math.max(0, buttons.size() - visibleCount)));

        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            boolean buttonVisible = !overflow || (i >= windowStart && i < windowStart + visibleCount);
            button.visible = buttonVisible;
            if (buttonVisible) {
                int visibleIndex = overflow ? i - windowStart : i;
                button.setX(layout.startX());
                button.setY(contentStart + visibleIndex * (tabHeight + layout.gap()));
                button.setWidth(computedWidth);
            }
        }

        previousButton.visible = overflow;
        nextButton.visible = overflow;
        previousButton.active = windowStart > 0;
        nextButton.active = windowStart + visibleCount < buttons.size();
        previousButton.setX(layout.startX());
        previousButton.setY(layout.startY());
        previousButton.setWidth(computedWidth);
        nextButton.setX(layout.startX());
        nextButton.setY(availableBottom - 16);
        nextButton.setWidth(computedWidth);

        stripLeft = layout.startX();
        stripTop = layout.startY();
        stripRight = layout.startX() + computedWidth;
        stripBottom = availableBottom;
    }

    private void layoutHorizontal() {
        int tabHeight = tabs.stream().mapToInt(TabDefinition::height).max().orElse(20);
        int availableRight = Math.max(layout.startX() + MIN_WIDTH, screenWidth - layout.endMargin());
        int availableWidth = availableRight - layout.startX();
        int fullWidth = buttons.size() * computedWidth + Math.max(0, buttons.size() - 1) * layout.gap();
        overflow = fullWidth > availableWidth;

        int contentStart = layout.startX();
        int contentRight = availableRight;
        int horizontalTabWidth = computedWidth;
        if (overflow) {
            contentStart += 24;
            contentRight -= 24;
            horizontalTabWidth = Math.min(computedWidth, Math.max(20, contentRight - contentStart));
        }
        visibleCount = Math.max(1, (contentRight - contentStart + layout.gap())
                / (horizontalTabWidth + layout.gap()));
        windowStart = Math.max(0, Math.min(windowStart, Math.max(0, buttons.size() - visibleCount)));

        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            boolean buttonVisible = !overflow || (i >= windowStart && i < windowStart + visibleCount);
            button.visible = buttonVisible;
            if (buttonVisible) {
                int visibleIndex = overflow ? i - windowStart : i;
                button.setX(contentStart + visibleIndex * (horizontalTabWidth + layout.gap()));
                button.setY(layout.startY());
                button.setWidth(horizontalTabWidth);
            }
        }

        previousButton.visible = overflow;
        nextButton.visible = overflow;
        previousButton.active = windowStart > 0;
        nextButton.active = windowStart + visibleCount < buttons.size();
        previousButton.setX(layout.startX());
        previousButton.setY(layout.startY() + Math.max(0, (tabHeight - 16) / 2));
        nextButton.setX(availableRight - 20);
        nextButton.setY(layout.startY() + Math.max(0, (tabHeight - 16) / 2));

        stripLeft = layout.startX();
        stripTop = layout.startY();
        stripRight = availableRight;
        stripBottom = layout.startY() + tabHeight;
    }

    private void shiftWindow(int direction) {
        if (!overflow || direction == 0) {
            return;
        }
        int next = Math.max(0, Math.min(
                Math.max(0, buttons.size() - visibleCount),
                windowStart + direction
        ));
        if (next != windowStart) {
            windowStart = next;
            layoutAutomatic();
        }
    }

    private void revealSelected() {
        if (!overflow || selectedIndex < 0 || selectedIndex >= buttons.size()) {
            return;
        }
        if (selectedIndex < windowStart) {
            windowStart = selectedIndex;
            layoutAutomatic();
        } else if (selectedIndex >= windowStart + visibleCount) {
            windowStart = selectedIndex - visibleCount + 1;
            layoutAutomatic();
        }
    }

    private void updateSelection() {
        revealSelected();
        for (int i = 0; i < buttons.size(); i++) {
            WidgetConditions.setActiveState(buttons.get(i), i != selectedIndex);
        }
    }

    record TabDefinition(int x, int y, int height, Component label, Runnable onSelect) {
    }
}
