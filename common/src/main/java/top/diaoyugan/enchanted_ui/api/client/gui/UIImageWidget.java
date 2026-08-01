package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Objects;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

/** Version-neutral image widget with fit, masking and pivoted transforms. */
public final class UIImageWidget extends AbstractWidget {
    private final Supplier<Identifier> texture;
    private final int textureWidth, textureHeight;
    private final boolean guiSprite;
    private final ImageFit fit;
    private final ImageMask mask;
    private final DoubleSupplier rotation, scale, opacity;
    private final float pivotX, pivotY;
    private final int background, border, shadow, cornerRadius;

    private UIImageWidget(Builder b) {
        super(b.x, b.y, b.width, b.height, b.narration);
        texture=b.texture; textureWidth=b.textureWidth; textureHeight=b.textureHeight; guiSprite=b.guiSprite;
        fit=b.fit; mask=b.mask; rotation=b.rotation; scale=b.scale; opacity=b.opacity;
        pivotX=b.pivotX; pivotY=b.pivotY; background=b.background; border=b.border; shadow=b.shadow; cornerRadius=b.cornerRadius;
        active = false;
    }

    public static Builder builder(Identifier texture) { return new Builder(() -> texture); }
    public static Builder builder(Supplier<Identifier> texture) { return new Builder(texture); }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        if ((shadow >>> 24) != 0) g.fill(getX() + 2, getY() + 2, getX() + width + 3, getY() + height + 3, shadow);
        if ((background >>> 24) != 0) g.fill(getX(), getY(), getX() + width, getY() + height, background);
        float px = getX() + width * pivotX, py = getY() + height * pivotY;
        g.pose().pushMatrix();
        g.pose().translate(px, py);
        g.pose().rotate((float) Math.toRadians(rotation.getAsDouble()));
        float scaleValue = (float) Math.max(0, scale.getAsDouble());
        g.pose().scale(scaleValue, scaleValue);
        g.pose().translate(-px, -py);
        renderMasked(g);
        g.pose().popMatrix();
        if ((border >>> 24) != 0) g.outline(getX(), getY(), width, height, border);
    }

    private void renderMasked(GuiGraphicsExtractor g) {
        if (mask == ImageMask.NONE) {
            renderImage(g);
            return;
        }
        int radius = mask == ImageMask.CIRCLE ? Math.min(width, height) / 2 : Math.min(cornerRadius, Math.min(width, height) / 2);
        for (int row = 0; row < height; row++) {
            int inset = maskInset(row, height, radius, mask == ImageMask.CIRCLE);
            if (inset * 2 >= width) continue;
            g.enableScissor(getX() + inset, getY() + row, getX() + width - inset, getY() + row + 1);
            renderImage(g);
            g.disableScissor();
        }
    }

    private static int maskInset(int row, int height, int radius, boolean circle) {
        if (radius <= 0) return 0;
        int edgeDistance = Math.min(row, height - 1 - row);
        if (!circle && edgeDistance >= radius) return 0;
        double dy = radius - edgeDistance - 0.5;
        return Math.max(0, (int) Math.ceil(radius - Math.sqrt(Math.max(0, radius * radius - dy * dy))));
    }

    private void renderImage(GuiGraphicsExtractor g) {
        Identifier id = texture.get();
        if (id == null) return;
        if (guiSprite) {
            g.blitSprite(RenderPipelines.GUI_TEXTURED, id, getX(), getY(), width, height, (float) clamp01(opacity.getAsDouble()));
            return;
        }
        int drawX=getX(), drawY=getY(), drawW=width, drawH=height;
        float u=0, v=0, sourceW=textureWidth, sourceH=textureHeight;
        double sourceRatio=textureWidth/(double)textureHeight, targetRatio=width/(double)Math.max(1,height);
        if (fit == ImageFit.CONTAIN) {
            if (sourceRatio > targetRatio) { drawH=(int)Math.round(width/sourceRatio); drawY+=(height-drawH)/2; }
            else { drawW=(int)Math.round(height*sourceRatio); drawX+=(width-drawW)/2; }
        } else if (fit == ImageFit.COVER) {
            if (sourceRatio > targetRatio) { sourceW=(float)(textureHeight*targetRatio); u=(textureWidth-sourceW)/2F; }
            else { sourceH=(float)(textureWidth/targetRatio); v=(textureHeight-sourceH)/2F; }
        }
        int alpha = (int) Math.round(clamp01(opacity.getAsDouble()) * 255.0);
        int color = (alpha << 24) | 0x00FFFFFF;
        g.blit(RenderPipelines.GUI_TEXTURED, id, drawX, drawY, u, v, drawW, drawH,
                Math.round(sourceW), Math.round(sourceH), textureWidth, textureHeight, color);
    }

    private static double clamp01(double value) { return Math.max(0, Math.min(1, value)); }
    @Override protected void updateWidgetNarration(NarrationElementOutput output) {}

    public static final class Builder {
        private final Supplier<Identifier> texture;
        private int x, y, width=64, height=64, textureWidth=256, textureHeight=256;
        private boolean guiSprite;
        private ImageFit fit=ImageFit.CONTAIN;
        private ImageMask mask=ImageMask.NONE;
        private DoubleSupplier rotation=()->0, scale=()->1, opacity=()->1;
        private float pivotX=.5F,pivotY=.5F;
        private int background, border, shadow, cornerRadius=8;
        private Component narration=Component.empty();
        private Builder(Supplier<Identifier> texture) { this.texture=Objects.requireNonNull(texture); }
        public Builder bounds(int x,int y,int width,int height){this.x=x;this.y=y;this.width=width;this.height=height;return this;}
        public Builder textureSize(int width,int height){textureWidth=Math.max(1,width);textureHeight=Math.max(1,height);return this;}
        public Builder guiSprite(boolean value){guiSprite=value;return this;}
        public Builder fit(ImageFit value){fit=Objects.requireNonNull(value);return this;}
        public Builder mask(ImageMask value){mask=Objects.requireNonNull(value);return this;}
        public Builder cornerRadius(int value){cornerRadius=Math.max(0,value);return this;}
        public Builder rotation(DoubleSupplier value){rotation=Objects.requireNonNull(value);return this;}
        public Builder scale(DoubleSupplier value){scale=Objects.requireNonNull(value);return this;}
        public Builder opacity(DoubleSupplier value){opacity=Objects.requireNonNull(value);return this;}
        public Builder pivot(float x,float y){pivotX=x;pivotY=y;return this;}
        public Builder background(int color){background=color;return this;}
        public Builder border(int color){border=color;return this;}
        public Builder shadow(int color){shadow=color;return this;}
        public Builder narration(Component value){narration=Objects.requireNonNull(value);return this;}
        public UIImageWidget build(){return new UIImageWidget(this);}
    }
}
