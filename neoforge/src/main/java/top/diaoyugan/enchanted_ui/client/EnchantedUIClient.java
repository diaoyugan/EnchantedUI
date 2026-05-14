package top.diaoyugan.enchanted_ui.client;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import top.diaoyugan.enchanted_ui.Constants;
import top.diaoyugan.enchanted_ui.client.command.Command;
import top.diaoyugan.enchanted_ui.client.gui.screen.DemoScreen;

@EventBusSubscriber(modid = Constants.ID, value = Dist.CLIENT)
public final class EnchantedUIClient {

    private EnchantedUIClient() {}

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

