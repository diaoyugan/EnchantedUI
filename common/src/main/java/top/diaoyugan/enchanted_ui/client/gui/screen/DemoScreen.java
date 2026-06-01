package top.diaoyugan.enchanted_ui.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import top.diaoyugan.enchanted_ui.api.client.gui.EnchantedUI;
import top.diaoyugan.enchanted_ui.api.client.gui.UIBottomBar;
import top.diaoyugan.enchanted_ui.api.client.gui.UIDialogAction;
import top.diaoyugan.enchanted_ui.api.client.gui.UIForm;
import top.diaoyugan.enchanted_ui.api.client.gui.UIFormSpec;
import top.diaoyugan.enchanted_ui.api.client.gui.UIScreenStyle;
import top.diaoyugan.enchanted_ui.api.client.gui.UISlider;
import top.diaoyugan.enchanted_ui.api.client.gui.UISummaryItem;
import top.diaoyugan.enchanted_ui.api.client.gui.UITabbedScreen;
import top.diaoyugan.enchanted_ui.api.client.gui.UITextField;
import top.diaoyugan.enchanted_ui.api.client.gui.UIWidget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DemoScreen extends UITabbedScreen {
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
    private long scanBudgetMicros = 2_500L;
    private float uiScale = 1.25F;
    private double threshold = 0.35D;
    private String notes = "Hello EnchantedUI!";
    private String profileName = "Explorer";
    private String selectedBiome = "Cherry Grove";
    private RenderProfile renderProfile = RenderProfile.BALANCED;
    private String themePreset = "Classic";
    private int inputTabShows = 0;

    private final Set<Integer> comboKeys = new HashSet<>();
    private final Set<String> enabledPanels = new HashSet<>(List.of("Map", "Stats"));
    private InputConstants.Key demoKeyValue = InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_G);
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
        super(parent, Component.literal("EnchantedUI Demo Screen"));
        style(UIScreenStyle.builder()
                .backgroundBlur(true)
                .bottomBarBlur(true)
                .bottomBarBackgroundColor(0x88202020)
                .bottomBarSeparatorColor(0x66FFFFFF)
                .build());

        tab(10, 30, 20, Component.literal("Main"), EnchantedUI.formPage(200, form -> {
            UIWidget title = form.title(Component.literal("General Demo"));
            title.setTooltip(Tooltip.create(Component.literal("This is a demo title for the basic form layout.")));

            form.toggleRow(
                    Component.literal("Feature A"),
                    () -> enabled,
                    v -> enabled = v,
                    Component.literal("Feature B"),
                    () -> showAdvanced,
                    v -> showAdvanced = v
            );
            form.toggle(Component.literal("Boolean Option"), () -> notifications, v -> notifications = v)
                    .tooltip(Component.literal("This is a demo toggle using a full-width layout."));
            form.toggle(Component.literal("Secondary Toggle"), () -> debugOverlay, v -> debugOverlay = v)
                    .tooltip(Component.literal("This is another demo toggle with a tooltip."));
            form.intSlider(Component.literal("Integer Slider"), 1, 16, () -> radius, v -> radius = v, false)
                    .tooltip(Component.literal("This is a demo slider bound to an integer value."));
            UISlider intensitySlider = form.intSlider(
                    Component.literal("Percentage Slider"),
                    0,
                    100,
                    () -> intensity,
                    v -> intensity = v,
                    true
            );
            intensitySlider.tooltip(Component.literal("This is a demo slider showing percentage formatting."));
            form.longSlider(
                    Component.literal("Long Slider"),
                    500L,
                    10_000L,
                    250L,
                    () -> scanBudgetMicros,
                    value -> scanBudgetMicros = value,
                    false
            ).setValueFormatter(value -> Component.literal((long) value + " us"))
                    .tooltip(Component.literal("This is a demo slider bound to a long value."));
            form.floatSlider(
                    Component.literal("Float Slider"),
                    0.50F,
                    2.00F,
                    0.05F,
                    () -> uiScale,
                    value -> uiScale = value,
                    false
            ).setValueFormatter(value -> Component.literal(String.format(java.util.Locale.ROOT, "%.2fx", value)))
                    .tooltip(Component.literal("This is a demo slider bound to a float value."));
            form.doubleSlider(
                    Component.literal("Double Slider"),
                    0.0D,
                    1.0D,
                    0.01D,
                    () -> threshold,
                    value -> threshold = value,
                    true
            ).tooltip(Component.literal("This is a demo slider bound to a double value with a 0.01 step."));
            form.space(4);
            form.title(Component.literal("Text Area Demo"));
            form.textArea(Component.literal("Notes"), 40, () -> notes, v -> notes = v);
        }));

        tab(10, 54, 20, Component.literal("Input"), EnchantedUI.formPage(200, new UIFormSpec() {
            @Override
            public void build(UIForm form) {
                form.title(Component.literal("Input Demo"));
                UITextField profileField = form.textField(
                    Component.literal("Text Field"),
                    () -> profileName,
                        value -> profileName = value,
                        value -> value.isBlank()
                                ? Component.literal("This demo field cannot be empty.")
                                : value.length() > 16 ? Component.literal("This demo field accepts up to 16 characters.") : null
                );
                profileField.tooltip(Component.literal("This is a demo single-line text field with validation."));
                form.intField(
                        Component.literal("Integer Field"),
                        0,
                        64,
                        () -> radius,
                        value -> radius = value
                ).tooltip(Component.literal("This is a demo integer input field with range validation."));
                form.doubleField(
                        Component.literal("Double Field"),
                        0.0D,
                        1.0D,
                        () -> threshold,
                        value -> threshold = value
                ).tooltip(Component.literal("This is a demo numeric input field with decimal support."));

                form.keyBinding(
                        Component.literal("Key Binding"),
                        () -> demoKeyValue,
                        key -> {
                            demoKeyValue = key;
                            demoKey.setKey(key);
                        },
                        demoKey::getTranslatedKeyMessage,
                        demoKey,
                        true
                ).tooltip(Component.literal("This is a demo control for capturing one key binding."));
                form.combinationKeyBinding(
                        Component.literal("Key Combination"),
                        () -> comboKeys,
                        v -> {
                            comboKeys.clear();
                            comboKeys.addAll(v);
                        }
                ).tooltip(Component.literal("This is a demo control for capturing multiple keys."));

                UISlider passesSlider = form.intSlider(
                        Component.literal("Custom Value Slider"),
                        0,
                        8,
                        () -> passes,
                        v -> passes = v,
                        false
                );
                passesSlider.setCustomValueKey("eui.config.value.times");
                passesSlider.tooltip(Component.literal("This is a demo slider using a translated value label."));
            }

            @Override
            public void onShow(UIForm form) {
                inputTabShows++;
                showToast(Component.literal("This demo tab has been opened " + inputTabShows + " times."), 40);
            }
        }));

        tab(10, 78, 20, Component.literal("Colors"), EnchantedUI.formPage(200, form -> {
            form.rgbaSlidersWithPreview(
                    Component.literal("Color Preview Demo"),
                    () -> r, v -> r = v,
                    () -> g, v -> g = v,
                    () -> b, v -> b = v,
                    () -> a, v -> a = v,
                    false
            );
        }));

        tab(10, 102, 20, Component.literal("Actions"), Style.EMPTY.withItalic(true), EnchantedUI.formPage(220, form -> {
            form.title(Component.literal("Button Demo"));
            form.buttonRow(
                    Component.literal("Toast Button"),
                    () -> showToast(Component.literal("This is a demo toast message.")),
                    Component.literal("Dialog Button"),
                    () -> showConfirm(Component.literal("Demo Confirmation"), Component.literal("This is a demo confirmation dialog."), () -> {
                        textureClicks++;
                        iconClicks++;
                        init();
                    })
            );

            UIWidget textureButton = form.textureButton(
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
                        showToast(Component.literal("This is a demo texture button action: " + textureClicks));
                        init();
                    }
            );
            textureButton.tooltip(Component.literal("This is a demo texture button from the form API."));

            UIWidget iconButton = form.iconButton(
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
                        showToast(Component.literal("This is a demo icon button action: " + iconClicks));
                        init();
                    }
            );
            iconButton.tooltip(Component.literal("This is a demo icon button from the form API."));

            form.button(Component.literal("Go To First Tab"), () -> showPage(0))
                    .tooltip(Component.literal("This is a demo button that switches to another tab."));
            form.buttonRow(
                    Component.literal("Save All"),
                    () -> showToast(Component.literal(saveAll() ? "This demo form state has been saved." : "This demo save was blocked by validation.")),
                    Component.literal("Reload Values"),
                    () -> {
                        reloadAll();
                        showToast(Component.literal("This demo form state has been reloaded."));
                    }
            ).forEach(button -> button.activeIf(this::hasUnsavedChanges));
            form.button(Component.literal("Dirty State"), () ->
                    showToast(Component.literal(hasUnsavedChanges() ? "This demo screen currently has unsaved changes." : "This demo screen has no unsaved changes.")));

            form.section(Component.literal("Nested Demo"), nested -> {
                nested.toggle(Component.literal("Nested Toggle"), () -> enabled, v -> enabled = v)
                        .tooltip(Component.literal("This is a demo toggle inside a nested section."));
                nested.button(Component.literal("Open Dialog"), () -> showDialog(
                        Component.literal("Nested Demo Dialog"),
                        List.of(
                                Component.literal("This is a demo text line for the texture button count: " + textureClicks),
                                Component.literal("This is a demo text line for the icon button count: " + iconClicks)
                        ),
                        new UIDialogAction(Component.literal("Close"), () -> {}, true)
                )).tooltip(Component.literal("This is a demo button that opens a custom dialog."));
            });
        }));

        tab(10, 126, 20, Component.literal("Selection"), EnchantedUI.formPage(220, form -> {
            form.section(Component.literal("Selection Demo"), nested -> {
                nested.enumSelect(
                        Component.literal("Enum Select"),
                        RenderProfile.class,
                        () -> renderProfile,
                        value -> renderProfile = value,
                        value -> Component.literal(value.name().toLowerCase().replace('_', ' '))
                );

                nested.searchableSelect(
                        Component.literal("Searchable Select"),
                        () -> selectedBiome,
                        value -> selectedBiome = value,
                        () -> biomeOptions,
                        Component::literal,
                        Component.literal("Search biome")
                );

                nested.multiSelect(
                        Component.literal("Multi Select"),
                        () -> enabledPanels,
                        values -> {
                            enabledPanels.clear();
                            enabledPanels.addAll(values);
                        },
                        () -> List.of("Map", "Stats", "Timeline", "Crafting"),
                        Component::literal
                );
            });

            form.title(Component.literal("Readonly Dropdown Demo"));
            form.dropdownList(
                    Component.literal("Readonly Dropdown"),
                    () -> presetEntries
            ).setTooltip(Tooltip.create(Component.literal("This is a demo readonly dropdown. Open it to view the entries.")));

            form.space(4);
            form.title(Component.literal("Editable List Demo"));
            form.editableDropdownList(
                    Component.literal("Editable Dropdown"),
                    220,
                    () -> editableEntries,
                    entries -> {
                        editableEntries.clear();
                        editableEntries.addAll(entries);
                    },
                    Component.literal("Add new entry"),
                     Component.literal("Add"),
                    5,
                    value -> value.length() < 3 ? Component.literal("This demo entry must be at least 3 characters.") : null,
                    false
            ).setTooltip(Tooltip.create(Component.literal("This is a demo editable dropdown list.")));

            form.space(8);
            form.section(Component.literal("Radio Group Demo"), nested -> {
                nested.radioGroup(
                        Component.literal("Radio Group"),
                        () -> themePreset,
                        value -> themePreset = value,
                        () -> themeOptions,
                        Component::literal
                );
            });

            form.space(8);
            for (int i = 1; i <= 18; i++) {
                form.title(Component.literal("This is demo scroll content line " + i + "."));
            }
        }));

        tab(10, 150, 20, Component.literal("Display"), EnchantedUI.formPage(220, form -> {
            form.title(Component.literal("Display Demo"));
            form.infoBlock(
                    Component.literal("Info Block"),
                    Component.literal("This is a demo information block.")
            ).tooltip(Component.literal("This is a demo block for neutral information."));

            form.keyValueRow(
                    Component.literal("Profile Name"),
                    () -> Component.literal(profileName)
            ).tooltip(Component.literal("This is a demo key-value display row."));

            form.statusBadge(
                    Component.literal("Form State"),
                    () -> Component.literal(hasUnsavedChanges() ? "Modified" : "Clean"),
                    () -> hasUnsavedChanges() ? 0xFF8A5A2A : 0xFF3C6E47
            ).tooltip(Component.literal("This is a demo status badge with a dynamic value."));

            form.progressBar(
                    Component.literal("Progress Bar"),
                    () -> threshold
            ).tooltip(Component.literal("This is a demo progress bar using a normalized value."));

            form.progressBar(
                    Component.literal("Colored Progress"),
                    220,
                    () -> intensity / 100.0D,
                    0xFF8A4EA3
            ).tooltip(Component.literal("This is a demo progress bar with a custom fill color."));

            form.space(4);
            form.emptyState(
                    Component.literal("Empty State"),
                    Component.literal("This demo block represents an empty result area.")
            ).tooltip(Component.literal("This is a demo empty state display block."));

            form.space(4);
            form.loadingState(
                    Component.literal("Loading State"),
                    Component.literal("This demo block represents pending work")
            ).tooltip(Component.literal("This is a demo loading state display block."));

            form.errorState(
                    Component.literal("Error State"),
                    Component.literal("This demo block can expose a retry action."),
                    Component.literal("Retry"),
                    () -> showToast(Component.literal("This is a demo retry action."))
            ).tooltip(Component.literal("This is a demo error state with an action."));

            form.readonlyList(
                    Component.literal("Readonly List"),
                    () -> presetEntries,
                    4
            ).tooltip(Component.literal("This is a demo always-visible readonly list."));

            form.summaryBlock(
                    Component.literal("Summary Block"),
                    () -> List.of(
                            new UISummaryItem(Component.literal("Render Profile"), Component.literal(renderProfile.name())),
                            new UISummaryItem(Component.literal("Theme"), Component.literal(themePreset)),
                            new UISummaryItem(Component.literal("Threshold"), Component.literal(String.format(java.util.Locale.ROOT, "%.2f", threshold))),
                            new UISummaryItem(Component.literal("Panels"), Component.literal(Integer.toString(enabledPanels.size())))
                    )
            ).tooltip(Component.literal("This is a demo summary block."));
        }));

        bottomBar(UIBottomBar.saveAndCloseWithExtra(
                Component.literal("Close Demo"),
                Component.literal("Save Demo"),
                this::saveAll,
                Component.literal("R"),
                Tooltip.create(Component.literal("This resets the demo values to their initial state.")),
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
        scanBudgetMicros = 2_500L;
        uiScale = 1.25F;
        threshold = 0.35D;
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
        demoKeyValue = InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_G);
        demoKey.setKey(demoKeyValue);
        KeyMapping.resetMapping();
        textureClicks = 0;
        iconClicks = 0;
        r = 255;
        g = 120;
        b = 80;
        a = 255;
    }
}


