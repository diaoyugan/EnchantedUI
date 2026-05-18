package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.Objects;
import java.util.function.BooleanSupplier;

@FunctionalInterface
public interface UIBottomBar {
    void add(UITabbedScreen screen, int centerX, int bottomY);

    static UIBottomBar none() {
        return (screen, centerX, bottomY) -> {
        };
    }

    static UIBottomBar closeOnly(Component label) {
        return (screen, centerX, bottomY) -> screen.add(
                Button.builder(label, b -> screen.requestClose())
                        .bounds(centerX - 75, bottomY, 150, 20)
                        .build()
        );
    }

    static UIBottomBar saveAndClose(
            Component closeLabel,
            Component saveAndExitLabel,
            BooleanSupplier saveAction
    ) {
        Objects.requireNonNull(saveAction, "saveAction");
        return (screen, centerX, bottomY) -> {
            screen.add(Button.builder(closeLabel, b -> screen.requestClose())
                    .bounds(centerX - 154, bottomY, 150, 20)
                    .build());

            screen.add(Button.builder(saveAndExitLabel, b -> {
                        if (saveAction.getAsBoolean()) {
                            screen.markAllClean();
                            screen.requestClose();
                        }
                    })
                    .bounds(centerX + 4, bottomY, 150, 20)
                    .build());
        };
    }

    static UIBottomBar saveAndCloseWithExtra(
            Component closeLabel,
            Component saveAndExitLabel,
            BooleanSupplier saveAction,
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
