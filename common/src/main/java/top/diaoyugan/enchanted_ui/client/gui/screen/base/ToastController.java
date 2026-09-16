package top.diaoyugan.enchanted_ui.client.gui.screen.base;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/** Owns transient toast lifetime and rendering. */
final class ToastController {
    private final List<ToastEntry> entries = new ArrayList<>();

    void show(Component message, int durationTicks) {
        entries.add(new ToastEntry(message, Math.max(20, durationTicks)));
    }

    void tick() {
        entries.forEach(ToastEntry::tick);
        entries.removeIf(ToastEntry::expired);
    }

    void extractRenderState(GuiGraphicsExtractor graphics, Font font, int screenWidth) {
        int y = 10;
        for (ToastEntry toast : entries) {
            int width = font.width(toast.message) + 16;
            int x = screenWidth - width - 10;
            graphics.fill(x, y, x + width, y + 18, 0xCC202020);
            graphics.outline(x, y, width, 18, 0xFF666666);
            graphics.text(font, toast.message, x + 8, y + 5, 0xFFFFFFFF, false);
            y += 22;
        }
    }

    private static final class ToastEntry {
        private final Component message;
        private int remainingTicks;

        private ToastEntry(Component message, int remainingTicks) {
            this.message = message;
            this.remainingTicks = remainingTicks;
        }

        private void tick() {
            remainingTicks--;
        }

        private boolean expired() {
            return remainingTicks <= 0;
        }
    }
}
