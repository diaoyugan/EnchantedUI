package top.diaoyugan.enchanted_ui.client.gui.builder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import top.diaoyugan.enchanted_ui.client.gui.layout.VerticalLayout;
import top.diaoyugan.enchanted_ui.client.gui.widget.button.TabButtonWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.input.CombinationKeyBindingButtonWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.input.KeyBindingButtonWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.BooleanOptionWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.ColorPreviewWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.IntSliderOptionWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.TextWidget;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

/**
 * Public entrypoint for building EnchantedUI-based screens.
 */
public final class UI {

    private UI() {
    }

    public static TabbedScreen tabbed(Screen parent, Component title) {
        return new TabbedScreen(parent, title);
    }

    public record BuildContext(int screenWidth, int screenHeight, int centerX) {
        public VerticalLayout vertical(int contentWidth, int startY, int gap) {
            int leftX = centerX - contentWidth / 2;
            return new VerticalLayout(leftX, startY, gap);
        }
    }

    public interface Page {
        List<AbstractWidget> build(BuildContext ctx);

        default void onSave() {
        }

        default void tick() {
        }

        default boolean keyPressed(KeyEvent event) {
            return false;
        }
    }

    public interface FormSpec {
        void build(Form form);

        default void onSave(Form form) {
        }

        default void tick(Form form) {
        }

        default boolean keyPressed(Form form, KeyEvent event) {
            return false;
        }
    }

    public static final class FormPage implements Page {

        private final int contentWidth;
        private final int startY;
        private final int gap;
        private final FormSpec spec;

        private Form lastForm;

        public FormPage(int contentWidth, int startY, int gap, FormSpec spec) {
            this.contentWidth = contentWidth;
            this.startY = startY;
            this.gap = gap;
            this.spec = Objects.requireNonNull(spec, "spec");
        }

        public Form lastForm() {
            return lastForm;
        }

        @Override
        public List<AbstractWidget> build(BuildContext ctx) {
            Form form = new Form(ctx, contentWidth, startY, gap);
            spec.build(form);
            lastForm = form;
            return form.widgets();
        }

        @Override
        public void onSave() {
            if (lastForm == null) return;
            lastForm.runSavers();
            spec.onSave(lastForm);
        }

        @Override
        public void tick() {
            if (lastForm == null) return;
            lastForm.tick();
            spec.tick(lastForm);
        }

        @Override
        public boolean keyPressed(KeyEvent event) {
            if (lastForm == null) return false;
            if (lastForm.keyPressed(event)) return true;
            return spec.keyPressed(lastForm, event);
        }
    }

    public static FormPage formPage(int contentWidth, FormSpec spec) {
        return new FormPage(contentWidth, 10, 4, spec);
    }

    public static FormPage formPage(int contentWidth, int startY, int gap, FormSpec spec) {
        return new FormPage(contentWidth, startY, gap, spec);
    }

    public static final class Form {
        private final BuildContext ctx;
        private final int contentWidth;
        private final VerticalLayout layout;
        private final List<AbstractWidget> widgets = new ArrayList<>();
        private final List<Runnable> savers = new ArrayList<>();

        public Form(BuildContext ctx, int contentWidth, int startY, int gap) {
            this.ctx = Objects.requireNonNull(ctx, "ctx");
            this.contentWidth = contentWidth;
            this.layout = ctx.vertical(contentWidth, startY, gap);
        }

        public BuildContext ctx() {
            return ctx;
        }

        public int contentWidth() {
            return contentWidth;
        }

        public VerticalLayout layout() {
            return layout;
        }

        public List<AbstractWidget> widgets() {
            return widgets;
        }

        public void runSavers() {
            for (Runnable saver : savers) saver.run();
        }

        public void tick() {
            for (AbstractWidget w : widgets) {
                if (w instanceof CombinationKeyBindingButtonWidget combo) {
                    combo.tick();
                }
            }
        }

