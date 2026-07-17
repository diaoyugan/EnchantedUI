package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Single-page preset for help, status, about, summary, and other primarily
 * informational screens. The content uses {@link UIForm}, so buttons, links
 * represented as actions, toggles, lists, and other interactive widgets remain
 * available. Long content scrolls inside a clipped viewport automatically.
 */
public abstract class UIInfoScreen extends UITabbedScreen {

    public static final int DEFAULT_CONTENT_WIDTH = 360;
    private static final int CONTENT_START_Y = 34;
    private boolean contentBuilt;

    protected UIInfoScreen(@Nullable Screen parent, Component title, Component closeLabel) {
        super(parent, title);
        headerTitle(Objects.requireNonNull(title, "title"));
        tabsVisible(false);
        contentViewport(8, 30, 8, 36);
        bottomBar(UIBottomBar.closeOnly(Objects.requireNonNull(closeLabel, "closeLabel")));
    }

    /** Adds informational and interactive rows in display order. */
    protected abstract void buildContent(UIForm form);

    /** Override to change the maximum content width. */
    protected int contentWidth() {
        return DEFAULT_CONTENT_WIDTH;
    }

    @Override
    protected void init() {
        if (!contentBuilt) {
            tab(0, 0, 20, Component.empty(), new UIFormPage(
                    contentWidth(),
                    CONTENT_START_Y,
                    4,
                    this::buildContent
            ));
            contentBuilt = true;
        }
        super.init();
    }
}
