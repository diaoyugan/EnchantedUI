package top.diaoyugan.enchanted_ui.client.gui.widget;

import net.minecraft.client.gui.components.AbstractWidget;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BooleanSupplier;

public final class WidgetConditions {
    private static final Map<AbstractWidget, Boolean> BASE_VISIBLE = new WeakHashMap<>();
    private static final Map<AbstractWidget, Boolean> BASE_ACTIVE = new WeakHashMap<>();
    private static final Map<AbstractWidget, BooleanSupplier> VISIBLE = new WeakHashMap<>();
    private static final Map<AbstractWidget, BooleanSupplier> ACTIVE = new WeakHashMap<>();

    private WidgetConditions() {
    }

    public static void visibleIf(AbstractWidget widget, @Nullable BooleanSupplier condition) {
        BASE_VISIBLE.putIfAbsent(widget, widget.visible);
        if (condition == null) {
            VISIBLE.remove(widget);
            return;
        }
        VISIBLE.put(widget, condition);
        refresh(widget);
    }

    public static void activeIf(AbstractWidget widget, @Nullable BooleanSupplier condition) {
        BASE_ACTIVE.putIfAbsent(widget, widget.active);
        if (condition == null) {
            ACTIVE.remove(widget);
            return;
        }
        ACTIVE.put(widget, condition);
        refresh(widget);
    }

    public static void refresh(AbstractWidget widget) {
        widget.visible = evaluateVisible(widget);
        widget.active = evaluateActive(widget);
    }

    public static boolean evaluateVisible(AbstractWidget widget) {
        boolean baseVisible = BASE_VISIBLE.computeIfAbsent(widget, ignored -> widget.visible);
        BooleanSupplier visible = VISIBLE.get(widget);
        return baseVisible && (visible == null || visible.getAsBoolean());
    }

    public static boolean evaluateActive(AbstractWidget widget) {
        boolean baseActive = BASE_ACTIVE.computeIfAbsent(widget, ignored -> widget.active);
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
    }
}
