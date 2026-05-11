package top.diaoyugan.enchanted_ui.client.inputs;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

/**
 * Minimal keybinding holder used by config UI widgets.
 *
 * Note: platform-specific registration/integration may be needed depending on loader.
 */
public final class KeyBinding {

    private KeyBinding() {
    }

    public static final KeyMapping ACTIVATION_KEY = new KeyMapping(
            "key.vm.switch",
            InputConstants.KEY_V,
            KeyMapping.Category.MISC
    );

    public static final KeyMapping OPEN_DEMO_SCREEN = new KeyMapping(
            "key.enchanted_ui.open_demo",
            InputConstants.KEY_O,
            KeyMapping.Category.MISC
    );
}
