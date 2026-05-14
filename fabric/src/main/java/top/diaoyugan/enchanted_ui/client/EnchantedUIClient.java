package top.diaoyugan.enchanted_ui.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import top.diaoyugan.enchanted_ui.client.command.Command;
import top.diaoyugan.enchanted_ui.client.gui.screen.DemoScreen;

import static top.diaoyugan.enchanted_ui.client.command.Command.pendingOpenDemo;


public class EnchantedUIClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                Command.registerAll(dispatcher)
        );

        ClientTickEvents.END_CLIENT_TICK.register(Command::tick);
    }
}
