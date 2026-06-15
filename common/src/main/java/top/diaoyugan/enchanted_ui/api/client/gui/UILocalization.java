package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.network.chat.Component;

/**
 * Small value objects for framework-generated text.
 * <p>
 * Use these when EnchantedUI creates text for you, such as RGBA channel names,
 * field validation errors, or key binding state labels. Built-in keys are based
 * on this class' runtime package, so relocating the library also relocates its
 * translation namespace.
 */
public final class UILocalization {
    private static final String FRAMEWORK_KEY_PREFIX = UILocalization.class.getPackageName() + ".text.";

    private UILocalization() {
    }

    public static String frameworkKey(String name) {
        return FRAMEWORK_KEY_PREFIX + name;
    }

    public static Component frameworkText(String name, String fallback, Object... args) {
        return Component.translatableWithFallback(frameworkKey(name), fallback, args);
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
                    frameworkText("config.rgba.red", "Red"),
                    frameworkText("config.rgba.green", "Green"),
                    frameworkText("config.rgba.blue", "Blue"),
                    frameworkText("config.rgba.alpha", "Alpha"),
                    frameworkText("config.color_preview", "Color preview")
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
            return new FieldValidationMessages(
                    frameworkKey("validation.int.required"),
                    frameworkKey("validation.int.range")
            );
        }

        public static FieldValidationMessages doubleDefaults() {
            return new FieldValidationMessages(
                    frameworkKey("validation.double.required"),
                    frameworkKey("validation.double.range")
            );
        }

        public Component required(Component label) {
            return Component.translatableWithFallback(requiredKey, "%s requires a numeric value.", label);
        }

        public Component intRange(Component label, int min, int max) {
            return Component.translatableWithFallback(rangeKey, "%s must be between %d and %d.", label, min, max);
        }

        public Component doubleRange(Component label, String min, String max) {
            return Component.translatableWithFallback(rangeKey, "%s must be between %s and %s.", label, min, max);
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
                    frameworkKey("config.keybind.current"),
                    frameworkKey("config.keybind.none"),
                    frameworkKey("config.keybind.listening")
            );
        }

        public Component current(Component label, Object keyName) {
            return Component.translatableWithFallback(currentKey, "%s: %s", label, keyName);
        }

        public Component none(Component label) {
            return Component.translatableWithFallback(noneKey, "%s: None", label);
        }

        public Component listening(Component label) {
            return Component.translatableWithFallback(listeningKey, "%s: Press keys...", label);
        }
    }
}
