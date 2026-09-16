package top.diaoyugan.enchanted_ui.api.client.gui.time;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.gui.UILabelWidget;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UIThemes;

import java.time.Duration;
import java.util.Objects;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

/** Reusable duration display for an independent timer or normalized progress source. */
public final class UITimerWidget extends AbstractWidget {
    private final Supplier<Duration> value;
    private final UILabelWidget.Align align;
    private final boolean showHours;

    public UITimerWidget(int x,int y,int width,int height,UITimer timer){this(x,y,width,height,timer::value,UILabelWidget.Align.CENTER,false);}
    public UITimerWidget(int x,int y,int width,int height,Supplier<Duration> value,UILabelWidget.Align align,boolean showHours){
        super(x,y,width,height,Component.empty());this.value=Objects.requireNonNull(value);this.align=Objects.requireNonNull(align);this.showHours=showHours;active=false;
    }
    public static UITimerWidget progress(int x,int y,int width,int height,UITimerMode mode,Supplier<Duration> duration,DoubleSupplier progress){
        return new UITimerWidget(x,y,width,height,()->{
            Duration total=nonNegative(duration.get());double normalized=Math.max(0,Math.min(1,progress.getAsDouble()));
            double factor=mode==UITimerMode.COUNT_DOWN?1.0D-normalized:normalized;
            return Duration.ofMillis(Math.max(0,Math.round(total.toMillis()*factor)));
        },UILabelWidget.Align.CENTER,false);
    }
    @Override protected void extractWidgetRenderState(GuiGraphicsExtractor g,int mouseX,int mouseY,float partialTick){
        Component text=Component.literal(format(value.get(),showHours));setMessage(text);int textWidth=Minecraft.getInstance().font.width(text);
        int drawX=switch(align){case LEFT->getX();case CENTER->getX()+(width-textWidth)/2;case RIGHT->getX()+width-textWidth;};
        g.text(Minecraft.getInstance().font,text,drawX,getY()+(height-9)/2,UIThemes.current().colors().textMuted(),false);
    }
    @Override protected void updateWidgetNarration(NarrationElementOutput output){output.add(NarratedElementType.TITLE,getMessage());}
    public static String format(Duration value,boolean showHours){
        long seconds=Math.max(0,nonNegative(value).toSeconds());long hours=seconds/3600;long minutes=(seconds%3600)/60;long remainder=seconds%60;
        return showHours||hours>0?String.format(java.util.Locale.ROOT,"%d:%02d:%02d",hours,minutes,remainder):String.format(java.util.Locale.ROOT,"%d:%02d",minutes,remainder);
    }
    private static Duration nonNegative(Duration value){return value==null||value.isNegative()?Duration.ZERO:value;}
}
