package top.diaoyugan.enchanted_ui.api.client.gui.music;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import top.diaoyugan.enchanted_ui.api.client.gui.UIBuildContext;
import top.diaoyugan.enchanted_ui.api.client.gui.UIFilterBar;
import top.diaoyugan.enchanted_ui.api.client.gui.UIIcon;
import top.diaoyugan.enchanted_ui.api.client.gui.UIImageTextPanel;
import top.diaoyugan.enchanted_ui.api.client.gui.UILocalization;
import top.diaoyugan.enchanted_ui.api.client.gui.UIPage;
import top.diaoyugan.enchanted_ui.api.client.gui.UIVirtualList;
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

/** Layout preset that adapts a music model to general-purpose EUI controls. */
public final class UIMusicPlayerPage implements UIPage {
    public enum Region { NAVIGATION, CONTENT, INSPECTOR, OVERLAY }

    public record RegionContext(UIBounds bounds, UIMusicPlayerModel model, UITheme theme) {}

    @FunctionalInterface
    public interface RegionExtension {
        List<AbstractWidget> build(RegionContext context);
    }

    private final UIMusicPlayerModel model;
    private final UITheme theme;
    private final Function<UIBuildContext, UIBounds> boundsFactory;
    private final boolean showNavigation;
    private final boolean showSearch;
    private final boolean showSort;
    private final boolean showNowPlaying;
    private final boolean showInspector;
    private final boolean showPlaylistEditor;
    private final int threeColumnAt;
    private final int roomyAt;
    private final int compactRowHeight;
    private final int regularRowHeight;
    private final int compactArtworkSize;
    private final IntUnaryOperator navigationWidth;
    private final IntUnaryOperator inspectorWidth;
    private final Consumer<UIMusicPlaylistController> playlistEditorOpener;
    @Nullable private final UIIcon artworkFallback;
    private final EnumMap<Region, List<RegionExtension>> extensions;
    private final List<UISubscription> subscriptions = new ArrayList<>();
    private UIVirtualList<UIMusicTrack, String> tracks;
    private String searchQuery = "";

    private UIMusicPlayerPage(Builder builder) {
        model = builder.model;
        theme = builder.theme;
        boundsFactory = builder.boundsFactory;
        showNavigation = builder.showNavigation;
        showSearch = builder.showSearch;
        showSort = builder.showSort;
        showNowPlaying = builder.showNowPlaying;
        showInspector = builder.showInspector;
        showPlaylistEditor = builder.showPlaylistEditor;
        threeColumnAt = builder.threeColumnAt;
        roomyAt = builder.roomyAt;
        compactRowHeight = builder.compactRowHeight;
        regularRowHeight = builder.regularRowHeight;
        compactArtworkSize = builder.compactArtworkSize;
        navigationWidth = builder.navigationWidth;
        inspectorWidth = builder.inspectorWidth;
        playlistEditorOpener = builder.playlistEditorOpener;
        artworkFallback = builder.artworkFallback;
        extensions = builder.copyExtensions();
    }

    public static Builder builder(UIMusicPlayerModel model) {
        return new Builder(model);
    }

    public UIMusicPlayerModel model() {
        return model;
    }

    public UITheme theme() {
        return theme;
    }

