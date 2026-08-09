package top.diaoyugan.enchanted_ui.api.client.gui.music;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.gui.*;
import top.diaoyugan.enchanted_ui.api.client.gui.layout.UIBounds;
import top.diaoyugan.enchanted_ui.api.client.gui.state.UISubscription;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UITheme;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UIThemes;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;

/** Reusable, configurable music-library page that can be embedded in any EUI screen. */
public final class UIMusicPlayerPage implements UIPage {
    public enum Region { NAVIGATION, CONTENT, INSPECTOR, OVERLAY }
    public record RegionContext(UIBounds bounds,UIMusicPlayerModel model,UITheme theme){}
    @FunctionalInterface public interface RegionExtension { List<AbstractWidget> build(RegionContext context); }

    private final UIMusicPlayerModel model;
    private final UITheme theme;
    private final Function<UIBuildContext,UIBounds> boundsFactory;
    private final boolean showNavigation,showSearch,showSort,showNowPlaying,showInspector,showPlaylistEditor;
    private final int threeColumnAt,roomyAt;
    private final int compactRowHeight,regularRowHeight,compactArtworkSize;
    private final IntUnaryOperator navigationWidth,inspectorWidth;
    private final Consumer<UIMusicPlaylistController> playlistEditorOpener;
    private final EnumMap<Region,List<RegionExtension>> extensions;
    private final List<UISubscription> subscriptions=new ArrayList<>();
    private UIVirtualList<UIMusicTrack,String> tracks;

    private UIMusicPlayerPage(Builder b){
        model=b.model;theme=b.theme;boundsFactory=b.boundsFactory;showNavigation=b.showNavigation;showSearch=b.showSearch;
        showSort=b.showSort;showNowPlaying=b.showNowPlaying;showInspector=b.showInspector;showPlaylistEditor=b.showPlaylistEditor;
        threeColumnAt=b.threeColumnAt;roomyAt=b.roomyAt;compactRowHeight=b.compactRowHeight;regularRowHeight=b.regularRowHeight;
        compactArtworkSize=b.compactArtworkSize;navigationWidth=b.navigationWidth;inspectorWidth=b.inspectorWidth;
        playlistEditorOpener=b.playlistEditorOpener;extensions=b.copyExtensions();
    }
    public static Builder builder(UIMusicPlayerModel model){return new Builder(model);}
    public UIMusicPlayerModel model(){return model;}
    public UITheme theme(){return theme;}

    @Override public List<AbstractWidget> build(UIBuildContext ctx){
        subscriptions.forEach(UISubscription::close);subscriptions.clear();List<AbstractWidget> widgets=new ArrayList<>();
        UIBounds page=boundsFactory.apply(ctx);int left=page.x(),top=page.y(),width=page.width(),height=page.height();int gap=theme.spacing().small();
        boolean roomy=width>=roomyAt,threeColumn=showInspector&&width>=threeColumnAt;
        int sidebar=showNavigation?Math.max(0,navigationWidth==null?(roomy?140:threeColumn?96:width>=260?Math.min(88,Math.max(72,width/4)):Math.min(64,Math.max(0,width/4))):navigationWidth.applyAsInt(width)):0;
        int inspector=threeColumn?Math.max(0,inspectorWidth==null?(roomy?180:120):inspectorWidth.applyAsInt(width)):0;
        sidebar=Math.min(sidebar,Math.max(0,width-120-(sidebar>0?gap:0)));
        inspector=Math.min(inspector,Math.max(0,width-sidebar-120-(sidebar>0?gap:0)-(inspector>0?gap:0)));
        boolean compactNowPlaying=showNowPlaying&&inspector==0;
        int listX=left+sidebar+(sidebar>0?gap:0);int listWidth=Math.max(120,width-sidebar-inspector-(sidebar>0?gap:0)-(inspector>0?gap:0));
        UIBounds navigationBounds=new UIBounds(left,top,sidebar,height);
        UIBounds contentBounds=new UIBounds(listX,top,listWidth,height);
        UIBounds inspectorBounds=new UIBounds(listX+listWidth+(inspector>0?gap:0),top,inspector,height);

        if(sidebar>0)buildNavigation(widgets,navigationBounds,threeColumn);
        int searchY=top,listY=top;
        if(compactNowPlaying){buildCompactNowPlaying(widgets,contentBounds);searchY=top+30;listY=searchY;}
        if(showSearch||showSort){buildSearchAndSort(widgets,listX,searchY,listWidth,compactNowPlaying);listY=searchY+(compactNowPlaying?20:24);}
        buildTracks(widgets,listX,listY,listWidth,Math.max(36,height-(listY-top)),compactNowPlaying);
        if(inspector>0)buildInspector(widgets,inspectorBounds);
        addExtensions(widgets,Region.NAVIGATION,navigationBounds);addExtensions(widgets,Region.CONTENT,contentBounds);
        addExtensions(widgets,Region.INSPECTOR,inspectorBounds);addExtensions(widgets,Region.OVERLAY,page);
        return widgets;
    }

