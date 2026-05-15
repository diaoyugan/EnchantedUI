package top.diaoyugan.enchanted_ui.client.gui.widget.list;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import top.diaoyugan.enchanted_ui.api.client.gui.UiTextValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class EditableDropdownListWidget extends AbstractDropdownListWidget {
    private static final Component DEFAULT_ADD_LABEL = Component.translatable("eui.dropdown.add");

    private final Supplier<List<String>> getter;
    private final Consumer<List<String>> setter;
    private final EditBox input;
    private final Component addLabel;
    private final UiTextValidator validator;
    private final boolean allowDuplicates;
    @Nullable
    private Component error;

    public EditableDropdownListWidget(
            int x,
            int y,
            int width,
            Component label,
            Supplier<List<String>> getter,
            Consumer<List<String>> setter,
            Component inputHint
    ) {
        this(x, y, width, label, getter, setter, inputHint, DEFAULT_ADD_LABEL, DEFAULT_VISIBLE_ROWS, UiTextValidator.alwaysValid(), true);
    }

    public EditableDropdownListWidget(
            int x,
            int y,
            int width,
            Component label,
            Supplier<List<String>> getter,
            Consumer<List<String>> setter,
            Component inputHint,
            Component addLabel,
            int visibleRows
    ) {
        this(x, y, width, label, getter, setter, inputHint, addLabel, visibleRows, UiTextValidator.alwaysValid(), true);
    }

    public EditableDropdownListWidget(
            int x,
            int y,
            int width,
            Component label,
            Supplier<List<String>> getter,
            Consumer<List<String>> setter,
            Component inputHint,
            Component addLabel,
            int visibleRows,
            UiTextValidator validator,
            boolean allowDuplicates
    ) {
        super(x, y, width, label, visibleRows);
        this.getter = getter;
        this.setter = setter;
        this.addLabel = addLabel;
        this.validator = validator;
        this.allowDuplicates = allowDuplicates;
        this.input = new EditBox(Minecraft.getInstance().font, x, y, width - 44, ROW_HEIGHT, inputHint);
        this.input.setHint(inputHint);
        this.input.setCanLoseFocus(true);
        this.input.setResponder(ignored -> validateInput());
        validateInput();
    }

    @Override
    protected List<Component> entries() {
        return getter.get().stream().map(Component::literal).collect(Collectors.toList());
    }

    @Override
    protected int footerHeight() {
        return ROW_HEIGHT;
    }

    @Override
    protected void extractFooter(GuiGraphicsExtractor guiGraphics, int left, int top, int mouseX, int mouseY, float partialTick) {
        int inputWidth = getWidth() - (PANEL_PADDING * 2) - 40;
        input.setRectangle(inputWidth, ROW_HEIGHT, left + PANEL_PADDING, top);
        input.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        int buttonLeft = left + getWidth() - PANEL_PADDING - 36;
        guiGraphics.fill(buttonLeft, top, buttonLeft + 36, top + ROW_HEIGHT, 0xFF2E5A2E);
        guiGraphics.centeredText(
                Minecraft.getInstance().font,
                addLabel,
                buttonLeft + 18,
                top + 6,
                0xFFFFFFFF
        );
    }

    @Override
    protected boolean mouseClickedInFooter(MouseButtonEvent event, boolean doubleClick, int left, int top) {
        if (input.mouseClicked(event, doubleClick)) {
            input.setFocused(true);
            return true;
        }
        int buttonLeft = left + getWidth() - PANEL_PADDING - 36;
        if (event.x() >= buttonLeft && event.x() < buttonLeft + 36 && event.y() >= top && event.y() < top + ROW_HEIGHT) {
            addCurrentInput();
            return true;
        }
        return false;
    }

    @Override
    protected boolean mouseReleasedInFooter(MouseButtonEvent event, int left, int top) {
        return input.mouseReleased(event);
    }

    @Override
    protected boolean mouseDraggedInFooter(MouseButtonEvent event, double dragX, double dragY, int left, int top) {
        return input.mouseDragged(event, dragX, dragY);
    }

    @Override
    protected boolean keyPressedInFooter(KeyEvent event) {
        if (input.isFocused() && input.keyPressed(event)) {
            return true;
        }
        if (input.isFocused() && (event.key() == InputConstants.KEY_RETURN || event.key() == InputConstants.KEY_NUMPADENTER)) {
            addCurrentInput();
            return true;
        }
        return false;
    }

    @Override
    protected boolean charTypedInFooter(net.minecraft.client.input.CharacterEvent event) {
        return input.isFocused() && input.charTyped(event);
    }

    @Override
    protected boolean preeditUpdatedInFooter(net.minecraft.client.input.PreeditEvent event) {
        return input.isFocused() && input.preeditUpdated(event);
    }

    @Override
    protected int entryActionWidth() {
        return 16;
    }

    @Override
    protected void onEntryAction(int index) {
        List<String> updated = new ArrayList<>(getter.get());
        if (index >= 0 && index < updated.size()) {
            updated.remove(index);
            setter.accept(updated);
        }
    }

    @Override
    protected void setInnerFocus(boolean focused) {
        input.setFocused(focused);
    }

    private void addCurrentInput() {
        String value = input.getValue().trim();
        if (value.isEmpty()) {
            return;
        }
        if (!validateInput()) {
            return;
        }
        List<String> updated = new ArrayList<>(getter.get());
        if (!allowDuplicates) {
            String normalized = value.toLowerCase(Locale.ROOT);
            boolean exists = updated.stream().map(entry -> entry.toLowerCase(Locale.ROOT)).anyMatch(normalized::equals);
            if (exists) {
                error = Component.translatable("eui.validation.duplicate_entry");
                input.setTooltip(net.minecraft.client.gui.components.Tooltip.create(error));
                input.setTextColor(0xFFFF7777);
                return;
            }
        }
        updated.add(value);
        setter.accept(updated);
        input.setValue("");
        validateInput();
    }

    private boolean validateInput() {
        error = validator.validate(input.getValue().trim());
        input.setTextColor(error == null ? 0xFFE0E0E0 : 0xFFFF7777);
        input.setTooltip(error == null ? null : net.minecraft.client.gui.components.Tooltip.create(error));
        return error == null;
    }
}