    @Override
    public List<AbstractWidget> build(UIBuildContext context) {
        subscriptions.forEach(UISubscription::close);
        subscriptions.clear();
        List<AbstractWidget> widgets = new ArrayList<>();
        UIBounds page = boundsFactory.apply(context);
        int gap = theme.spacing().small();
        boolean roomy = page.width() >= roomyAt;
        boolean threeColumn = showInspector && page.width() >= threeColumnAt;

        int sidebar = showNavigation
                ? Math.max(0, navigationWidth == null
                        ? defaultNavigationWidth(page.width(), roomy, threeColumn)
                        : navigationWidth.applyAsInt(page.width()))
                : 0;
        int inspector = threeColumn
                ? Math.max(0, inspectorWidth == null ? (roomy ? 180 : 120) : inspectorWidth.applyAsInt(page.width()))
                : 0;

        sidebar = Math.min(sidebar, Math.max(0, page.width() - 120 - (sidebar > 0 ? gap : 0)));
        inspector = Math.min(
                inspector,
                Math.max(0, page.width() - sidebar - 120 - (sidebar > 0 ? gap : 0) - (inspector > 0 ? gap : 0))
        );

        boolean compactNowPlaying = showNowPlaying && inspector == 0;
        int contentX = page.x() + sidebar + (sidebar > 0 ? gap : 0);
        int contentWidth = Math.max(
                120,
                page.width() - sidebar - inspector - (sidebar > 0 ? gap : 0) - (inspector > 0 ? gap : 0)
        );
        UIBounds navigationBounds = new UIBounds(page.x(), page.y(), sidebar, page.height());
        UIBounds contentBounds = new UIBounds(contentX, page.y(), contentWidth, page.height());
        UIBounds inspectorBounds = new UIBounds(
                contentX + contentWidth + (inspector > 0 ? gap : 0),
                page.y(),
                inspector,
                page.height()
        );

        if (sidebar > 0) buildNavigation(widgets, navigationBounds, threeColumn);

        int toolbarY = page.y();
        int listY = page.y();
        if (compactNowPlaying) {
            buildCompactNowPlaying(widgets, contentBounds);
            toolbarY += compactArtworkSize + 2;
            listY = toolbarY;
        }
        if (showSearch || showSort) {
            int toolbarHeight = compactNowPlaying ? 18 : 20;
            buildFilterBar(widgets, new UIBounds(contentX, toolbarY, contentWidth, toolbarHeight));
            listY = toolbarY + toolbarHeight + theme.spacing().small();
        }
        buildTracks(widgets, new UIBounds(
                contentX,
                listY,
                contentWidth,
                Math.max(36, page.height() - (listY - page.y()))
        ), compactNowPlaying);

        if (inspector > 0) buildInspector(widgets, inspectorBounds);
        addExtensions(widgets, Region.NAVIGATION, navigationBounds);
        addExtensions(widgets, Region.CONTENT, contentBounds);
        addExtensions(widgets, Region.INSPECTOR, inspectorBounds);
        addExtensions(widgets, Region.OVERLAY, page);
        return widgets;
    }

    private static int defaultNavigationWidth(int width, boolean roomy, boolean threeColumn) {
        if (roomy) return 140;
        if (threeColumn) return 96;
        if (width >= 260) return Math.min(88, Math.max(72, width / 4));
        return Math.min(64, Math.max(0, width / 4));
    }

    private void buildNavigation(List<AbstractWidget> widgets, UIBounds bounds, boolean threeColumn) {
        UIMusicPlaylistController controller = model.playlistController();
        int managerHeight = showPlaylistEditor && controller != null && playlistEditorOpener != null ? 24 : 0;
        UIVirtualList<NavigationEntry, String> navigation = UIVirtualList.<NavigationEntry, String>builder(NavigationEntry::id)
                .bounds(bounds.x(), bounds.y(), bounds.width(), bounds.height() - managerHeight)
                .items(this::navigationEntries)
                .text(NavigationEntry::label)
                .rowHeight(threeColumn ? regularRowHeight : compactRowHeight)
                .onSelect(entry -> {
                    if (entry.path() != null) model.selectRegistrationPath(entry.path());
                    else model.selectPlaylist(entry.playlist());
                })
                .marqueeText(true)
                .theme(theme)
                .build();
        widgets.add(navigation);

        if (managerHeight > 0) {
            widgets.add(Button.builder(
                    UILocalization.frameworkText("music.playlist.edit", "Edit playlists"),
                    ignored -> playlistEditorOpener.accept(controller)
            ).bounds(bounds.x(), bounds.y() + bounds.height() - 20, bounds.width(), 20).build());
        }
    }

    private void buildCompactNowPlaying(List<AbstractWidget> widgets, UIBounds bounds) {
        UIImageTextPanel.Builder panel = UIImageTextPanel.builder(() -> current() == null ? null : current().artwork())
                .orientation(UIImageTextPanel.Orientation.HORIZONTAL)
                .imageSize(compactArtworkSize)
                .lineHeight(14)
                .theme(theme)
                .line(
                        () -> current() == null
                                ? UILocalization.frameworkText("music.now_playing.empty", "Nothing playing")
                                : current().title(),
                        () -> theme.colors().text()
                )
                .line(
                        () -> current() == null
                                ? Component.empty()
                                : Component.literal("").append(current().artist()).append(" · ").append(current().album()),
                        () -> theme.colors().textMuted()
                );
        if (artworkFallback != null) panel.fallbackIcon(artworkFallback);
        widgets.addAll(panel.build().build(new UIBounds(
                bounds.x(),
                bounds.y(),
                bounds.width(),
                compactArtworkSize
        )));
    }

