package top.diaoyugan.enchanted_ui.client.gui.render.sprite;

import net.minecraft.resources.Identifier;

public record SpriteData(
        Identifier texture,
        float u,
        float v,
        int texW,
        int texH
) {}
