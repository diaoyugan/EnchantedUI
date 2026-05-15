package top.diaoyugan.enchanted_ui.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import top.diaoyugan.enchanted_ui.api.client.gui.EnchantedUI;
import top.diaoyugan.enchanted_ui.api.client.gui.UiBottomBar;
import top.diaoyugan.enchanted_ui.api.client.gui.UiButton;
import top.diaoyugan.enchanted_ui.api.client.gui.UiDialogAction;
import top.diaoyugan.enchanted_ui.api.client.gui.UiForm;
import top.diaoyugan.enchanted_ui.api.client.gui.UiFormSpec;
import top.diaoyugan.enchanted_ui.api.client.gui.UiSlider;
import top.diaoyugan.enchanted_ui.api.client.gui.UiTabbedScreen;
import top.diaoyugan.enchanted_ui.api.client.gui.UiText;
import top.diaoyugan.enchanted_ui.api.client.gui.UiTextField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DemoScreen extends UiTabbedScreen {
    private enum RenderProfile {
        COMPACT,
        BALANCED,
        DETAILED
    }

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
    private String profileName = "Explorer";
    private String selectedBiome = "Cherry Grove";
    private RenderProfile renderProfile = RenderProfile.BALANCED;
    private String themePreset = "Classic";
    private int inputTabShows = 0;

    private final Set<Integer> comboKeys = new HashSet<>();
    private final Set<String> enabledPanels = new HashSet<>(List.of("Map", "Stats"));
    private final KeyMapping demoKey = new KeyMapping("enchantedui.demo.dummy", InputConstants.KEY_G, KeyMapping.Category.MISC);
    private final List<String> editableEntries = new ArrayList<>(List.of("Custom ore", "Custom log"));
    private final List<String> biomeOptions = List.of(
            "Cherry Grove",
            "Frozen Peaks",
            "Mangrove Swamp",
            "Meadow",
            "Soul Sand Valley",
            "Windswept Hills"
    );
    private final List<String> themeOptions = List.of("Classic", "Blueprint", "Amber");
    private final List<Component> presetEntries = Arrays.asList(
            Component.literal("Copper vein"),
            Component.literal("Iron vein"),
            Component.literal("Diamond cluster"),
            Component.literal("Redstone pocket"),
            Component.literal("Quartz seam"),
            Component.literal("Ancient debris")
    );

    private int textureClicks = 0;
    private int iconClicks = 0;

    private int r = 255;
    private int g = 120;
    private int b = 80;
    private int a = 255;

    public DemoScreen(Screen parent) {
        super(parent, Component.literal("EnchantedUI Demo"));

        tab(10, 30, 20, Component.literal("Main"), EnchantedUI.formPage(200, form -> {
            UiText title = form.title(Component.literal("Main"));
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
            UiSlider intensitySlider = form.intSlider(
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
                UiTextField profileField = form.textField(
                        Component.literal("Profile name"),
                        () -> profileName,
                        value -> profileName = value,
                        value -> value.isBlank()
                                ? Component.literal("Name cannot be empty")
                                : value.length() > 16 ? Component.literal("Keep it under 16 characters") : null
                );
                profileField.tooltip(Component.literal("Single-line text field with validation"));

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

                UiSlider passesSlider = form.intSlider(
                        Component.literal("Passes"),
                        0,
                        8,
                        () -> passes,
                        v -> passes = v,
                        false
                );
                passesSlider.setCustomValueKey("eui.config.value.times");
                passesSlider.tooltip(Component.literal("Demonstrates custom translated slider value text"));
            }

            @Override
            public void onShow(UiForm form) {
                inputTabShows++;
                showToast(Component.literal("Input tab opened " + inputTabShows + " times"), 40);
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

        tab(10, 102, 20, Component.literal("Actions"), Style.EMPTY.withItalic(true), EnchantedUI.formPage(220, form -> {
            form.title(Component.literal("Buttons and feedback"));
            form.buttonRow(
                    Component.literal("Toast"),
                    () -> showToast(Component.literal("Framework toast fired")),
                    Component.literal("Dialog"),
                    () -> showConfirm(Component.literal("Run action"), Component.literal("Increment the button counters?"), () -> {
                        textureClicks++;
                        iconClicks++;
                        init();
                    })
            );

            UiButton textureButton = form.textureButton(
                    20,
                    20,
                    DIAMOND_TEXTURE,
                    20,
                    20,
                    EMERALD_TEXTURE,
                    20,
                    20,
                    () -> {
                        textureClicks++;
                        showToast(Component.literal("Texture action " + textureClicks));
                        init();
                    }
            );
            textureButton.tooltip(Component.literal("TextureButton from the form API"));

            UiButton iconButton = form.iconButton(
                    20,
                    REDSTONE_TEXTURE,
                    16,
                    16,
                    EMERALD_TEXTURE,
                    16,
                    16,
                    14,
                    () -> {
                        iconClicks++;
                        showToast(Component.literal("Icon action " + iconClicks));
                        init();
                    }
            );
            iconButton.tooltip(Component.literal("IconButton from the form API"));

            form.button(Component.literal("Back to Main"), () -> showPage(0))
                    .tooltip(Component.literal("Regular action button"));

            form.section(Component.literal("Indented group"), nested -> {
                nested.toggle(Component.literal("Nested toggle"), () -> enabled, v -> enabled = v);
                nested.button(Component.literal("Open info dialog"), () -> showDialog(
                        Component.literal("Section action"),
                        List.of(
                                Component.literal("Texture clicks: " + textureClicks),
                                Component.literal("Icon clicks: " + iconClicks)
                        ),
                        new UiDialogAction(Component.literal("Close"), () -> {}, true)
                ));
            });
        }));

        tab(10, 126, 20, Component.literal("Selection"), EnchantedUI.formPage(220, form -> {
            form.section(Component.literal("Selection widgets"), nested -> {
                nested.enumSelect(
                        Component.literal("Render profile"),
                        RenderProfile.class,
                        () -> renderProfile,
                        value -> renderProfile = value,
                        value -> Component.literal(value.name().toLowerCase().replace('_', ' '))
                );

                nested.searchableSelect(
                        Component.literal("Biome"),
                        () -> selectedBiome,
                        value -> selectedBiome = value,
                        () -> biomeOptions,
                        Component::literal,
                        Component.literal("Search biome")
                );

                nested.multiSelect(
                        Component.literal("Panels"),
                        () -> enabledPanels,
                        values -> {
                            enabledPanels.clear();
                            enabledPanels.addAll(values);
                        },
                        () -> List.of("Map", "Stats", "Timeline", "Crafting"),
                        Component::literal
                );

                nested.radioGroup(
                        Component.literal("Theme preset"),
                        () -> themePreset,
                        value -> themePreset = value,
                        () -> themeOptions,
                        Component::literal
                );
            });

            form.space(4);
            form.title(Component.literal("Readonly list"));
            form.dropdownList(
                    Component.literal("Preset entries"),
                    () -> presetEntries
            ).setTooltip(Tooltip.create(Component.literal("Click to expand and inspect built-in entries")));

            form.space(4);
            form.title(Component.literal("Editable list"));
            form.editableDropdownList(
                    Component.literal("Custom entries"),
                    220,
                    () -> editableEntries,
                    entries -> {
                        editableEntries.clear();
                        editableEntries.addAll(entries);
                    },
                    Component.literal("Add new entry"),
                    Component.literal("Add"),
                    5,
                    value -> value.length() < 3 ? Component.literal("Entry must be at least 3 characters") : null,
                    false
            ).setTooltip(Tooltip.create(Component.literal("Expanded view allows adding or removing entries")));

            form.space(8);
            for (int i = 1; i <= 18; i++) {
                form.title(Component.literal("Scroll sample line " + i));
            }
        }));

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
        profileName = "Explorer";
        selectedBiome = "Cherry Grove";
        renderProfile = RenderProfile.BALANCED;
        themePreset = "Classic";
        inputTabShows = 0;
        editableEntries.clear();
        editableEntries.addAll(List.of("Custom ore", "Custom log"));
        enabledPanels.clear();
        enabledPanels.addAll(List.of("Map", "Stats"));
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
