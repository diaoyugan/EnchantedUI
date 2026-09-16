package top.diaoyugan.enchanted_ui.api.client.gui.theme;

/** Immutable visual tokens shared by EUI widgets. */
public record UITheme(Colors colors, Spacing spacing, Typography typography, Radius radius, Shadows shadows, Animation animation) {
    public static final UITheme DEFAULT = new UITheme(
            new Colors(0xEE17191D, 0xFF24272D, 0xFF30343C, 0xFF4D8DFF, 0xFF62C98D,
                    0xFFFFFFFF, 0xFFB7BBC5, 0xFF59606D, 0xAA000000),
            new Spacing(2, 4, 8, 12, 16), new Typography(0.75F, 1.0F, 1.25F),
            new Radius(2, 4, 8, 999), new Shadows(0x55000000, 2, 3), new Animation(150, 250, 400)
    );

    public record Colors(int background, int surface, int surfaceHovered, int accent, int playing,
                         int text, int textMuted, int border, int shadow) {}
    public record Spacing(int xSmall, int small, int medium, int large, int xLarge) {}
    public record Typography(float smallScale, float bodyScale, float titleScale) {}
    public record Radius(int small, int medium, int card, int round) {}
    public record Shadows(int color, int offsetX, int offsetY) {}
    public record Animation(int fastMillis, int normalMillis, int slowMillis) {}
}
