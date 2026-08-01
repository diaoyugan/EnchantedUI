package top.diaoyugan.enchanted_ui.standalone.gui.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import top.diaoyugan.enchanted_ui.api.client.gui.music.UIPlaybackState;
import top.diaoyugan.enchanted_ui.api.client.gui.music.UIMusicPlayerModel;
import top.diaoyugan.enchanted_ui.api.client.gui.music.UIMusicPlayerScreen;
import top.diaoyugan.enchanted_ui.api.client.gui.music.UIMusicPlaylist;
import top.diaoyugan.enchanted_ui.api.client.gui.music.UIMusicTrack;
import top.diaoyugan.enchanted_ui.api.client.gui.music.UIPlaybackMode;
import top.diaoyugan.enchanted_ui.api.client.gui.music.UIMusicSortOption;
import top.diaoyugan.enchanted_ui.api.client.gui.music.UIMusicRegistrationPath;
import top.diaoyugan.enchanted_ui.api.client.gui.music.UIMusicPlaylistController;
import top.diaoyugan.enchanted_ui.api.client.gui.state.UIBinding;
import top.diaoyugan.enchanted_ui.api.client.gui.state.UIState;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/** Interactive integration demo for the responsive music-player preset. */
public final class MusicPlayerDemoScreen extends UIMusicPlayerScreen {
    private final DemoPlayerModel demo;

    public MusicPlayerDemoScreen(Screen parent) {
        this(parent, new DemoPlayerModel());
    }

    private MusicPlayerDemoScreen(Screen parent, DemoPlayerModel demo) {
        super(parent, Component.literal("EnchantedUI Music Library"), demo);
        this.demo = demo;
    }

    @Override
    public void tick() {
        demo.tick();
        super.tick();
    }

    private static final class DemoPlayerModel implements UIMusicPlayerModel {
        private static final double DEMO_TRACK_SECONDS = 18.0D;
        private static final Identifier DISC_13 = texture("minecraft:textures/item/music_disc_13.png");
        private static final Identifier DISC_CAT = texture("minecraft:textures/item/music_disc_cat.png");
        private static final Identifier DISC_PIGSTEP = texture("minecraft:textures/item/music_disc_pigstep.png");
        private static final Identifier DISC_OTHERSIDE = texture("minecraft:textures/item/music_disc_otherside.png");

        private final List<UIMusicTrack> allTracks = List.of(
                track("bg_sweden", "Sweden", "C418", "Minecraft - Volume Alpha", "Vanilla file scan", DISC_13, "vanilla_alpha"),
                track("bg_clark", "Clark", "C418", "Minecraft - Volume Alpha", "Vanilla file scan", DISC_CAT, "vanilla_alpha"),
                track("bg_mice", "Living Mice", "C418", "Minecraft - Volume Alpha", "Vanilla file scan", DISC_13, "vanilla_alpha"),
                track("bg_haggstrom", "Haggstrom", "C418", "Minecraft - Volume Alpha", "Vanilla file scan", DISC_CAT, "vanilla_alpha"),
                track("disc_pigstep", "Pigstep", "Lena Raine", "Nether Update", "Vanilla file scan", DISC_PIGSTEP, "vanilla_discs"),
                track("fav_otherside", "otherside", "Lena Raine", "Caves & Cliffs", "Vanilla file scan", DISC_OTHERSIDE, "vanilla_discs"),
                track("disc_relic", "Relic", "Aaron Cherof", "Trails & Tales", "External file registration", DISC_13, "external_trails"),
                track("fav_creator", "Creator - Music Box Version with an Intentionally Long Demo Name", "Lena Raine", "Tricky Trials Extended Soundtrack Collection", "External file registration", DISC_OTHERSIDE, "external_trials"),
                track("disc_precipice", "Precipice", "Aaron Cherof", "Tricky Trials Extended Soundtrack Collection", "External file registration", DISC_PIGSTEP, "external_trials"),
                track("bg_infinite", "Infinite Amethyst", "Lena Raine", "Caves & Cliffs", "External file registration", DISC_CAT, "external_caves")
        );
        private final UIState<List<UIMusicTrack>> visibleTracks = UIState.of(allTracks);
        private final List<UIMusicPlaylist> builtInPlaylists = List.of(
                new UIMusicPlaylist("all", Component.literal("All tracks"), track -> true),
                new UIMusicPlaylist("background", Component.literal("Background music"), track -> track.id().startsWith("bg_")),
                new UIMusicPlaylist("discs", Component.literal("Music discs"), track -> track.id().startsWith("disc_")),
                new UIMusicPlaylist("favorites", Component.literal("Favorites"), track -> track.id().startsWith("fav_"))
        );
        private final UIState<List<UIMusicPlaylist>> userPlaylists = UIState.of(List.of());
        private final UIState<List<UIMusicPlaylist>> playlists = UIState.of(builtInPlaylists);
        private final UIState<List<UIMusicRegistrationPath>> registrationPaths = UIState.of(List.of(
                path("vanilla",null,"Vanilla file scan",UIMusicRegistrationPath.Kind.PROVIDER,track->track.source().getString().equals("Vanilla file scan")),
                path("vanilla_alpha","vanilla","Volume Alpha",UIMusicRegistrationPath.Kind.ALBUM,track->track.registrationPathId().equals("vanilla_alpha")),
                path("vanilla_discs","vanilla","Music discs",UIMusicRegistrationPath.Kind.ALBUM,track->track.registrationPathId().equals("vanilla_discs")),
                path("external",null,"External file registration",UIMusicRegistrationPath.Kind.PROVIDER,track->track.source().getString().equals("External file registration")),
                path("external_trails","external","Trails & Tales",UIMusicRegistrationPath.Kind.ALBUM,track->track.registrationPathId().equals("external_trails")),
                path("external_trials","external","Tricky Trials Extended Soundtrack Collection",UIMusicRegistrationPath.Kind.ALBUM,track->track.registrationPathId().equals("external_trials")),
                path("external_caves","external","Caves & Cliffs",UIMusicRegistrationPath.Kind.ALBUM,track->track.registrationPathId().equals("external_caves"))
        ));
        private final UIState<UIMusicTrack> selected = UIState.of(allTracks.getFirst());
        private final UIState<UIMusicTrack> current = UIState.of(allTracks.getFirst());
        private final UIState<UIPlaybackState> playback = UIState.of(UIPlaybackState.PAUSED);
        private final UIState<Double> progress = UIState.of(0.0D);
        private final UIState<Double> volume = UIState.of(0.72D);
        private final UIState<UIPlaybackMode> mode = UIState.of(UIPlaybackMode.ALL_LOOP);
        private final UIState<UIMusicSortOption> sort = UIState.of(UIMusicSortOption.DEFAULT);
        private final DemoPlaylistController playlistEditor = new DemoPlaylistController();
        private final Random random = new Random();
        private long lastUpdateNanos = System.nanoTime();

