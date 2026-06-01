package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.network.chat.Component;

/**
 * Small value objects for framework-generated text.
 * <p>
 * Use these when EnchantedUI creates text for you, such as RGBA channel names,
 * field validation errors, or key binding state labels. Passing keys from your
 * own mod namespace keeps your UI isolated from EnchantedUI fallback keys.
 */
public final class UILocalization {
    private UILocalization() {
    }

    /**
     * Labels used by {@link UIForm#rgbaSlidersWithPreview}.
     *
     * @param red red channel label
     * @param green green channel label
     * @param blue blue channel label
     * @param alpha alpha channel label
     * @param preview preview widget label
     */
    public record ColorLabels(Component red, Component green, Component blue, Component alpha, Component preview) {
        public static ColorLabels defaults() {
            return new ColorLabels(
                    Component.translatable("eui.config.rgba.red"),
                    Component.translatable("eui.config.rgba.green"),
                    Component.translatable("eui.config.rgba.blue"),
                    Component.translatable("eui.config.rgba.alpha"),
                    Component.translatable("eui.config.color_preview")
            );
        }
    }

    /**
     * Translation keys used by typed form fields when validation fails.
     *
     * @param requiredKey key used when the value is empty or cannot be parsed
     * @param rangeKey key used when the parsed value is outside the allowed range
     */
    public record FieldValidationMessages(String requiredKey, String rangeKey) {
        public static FieldValidationMessages intDefaults() {
            return new FieldValidationMessages("eui.validation.int.required", "eui.validation.int.range");
        }

        public static FieldValidationMessages doubleDefaults() {
            return new FieldValidationMessages("eui.validation.double.required", "eui.validation.double.range");
        }

        public Component required(Component label) {
            return Component.translatable(requiredKey, label);
        }

        public Component intRange(Component label, int min, int max) {
            return Component.translatable(rangeKey, label, min, max);
        }

        public Component doubleRange(Component label, String min, String max) {
            return Component.translatable(rangeKey, label, min, max);
        }
    }

    /**
     * Translation keys used by key binding controls.
     *
     * @param currentKey key used when a binding has a value
     * @param noneKey key used by combination bindings when no keys are selected
     * @param listeningKey key used while the control is waiting for input
     */
    public record KeyBindingMessages(String currentKey, String noneKey, String listeningKey) {
        public static KeyBindingMessages defaults() {
            return new KeyBindingMessages(
                    "eui.config.keybind.current",
                    "eui.config.keybind.none",
                    "eui.config.keybind.listening"
            );
        }
    }
}