    private void buildNavigation(List<AbstractWidget> widgets,UIBounds b,boolean threeColumn){
        UIMusicPlaylistController controller=model.playlistController();int managerHeight=showPlaylistEditor&&controller!=null&&playlistEditorOpener!=null?24:0;
        UIVirtualList<NavigationEntry,String> navigation=UIVirtualList.<NavigationEntry,String>builder(NavigationEntry::id)
                .bounds(b.x(),b.y(),b.width(),b.height()-managerHeight).items(this::navigationEntries).text(NavigationEntry::label).rowHeight(threeColumn?regularRowHeight:compactRowHeight)
                .onSelect(entry->{if(entry.path()!=null)model.selectRegistrationPath(entry.path());else model.selectPlaylist(entry.playlist());})
                .marqueeText(true).theme(theme).build();widgets.add(navigation);
        if(managerHeight>0)widgets.add(Button.builder(UILocalization.frameworkText("music.playlist.edit","Edit playlists"),ignored->playlistEditorOpener.accept(controller))
                .bounds(b.x(),b.y()+b.height()-20,b.width(),20).build());
    }

    private void buildCompactNowPlaying(List<AbstractWidget> widgets,UIBounds b){
        int artSize=compactArtworkSize;widgets.add(UIImageWidget.builder(()->current()==null?null:current().artwork()).bounds(b.x(),b.y(),artSize,artSize)
                .fit(ImageFit.COVER).background(theme.colors().surface()).border(theme.colors().border()).build());
        widgets.add(new UIMarqueeLabel(b.x()+artSize+5,b.y(),b.width()-artSize-5,14,
                ()->current()==null?UILocalization.frameworkText("music.now_playing.empty","Nothing playing"):current().title(),()->theme.colors().text(),UILabelWidget.Align.LEFT));
        widgets.add(new UIMarqueeLabel(b.x()+artSize+5,b.y()+14,b.width()-artSize-5,14,
                ()->current()==null?Component.empty():Component.literal("").append(current().artist()).append(" · ").append(current().album()),
                ()->theme.colors().textMuted(),UILabelWidget.Align.LEFT));
    }

    private void buildSearchAndSort(List<AbstractWidget> widgets,int x,int y,int width,boolean compact){
        int height=compact?18:20;int sortWidth=showSort?Math.min(70,Math.max(44,width/4)):0;
        if(showSearch){EditBox search=new EditBox(Minecraft.getInstance().font,x,y,Math.max(40,width-sortWidth-(sortWidth>0?4:0)),height,
                UILocalization.frameworkText("music.search.label","Search tracks"));search.setHint(UILocalization.frameworkText("music.search.hint","Search / filter"));
            search.setResponder(value->{if(tracks!=null)tracks.query(value);});widgets.add(search);}
        if(showSort){UIDynamicButton sort=new UIDynamicButton(x+width-sortWidth,y,sortWidth,height,()->model.sort().get().label(),this::cycleSort);
            sort.setTooltip(net.minecraft.client.gui.components.Tooltip.create(UILocalization.frameworkText("music.sort.tooltip","Change track sorting")));widgets.add(sort);}
    }

