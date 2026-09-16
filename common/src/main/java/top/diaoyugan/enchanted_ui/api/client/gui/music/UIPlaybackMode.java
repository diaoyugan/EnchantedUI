package top.diaoyugan.enchanted_ui.api.client.gui.music;

import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.gui.UILocalization;

public enum UIPlaybackMode {
    SEQUENTIAL("->", "sequential", "Sequential"), ALL_LOOP("A", "all_loop", "Loop all"),
    SINGLE_LOOP("1", "single_loop", "Repeat one"), SHUFFLE("S", "shuffle", "Shuffle");
    private final String symbol;
    private final Component label;
    UIPlaybackMode(String symbol,String key,String fallback){this.symbol=symbol;this.label=UILocalization.frameworkText("music.mode."+key,fallback);}
    public String symbol(){return symbol;}
    public Component label(){return label;}
}