        public boolean keyPressed(KeyEvent event) {
            boolean handled = false;
            for (AbstractWidget w : widgets) {
                if (w instanceof KeyBindingButtonWidget single) {
                    handled |= single.keyPressed(event);
                } else if (w instanceof CombinationKeyBindingButtonWidget combo) {
                    handled |= combo.keyPressed(event);
                }
            }
            return handled;
        }

        public Form space(int height) {
            layout.next(height);
            return this;
        }

        public TextWidget title(Component text) {
            TextWidget w = new TextWidget(layout.x(), layout.y(), text);
            widgets.add(w);
            layout.next(10);
            return w;
        }

        public BooleanOptionWidget toggle(Component label, BooleanSupplier getter, Consumer<Boolean> setter) {
            BooleanOptionWidget w = new BooleanOptionWidget(
                    layout.x(), layout.y(),
                    contentWidth, 20,
                    label,
                    getter,
                    setter
            );
            widgets.add(w);
            layout.next(20);
            return w;
        }

        public List<BooleanOptionWidget> toggleRow(
                Component leftLabel,
                BooleanSupplier leftGetter,
                Consumer<Boolean> leftSetter,
                Component rightLabel,
                BooleanSupplier rightGetter,
                Consumer<Boolean> rightSetter
        ) {
            int halfWidth = (contentWidth - 4) / 2;

            BooleanOptionWidget left = new BooleanOptionWidget(
                    layout.x(), layout.y(),
                    halfWidth, 20,
                    leftLabel,
                    leftGetter,
                    leftSetter
            );
            left.setHalfWidth();

            BooleanOptionWidget right = new BooleanOptionWidget(
                    layout.x() + halfWidth + 4, layout.y(),
                    halfWidth, 20,
                    rightLabel,
                    rightGetter,
                    rightSetter
            );
            right.setHalfWidth();

            widgets.add(left);
            widgets.add(right);
            layout.next(20);

            return List.of(left, right);
        }

        public IntSliderOptionWidget intSlider(
                Component label,
                int min,
                int max,
                IntSupplier getter,
                IntConsumer setter,
                boolean percentage
        ) {
            IntSliderOptionWidget w = new IntSliderOptionWidget(
                    layout.x(), layout.y(),
                    contentWidth, 20,
                    label,
                    min, max,
                    getter,
                    setter,
                    percentage
            );
            widgets.add(w);
            layout.next(20);
            return w;
        }

        public IntSliderOptionWidget intSlider(
                Component label,
                int width,
                int min,
                int max,
                IntSupplier getter,
                IntConsumer setter,
                boolean percentage
        ) {
            IntSliderOptionWidget w = new IntSliderOptionWidget(
                    layout.x(), layout.y(),
                    width, 20,
                    label,
                    min, max,
                    getter,
                    setter,
                    percentage
            );
            widgets.add(w);
            layout.next(20);
            return w;
        }

        public MultiLineEditBox textArea(
                Component label,
                int height,
                Supplier<String> getter,
                Consumer<String> setter
        ) {
            MultiLineEditBox box = MultiLineEditBox.builder()
                    .setX(layout.x())
                    .setY(layout.y())
                    .setShowBackground(true)
                    .setShowDecorations(true)
                    .build(
                            Minecraft.getInstance().font,
                            contentWidth,
                            height,
                            label
                    );

            box.setValue(getter.get());

            widgets.add(box);
            savers.add(() -> setter.accept(box.getValue()));

            layout.next(height);
            return box;
        }

        public KeyBindingButtonWidget keyBinding(
                Component label,
                Consumer<InputConstants.Key> setter,
                Supplier<Component> displaySupplier,
                KeyMapping vanillaKeyMapping,
                boolean syncVanilla
        ) {
            KeyBindingButtonWidget w = new KeyBindingButtonWidget(
                    layout.x(), layout.y(),
                    contentWidth, 20,
                    label,
                    setter,
                    displaySupplier,
                    vanillaKeyMapping,
                    syncVanilla
            );
            widgets.add(w);
            layout.next(20);
            return w;
        }

