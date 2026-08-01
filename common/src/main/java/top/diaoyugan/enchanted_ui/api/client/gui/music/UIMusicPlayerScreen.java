package top.diaoyugan.enchanted_ui.api.client.gui.music;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import top.diaoyugan.enchanted_ui.api.client.gui.*;
import top.diaoyugan.enchanted_ui.api.client.gui.state.UISubscription;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UITheme;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UIThemes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Responsive three-column music-library screen with a fixed transport dock. */
public class UIMusicPlayerScreen extends UITabbedScreen {
    private final UIMusicPlayerModel model;
    private final UITheme theme;

    public UIMusicPlayerScreen(@Nullable Screen parent, Component title, UIMusicPlayerModel model) {
        this(parent,title,model,UIThemes.current());
    }

    public UIMusicPlayerScreen(@Nullable Screen parent, Component title, UIMusicPlayerModel model, UITheme theme) {
        super(parent,title); this.model=Objects.requireNonNull(model);this.theme=Objects.requireNonNull(theme);
        headerTitle(title);tabsVisible(false);contentViewport(8,34,8,54);
        tab(0,0,20,Component.empty(),new PlayerPage());
        bottomBar((UIBottomBar) (screen,centerX,bottomY)->addTransport(screen,centerX,bottomY));
    }

    /** Adds the default transport dock. Subclasses may replace or rearrange every control. */
    protected void addTransport(UITabbedScreen screen,int centerX,int y) {
        int controlsWidth=Math.min(540,Math.max(176,screen.width-16));
        int left=centerX-controlsWidth/2;
        boolean narrow=controlsWidth<240;
        int progressWidth=Math.max(12,controlsWidth-126-(narrow?0:66));
        UISupplierButton previous=new UISupplierButton(left,y,26,20,()->Component.literal("|<"),()->UILocalization.frameworkText("music.control.previous","Previous track"),model::previous);
        previous.setTooltip(net.minecraft.client.gui.components.Tooltip.create(UILocalization.frameworkText("music.control.previous","Previous track")));screen.add(previous);
        UISupplierButton playPause=new UISupplierButton(left+30,y,30,20,()->Component.literal(model.playbackState().get()==UIPlaybackState.PLAYING?"||":">"),
                ()->model.playbackState().get()==UIPlaybackState.PLAYING?UILocalization.frameworkText("music.control.pause","Pause"):UILocalization.frameworkText("music.control.play","Play"),model::togglePlayback);
        playPause.setTooltip(net.minecraft.client.gui.components.Tooltip.create(UILocalization.frameworkText("music.control.play_pause","Play / pause")));screen.add(playPause);
        UISupplierButton next=new UISupplierButton(left+64,y,26,20,()->Component.literal(">|"),()->UILocalization.frameworkText("music.control.next","Next track"),model::next);
        next.setTooltip(net.minecraft.client.gui.components.Tooltip.create(UILocalization.frameworkText("music.control.next","Next track")));screen.add(next);
        UISupplierButton mode=new UISupplierButton(left+94,y,28,20,()->Component.literal(model.playbackMode().get().symbol()),
                ()->UILocalization.frameworkText("music.control.mode.current","Playback mode: %s",model.playbackMode().get().label()),this::cyclePlaybackMode);
        mode.setTooltip(net.minecraft.client.gui.components.Tooltip.create(UILocalization.frameworkText("music.control.mode.tooltip","Change playback mode")));screen.add(mode);
        screen.add(new UIRangeSlider(left+126,y+2,progressWidth,16,UILocalization.frameworkText("music.control.progress","Playback progress"),()->model.progress().get(),model::seek));
        if(!narrow){screen.add(new UILabelWidget(left+controlsWidth-62,y,10,20,()->Component.literal("V")));
            screen.add(new UIRangeSlider(left+controlsWidth-50,y+2,50,16,UILocalization.frameworkText("music.control.volume","Volume"),()->model.volume().get(),model::volume));}
    }

    protected final UIMusicPlayerModel model(){return model;}
    protected final UITheme playerTheme(){return theme;}

    protected void cyclePlaybackMode(){
        List<UIPlaybackMode> modes=model.playbackModes();if(modes.isEmpty())return;
        int index=modes.indexOf(model.playbackMode().get());model.playbackMode(modes.get((index+1+modes.size())%modes.size()));
    }

