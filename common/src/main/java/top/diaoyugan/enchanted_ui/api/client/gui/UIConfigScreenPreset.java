package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Shared declaration lifecycle for tabbed configuration-screen presets.
 * <p>
 * Layout-specific subclasses configure the title, tab strip, and content
 * viewport in their constructor. Consumer screens only implement
 * {@link #buildConfig(Builder)}. Keeping declaration here prevents every visual
 * preset from carrying its own copy of page, style, and bottom-bar wiring.
 */
public abstract class UIConfigScreenPreset extends UITabbedScreen {

    private final int defaultContentWidth;
    private final int contentStartY;
    private final int tabHeight;
    private boolean configBuilt;

    protected UIConfigScreenPreset(
            @Nullable Screen parent,
            Component title,
            int defaultContentWidth,
            int contentStartY,
            int tabHeight
    ) {
        super(parent, title);
        if (defaultContentWidth <= 0 || contentStartY < 0 || tabHeight <= 0) {
            throw new IllegalArgumentException("Preset layout dimensions are invalid");
        }
        this.defaultContentWidth = defaultContentWidth;
        this.contentStartY = contentStartY;
        this.tabHeight = tabHeight;
    }

    /** Declares pages and optional overrides for this preset. */
    protected abstract void buildConfig(Builder config);

    @Override
    protected final void init() {
        // Screen initialization happens after subclass construction, so page
        // callbacks may safely capture fields declared by the consumer screen.
        if (!configBuilt) {
            Builder builder = new Builder();
            buildConfig(builder);
            builder.close();
            configBuilt = true;
        }
        super.init();
    }

    public final class Builder {
        private boolean open = true;

        private Builder() {
        }

        public Builder formPage(Component label, UIFormSpec spec) {
            return formPage(label, defaultContentWidth, spec);
        }

        public Builder formPage(Component label, int contentWidth, UIFormSpec spec) {
            return page(label, new UIFormPage(contentWidth, contentStartY, 4, spec));
        }

        public Builder page(Component label, UIPage page) {
            ensureOpen();
            UIConfigScreenPreset.this.tab(0, 0, tabHeight, label, page);
            return this;
        }

        public Builder formPage(Component label, Style style, UIFormSpec spec) {
            return formPage(label, style, defaultContentWidth, spec);
        }

        public Builder formPage(Component label, Style style, int contentWidth, UIFormSpec spec) {
            ensureOpen();
            UIConfigScreenPreset.this.tab(
                    0,
                    0,
                    tabHeight,
                    label,
                    Objects.requireNonNull(style, "style"),
                    new UIFormPage(contentWidth, contentStartY, 4, spec)
            );
            return this;
        }

        public Builder bottomBar(UIBottomBar bottomBar) {
            ensureOpen();
            UIConfigScreenPreset.this.bottomBar(bottomBar);
            return this;
        }

        public Builder style(UIScreenStyle style) {
            ensureOpen();
            UIConfigScreenPreset.this.style(style);
            return this;
        }

        public Builder unsavedChangesPrompt(UIUnsavedChangesPrompt prompt) {
            ensureOpen();
            UIConfigScreenPreset.this.unsavedChangesPrompt(prompt);
            return this;
        }

        private void close() {
            open = false;
        }

        private void ensureOpen() {
            if (!open) {
                throw new IllegalStateException("This config screen builder is no longer active");
            }
        }
    }
}