    private void buildFilterBar(List<AbstractWidget> widgets, UIBounds bounds) {
        int sortWidth = showSort ? Math.min(70, Math.max(44, bounds.width() / 4)) : 0;
        UIFilterBar.Builder filter = UIFilterBar.builder()
                .height(bounds.height())
                .gap(theme.spacing().small());
        if (showSearch) {
            filter.search(
                    UILocalization.frameworkText("music.search.label", "Search tracks"),
                    UILocalization.frameworkText("music.search.hint", "Search / filter"),
                    () -> searchQuery,
                    value -> {
                        searchQuery = value;
                        if (tracks != null) tracks.query(value);
                    }
            );
        }
        if (showSort) {
            filter.action(
                    sortWidth,
                    () -> model.sort().get().label(),
                    () -> UILocalization.frameworkText("music.sort.tooltip", "Change track sorting"),
                    this::cycleSort
            );
        }
        widgets.addAll(filter.build().build(bounds));
    }

    private void buildTracks(List<AbstractWidget> widgets, UIBounds bounds, boolean compact) {
        tracks = UIVirtualList.<UIMusicTrack, String>builder(UIMusicTrack::id)
                .bounds(bounds.x(), bounds.y(), bounds.width(), bounds.height())
                .items(this::sortedTracks)
                .rowHeight(compact ? compactRowHeight : regularRowHeight)
                .text(track -> Component.literal(Objects.equals(current(), track) ? "▶ " : "")
                        .append(track.artist()).append(" - ").append(track.title()))
                .filter((track, query) -> searchText(track).contains(query.toLowerCase()))
                .playing(track -> Objects.equals(current(), track))
                .marqueeText(true)
                .onSelect(model::select)
                .onActivate(model::play)
                .rowAction(24, (track, button) -> model.play(track))
                .theme(theme)
                .build();
        tracks.query(searchQuery);
        widgets.add(tracks);
        subscriptions.add(model.selectedTrack().subscribeNow(track -> {
            if (track != null && tracks != null) tracks.selectKey(track.id());
        }));
    }

    private static String searchText(UIMusicTrack track) {
        return (track.title().getString() + " "
                + track.artist().getString() + " "
                + track.album().getString() + " "
                + track.source().getString()).toLowerCase();
    }

    private void buildInspector(List<AbstractWidget> widgets, UIBounds bounds) {
        if (bounds.width() < 24 || bounds.height() < 24) return;
        int artworkSize = Math.min(bounds.width() - 20, Math.max(64, bounds.height() / 2));
        UIImageTextPanel.Builder panel = UIImageTextPanel.builder(() -> current() == null ? null : current().artwork())
                .orientation(UIImageTextPanel.Orientation.VERTICAL)
                .imageSize(artworkSize)
                .lineHeight(18)
                .gap(theme.spacing().medium())
                .theme(theme)
                .line(
                        () -> current() == null
                                ? UILocalization.frameworkText("music.now_playing.empty", "Nothing playing")
                                : current().title(),
                        () -> theme.colors().text()
                )
                .line(() -> current() == null ? Component.empty() : current().artist(), () -> theme.colors().textMuted())
                .line(() -> current() == null ? Component.empty() : current().album(), () -> theme.colors().textMuted())
                .line(() -> current() == null ? Component.empty() : current().source(), () -> theme.colors().textMuted());
        if (artworkFallback != null) panel.fallbackIcon(artworkFallback);
        widgets.addAll(panel.build().build(new UIBounds(
                bounds.x(),
                bounds.y() + 12,
                bounds.width(),
                Math.max(0, bounds.height() - 12)
        )));
    }

    private UIMusicTrack current() {
        return model.currentTrack().get();
    }

    private void cycleSort() {
        List<UIMusicSortOption> options = model.sortOptions();
        if (options.isEmpty()) return;
        int index = options.indexOf(model.sort().get());
        model.sort(options.get((index + 1 + options.size()) % options.size()));
    }

    private List<UIMusicTrack> sortedTracks() {
        List<UIMusicTrack> values = new ArrayList<>(model.tracks().get());
        values.sort(model.sort().get().comparator());
        return values;
    }

    private void addExtensions(List<AbstractWidget> widgets, Region region, UIBounds bounds) {
        if (bounds.width() <= 0 || bounds.height() <= 0) return;
        for (RegionExtension extension : extensions.get(region)) {
            List<AbstractWidget> values = extension.build(new RegionContext(bounds, model, theme));
            if (values != null) widgets.addAll(values);
        }
    }

    private List<NavigationEntry> navigationEntries() {
        List<NavigationEntry> entries = new ArrayList<>();
        List<UIMusicRegistrationPath> paths = model.registrationPaths().get();
        for (UIMusicRegistrationPath path : paths) {
            entries.add(new NavigationEntry(
                    "path:" + path.id(),
                    Component.literal("  ".repeat(pathDepth(path, paths))).append(path.name()),
                    path,
                    null
            ));
        }
        for (UIMusicPlaylist playlist : model.playlists().get()) {
            entries.add(new NavigationEntry(
                    "playlist:" + playlist.id(),
                    Component.literal(playlist.userCreated() ? "+ " : "* ").append(playlist.name()),
                    null,
                    playlist
            ));
        }
        return entries;
    }