    protected void cycleSort(){
        List<UIMusicSortOption> options=model.sortOptions();if(options.isEmpty())return;
        int index=options.indexOf(model.sort().get());model.sort(options.get((index+1+options.size())%options.size()));
    }

    protected List<UIMusicTrack> sortedTracks(){
        List<UIMusicTrack> result=new ArrayList<>(model.tracks().get());result.sort(model.sort().get().comparator());return result;
    }

    private final class PlayerPage implements UIPage {
        private UIVirtualList<UIMusicTrack,String> tracks;
        private final List<UISubscription> subscriptions=new ArrayList<>();
        @Override public List<AbstractWidget> build(UIBuildContext ctx) {
            subscriptions.forEach(UISubscription::close);
            subscriptions.clear();
            List<AbstractWidget> widgets=new ArrayList<>();
            int left=ctx.viewportLeft(),top=38,width=ctx.availableWidth(),height=Math.max(80,ctx.screenHeight()-96);
            int gap=theme.spacing().small();
            boolean roomy=width>=700;
            boolean threeColumn=width>=480;
            int sidebar=roomy?140:threeColumn?96:width>=260?Math.min(88,Math.max(72,width/4)):Math.min(64,Math.max(0,width/4));
            int inspector=roomy?180:threeColumn?120:0;
            boolean compactNowPlaying=inspector==0;
            int listX=left+sidebar+(sidebar>0?gap:0);
            int listWidth=Math.max(120,width-sidebar-inspector-(sidebar>0?gap:0)-(inspector>0?gap:0));
            if(sidebar>0){
                UIMusicPlaylistController playlistController=model.playlistController();int managerHeight=playlistController==null?0:24;
                UIVirtualList<NavigationEntry,String> navigation=UIVirtualList.<NavigationEntry,String>builder(NavigationEntry::id)
                        .bounds(left,top,sidebar,height-managerHeight).items(this::navigationEntries).text(NavigationEntry::label).rowHeight(threeColumn?24:22)
                        .onSelect(entry->{if(entry.path()!=null)model.selectRegistrationPath(entry.path());else model.selectPlaylist(entry.playlist());})
                        .marqueeText(true).theme(theme).build();widgets.add(navigation);
                if(playlistController!=null)widgets.add(Button.builder(UILocalization.frameworkText("music.playlist.edit","Edit playlists"),ignored->Minecraft.getInstance().setScreenAndShow(
                        new UIUserPlaylistScreen(UIMusicPlayerScreen.this,UILocalization.frameworkText("music.playlist.screen.title","User playlists"),playlistController)))
                        .bounds(left,top+height-20,sidebar,20).build());
            }
            int searchY=top;
            int listY=top+24;
            if(compactNowPlaying){
                int artSize=28;
                UIImageWidget compactArt=UIImageWidget.builder(()->model.currentTrack().get()==null?null:model.currentTrack().get().artwork())
                        .bounds(listX,top,artSize,artSize).fit(ImageFit.COVER)
                        .background(theme.colors().surface()).border(theme.colors().border()).build();
                widgets.add(compactArt);
                widgets.add(new UIScrollingLabelWidget(listX+artSize+5,top,listWidth-artSize-5,14,
                        ()->model.currentTrack().get()==null?UILocalization.frameworkText("music.now_playing.empty","Nothing playing"):model.currentTrack().get().title(),
                        ()->theme.colors().text(),UILabelWidget.Align.LEFT));
                widgets.add(new UIScrollingLabelWidget(listX+artSize+5,top+14,listWidth-artSize-5,14,
                        ()->model.currentTrack().get()==null?Component.empty():Component.literal("").append(model.currentTrack().get().artist()).append(" · ").append(model.currentTrack().get().album()),
                        ()->theme.colors().textMuted(),UILabelWidget.Align.LEFT));
                searchY=top+30;
                listY=searchY+20;
            }
            int sortWidth=Math.min(70,Math.max(44,listWidth/4));
            EditBox search=new EditBox(Minecraft.getInstance().font,listX,searchY,Math.max(40,listWidth-sortWidth-4),compactNowPlaying?18:20,UILocalization.frameworkText("music.search.label","Search tracks"));
            search.setHint(UILocalization.frameworkText("music.search.hint","Search / filter"));widgets.add(search);
            UISupplierButton sortButton=new UISupplierButton(listX+listWidth-sortWidth,searchY,sortWidth,compactNowPlaying?18:20,()->model.sort().get().label(),UIMusicPlayerScreen.this::cycleSort);
            sortButton.setTooltip(net.minecraft.client.gui.components.Tooltip.create(UILocalization.frameworkText("music.sort.tooltip","Change track sorting")));widgets.add(sortButton);
            tracks=UIVirtualList.<UIMusicTrack,String>builder(UIMusicTrack::id).bounds(listX,listY,listWidth,Math.max(36,height-(listY-top)))
                    .items(UIMusicPlayerScreen.this::sortedTracks).rowHeight(compactNowPlaying?22:24)
                    .text(track->Component.literal((Objects.equals(model.currentTrack().get(),track)?"▶ ":"")).append(track.artist()).append(" - ").append(track.title()))
                    .filter((track,q)->(track.title().getString()+" "+track.artist().getString()+" "+track.album().getString()+" "+track.source().getString()).toLowerCase().contains(q.toLowerCase()))
                    .playing(track->Objects.equals(model.currentTrack().get(),track)).marqueeText(true).onSelect(model::select).onActivate(model::play)
                    .rowAction(24,(track,button)->model.play(track)).theme(theme).build();
            search.setResponder(tracks::query);widgets.add(tracks);
            subscriptions.add(model.selectedTrack().subscribeNow(track->{if(track!=null&&tracks!=null)tracks.selectKey(track.id());}));
            if(inspector>0){
                int x=listX+listWidth+gap,artSize=Math.min(inspector-20,Math.max(64,height/2));
                UIImageWidget art=UIImageWidget.builder(()->model.currentTrack().get()==null?null:model.currentTrack().get().artwork())
                        .bounds(x+(inspector-artSize)/2,top+12,artSize,artSize).fit(ImageFit.COVER)
                        .background(theme.colors().surface()).border(theme.colors().border()).shadow(theme.colors().shadow()).build();widgets.add(art);
                widgets.add(new UIScrollingLabelWidget(x,top+artSize+22,inspector,18,()->model.currentTrack().get()==null?UILocalization.frameworkText("music.now_playing.empty","Nothing playing"):model.currentTrack().get().title(),()->theme.colors().text(),UILabelWidget.Align.CENTER));
                widgets.add(new UIScrollingLabelWidget(x,top+artSize+42,inspector,16,()->model.currentTrack().get()==null?Component.empty():model.currentTrack().get().artist(),()->theme.colors().textMuted(),UILabelWidget.Align.CENTER));
                widgets.add(new UIScrollingLabelWidget(x,top+artSize+58,inspector,16,()->model.currentTrack().get()==null?Component.empty():model.currentTrack().get().album(),()->theme.colors().textMuted(),UILabelWidget.Align.CENTER));
                widgets.add(new UIScrollingLabelWidget(x,top+artSize+74,inspector,16,()->model.currentTrack().get()==null?Component.empty():model.currentTrack().get().source(),()->theme.colors().textMuted(),UILabelWidget.Align.CENTER));
            }
            return widgets;
        }
        @Override public void onClose(){subscriptions.forEach(UISubscription::close);subscriptions.clear();}

        private List<NavigationEntry> navigationEntries(){
            List<NavigationEntry> entries=new ArrayList<>();List<UIMusicRegistrationPath> paths=model.registrationPaths().get();
            for(UIMusicRegistrationPath path:paths)entries.add(new NavigationEntry("path:"+path.id(),Component.literal("  ".repeat(pathDepth(path,paths))).append(path.name()),path,null));
            for(UIMusicPlaylist playlist:model.playlists().get())entries.add(new NavigationEntry("playlist:"+playlist.id(),
                    Component.literal(playlist.userCreated()?"+ ":"* ").append(playlist.name()),null,playlist));
            return entries;
        }
        private int pathDepth(UIMusicRegistrationPath path,List<UIMusicRegistrationPath> paths){int depth=0;String parent=path.parentId();
            while(parent!=null&&depth<8){String current=parent;UIMusicRegistrationPath found=paths.stream().filter(value->value.id().equals(current)).findFirst().orElse(null);if(found==null)break;depth++;parent=found.parentId();}return depth;}
    }

    private record NavigationEntry(String id,Component label,UIMusicRegistrationPath path,UIMusicPlaylist playlist){}
}
