package top.diaoyugan.enchanted_ui.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.minecraft.client.Minecraft;
import top.diaoyugan.enchanted_ui.Constants;
import top.diaoyugan.enchanted_ui.client.gui.screen.DemoScreen;
import top.diaoyugan.enchanted_ui.client.inputs.KeyBinding;

@EventBusSubscriber(modid = Constants.ID, value = Dist.CLIENT)
public final class EnchantedUIClient {

    private EnchantedUIClient() {}

    @SubscribeEvent
    public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.register(KeyBinding.OPEN_DEMO_SCREEN);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        while (KeyBinding.OPEN_DEMO_SCREEN.consumeClick()) {
            mc.setScreenAndShow(new DemoScreen(null));
        }
    }
}

