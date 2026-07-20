package top.diaoyugan.enchanted_ui.client.gui.builder;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus;
import top.diaoyugan.enchanted_ui.api.client.gui.UISummaryItem;
import top.diaoyugan.enchanted_ui.client.gui.layout.VerticalLayout;
import top.diaoyugan.enchanted_ui.client.gui.widget.display.EmptyStateWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.display.ErrorStateWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.display.InfoBlockWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.display.KeyValueRowWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.display.LoadingStateWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.display.ProgressBarWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.display.ReadonlyListWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.display.StatusBadgeWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.display.SummaryBlockWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.TextWidget;

import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

@ApiStatus.Internal
public final class FormDisplayFactory {
    private final int contentWidth;
    private final VerticalLayout layout;
    private final List<AbstractWidget> widgets;

    FormDisplayFactory(int contentWidth, VerticalLayout layout, List<AbstractWidget> widgets) {
        this.contentWidth = contentWidth;
        this.layout = layout;
        this.widgets = widgets;
    }

    public TextWidget title(Component text) {
        TextWidget widget = new TextWidget(layout.x(), layout.y(), text);
        widgets.add(widget);
        layout.next(10);
        return widget;
    }

    public ProgressBarWidget progressBar(Component label, int width, DoubleSupplier progressSupplier, Supplier<Component> valueSupplier, int fillColor) {
        ProgressBarWidget widget = new ProgressBarWidget(layout.x(), layout.y(), width, 20, label, progressSupplier, valueSupplier, fillColor);
        widgets.add(widget);
        layout.next(20);
        return widget;
    }

    public KeyValueRowWidget keyValueRow(Component label, Supplier<Component> valueSupplier) {
        KeyValueRowWidget widget = new KeyValueRowWidget(layout.x(), layout.y(), contentWidth, 20, label, valueSupplier);
        widgets.add(widget);
        layout.next(20);
        return widget;
    }

    public StatusBadgeWidget statusBadge(Component label, Supplier<Component> statusSupplier, IntSupplier colorSupplier) {
        StatusBadgeWidget widget = new StatusBadgeWidget(layout.x(), layout.y(), contentWidth, 20, label, statusSupplier, colorSupplier);
        widgets.add(widget);
        layout.next(20);
        return widget;
    }

    public EmptyStateWidget emptyState(Component title, Component description, int height) {
        EmptyStateWidget widget = new EmptyStateWidget(layout.x(), layout.y(), contentWidth, Math.max(36, height), title, description);
        widgets.add(widget);
        layout.next(widget.getHeight());
        return widget;
    }

    public InfoBlockWidget infoBlock(Component title, Component message, int accentColor) {
        InfoBlockWidget widget = new InfoBlockWidget(layout.x(), layout.y(), contentWidth, 44, title, message, accentColor);
        widgets.add(widget);
        layout.next(widget.getHeight());
        return widget;
    }

    public LoadingStateWidget loadingState(Component title, Component message) {
        LoadingStateWidget widget = new LoadingStateWidget(layout.x(), layout.y(), contentWidth, 44, title, message);
        widgets.add(widget);
        layout.next(widget.getHeight());
        return widget;
    }

    public ErrorStateWidget errorState(Component title, Component message, @Nullable Component actionLabel, @Nullable Runnable action) {
        ErrorStateWidget widget = new ErrorStateWidget(layout.x(), layout.y(), contentWidth, 50, title, message, actionLabel, action);
        widgets.add(widget);
        layout.next(widget.getHeight());
        return widget;
    }

    public ReadonlyListWidget readonlyList(
            Component label,
            Supplier<List<Component>> entriesSupplier,
            int visibleRows,
            Component emptyText,
            IntFunction<Component> overflowText
    ) {
        ReadonlyListWidget widget = new ReadonlyListWidget(layout.x(), layout.y(), contentWidth, label, entriesSupplier, visibleRows, emptyText, overflowText);
        widgets.add(widget);
        layout.next(widget.getHeight());
        return widget;
    }

    public SummaryBlockWidget summaryBlock(Component title, Supplier<List<UISummaryItem>> itemsSupplier, int rows, Component emptyText) {
        SummaryBlockWidget widget = new SummaryBlockWidget(layout.x(), layout.y(), contentWidth, title, itemsSupplier, rows, emptyText);
        widgets.add(widget);
        layout.next(widget.getHeight());
        return widget;
    }
}
