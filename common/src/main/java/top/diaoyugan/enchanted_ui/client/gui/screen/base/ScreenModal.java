package top.diaoyugan.enchanted_ui.client.gui.screen.base;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.PreeditEvent;
import net.minecraft.network.chat.Component;

import java.util.List;

/** Owns modal button interaction and rendering independently of a Screen instance. */
final class ScreenModal {
    private final Component title;
    private final List<Component> lines;
    private final List<Button> buttons;
    private final Runnable close;

    private int left;
    private int top;
    private int width;
    private int height;

    ScreenModal(
            Component title,
            List<Component> lines,
            List<BaseTabbedScreen.DialogAction> actions,
            Runnable close
    ) {
        this.title = title;
        this.lines = List.copyOf(lines);
        this.close = close;
        this.buttons = actions.stream().map(action -> Button.builder(action.label(), ignored -> {
            action.action().run();
            if (action.closeAfterRun()) {
                close.run();
            }
        }).bounds(0, 0, 90, 20).build()).toList();
    }

    boolean keyPressed(KeyEvent event) {
        if (event.key() != InputConstants.KEY_ESCAPE) {
            return false;
        }
        close.run();
        return true;
    }

    boolean charTyped(CharacterEvent event) {
        return true;
    }

    boolean preeditUpdated(PreeditEvent event) {
        return true;
    }

    boolean mouseClicked(MouseButtonEvent event, boolean doubleClick, int screenWidth, int screenHeight) {
        layoutButtons(screenWidth, screenHeight);
        for (Button button : buttons) {
            if (button.mouseClicked(event, doubleClick)) {
                return true;
            }
        }
        return true;
    }

    boolean mouseReleased(MouseButtonEvent event) {
        for (Button button : buttons) {
            if (button.mouseReleased(event)) {
                return true;
            }
        }
        return true;
    }

    boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        for (Button button : buttons) {
            if (button.mouseDragged(event, dragX, dragY)) {
                return true;
            }
        }
        return true;
    }

    void extractRenderState(
            GuiGraphicsExtractor graphics,
            Font font,
            int screenWidth,
            int screenHeight,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        layoutButtons(screenWidth, screenHeight);

        graphics.fill(0, 0, screenWidth, screenHeight, 0x88000000);
        graphics.fill(left, top, left + width, top + height, 0xEE1E1E1E);
        graphics.outline(left, top, width, height, 0xFF777777);
        graphics.text(font, title, left + 10, top + 10, 0xFFFFFFFF, false);

        int textY = top + 30;
        for (Component line : lines) {
            graphics.text(font, line, left + 10, textY, 0xFFD0D0D0, false);
            textY += 12;
        }

        for (Button button : buttons) {
            button.extractRenderState(graphics, mouseX, mouseY, partialTick);
        }
    }

    private void layoutButtons(int screenWidth, int screenHeight) {
        int lineHeight = lines.size() * 12;
        width = Math.max(180, Math.min(320, screenWidth - 40));
        height = 62 + lineHeight + 24;
        left = (screenWidth - width) / 2;
        top = (screenHeight - height) / 2;

        int totalButtonsWidth = buttons.size() * 90 + Math.max(0, buttons.size() - 1) * 6;
        int buttonX = left + (width - totalButtonsWidth) / 2;
        int buttonY = top + height - 30;
        for (Button button : buttons) {
            button.setPosition(buttonX, buttonY);
            buttonX += 96;
        }
    }
}
