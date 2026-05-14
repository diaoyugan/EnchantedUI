package top.diaoyugan.enchanted_ui.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import top.diaoyugan.enchanted_ui.Constants;

@EventBusSubscriber(modid = Constants.ID, value = Dist.CLIENT)
public final class EnchantedUIClient {

    private EnchantedUIClient() {}
}

