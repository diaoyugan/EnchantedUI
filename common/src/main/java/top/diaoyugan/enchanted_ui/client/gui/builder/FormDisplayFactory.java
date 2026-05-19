package top.diaoyugan.enchanted_ui.client.gui.builder;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
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

import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

final class FormDisplayFactory {
    private static final int DEFAULT_PROGRESS_COLOR = 0xFF3977C2;
    private static final int DEFAULT_STATUS_COLOR = 0xFF3C6E47;

    private final int contentWidth;
    private final VerticalLayout layout;
    private final List<AbstractWidget> widgets;

    FormDisplayFactory(int contentWidth, VerticalLayout layout, List<AbstractWidget> widgets) {
        this.contentWidth = contentWidth;
        this.layout = layout;
        this.widgets = widgets;
    }

    ProgressBarWidget progressBar(Component label, DoubleSupplier progressSupplier) {
        return progressBar(label, contentWidth, progressSupplier, DEFAULT_PROGRESS_COLOR);
    }

    ProgressBarWidget progressBar(Component label, int width, DoubleSupplier progressSupplier, int fillColor) {
        ProgressBarWidget widget = new ProgressBarWidget(layout.x(), layout.y(), width, 20, label, progressSupplier, fillColor);
        widgets.add(widget);
        layout.next(20);
        return widget;
    }

    ProgressBarWidget progressBar(Component label, int width, DoubleSupplier progressSupplier, Supplier<Component> valueSupplier, int fillColor) {
        ProgressBarWidget widget = new ProgressBarWidget(layout.x(), layout.y(), width, 20, label, progressSupplier, valueSupplier, fillColor);
        widgets.add(widget);
        layout.next(20);
        return widget;
    }

    KeyValueRowWidget keyValueRow(Component label, Supplier<Component> valueSupplier) {
        KeyValueRowWidget widget = new KeyValueRowWidget(layout.x(), layout.y(), contentWidth, 20, label, valueSupplier);
        widgets.add(widget);
        layout.next(20);
        return widget;
    }

    StatusBadgeWidget statusBadge(Component label, Supplier<Component> statusSupplier) {
        return statusBadge(label, statusSupplier, () -> DEFAULT_STATUS_COLOR);
    }

    StatusBadgeWidget statusBadge(Component label, Supplier<Component> statusSupplier, IntSupplier colorSupplier) {
        StatusBadgeWidget widget = new StatusBadgeWidget(layout.x(), layout.y(), contentWidth, 20, label, statusSupplier, colorSupplier);
        widgets.add(widget);
        layout.next(20);
        return widget;
    }

    EmptyStateWidget emptyState(Component title, Component description) {
        return emptyState(title, description, 44);
    }

    EmptyStateWidget emptyState(Component title, Component description, int height) {
        EmptyStateWidget widget = new EmptyStateWidget(layout.x(), layout.y(), contentWidth, Math.max(36, height), title, description);
        widgets.add(widget);
        layout.next(widget.getHeight());
        return widget;
    }

    InfoBlockWidget infoBlock(Component title, Component message) {
        return infoBlock(title, message, 0xFF3977C2);
    }

    InfoBlockWidget infoBlock(Component title, Component message, int accentColor) {
        InfoBlockWidget widget = new InfoBlockWidget(layout.x(), layout.y(), contentWidth, 44, title, message, accentColor);
        widgets.add(widget);
        layout.next(widget.getHeight());
        return widget;
    }

    LoadingStateWidget loadingState(Component title, Component message) {
        LoadingStateWidget widget = new LoadingStateWidget(layout.x(), layout.y(), contentWidth, 44, title, message);
        widgets.add(widget);
        layout.next(widget.getHeight());
        return widget;
    }

    ErrorStateWidget errorState(Component title, Component message) {
        return errorState(title, message, null, null);
    }

    ErrorStateWidget errorState(Component title, Component message, @Nullable Component actionLabel, @Nullable Runnable action) {
        ErrorStateWidget widget = new ErrorStateWidget(layout.x(), layout.y(), contentWidth, 50, title, message, actionLabel, action);
        widgets.add(widget);
        layout.next(widget.getHeight());
        return widget;
    }

    ReadonlyListWidget readonlyList(Component label, Supplier<List<Component>> entriesSupplier) {
        return readonlyList(label, entriesSupplier, 5);
    }

    ReadonlyListWidget readonlyList(Component label, Supplier<List<Component>> entriesSupplier, int visibleRows) {
        ReadonlyListWidget widget = new ReadonlyListWidget(layout.x(), layout.y(), contentWidth, label, entriesSupplier, visibleRows);
        widgets.add(widget);
        layout.next(widget.getHeight());
        return widget;
    }

    ReadonlyListWidget readonlyList(
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

    SummaryBlockWidget summaryBlock(Component title, Supplier<List<UISummaryItem>> itemsSupplier) {
        return summaryBlock(title, itemsSupplier, 4);
    }

    SummaryBlockWidget summaryBlock(Component title, Supplier<List<UISummaryItem>> itemsSupplier, int rows) {
        SummaryBlockWidget widget = new SummaryBlockWidget(layout.x(), layout.y(), contentWidth, title, itemsSupplier, rows);
        widgets.add(widget);
        layout.next(widget.getHeight());
        return widget;
    }

    SummaryBlockWidget summaryBlock(Component title, Supplier<List<UISummaryItem>> itemsSupplier, int rows, Component emptyText) {
        SummaryBlockWidget widget = new SummaryBlockWidget(layout.x(), layout.y(), contentWidth, title, itemsSupplier, rows, emptyText);
        widgets.add(widget);
        layout.next(widget.getHeight());
        return widget;
    }
}
