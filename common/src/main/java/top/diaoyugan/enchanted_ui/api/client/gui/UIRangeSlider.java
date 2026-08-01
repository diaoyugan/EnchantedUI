package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UITheme;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UIThemes;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

/** Compact seek/volume control bound to a normalized value. */
public final class UIRangeSlider extends AbstractWidget {
    private final DoubleSupplier value;
    private final DoubleConsumer setter;
    private final UITheme theme;
    public UIRangeSlider(int x,int y,int width,int height,Component narration,DoubleSupplier value,DoubleConsumer setter) {
        this(x,y,width,height,narration,value,setter,UIThemes.current());
    }
    public UIRangeSlider(int x,int y,int width,int height,Component narration,DoubleSupplier value,DoubleConsumer setter,UITheme theme) {
        super(x,y,width,height,narration);this.value=value;this.setter=setter;this.theme=theme;
    }
    @Override protected void extractWidgetRenderState(GuiGraphicsExtractor g,int mouseX,int mouseY,float partialTick) {
        int center=getY()+height/2;g.fill(getX(),center-1,getX()+width,center+2,theme.colors().border());
        int filled=(int)Math.round(width*clamp(value.getAsDouble()));g.fill(getX(),center-1,getX()+filled,center+2,theme.colors().accent());
        g.fill(getX()+filled-2,center-3,getX()+filled+3,center+4,isHoveredOrFocused()?theme.colors().text():theme.colors().textMuted());
    }
    @Override public void onClick(MouseButtonEvent event,boolean doubleClick){set(event.x());}
    @Override protected void onDrag(MouseButtonEvent event,double dragX,double dragY){set(event.x());}
    private void set(double x){setter.accept(clamp((x-getX())/Math.max(1.0,width)));}
    private static double clamp(double value){return Math.max(0,Math.min(1,value));}
    @Override protected void updateWidgetNarration(NarrationElementOutput output){}
}
