package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.gui.animation.UIAnimation;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Supplier;

/** Automatically pans overflowing text while leaving short text aligned normally. */
public final class UIScrollingLabelWidget extends AbstractWidget {
    private final Supplier<Component> text;
    private final Supplier<Integer> color;
    private final UILabelWidget.Align align;
    private final UIAnimation animation=UIAnimation.pingPong().duration(Duration.ofSeconds(5));
    public UIScrollingLabelWidget(int x,int y,int width,int height,Supplier<Component> text,Supplier<Integer> color,UILabelWidget.Align align){
        super(x,y,width,height,Component.empty());this.text=Objects.requireNonNull(text);this.color=Objects.requireNonNull(color);this.align=Objects.requireNonNull(align);active=false;
    }
    @Override protected void extractWidgetRenderState(GuiGraphicsExtractor g,int mouseX,int mouseY,float partialTick){
        Component value=text.get();int textWidth=Minecraft.getInstance().font.width(value);int drawX=getX();
        if(textWidth>width) drawX-=Math.round((textWidth-width)*animation.value());
        else if(align==UILabelWidget.Align.CENTER)drawX+=(width-textWidth)/2;
        else if(align==UILabelWidget.Align.RIGHT)drawX+=width-textWidth;
        g.enableScissor(getX(),getY(),getX()+width,getY()+height);
        g.text(Minecraft.getInstance().font,value,drawX,getY()+(height-9)/2,color.get(),false);
        g.disableScissor();
    }
    @Override protected void updateWidgetNarration(NarrationElementOutput output){}
}
