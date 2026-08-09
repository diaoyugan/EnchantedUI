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

/** General-purpose label that pans text only when it exceeds its available width. */
public class UIMarqueeLabel extends AbstractWidget {
    private final Supplier<Component> text;
    private final Supplier<Integer> color;
    private final UILabelWidget.Align align;
    private final UIAnimation animation;
    public UIMarqueeLabel(int x,int y,int width,int height,Supplier<Component> text,Supplier<Integer> color,UILabelWidget.Align align){
        this(x,y,width,height,text,color,align,Duration.ofSeconds(5));
    }
    public UIMarqueeLabel(int x,int y,int width,int height,Supplier<Component> text,Supplier<Integer> color,UILabelWidget.Align align,Duration duration){
        super(x,y,width,height,Component.empty());this.text=Objects.requireNonNull(text);this.color=Objects.requireNonNull(color);this.align=Objects.requireNonNull(align);
        animation=UIAnimation.pingPong().duration(Objects.requireNonNull(duration));active=false;
    }
    @Override protected void extractWidgetRenderState(GuiGraphicsExtractor g,int mouseX,int mouseY,float partialTick){
        Component value=text.get();int textWidth=Minecraft.getInstance().font.width(value);int drawX=getX();
        if(textWidth>width)drawX-=Math.round((textWidth-width)*animation.value());
        else if(align==UILabelWidget.Align.CENTER)drawX+=(width-textWidth)/2;
        else if(align==UILabelWidget.Align.RIGHT)drawX+=width-textWidth;
        g.enableScissor(getX(),getY(),getX()+width,getY()+height);
        g.text(Minecraft.getInstance().font,value,drawX,getY()+(height-9)/2,color.get(),false);g.disableScissor();
    }
    @Override protected void updateWidgetNarration(NarrationElementOutput output){}
}