    private void buildTracks(List<AbstractWidget> widgets,int x,int y,int width,int height,boolean compact){
        tracks=UIVirtualList.<UIMusicTrack,String>builder(UIMusicTrack::id).bounds(x,y,width,height).items(this::sortedTracks).rowHeight(compact?compactRowHeight:regularRowHeight)
                .text(track->Component.literal((Objects.equals(current(),track)?"▶ ":"")).append(track.artist()).append(" - ").append(track.title()))
                .filter((track,q)->(track.title().getString()+" "+track.artist().getString()+" "+track.album().getString()+" "+track.source().getString()).toLowerCase().contains(q.toLowerCase()))
                .playing(track->Objects.equals(current(),track)).marqueeText(true).onSelect(model::select).onActivate(model::play)
                .rowAction(24,(track,button)->model.play(track)).theme(theme).build();widgets.add(tracks);
        subscriptions.add(model.selectedTrack().subscribeNow(track->{if(track!=null&&tracks!=null)tracks.selectKey(track.id());}));
    }

    private void buildInspector(List<AbstractWidget> widgets,UIBounds b){
        if(b.width()<24||b.height()<24)return;
        int artSize=Math.min(b.width()-20,Math.max(64,b.height()/2));int artX=b.x()+(b.width()-artSize)/2;
        widgets.add(UIImageWidget.builder(()->current()==null?null:current().artwork()).bounds(artX,b.y()+12,artSize,artSize).fit(ImageFit.COVER)
                .background(theme.colors().surface()).border(theme.colors().border()).shadow(theme.colors().shadow()).build());
        widgets.add(marquee(b,b.y()+artSize+22,18,()->current()==null?UILocalization.frameworkText("music.now_playing.empty","Nothing playing"):current().title(),theme.colors().text()));
        widgets.add(marquee(b,b.y()+artSize+42,16,()->current()==null?Component.empty():current().artist(),theme.colors().textMuted()));
        widgets.add(marquee(b,b.y()+artSize+58,16,()->current()==null?Component.empty():current().album(),theme.colors().textMuted()));
        widgets.add(marquee(b,b.y()+artSize+74,16,()->current()==null?Component.empty():current().source(),theme.colors().textMuted()));
    }

    private UIMarqueeLabel marquee(UIBounds b,int y,int height,java.util.function.Supplier<Component> text,int color){
        return new UIMarqueeLabel(b.x(),y,b.width(),height,text,()->color,UILabelWidget.Align.CENTER);
    }
    private UIMusicTrack current(){return model.currentTrack().get();}
    private void cycleSort(){List<UIMusicSortOption> options=model.sortOptions();if(options.isEmpty())return;int i=options.indexOf(model.sort().get());model.sort(options.get((i+1+options.size())%options.size()));}
    private List<UIMusicTrack> sortedTracks(){List<UIMusicTrack> values=new ArrayList<>(model.tracks().get());values.sort(model.sort().get().comparator());return values;}
    private void addExtensions(List<AbstractWidget> widgets,Region region,UIBounds bounds){if(bounds.width()<=0||bounds.height()<=0)return;
        for(RegionExtension extension:extensions.get(region)){List<AbstractWidget> values=extension.build(new RegionContext(bounds,model,theme));if(values!=null)widgets.addAll(values);}}
    private List<NavigationEntry> navigationEntries(){List<NavigationEntry> entries=new ArrayList<>();List<UIMusicRegistrationPath> paths=model.registrationPaths().get();
        for(UIMusicRegistrationPath path:paths)entries.add(new NavigationEntry("path:"+path.id(),Component.literal("  ".repeat(pathDepth(path,paths))).append(path.name()),path,null));
        for(UIMusicPlaylist playlist:model.playlists().get())entries.add(new NavigationEntry("playlist:"+playlist.id(),Component.literal(playlist.userCreated()?"+ ":"* ").append(playlist.name()),null,playlist));return entries;}
    private int pathDepth(UIMusicRegistrationPath path,List<UIMusicRegistrationPath> paths){int depth=0;String parent=path.parentId();while(parent!=null&&depth<8){String id=parent;
        UIMusicRegistrationPath found=paths.stream().filter(value->value.id().equals(id)).findFirst().orElse(null);if(found==null)break;depth++;parent=found.parentId();}return depth;}
    @Override public void onClose(){subscriptions.forEach(UISubscription::close);subscriptions.clear();}
    private record NavigationEntry(String id,Component label,UIMusicRegistrationPath path,UIMusicPlaylist playlist){}

