package top.diaoyugan.enchanted_ui.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import top.diaoyugan.enchanted_ui.api.client.gui.EnchantedUI;
import top.diaoyugan.enchanted_ui.api.client.gui.UiBuildContext;
import top.diaoyugan.enchanted_ui.api.client.gui.UiBottomBar;
import top.diaoyugan.enchanted_ui.api.client.gui.UiForm;
import top.diaoyugan.enchanted_ui.api.client.gui.UiFormSpec;
import top.diaoyugan.enchanted_ui.api.client.gui.UiPage;
import top.diaoyugan.enchanted_ui.api.client.gui.UiTabbedScreen;
import top.diaoyugan.enchanted_ui.client.gui.layout.VerticalLayout;
import top.diaoyugan.enchanted_ui.client.gui.widget.button.IconButton;
import top.diaoyugan.enchanted_ui.client.gui.widget.button.TextureButton;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.IntSliderOptionWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.TextWidget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A minimal showcase screen for the public EnchantedUI API.
 */
public class DemoScreen extends UiTabbedScreen {

    private static final Identifier DIAMOND_TEXTURE = Objects.requireNonNull(Identifier.tryParse("minecraft:textures/item/diamond.png"));
    private static final Identifier EMERALD_TEXTURE = Objects.requireNonNull(Identifier.tryParse("minecraft:textures/item/emerald.png"));
    private static final Identifier REDSTONE_TEXTURE = Objects.requireNonNull(Identifier.tryParse("minecraft:textures/item/redstone.png"));

    private boolean enabled = true;
    private boolean showAdvanced = false;
    private boolean debugOverlay = true;
    private boolean notifications = true;
    private int radius = 5;
    private int intensity = 65;
    private int passes = 3;
    private String notes = "Hello EnchantedUI!";
    private final Set<Integer> comboKeys = new HashSet<>();
    private final KeyMapping demoKey = new KeyMapping("enchantedui.demo.dummy", InputConstants.KEY_G, KeyMapping.Category.MISC);

    private int textureClicks = 0;
    private int iconClicks = 0;

    private int r = 255;
    private int g = 120;
    private int b = 80;
    private int a = 255;

