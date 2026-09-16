package top.diaoyugan.enchanted_ui.client.gui.widget.button;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import top.diaoyugan.enchanted_ui.client.gui.render.sprite.WidgetState;
import top.diaoyugan.enchanted_ui.client.gui.render.sprite.EnhancedWidgetSprites;
import top.diaoyugan.enchanted_ui.client.gui.render.sprite.SpriteData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UIThemes;

public class IconButton extends Button.Plain {
    private final EnhancedWidgetSprites iconSprites;
    private final int iconSize;
    private final List<ConditionalIcon> conditionalIcons;
    private final BooleanSupplier selected;
    private final boolean circular;


    public IconButton(int x, int y, int width, int height,
                      EnhancedWidgetSprites iconSprites,
                      int iconSize,
                      OnPress onPress) {

        this(x, y, width, height, iconSprites, iconSize, onPress, List.of(), () -> false, false, Component.empty());
    }

    private IconButton(int x, int y, int width, int height,
                       EnhancedWidgetSprites iconSprites, int iconSize, OnPress onPress,
                       List<ConditionalIcon> conditionalIcons, BooleanSupplier selected,
                       boolean circular, Component narration) {

        super(x, y, width, height, narration, onPress, Button.DEFAULT_NARRATION);

        this.iconSprites = iconSprites;
        this.iconSize = iconSize;
        this.conditionalIcons = conditionalIcons;
        this.selected = selected;
        this.circular = circular;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {

        if (!circular) {
            super.extractContents(g, mouseX, mouseY, partialTick);
        } else {
            var colors = UIThemes.current().colors();
            int color = selected.getAsBoolean() ? colors.accent() : isHoveredOrFocused() ? colors.surfaceHovered() : colors.surface();
            int radius = Math.min(width, height) / 2;
            for (int row = -radius; row < radius; row++) {
                int half = (int)Math.sqrt(Math.max(0, radius * radius - row * row));
                g.fill(getX() + width / 2 - half, getY() + height / 2 + row,
                        getX() + width / 2 + half, getY() + height / 2 + row + 1, color);
            }
        }

        EnhancedWidgetSprites activeSprites = iconSprites;
        for (ConditionalIcon conditional : conditionalIcons) {
            if (conditional.condition().getAsBoolean()) { activeSprites = conditional.sprites(); break; }
        }
        SpriteData sprite = activeSprites.get(this.active, this.isHoveredOrFocused());

        int iconX = this.getX() + (this.width - iconSize) / 2;
        int iconY = this.getY() + (this.height - iconSize) / 2;

        g.blit(
                RenderPipelines.GUI_TEXTURED,
                sprite.texture(),
                iconX,
                iconY,
                sprite.u(),
                sprite.v(),
                iconSize,
                iconSize,
                sprite.texW(),
                sprite.texH()
        );
        if (!active) {
            g.fill(iconX, iconY, iconX + iconSize, iconY + iconSize, 0x66000000);
        }
    }

    /* ---------- Builder ---------- */

    public static class Builder {

        private final int x, y, width, height;

        private Identifier texture;
        private int texW, texH;

        private Identifier hoverTexture;
        private int hoverTexW, hoverTexH;

        private float u, v;
        private float hoverVShift = 0;

        private int iconSize = 16;

        private OnPress onPress = b -> {};
        private final List<ConditionalIcon> conditionalIcons = new ArrayList<>();
        private BooleanSupplier selected = () -> false;
        private boolean circular;
        private Component narration = Component.empty();

        public Builder(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public Builder icon(Identifier texture, int texW, int texH) {
            this.texture = texture;
            this.texW = texW;
            this.texH = texH;
            return this;
        }

        public Builder hoverIcon(Identifier texture, int texW, int texH) {
            this.hoverTexture = texture;
            this.hoverTexW = texW;
            this.hoverTexH = texH;
            return this;
        }

        public Builder uv(float u, float v) {
            this.u = u;
            this.v = v;
            return this;
        }

        public Builder hoverShift(float shift) {
            this.hoverVShift = shift;
            return this;
        }

        public Builder iconSize(int size) {
            this.iconSize = size;
            return this;
        }

        public Builder onPress(OnPress onPress) {
            this.onPress = onPress;
            return this;
        }

        public Builder iconWhen(BooleanSupplier condition, Identifier texture, int texW, int texH) {
            return iconWhen(condition, texture, texW, texH, u, v);
        }

        public Builder iconWhen(BooleanSupplier condition, Identifier texture, int texW, int texH, float u, float v) {
            SpriteData sprite = new SpriteData(texture, u, v, texW, texH);
            conditionalIcons.add(new ConditionalIcon(condition, new EnhancedWidgetSprites(sprite)));
            return this;
        }

        public Builder selected(BooleanSupplier selected) {
            this.selected = selected;
            return this;
        }

        public Builder circular(boolean circular) {
            this.circular = circular;
            return this;
        }

        public Builder narration(Component narration) {
            this.narration = narration;
            return this;
        }

        public IconButton build() {

            if (texture == null) {
                throw new IllegalStateException("Icon texture not set");
            }

            SpriteData normal = new SpriteData(texture, u, v, texW, texH);

            SpriteData hovered;

            if (hoverTexture != null) {
                hovered = new SpriteData(hoverTexture, u, v, hoverTexW, hoverTexH);
            } else {
                hovered = new SpriteData(texture, u, v + hoverVShift, texW, texH);
            }

            EnhancedWidgetSprites sprites = new EnhancedWidgetSprites(normal)
                    .with(WidgetState.HOVERED, hovered);

            return new IconButton(
                    x, y, width, height,
                    sprites,
                    iconSize,
                    onPress,
                    List.copyOf(conditionalIcons),
                    selected,
                    circular,
                    narration
            );
        }
    }

    private record ConditionalIcon(BooleanSupplier condition, EnhancedWidgetSprites sprites) {}
}
