package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Objects;
import java.util.function.Supplier;

/** General-purpose button with independently supplied visual label and narration text. */
public class UIDynamicButton extends Button.Plain {
    private final Supplier<Component> label;
    private final Supplier<Component> narration;

    public UIDynamicButton(int x,int y,int width,int height,Supplier<Component> label,Runnable onPress){
        this(x,y,width,height,label,label,onPress);
    }

    public UIDynamicButton(int x,int y,int width,int height,Supplier<Component> label,Supplier<Component> narration,Runnable onPress){
        super(x,y,width,height,Component.empty(),ignored->onPress.run(),Button.DEFAULT_NARRATION);
        this.label=Objects.requireNonNull(label);this.narration=Objects.requireNonNull(narration);
    }

    @Override public void extractContents(GuiGraphicsExtractor g,int mouseX,int mouseY,float partialTick){setMessage(label.get());super.extractContents(g,mouseX,mouseY,partialTick);}
    @Override protected MutableComponent createNarrationMessage(){return wrapDefaultNarrationMessage(narration.get());}
}