    public static final class Builder {
        private final UIMusicPlayerModel model;private UITheme theme=UIThemes.current();
        private Function<UIBuildContext,UIBounds> boundsFactory=ctx->new UIBounds(ctx.viewportLeft(),38,ctx.availableWidth(),Math.max(80,ctx.screenHeight()-96));
        private boolean showNavigation=true,showSearch=true,showSort=true,showNowPlaying=true,showInspector=true,showPlaylistEditor=true;
        private int threeColumnAt=480,roomyAt=700,compactRowHeight=22,regularRowHeight=24,compactArtworkSize=28;
        private IntUnaryOperator navigationWidth,inspectorWidth;private Consumer<UIMusicPlaylistController> playlistEditorOpener;
        private final EnumMap<Region,List<RegionExtension>> extensions=new EnumMap<>(Region.class);
        private Builder(UIMusicPlayerModel model){this.model=Objects.requireNonNull(model);for(Region region:Region.values())extensions.put(region,new ArrayList<>());}
        public Builder theme(UITheme value){theme=Objects.requireNonNull(value);return this;}
        public Builder bounds(Function<UIBuildContext,UIBounds> value){boundsFactory=Objects.requireNonNull(value);return this;}
        public Builder showNavigation(boolean value){showNavigation=value;return this;}
        public Builder showSearch(boolean value){showSearch=value;return this;}
        public Builder showSort(boolean value){showSort=value;return this;}
        public Builder showNowPlaying(boolean value){showNowPlaying=value;return this;}
        public Builder showInspector(boolean value){showInspector=value;return this;}
        public Builder showPlaylistEditor(boolean value){showPlaylistEditor=value;return this;}
        public Builder breakpoints(int threeColumnAt,int roomyAt){this.threeColumnAt=Math.max(240,threeColumnAt);this.roomyAt=Math.max(this.threeColumnAt,roomyAt);return this;}
        public Builder navigationWidth(IntUnaryOperator value){navigationWidth=Objects.requireNonNull(value);return this;}
        public Builder inspectorWidth(IntUnaryOperator value){inspectorWidth=Objects.requireNonNull(value);return this;}
        public Builder rowHeights(int compact,int regular){compactRowHeight=Math.max(12,compact);regularRowHeight=Math.max(12,regular);return this;}
        public Builder compactArtworkSize(int value){compactArtworkSize=Math.max(12,value);return this;}
        public Builder playlistEditorOpener(Consumer<UIMusicPlaylistController> value){playlistEditorOpener=value;return this;}
        public Builder extend(Region region,RegionExtension extension){extensions.get(region).add(Objects.requireNonNull(extension));return this;}
        private EnumMap<Region,List<RegionExtension>> copyExtensions(){EnumMap<Region,List<RegionExtension>> copy=new EnumMap<>(Region.class);
            extensions.forEach((key,value)->copy.put(key,List.copyOf(value)));return copy;}
        public UIMusicPlayerPage build(){return new UIMusicPlayerPage(this);}
    }
}