        public CombinationKeyBindingButtonWidget combinationKeyBinding(
                Component label,
                Supplier<Set<Integer>> getter,
                Consumer<Set<Integer>> setter
        ) {
            CombinationKeyBindingButtonWidget w = new CombinationKeyBindingButtonWidget(
                    layout.x(), layout.y(),
                    contentWidth, 20,
                    label,
                    getter,
                    setter
            );
            widgets.add(w);
            layout.next(20);
            return w;
        }

        public ColorGroup rgbaSlidersWithPreview(
                Component title,
                Supplier<Integer> rGetter,
                IntConsumer rSetter,
                Supplier<Integer> gGetter,
                IntConsumer gSetter,
                Supplier<Integer> bGetter,
                IntConsumer bSetter,
                Supplier<Integer> aGetter,
                IntConsumer aSetter,
                boolean alphaAsPercentage
        ) {
            title(title);

            int sliderWidth = 90;
            int sliderHeight = 20;
            int previewHeight = (sliderHeight * 4) + 24;

            IntSliderOptionWidget r = intSlider(Component.literal("R"), sliderWidth, 0, 255, () -> rGetter.get(), rSetter, false);
            int previewX = layout.x() + sliderWidth;

            ColorPreviewWidget preview = new ColorPreviewWidget(
                    previewX + 20,
                    r.getY(),
                    sliderWidth,
                    previewHeight,
                    rGetter::get,
                    gGetter::get,
                    bGetter::get,
                    aGetter::get
            );
            widgets.add(preview);

            IntSliderOptionWidget g = intSlider(Component.literal("G"), sliderWidth, 0, 255, () -> gGetter.get(), gSetter, false);
            IntSliderOptionWidget b = intSlider(Component.literal("B"), sliderWidth, 0, 255, () -> bGetter.get(), bSetter, false);
            IntSliderOptionWidget a = intSlider(Component.literal("A"), sliderWidth, 0, 255, () -> aGetter.get(), aSetter, alphaAsPercentage);

            return new ColorGroup(r, g, b, a, preview);
        }
    }

    public record ColorGroup(
            IntSliderOptionWidget r,
            IntSliderOptionWidget g,
            IntSliderOptionWidget b,
            IntSliderOptionWidget a,
            ColorPreviewWidget preview
    ) {
    }

    public static class TabbedScreen extends Screen {

        private static final int TAB_MIN_WIDTH = 60;
        private static final int TAB_PADDING = 10;

        @Nullable
        private final Screen parent;
        private final List<TabSpec> tabs = new ArrayList<>();
        private final List<List<AbstractWidget>> pages = new ArrayList<>();
        private final List<Button> tabButtons = new ArrayList<>();

        private int currentPage = 0;
        private BottomBar bottomBar = BottomBar.none();

        public TabbedScreen(@Nullable Screen parent, Component title) {
            super(title);
            this.parent = parent;
        }

        public <T extends GuiEventListener & Renderable & NarratableEntry> T add(T widget) {
            return addRenderableWidget(widget);
        }

        public TabbedScreen tab(int x, int y, int height, Component label, Page page) {
            tabs.add(new TabSpec(x, y, height, label, page));
            return this;
        }

        public TabbedScreen tab(int x, int y, int height, Component label, Style style, Page page) {
            return tab(x, y, height, label.copy().setStyle(style), page);
        }

        public TabbedScreen bottomBar(BottomBar bottomBar) {
            this.bottomBar = Objects.requireNonNull(bottomBar, "bottomBar");
            return this;
        }

        public int currentPage() {
            return currentPage;
        }

        public void showPage(int index) {
            if (index < 0 || index >= pages.size()) return;

            if (currentPage < pages.size()) {
                for (AbstractWidget w : pages.get(currentPage)) {
                    removeWidget(w);
                }
            }

            for (AbstractWidget w : pages.get(index)) {
                addRenderableWidget(w);
            }
            currentPage = index;
            updateTabButtons();
        }

        @Nullable
        public Screen parent() {
            return parent;
        }

