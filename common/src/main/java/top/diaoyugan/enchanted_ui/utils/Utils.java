package top.diaoyugan.enchanted_ui.utils;
import top.diaoyugan.enchanted_ui.config.Config;
import top.diaoyugan.enchanted_ui.config.ConfigItems;

public class Utils {
    /**
     * 获取配置对象。
     *
     * @return 当前配置项 {@link ConfigItems}
     */
    public static ConfigItems getConfig() {
        return Config.getInstance().getConfigItems();
    }
}