package top.diaoyugan.enchanted_ui.client.gui.widget.input;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import top.diaoyugan.enchanted_ui.api.client.gui.UiTextValidator;

public class ValidatedTextFieldWidget extends EditBox {
    private static final int VALID_TEXT_COLOR = 0xFFE0E0E0;
    private static final int INVALID_TEXT_COLOR = 0xFFFF7777;

    private final UiTextValidator validator;
    @Nullable
    private Component error;

    public ValidatedTextFieldWidget(
            int x,
            int y,
            int width,
            int height,
            Component hint,
            UiTextValidator validator
    ) {
        super(Minecraft.getInstance().font, x, y, width, height, hint);
        this.validator = validator;
        setHint(hint);
        setResponder(ignored -> validateNow());
        validateNow();
    }

    public boolean validateNow() {
        error = validator.validate(getValue());
        setTextColor(error == null ? VALID_TEXT_COLOR : INVALID_TEXT_COLOR);
        setTooltip(error == null ? null : Tooltip.create(error));
        return error == null;
    }

    public boolean isValidValue() {
        return error == null;
    }

    @Nullable
    public Component validationError() {
        return error;
    }
}
