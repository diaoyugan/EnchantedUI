package top.diaoyugan.enchanted_ui.client.gui.widget;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BooleanSupplier;

public final class WidgetConditions {
    private static final Map<AbstractWidget, Boolean> BASE_VISIBLE = new WeakHashMap<>();
    private static final Map<AbstractWidget, Boolean> BASE_ACTIVE = new WeakHashMap<>();
    private static final Map<AbstractWidget, BooleanSupplier> VISIBLE = new WeakHashMap<>();
    private static final Map<AbstractWidget, BooleanSupplier> ACTIVE = new WeakHashMap<>();
    private static final Map<AbstractWidget, Tooltip> TOOLTIP = new WeakHashMap<>();
    private static final Map<AbstractWidget, Tooltip> DISABLED_TOOLTIP = new WeakHashMap<>();

    private WidgetConditions() {
    }

    public static void visibleIf(AbstractWidget widget, @Nullable BooleanSupplier condition) {
        BASE_VISIBLE.putIfAbsent(widget, true);
        if (condition == null) {
            VISIBLE.remove(widget);
        } else {
            VISIBLE.put(widget, condition);
        }
        refresh(widget);
    }

    public static void activeIf(AbstractWidget widget, @Nullable BooleanSupplier condition) {
        BASE_ACTIVE.putIfAbsent(widget, true);
        if (condition == null) {
            ACTIVE.remove(widget);
        } else {
            ACTIVE.put(widget, condition);
        }
        refresh(widget);
    }

    public static void refresh(AbstractWidget widget) {
        widget.visible = evaluateVisible(widget);
        widget.active = evaluateActive(widget);
        refreshTooltip(widget);
    }

    public static boolean evaluateVisible(AbstractWidget widget) {
        boolean baseVisible = BASE_VISIBLE.getOrDefault(widget, true);
        BooleanSupplier visible = VISIBLE.get(widget);
        return baseVisible && (visible == null || visible.getAsBoolean());
    }

    public static boolean evaluateActive(AbstractWidget widget) {
        boolean baseActive = BASE_ACTIVE.getOrDefault(widget, true);
        BooleanSupplier active = ACTIVE.get(widget);
        return baseActive && (active == null || active.getAsBoolean());
    }

    public static void setVisibleState(AbstractWidget widget, boolean visible) {
        BASE_VISIBLE.put(widget, visible);
        widget.visible = visible;
    }

    public static void setActiveState(AbstractWidget widget, boolean active) {
        BASE_ACTIVE.put(widget, active);
        widget.active = active;
        refreshTooltip(widget);
    }

    public static void setTooltip(AbstractWidget widget, @Nullable Tooltip tooltip) {
        if (tooltip == null) {
            TOOLTIP.remove(widget);
        } else {
            TOOLTIP.put(widget, tooltip);
        }
        refreshTooltip(widget);
    }

    public static void setDisabledTooltip(AbstractWidget widget, @Nullable Tooltip tooltip) {
        if (tooltip == null) {
            DISABLED_TOOLTIP.remove(widget);
        } else {
            DISABLED_TOOLTIP.put(widget, tooltip);
        }
        refreshTooltip(widget);
    }

    public static void refreshTooltip(AbstractWidget widget) {
        if (!TOOLTIP.containsKey(widget) && !DISABLED_TOOLTIP.containsKey(widget)) {
            return;
        }
        Tooltip tooltip = widget.active
                ? TOOLTIP.get(widget)
                : DISABLED_TOOLTIP.getOrDefault(widget, TOOLTIP.get(widget));
        widget.setTooltip(tooltip);
    }
}
