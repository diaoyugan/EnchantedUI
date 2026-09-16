package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import java.util.Objects;
import java.util.function.Supplier;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UIThemes;

/** Supplier-backed label that needs no tick-time message mutation. */
public final class UILabelWidget extends AbstractWidget {
    public enum Align { LEFT, CENTER, RIGHT }
    private final Supplier<Component> text;
    private final Supplier<Integer> color;
    private final Align align;
    public UILabelWidget(int x,int y,int width,int height,Supplier<Component> text,Supplier<Integer> color,Align align) {
        super(x,y,width,height,Component.empty()); this.text=Objects.requireNonNull(text); this.color=Objects.requireNonNull(color); this.align=align; active=false;
    }
    public UILabelWidget(int x,int y,int width,int height,Supplier<Component> text) { this(x,y,width,height,text,()->UIThemes.current().colors().text(),Align.LEFT); }
    @Override protected void extractWidgetRenderState(GuiGraphicsExtractor g,int mouseX,int mouseY,float partialTick) {
        Component value=text.get(); int textWidth=Minecraft.getInstance().font.width(value);
        int drawX=switch(align){case LEFT->getX();case CENTER->getX()+(width-textWidth)/2;case RIGHT->getX()+width-textWidth;};
        g.text(Minecraft.getInstance().font,value,drawX,getY()+(height-9)/2,color.get(),false);
    }
    @Override protected void updateWidgetNarration(NarrationElementOutput output) {}
}