        @Override public UIBinding<List<UIMusicTrack>> tracks() { return visibleTracks; }
        @Override public UIBinding<List<UIMusicPlaylist>> playlists() { return playlists; }
        @Override public UIBinding<UIMusicTrack> selectedTrack() { return selected; }
        @Override public UIBinding<UIMusicTrack> currentTrack() { return current; }
        @Override public UIBinding<UIPlaybackState> playbackState() { return playback; }
        @Override public UIBinding<Double> progress() { return progress; }
        @Override public UIBinding<Double> volume() { return volume; }
        @Override public UIBinding<UIPlaybackMode> playbackMode(){return mode;}
        @Override public void playbackMode(UIPlaybackMode value){mode.set(value);}
        @Override public UIBinding<UIMusicSortOption> sort(){return sort;}
        @Override public void sort(UIMusicSortOption value){sort.set(value);}
        @Override public UIBinding<List<UIMusicRegistrationPath>> registrationPaths(){return registrationPaths;}
        @Override public UIMusicPlaylistController playlistController(){return playlistEditor;}

        @Override public void select(UIMusicTrack track) { selected.set(track); }

        @Override public void play(UIMusicTrack track) {
            if (track == null) return;
            selected.set(track);
            current.set(track);
            progress.set(0.0D);
            playback.set(UIPlaybackState.PLAYING);
            lastUpdateNanos = System.nanoTime();
        }

        @Override public void previous() { move(-1); }
        @Override public void next() { move(1); }

        @Override public void togglePlayback() {
            playback.set(playback.get() == UIPlaybackState.PLAYING ? UIPlaybackState.PAUSED : UIPlaybackState.PLAYING);
            lastUpdateNanos = System.nanoTime();
        }

        @Override public void seek(double value) {
            progress.set(clamp(value));
            lastUpdateNanos = System.nanoTime();
        }

        @Override public void volume(double value) { volume.set(clamp(value)); }

        @Override public void selectPlaylist(UIMusicPlaylist playlist) {
            List<UIMusicTrack> filtered = allTracks.stream().filter(playlist.includes()).toList();
            visibleTracks.set(filtered);
            if (!filtered.isEmpty() && !filtered.contains(selected.get())) selected.set(filtered.getFirst());
        }

        @Override public void selectRegistrationPath(UIMusicRegistrationPath path){
            List<UIMusicTrack> filtered=allTracks.stream().filter(path.includes()).toList();visibleTracks.set(filtered);
            if(!filtered.isEmpty()&&!filtered.contains(selected.get()))selected.set(filtered.getFirst());
        }

