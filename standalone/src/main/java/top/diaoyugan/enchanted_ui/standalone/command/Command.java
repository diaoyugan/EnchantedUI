package top.diaoyugan.enchanted_ui.standalone.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import top.diaoyugan.enchanted_ui.standalone.gui.screen.DemoScreen;
import top.diaoyugan.enchanted_ui.standalone.gui.screen.InfoDemoScreen;
import top.diaoyugan.enchanted_ui.standalone.gui.screen.TopTabbedDemoScreen;

public final class Command {
    private static PendingScreen pendingScreen;

    private enum PendingScreen {
        SIDEBAR,
        TOP_TABS,
        INFO
    }

    private Command() {
    }

    public static void registerAll(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("enchantedui")
                .then(Commands.literal("demo")
                        .executes(ctx -> {
                            // Chat/command execution may close the current screen right after this callback.
                            // Defer opening to the next client tick to prevent the new screen from being replaced.
                            pendingScreen = PendingScreen.SIDEBAR;
                            return 1;
                        })
                        .then(Commands.literal("top")
                                .executes(ctx -> {
                                    pendingScreen = PendingScreen.TOP_TABS;
                                    return 1;
                                }))
                        .then(Commands.literal("info")
                                .executes(ctx -> {
                                    pendingScreen = PendingScreen.INFO;
                                    return 1;
                                }))
                )
        );
    }

    public static void tick(Minecraft client) {
        if (pendingScreen == null) return;
        PendingScreen screen = pendingScreen;
        pendingScreen = null;
        openDemo(client, screen);
    }

    private static void openDemo(Minecraft mc, PendingScreen screen) {
        if (mc == null) return;

        // Parent can be null; closing returns to game.
        mc.setScreenAndShow(switch (screen) {
            case SIDEBAR -> new DemoScreen(null);
            case TOP_TABS -> new TopTabbedDemoScreen(null);
            case INFO -> new InfoDemoScreen(null);
        });
    }
}