        @Override
        protected void init() {
            int centerX = width / 2 + 10;

            pages.clear();
            tabButtons.clear();
            clearWidgets();

            for (TabSpec tab : tabs) {
                pages.add(tab.page.build(new BuildContext(width, height, centerX)));
            }

            buildTabButtons();
            showPage(currentPage);

            bottomBar.add(this, width / 2, height - 28);
        }

        private void buildTabButtons() {
            if (minecraft == null) return;

            int computedWidth = TAB_MIN_WIDTH;
            for (TabSpec tab : tabs) {
                computedWidth = Math.max(computedWidth, minecraft.font.width(tab.label) + TAB_PADDING);
            }

            for (int i = 0; i < tabs.size(); i++) {
                TabSpec tab = tabs.get(i);
                int index = i;
                Button btn = new TabButtonWidget(
                        tab.x, tab.y, computedWidth, tab.height,
                        tab.label,
                        b -> showPage(index)
                );
                tabButtons.add(btn);
                addRenderableWidget(btn);
            }

            updateTabButtons();
        }

        private void updateTabButtons() {
            for (int i = 0; i < tabButtons.size(); i++) {
                tabButtons.get(i).active = (i != currentPage);
            }
        }

        public void saveAll() {
            for (TabSpec tab : tabs) {
                tab.page.onSave();
            }
        }

        @Override
        public boolean keyPressed(KeyEvent event) {
            if (currentPage >= 0 && currentPage < tabs.size()) {
                if (tabs.get(currentPage).page.keyPressed(event)) return true;
            }
            return super.keyPressed(event);
        }

        @Override
        public void tick() {
            super.tick();
            if (currentPage >= 0 && currentPage < tabs.size()) {
                tabs.get(currentPage).page.tick();
            }
        }

        @Override
        public void onClose() {
            if (minecraft != null) {
                minecraft.setScreenAndShow(parent);
            }
        }

        private record TabSpec(int x, int y, int height, Component label, Page page) {
            private TabSpec {
                Objects.requireNonNull(label, "label");
                Objects.requireNonNull(page, "page");
            }
        }
    }

    public interface BottomBar {
        void add(TabbedScreen screen, int centerX, int bottomY);

        static BottomBar none() {
            return (screen, centerX, bottomY) -> {
            };
        }

        static BottomBar closeOnly(Component label) {
            return (screen, centerX, bottomY) -> screen.add(
                    Button.builder(label, b -> screen.onClose())
                            .bounds(centerX - 75, bottomY, 150, 20)
                            .build()
            );
        }

        static BottomBar saveAndClose(
                Component closeLabel,
                Component saveAndExitLabel,
                Runnable saveAction
        ) {
            Objects.requireNonNull(saveAction, "saveAction");
            return (screen, centerX, bottomY) -> {
                screen.add(Button.builder(closeLabel, b -> screen.onClose())
                        .bounds(centerX - 154, bottomY, 150, 20)
                        .build());

                screen.add(Button.builder(saveAndExitLabel, b -> {
                            saveAction.run();
                            screen.onClose();
                        })
                        .bounds(centerX + 4, bottomY, 150, 20)
                        .build());
            };
        }

        static BottomBar saveAndCloseWithExtra(
                Component closeLabel,
                Component saveAndExitLabel,
                Runnable saveAction,
                Component extraLabel,
                Tooltip extraTooltip,
                Runnable extraAction
        ) {
            Objects.requireNonNull(saveAction, "saveAction");
            Objects.requireNonNull(extraAction, "extraAction");
            return (screen, centerX, bottomY) -> {
                saveAndClose(closeLabel, saveAndExitLabel, saveAction).add(screen, centerX, bottomY);

                Button extra = Button.builder(extraLabel, b -> extraAction.run())
                        .bounds(centerX - 194, bottomY, 20, 20)
                        .build();
                if (extraTooltip != null) {
                    extra.setTooltip(extraTooltip);
                }
                screen.add(extra);
            };
        }
    }

    public static Minecraft mc() {
        return Minecraft.getInstance();
    }
}
