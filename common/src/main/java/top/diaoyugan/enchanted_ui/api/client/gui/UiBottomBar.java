package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.Objects;

@FunctionalInterface
public interface UiBottomBar {
    void add(UiTabbedScreen screen, int centerX, int bottomY);

    static UiBottomBar none() {
        return (screen, centerX, bottomY) -> {
        };
    }

    static UiBottomBar closeOnly(Component label) {
        return (screen, centerX, bottomY) -> screen.add(
                Button.builder(label, b -> screen.onClose())
                        .bounds(centerX - 75, bottomY, 150, 20)
                        .build()
        );
    }

    static UiBottomBar saveAndClose(
            Component closeLabel,
            Component saveAndExitLabel,
            Runnable saveAction
    ) {
        Objects.requireNonNull(saveAction, "saveAction");
        return (screen, centerX, bottomY) -> {
            screen.add(Button.builder(closeLabel, b -> screen.onClose())
                    .bounds(centerX - 154, bottomY, 150, 20)
                    .build());

            screen.add(Button.builder(saveAndExitLabel, b -> {
                        saveAction.run();
                        screen.onClose();
                    })
                    .bounds(centerX + 4, bottomY, 150, 20)
                    .build());
        };
    }

    static UiBottomBar saveAndCloseWithExtra(
            Component closeLabel,
            Component saveAndExitLabel,
            Runnable saveAction,
            Component extraLabel,
            Tooltip extraTooltip,
            Runnable extraAction
    ) {
        Objects.requireNonNull(saveAction, "saveAction");
        Objects.requireNonNull(extraAction, "extraAction");
        return (screen, centerX, bottomY) -> {
            saveAndClose(closeLabel, saveAndExitLabel, saveAction).add(screen, centerX, bottomY);

            Button extra = Button.builder(extraLabel, b -> extraAction.run())
                    .bounds(centerX - 194, bottomY, 20, 20)
                    .build();
            if (extraTooltip != null) {
                extra.setTooltip(extraTooltip);
            }
            screen.add(extra);
        };
    }
}
