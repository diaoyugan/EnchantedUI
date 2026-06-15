package top.diaoyugan.enchanted_ui.client;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import top.diaoyugan.enchanted_ui.standalone.command.Command;
import top.diaoyugan.enchanted_ui.standalone.gui.screen.DemoScreen;

@Mod(EnchantedUIClient.MOD_ID)
@EventBusSubscriber(modid = EnchantedUIClient.MOD_ID, value = Dist.CLIENT)
public final class EnchantedUIClient {
    static final String MOD_ID = "enchanted_ui";

    public EnchantedUIClient() {}

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        Command.registerAll(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onEndClientTick(ClientTickEvent.Post event){
        Command.tick(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void initClient(FMLClientSetupEvent event){
        ModContainer container = event.getContainer();
        container.registerExtensionPoint(IConfigScreenFactory.class,
                (modContainer, parent) -> new DemoScreen(parent)
        );
    }
}
