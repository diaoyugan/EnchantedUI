package top.diaoyugan.enchanted_ui.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import top.diaoyugan.enchanted_ui.client.gui.screen.DemoScreen;


public class EnchantedUIClient implements ClientModInitializer {

    private static boolean pendingOpenDemo = false;

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommands.literal("enchantedui")
                        .then(ClientCommands.literal("demo")
                                .executes(ctx -> {
                                    // Chat/command execution may close the current screen right after this callback.
                                    // Defer opening to the next client tick to prevent the new screen from being replaced.
                                    pendingOpenDemo = true;
                                    return 1;
                                })
                        )
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!pendingOpenDemo) return;
            pendingOpenDemo = false;
            openDemo(client);
        });
    }

    private static void openDemo(Minecraft mc) {
        if (mc == null) return;

        // Parent can be null; closing returns to game.
        mc.setScreenAndShow(new DemoScreen(null));
    }
}
