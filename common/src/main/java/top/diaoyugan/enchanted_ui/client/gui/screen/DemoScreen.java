package top.diaoyugan.enchanted_ui.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.client.gui.builder.UI;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A minimal showcase screen for the public EnchantedUI API.
 */
public class DemoScreen extends UI.TabbedScreen {

    private boolean enabled = true;
    private boolean showAdvanced = false;
    private int radius = 5;
    private String notes = "Hello EnchantedUI!";
    private final Set<Integer> comboKeys = new HashSet<>();

    private int r = 255;
    private int g = 120;
    private int b = 80;
    private int a = 255;

    public DemoScreen(Screen parent) {
        super(parent, Component.literal("EnchantedUI Demo"));

        tab(10, 30, 20, Component.literal("Main"), UI.formPage(200, form -> {
            form.title(Component.literal("Main"));
            form.toggleRow(
                    Component.literal("Enabled"),
                    () -> enabled,
                    v -> enabled = v,
                    Component.literal("Advanced"),
                    () -> showAdvanced,
                    v -> showAdvanced = v
            );
            form.intSlider(Component.literal("Radius"), 1, 16, () -> radius, v -> radius = v, false);
            form.space(4);
            form.title(Component.literal("Notes"));
            form.textArea(Component.literal("Notes"), 40, () -> notes, v -> notes = v);
        }));

        tab(10, 54, 20, Component.literal("Input"), UI.formPage(200, new UI.FormSpec() {
            private final KeyMapping dummy = new KeyMapping("enchantedui.demo.dummy", InputConstants.KEY_G, KeyMapping.Category.MISC);

            @Override
            public void build(UI.Form form) {
                form.title(Component.literal("Input"));
                form.keyBinding(
                        Component.literal("Single key"),
                        dummy::setKey,
                        dummy::getTranslatedKeyMessage,
                        dummy,
                        true
                );
                form.combinationKeyBinding(
                        Component.literal("Key combo"),
                        () -> comboKeys,
                        v -> {
                            comboKeys.clear();
                            comboKeys.addAll(v);
                        }
                );
            }
        }));

        tab(10, 78, 20, Component.literal("Colors"), UI.formPage(200, form -> {
            form.rgbaSlidersWithPreview(
                    Component.literal("RGBA"),
                    () -> r, v -> r = v,
                    () -> g, v -> g = v,
                    () -> b, v -> b = v,
                    () -> a, v -> a = v,
                    false
            );
        }));

        bottomBar(UI.BottomBar.closeOnly(Component.literal("Close")));
    }
}
