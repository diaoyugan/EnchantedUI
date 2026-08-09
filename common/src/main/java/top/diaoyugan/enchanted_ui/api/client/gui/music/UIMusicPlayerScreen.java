package top.diaoyugan.enchanted_ui.api.client.gui.music;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import top.diaoyugan.enchanted_ui.api.client.gui.UIBottomBar;
import top.diaoyugan.enchanted_ui.api.client.gui.UILocalization;
import top.diaoyugan.enchanted_ui.api.client.gui.UITabbedScreen;
import top.diaoyugan.enchanted_ui.api.client.gui.layout.UIBounds;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UITheme;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UIThemes;

import java.util.Objects;

/** Thin preset that assembles a reusable player page and reusable transport controls. */
public class UIMusicPlayerScreen extends UITabbedScreen {
    private final UIMusicPlayerPage playerPage;
    @Nullable private final UIMusicTransportControls transportControls;

    public UIMusicPlayerScreen(@Nullable Screen parent,Component title,UIMusicPlayerModel model){this(parent,title,model,UIThemes.current());}

    public UIMusicPlayerScreen(@Nullable Screen parent,Component title,UIMusicPlayerModel model,UITheme theme){
        super(parent,title);Objects.requireNonNull(model);Objects.requireNonNull(theme);
        playerPage=UIMusicPlayerPage.builder(model).theme(theme).playlistEditorOpener(controller->Minecraft.getInstance().setScreenAndShow(
                new UIUserPlaylistScreen(this,UILocalization.frameworkText("music.playlist.screen.title","User playlists"),controller))).build();
        transportControls=UIMusicTransportControls.builder(model).theme(theme).build();configure(title);
    }

    /** Builds a customized preset from independently configured page and transport objects. */
    public UIMusicPlayerScreen(@Nullable Screen parent,Component title,UIMusicPlayerPage playerPage,@Nullable UIMusicTransportControls transportControls){
        super(parent,title);this.playerPage=Objects.requireNonNull(playerPage);this.transportControls=transportControls;configure(title);
    }

    private void configure(Component title){
        headerTitle(title);tabsVisible(false);contentViewport(8,34,8,transportControls==null?16:54);tab(0,0,20,Component.empty(),playerPage);
        bottomBar(transportControls==null?UIBottomBar.none():(UIBottomBar)(screen,centerX,y)->addTransport(screen,centerX,y));
    }

    /** Registers default transport widgets. Override to add, remove, or rearrange them. */
    protected void addTransport(UITabbedScreen screen,int centerX,int y){
        if(transportControls==null)return;UIBounds region=new UIBounds(8,y,Math.max(0,screen.width-16),20);
        for(AbstractWidget widget:transportControls.build(region))screen.add(widget);
    }

    public final UIMusicPlayerPage playerPage(){return playerPage;}
    @Nullable public final UIMusicTransportControls transportControls(){return transportControls;}
    protected final UIMusicPlayerModel model(){return playerPage.model();}
    protected final UITheme playerTheme(){return playerPage.theme();}
}
