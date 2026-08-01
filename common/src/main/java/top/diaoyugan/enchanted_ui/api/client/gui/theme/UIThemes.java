package top.diaoyugan.enchanted_ui.api.client.gui.theme;

import java.util.Objects;

/** Process-wide theme entry point. Screens may temporarily replace the active tokens. */
public final class UIThemes {
    private static UITheme current = UITheme.DEFAULT;
    private UIThemes() {}
    public static UITheme current() { return current; }
    public static void use(UITheme theme) { current = Objects.requireNonNull(theme, "theme"); }
}
