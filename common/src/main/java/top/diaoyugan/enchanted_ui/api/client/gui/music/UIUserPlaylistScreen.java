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

/** Persistence-neutral editor for user-created playlists. */
public final class UIUserPlaylistScreen extends UITabbedScreen {
    private final UIMusicPlaylistController controller;
    private final UITheme theme;
    public UIUserPlaylistScreen(@Nullable Screen parent,Component title,UIMusicPlaylistController controller){
        super(parent,title);this.controller=controller;this.theme=UIThemes.current();
        headerTitle(title);tabsVisible(false);contentViewport(8,34,8,38);
        tab(0,0,20,Component.empty(),new EditorPage());bottomBar(UIBottomBar.closeOnly(UILocalization.frameworkText("music.playlist.done","Done")));
    }

    private final class EditorPage implements UIPage {
        private final List<UISubscription> subscriptions=new ArrayList<>();
        @Override public List<AbstractWidget> build(UIBuildContext ctx){
            subscriptions.forEach(UISubscription::close);subscriptions.clear();
            List<AbstractWidget> widgets=new ArrayList<>();
            int left=ctx.viewportLeft(),top=38,width=ctx.availableWidth(),height=Math.max(80,ctx.screenHeight()-82),gap=theme.spacing().small();
            int sidebar=Math.min(130,Math.max(76,width/3));int rightX=left+sidebar+gap;int rightWidth=Math.max(100,width-sidebar-gap);
            UIVirtualList<UIMusicPlaylist,String> playlists=UIVirtualList.<UIMusicPlaylist,String>builder(UIMusicPlaylist::id)
                    .bounds(left,top,sidebar,Math.max(36,height-24)).items(()->controller.playlists().get()).text(UIMusicPlaylist::name)
                    .selected(()->controller.selectedPlaylist().get()).onSelect(controller::select).marqueeText(true).theme(theme).build();
            widgets.add(playlists);
            widgets.add(Button.builder(UILocalization.frameworkText("music.playlist.new","New"),ignored->controller.create(
                    UILocalization.frameworkText("music.playlist.default_name","New playlist").getString())).bounds(left,top+height-20,sidebar,20).build());
            EditBox name=new EditBox(Minecraft.getInstance().font,rightX,top,Math.max(40,rightWidth-88),20,
                    UILocalization.frameworkText("music.playlist.name_hint","Playlist name"));widgets.add(name);
            widgets.add(Button.builder(UILocalization.frameworkText("music.playlist.save","Save"),ignored->{UIMusicPlaylist selected=controller.selectedPlaylist().get();if(selected!=null&&!name.getValue().isBlank())controller.rename(selected,name.getValue());})
                    .bounds(rightX+rightWidth-84,top,40,20).build());
            widgets.add(Button.builder(UILocalization.frameworkText("music.playlist.delete","Delete"),ignored->{UIMusicPlaylist selected=controller.selectedPlaylist().get();if(selected!=null)controller.delete(selected);})
                    .bounds(rightX+rightWidth-40,top,40,20).build());
            UIVirtualList<UIMusicTrack,String> members=UIVirtualList.<UIMusicTrack,String>builder(UIMusicTrack::id)
                    .bounds(rightX,top+24,rightWidth,Math.max(36,height-24)).items(()->controller.availableTracks().get()).rowHeight(22).multiSelect(true)
                    .selectedItems(()->controller.tracksInSelectedPlaylist().get()).text(track->Component.literal("").append(track.artist()).append(" - ").append(track.title()))
                    .marqueeText(true).onMultiSelect(tracks->{UIMusicPlaylist selected=controller.selectedPlaylist().get();if(selected!=null)controller.setTracks(selected,tracks);}).theme(theme).build();
            widgets.add(members);
            subscriptions.add(controller.selectedPlaylist().subscribeNow(selected->name.setValue(selected==null?"":selected.name().getString())));
            return widgets;
        }
        @Override public void onClose(){subscriptions.forEach(UISubscription::close);subscriptions.clear();}
    }
}
