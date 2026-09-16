package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.resources.Identifier;

/** Texture-region descriptor used by public icon-button builders. */
public record UIIcon(Identifier texture, int textureWidth, int textureHeight, float u, float v) {
    public UIIcon(Identifier texture, int textureWidth, int textureHeight) { this(texture, textureWidth, textureHeight, 0, 0); }
}
