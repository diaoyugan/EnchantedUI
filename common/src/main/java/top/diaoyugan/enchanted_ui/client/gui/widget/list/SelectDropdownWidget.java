package top.diaoyugan.enchanted_ui.client.gui.widget.list;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SelectDropdownWidget<T> extends AbstractDropdownListWidget {
    private final Supplier<T> getter;
    private final Consumer<T> setter;
    private final Supplier<List<T>> entriesSupplier;
    private final Function<T, Component> display;

    public SelectDropdownWidget(
            int x,
            int y,
            int width,
            Component label,
            Supplier<T> getter,
            Consumer<T> setter,
            Supplier<List<T>> entriesSupplier,
            Function<T, Component> display,
            int visibleRows
    ) {
        super(x, y, width, label, visibleRows);
        this.getter = getter;
        this.setter = setter;
        this.entriesSupplier = entriesSupplier;
        this.display = display;
    }

    @Override
    protected List<Component> entries() {
        return entriesSupplier.get().stream().map(display).toList();
    }

    @Override
    protected Component headerText() {
        T current = getter.get();
        Component currentText = current == null ? Component.translatable("eui.select.none") : display.apply(current);
        return label().copy().append(Component.literal(": ")).append(currentText);
    }

    @Override
    protected void onEntryClicked(int index) {
        List<T> entries = entriesSupplier.get();
        if (index >= 0 && index < entries.size()) {
            setter.accept(entries.get(index));
            collapse();
        }
    }

    @Override
    protected int footerHeight() {
        return 0;
    }

    @Override
    protected void extractFooter(GuiGraphicsExtractor guiGraphics, int left, int top, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    protected boolean mouseClickedInFooter(MouseButtonEvent event, boolean doubleClick, int left, int top) {
        return false;
    }

    @Override
    protected boolean mouseReleasedInFooter(MouseButtonEvent event, int left, int top) {
        return false;
    }

    @Override
    protected boolean mouseDraggedInFooter(MouseButtonEvent event, double dragX, double dragY, int left, int top) {
        return false;
    }

    @Override
    protected boolean keyPressedInFooter(KeyEvent event) {
        return false;
    }

    @Override
    protected boolean charTypedInFooter(net.minecraft.client.input.CharacterEvent event) {
        return false;
    }

    @Override
    protected boolean preeditUpdatedInFooter(net.minecraft.client.input.PreeditEvent event) {
        return false;
    }

    @Override
    protected int entryActionWidth() {
        return 0;
    }

    @Override
    protected void onEntryAction(int index) {
    }

    @Override
    protected void setInnerFocus(boolean focused) {
    }
}
