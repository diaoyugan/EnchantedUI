package top.diaoyugan.enchanted_ui.client.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import top.diaoyugan.enchanted_ui.client.gui.screen.DemoScreen;

public class Command {
    public static boolean pendingOpenDemo = false;

    public static void registerAll(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("enchantedui")
                .then(Commands.literal("demo")
                        .executes(ctx -> {
                            // Chat/command execution may close the current screen right after this callback.
                            // Defer opening to the next client tick to prevent the new screen from being replaced.
                            pendingOpenDemo = true;
                            return 1;
                        })
                )
        );
    }

    public static void tick(Minecraft client){
        if (!pendingOpenDemo) return;
        pendingOpenDemo = false;
        openDemo(client);
    }

    private static void openDemo(Minecraft mc) {
        if (mc == null) return;

        // Parent can be null; closing returns to game.
        mc.setScreenAndShow(new DemoScreen(null));
    }
}
