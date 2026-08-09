package top.diaoyugan.enchanted_ui.api.client.gui.music;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.gui.*;
import top.diaoyugan.enchanted_ui.api.client.gui.layout.UIBounds;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UITheme;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UIThemes;
import top.diaoyugan.enchanted_ui.api.client.gui.time.UITimerMode;
import top.diaoyugan.enchanted_ui.api.client.gui.time.UITimerWidget;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Independently reusable music transport group; build its widgets into any screen region. */
public final class UIMusicTransportControls {
    private final UIMusicPlayerModel model;
    private final UITheme theme;
    private final boolean previous,playPause,next,mode,progress,volume,elapsed,remaining;
    private final int maxWidth;
    private UIMusicTransportControls(Builder b){model=b.model;theme=b.theme;previous=b.previous;playPause=b.playPause;next=b.next;
        mode=b.mode;progress=b.progress;volume=b.volume;elapsed=b.elapsed;remaining=b.remaining;maxWidth=b.maxWidth;}
    public static Builder builder(UIMusicPlayerModel model){return new Builder(model);}
    public UIMusicPlayerModel model(){return model;}

    public List<AbstractWidget> build(UIBounds region){
        List<AbstractWidget> widgets=new ArrayList<>();int width=Math.min(maxWidth,region.width());int left=region.x()+(region.width()-width)/2;
        int y=region.y()+(region.height()-20)/2;int cursor=left;
        if(previous){UIDynamicButton button=button(cursor,y,26,"|<",()->UILocalization.frameworkText("music.control.previous","Previous track"),model::previous);widgets.add(button);cursor+=30;}
        if(playPause){UIDynamicButton button=new UIDynamicButton(cursor,y,30,20,()->Component.literal(model.playbackState().get()==UIPlaybackState.PLAYING?"||":">"),
                ()->model.playbackState().get()==UIPlaybackState.PLAYING?UILocalization.frameworkText("music.control.pause","Pause"):UILocalization.frameworkText("music.control.play","Play"),model::togglePlayback);
            button.setTooltip(Tooltip.create(UILocalization.frameworkText("music.control.play_pause","Play / pause")));widgets.add(button);cursor+=34;}
        if(next){widgets.add(button(cursor,y,26,">|",()->UILocalization.frameworkText("music.control.next","Next track"),model::next));cursor+=30;}
        if(mode){UIDynamicButton button=new UIDynamicButton(cursor,y,28,20,()->Component.literal(model.playbackMode().get().symbol()),
                ()->UILocalization.frameworkText("music.control.mode.current","Playback mode: %s",model.playbackMode().get().label()),this::cycleMode);
            button.setTooltip(Tooltip.create(UILocalization.frameworkText("music.control.mode.tooltip","Change playback mode")));widgets.add(button);cursor+=32;}

        int right=left+width;boolean showVolume=volume&&width>=240;if(showVolume)right-=66;
        boolean showTimers=(elapsed||remaining)&&width>=280;int timerWidth=34;
        int timerSpace=showTimers?(elapsed?timerWidth+4:0)+(remaining?timerWidth+4:0):0;
        int progressWidth=Math.max(12,right-cursor-timerSpace);
        if(showTimers&&elapsed){UITimerWidget timer=UITimerWidget.progress(cursor,y+2,timerWidth,16,UITimerMode.COUNT_UP,()->safeDuration(model.duration().get()),()->model.progress().get());
            timer.setTooltip(Tooltip.create(UILocalization.frameworkText("music.timer.elapsed","Elapsed time")));widgets.add(timer);cursor+=timerWidth+4;}
        if(progress){widgets.add(new UINormalizedSlider(cursor,y+2,progressWidth,16,UILocalization.frameworkText("music.control.progress","Playback progress"),()->model.progress().get(),model::seek,theme));cursor+=progressWidth;}
        if(showTimers&&remaining){cursor+=4;UITimerWidget timer=UITimerWidget.progress(cursor,y+2,timerWidth,16,UITimerMode.COUNT_DOWN,()->safeDuration(model.duration().get()),()->model.progress().get());
            timer.setTooltip(Tooltip.create(UILocalization.frameworkText("music.timer.remaining","Remaining time")));widgets.add(timer);}
        if(showVolume){widgets.add(new UILabelWidget(left+width-62,y,10,20,()->Component.literal("V")));
            widgets.add(new UINormalizedSlider(left+width-50,y+2,50,16,UILocalization.frameworkText("music.control.volume","Volume"),()->model.volume().get(),model::volume,theme));}
        return widgets;
    }

    private UIDynamicButton button(int x,int y,int width,String symbol,java.util.function.Supplier<Component> narration,Runnable action){
        UIDynamicButton button=new UIDynamicButton(x,y,width,20,()->Component.literal(symbol),narration,action);button.setTooltip(Tooltip.create(narration.get()));return button;
    }
    private void cycleMode(){List<UIPlaybackMode> values=model.playbackModes();if(values.isEmpty())return;int i=values.indexOf(model.playbackMode().get());model.playbackMode(values.get((i+1+values.size())%values.size()));}
    private static Duration safeDuration(Duration value){return value==null||value.isNegative()?Duration.ZERO:value;}

    public static final class Builder {
        private final UIMusicPlayerModel model;private UITheme theme=UIThemes.current();
        private boolean previous=true,playPause=true,next=true,mode=true,progress=true,volume=true,elapsed=true,remaining=true;private int maxWidth=620;
        private Builder(UIMusicPlayerModel model){this.model=Objects.requireNonNull(model);}
        public Builder theme(UITheme value){theme=Objects.requireNonNull(value);return this;}
        public Builder previous(boolean value){previous=value;return this;}
        public Builder playPause(boolean value){playPause=value;return this;}
        public Builder next(boolean value){next=value;return this;}
        public Builder playbackMode(boolean value){mode=value;return this;}
        public Builder progress(boolean value){progress=value;return this;}
        public Builder volume(boolean value){volume=value;return this;}
        public Builder elapsedTimer(boolean value){elapsed=value;return this;}
        public Builder remainingTimer(boolean value){remaining=value;return this;}
        public Builder maxWidth(int value){maxWidth=Math.max(120,value);return this;}
        public UIMusicTransportControls build(){return new UIMusicTransportControls(this);}
    }
}