        private void tick() {
            long now = System.nanoTime();
            if (playback.get() == UIPlaybackState.PLAYING) {
                double elapsed = (now - lastUpdateNanos) / 1_000_000_000.0D;
                double nextProgress = progress.get() + elapsed / DEMO_TRACK_SECONDS;
                if (nextProgress >= 1.0D) onTrackEnded();
                else progress.set(nextProgress);
            }
            lastUpdateNanos = now;
        }

        private void move(int amount) {
            int index = allTracks.indexOf(current.get());
            int target = Math.floorMod((index < 0 ? 0 : index) + amount, allTracks.size());
            play(allTracks.get(target));
        }

        private void onTrackEnded(){
            switch(mode.get()){
                case SINGLE_LOOP->play(current.get());
                case SHUFFLE->play(allTracks.get(random.nextInt(allTracks.size())));
                case ALL_LOOP->move(1);
                case SEQUENTIAL->{int index=allTracks.indexOf(current.get());if(index>=0&&index+1<allTracks.size())play(allTracks.get(index+1));else{progress.set(1.0D);playback.set(UIPlaybackState.STOPPED);}}
            }
        }

        private static UIMusicTrack track(String id,String title,String artist,String album,String source,Identifier artwork,String path) {
            return new UIMusicTrack(id,Component.literal(title),Component.literal(artist),Component.literal(album),Component.literal(source),artwork,path);
        }

        private static UIMusicRegistrationPath path(String id,String parent,String name,UIMusicRegistrationPath.Kind kind,java.util.function.Predicate<UIMusicTrack> includes){
            return new UIMusicRegistrationPath(id,parent,Component.literal(name),kind,includes);
        }

        private static Identifier texture(String value) {
            return Objects.requireNonNull(Identifier.tryParse(value));
        }

        private static double clamp(double value) { return Math.max(0.0D, Math.min(1.0D, value)); }

        private final class DemoPlaylistController implements UIMusicPlaylistController {
            private final UIState<UIMusicPlaylist> selectedPlaylist=UIState.of(null);
            private final UIState<List<UIMusicTrack>> selectedTracks=UIState.of(List.of());
            private final Map<String,Set<String>> memberships=new HashMap<>();
            private int nextId=1;
            @Override public UIBinding<List<UIMusicPlaylist>> playlists(){return userPlaylists;}
            @Override public UIBinding<UIMusicPlaylist> selectedPlaylist(){return selectedPlaylist;}
            @Override public UIBinding<List<UIMusicTrack>> availableTracks(){return UIBinding.constant(allTracks);}
            @Override public UIBinding<List<UIMusicTrack>> tracksInSelectedPlaylist(){return selectedTracks;}
            @Override public void select(UIMusicPlaylist playlist){selectedPlaylist.set(playlist);refreshSelectedTracks();}
            @Override public void create(String name){String id="user_"+nextId++;memberships.put(id,new HashSet<>());
                UIMusicPlaylist playlist=userPlaylist(id,name);List<UIMusicPlaylist> values=new ArrayList<>(userPlaylists.get());values.add(playlist);userPlaylists.set(List.copyOf(values));refreshPlayerPlaylists();select(playlist);}
            @Override public void rename(UIMusicPlaylist playlist,String name){List<UIMusicPlaylist> values=new ArrayList<>(userPlaylists.get());
                for(int i=0;i<values.size();i++)if(values.get(i).id().equals(playlist.id()))values.set(i,userPlaylist(playlist.id(),name));
                userPlaylists.set(List.copyOf(values));refreshPlayerPlaylists();selectedPlaylist.set(values.stream().filter(value->value.id().equals(playlist.id())).findFirst().orElse(null));}
            @Override public void delete(UIMusicPlaylist playlist){memberships.remove(playlist.id());userPlaylists.set(userPlaylists.get().stream().filter(value->!value.id().equals(playlist.id())).toList());
                refreshPlayerPlaylists();selectedPlaylist.set(userPlaylists.get().isEmpty()?null:userPlaylists.get().getFirst());refreshSelectedTracks();}
            @Override public void setTracks(UIMusicPlaylist playlist,List<UIMusicTrack> tracks){Set<String> ids=new HashSet<>();tracks.forEach(track->ids.add(track.id()));memberships.put(playlist.id(),ids);refreshSelectedTracks();}
            private UIMusicPlaylist userPlaylist(String id,String name){return new UIMusicPlaylist(id,Component.literal(name),track->memberships.getOrDefault(id,Set.of()).contains(track.id()),true);}
            private void refreshSelectedTracks(){UIMusicPlaylist playlist=selectedPlaylist.get();Set<String> ids=playlist==null?Set.of():memberships.getOrDefault(playlist.id(),Set.of());selectedTracks.set(allTracks.stream().filter(track->ids.contains(track.id())).toList());}
        }
        private void refreshPlayerPlaylists(){List<UIMusicPlaylist> values=new ArrayList<>(builtInPlaylists);values.addAll(userPlaylists.get());playlists.set(List.copyOf(values));}
    }
}