    public DemoScreen(Screen parent) {
        super(parent, Component.literal("EnchantedUI Demo"));

        tab(10, 30, 20, Component.literal("Main"), EnchantedUI.formPage(200, form -> {
            TextWidget title = form.title(Component.literal("Main"));
            title.setTooltip(Tooltip.create(Component.literal("Simple form layout and built-in option widgets")));

            form.toggleRow(
                    Component.literal("Enabled"),
                    () -> enabled,
                    v -> enabled = v,
                    Component.literal("Advanced"),
                    () -> showAdvanced,
                    v -> showAdvanced = v
            );
            form.toggle(Component.literal("Notifications"), () -> notifications, v -> notifications = v)
                    .tooltip(Component.literal("Single full-width boolean option"));
            form.toggle(Component.literal("Debug Overlay"), () -> debugOverlay, v -> debugOverlay = v)
                    .tooltip(Component.literal("Another toggle with tooltip"));
            form.intSlider(Component.literal("Radius"), 1, 16, () -> radius, v -> radius = v, false);
            IntSliderOptionWidget intensitySlider = form.intSlider(
                    Component.literal("Intensity"),
                    0,
                    100,
                    () -> intensity,
                    v -> intensity = v,
                    true
            );
            intensitySlider.tooltip(Component.literal("Percentage display slider"));
            form.space(4);
            form.title(Component.literal("Notes"));
            form.textArea(Component.literal("Notes"), 40, () -> notes, v -> notes = v);
        }));

        tab(10, 54, 20, Component.literal("Input"), EnchantedUI.formPage(200, new UiFormSpec() {
            @Override
            public void build(UiForm form) {
                form.title(Component.literal("Input"));
                form.keyBinding(
                        Component.literal("Single key"),
                        demoKey::setKey,
                        demoKey::getTranslatedKeyMessage,
                        demoKey,
                        true
                ).tooltip(Component.literal("Captures one key and syncs the vanilla KeyMapping"));
                form.combinationKeyBinding(
                        Component.literal("Key combo"),
                        () -> comboKeys,
                        v -> {
                            comboKeys.clear();
                            comboKeys.addAll(v);
                        }
                ).tooltip(Component.literal("Press several keys, then release one to finish"));

                IntSliderOptionWidget passesSlider = form.intSlider(
                        Component.literal("Passes"),
                        0,
                        8,
                        () -> passes,
                        v -> passes = v,
                        false
                );
                passesSlider.setCustomValueKey("vm.config.value.times");
                passesSlider.tooltip(Component.literal("Demonstrates custom translated slider value text"));
            }
        }));

        tab(10, 78, 20, Component.literal("Colors"), EnchantedUI.formPage(200, form -> {
            form.rgbaSlidersWithPreview(
                    Component.literal("RGBA"),
                    () -> r, v -> r = v,
                    () -> g, v -> g = v,
                    () -> b, v -> b = v,
                    () -> a, v -> a = v,
                    false
            );
        }));

        tab(10, 102, 20, Component.literal("Buttons"), Style.EMPTY.withItalic(true), new UiPage() {
            @Override
            public List<AbstractWidget> build(UiBuildContext ctx) {
                List<AbstractWidget> widgets = new ArrayList<>();
                VerticalLayout layout = ctx.vertical(220, 10, 6);

                TextWidget title = new TextWidget(layout.x(), layout.y(), Component.literal("Buttons"));
                widgets.add(title);
                layout.next(12);

                widgets.add(new TextWidget(
                        layout.x(),
                        layout.y(),
                        Component.literal("Texture clicks: " + textureClicks + " | Icon clicks: " + iconClicks)
                ));
                layout.next(16);

                TextureButton textureButton = new TextureButton.Builder(layout.x(), layout.y(), 20, 20)
                        .texture(DIAMOND_TEXTURE, 20, 20)
                        .hoverTexture(EMERALD_TEXTURE, 20, 20)
                        .tooltip(Component.literal("TextureButton using different normal/hover textures"))
                        .onPress(b -> {
                            textureClicks++;
                            init();
                        })
                        .build();
                widgets.add(textureButton);

                IconButton iconButton = new IconButton.Builder(layout.x() + 28, layout.y(), 20, 20)
                        .icon(REDSTONE_TEXTURE, 16, 16)
                        .hoverIcon(EMERALD_TEXTURE, 16, 16)
                        .iconSize(14)
                        .onPress(b -> {
                            iconClicks++;
                            init();
                        })
                        .build();
                iconButton.setTooltip(Tooltip.create(Component.literal("IconButton with immediate counter refresh")));
                widgets.add(iconButton);

                Button nextPage = Button.builder(Component.literal("Back to Main"), b -> showPage(0))
                        .bounds(layout.x() + 56, layout.y(), 100, 20)
                        .tooltip(Tooltip.create(Component.literal("Regular widget added to the page")))
                        .build();
                widgets.add(nextPage);
                layout.next(24);

                widgets.add(new TextWidget(
                        layout.x(),
                        layout.y(),
                        Component.literal("This tab mixes EnchantedUI widgets with vanilla widgets.")
                ));

                return widgets;
            }
        });

        bottomBar(UiBottomBar.saveAndCloseWithExtra(
                Component.literal("Close"),
                Component.literal("Save & Close"),
                this::saveAll,
                Component.literal("R"),
                Tooltip.create(Component.literal("Reset all demo values")),
                () -> {
                    resetState();
                    init();
                }
        ));
    }

    private void resetState() {
        enabled = true;
        showAdvanced = false;
        debugOverlay = true;
        notifications = true;
        radius = 5;
        intensity = 65;
        passes = 3;
        notes = "Hello EnchantedUI!";
        comboKeys.clear();
        demoKey.setKey(InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_G));
        KeyMapping.resetMapping();
        textureClicks = 0;
        iconClicks = 0;
        r = 255;
        g = 120;
        b = 80;
        a = 255;
    }
}