    private int pathDepth(UIMusicRegistrationPath path, List<UIMusicRegistrationPath> paths) {
        int depth = 0;
        String parent = path.parentId();
        while (parent != null && depth < 8) {
            String id = parent;
            UIMusicRegistrationPath found = paths.stream()
                    .filter(value -> value.id().equals(id))
                    .findFirst()
                    .orElse(null);
            if (found == null) break;
            depth++;
            parent = found.parentId();
        }
        return depth;
    }

    @Override
    public void onClose() {
        subscriptions.forEach(UISubscription::close);
        subscriptions.clear();
    }

    private record NavigationEntry(
            String id,
            Component label,
            @Nullable UIMusicRegistrationPath path,
            @Nullable UIMusicPlaylist playlist
    ) {}

    public static final class Builder {
        private final UIMusicPlayerModel model;
        private UITheme theme = UIThemes.current();
        private Function<UIBuildContext, UIBounds> boundsFactory = context -> new UIBounds(
                context.viewportLeft(),
                38,
                context.availableWidth(),
                Math.max(80, context.screenHeight() - 96)
        );
        private boolean showNavigation = true;
        private boolean showSearch = true;
        private boolean showSort = true;
        private boolean showNowPlaying = true;
        private boolean showInspector = true;
        private boolean showPlaylistEditor = true;
        private int threeColumnAt = 480;
        private int roomyAt = 700;
        private int compactRowHeight = 22;
        private int regularRowHeight = 24;
        private int compactArtworkSize = 28;
        private IntUnaryOperator navigationWidth;
        private IntUnaryOperator inspectorWidth;
        private Consumer<UIMusicPlaylistController> playlistEditorOpener;
        @Nullable private UIIcon artworkFallback;
        private final EnumMap<Region, List<RegionExtension>> extensions = new EnumMap<>(Region.class);

        private Builder(UIMusicPlayerModel model) {
            this.model = Objects.requireNonNull(model);
            for (Region region : Region.values()) extensions.put(region, new ArrayList<>());
        }

        public Builder theme(UITheme value) {
            theme = Objects.requireNonNull(value);
            return this;
        }

        public Builder bounds(Function<UIBuildContext, UIBounds> value) {
            boundsFactory = Objects.requireNonNull(value);
            return this;
        }

        public Builder showNavigation(boolean value) {
            showNavigation = value;
            return this;
        }

        public Builder showSearch(boolean value) {
            showSearch = value;
            return this;
        }

        public Builder showSort(boolean value) {
            showSort = value;
            return this;
        }

        public Builder showNowPlaying(boolean value) {
            showNowPlaying = value;
            return this;
        }

        public Builder showInspector(boolean value) {
            showInspector = value;
            return this;
        }

        public Builder showPlaylistEditor(boolean value) {
            showPlaylistEditor = value;
            return this;
        }

        public Builder breakpoints(int threeColumnAt, int roomyAt) {
            this.threeColumnAt = Math.max(240, threeColumnAt);
            this.roomyAt = Math.max(this.threeColumnAt, roomyAt);
            return this;
        }

        public Builder navigationWidth(IntUnaryOperator value) {
            navigationWidth = Objects.requireNonNull(value);
            return this;
        }

        public Builder inspectorWidth(IntUnaryOperator value) {
            inspectorWidth = Objects.requireNonNull(value);
            return this;
        }

        public Builder rowHeights(int compact, int regular) {
            compactRowHeight = Math.max(12, compact);
            regularRowHeight = Math.max(12, regular);
            return this;
        }

        public Builder compactArtworkSize(int value) {
            compactArtworkSize = Math.max(12, value);
            return this;
        }

        public Builder artworkFallback(UIIcon value) {
            artworkFallback = value;
            return this;
        }

        public Builder playlistEditorOpener(Consumer<UIMusicPlaylistController> value) {
            playlistEditorOpener = value;
            return this;
        }

        public Builder extend(Region region, RegionExtension extension) {
            extensions.get(region).add(Objects.requireNonNull(extension));
            return this;
        }

        private EnumMap<Region, List<RegionExtension>> copyExtensions() {
            EnumMap<Region, List<RegionExtension>> copy = new EnumMap<>(Region.class);
            extensions.forEach((key, value) -> copy.put(key, List.copyOf(value)));
            return copy;
        }

        public UIMusicPlayerPage build() {
            return new UIMusicPlayerPage(this);
        }
    }
}
