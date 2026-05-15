package top.diaoyugan.enchanted_ui.client.gui.widget.list;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Supplier;

public class DropdownListWidget extends AbstractDropdownListWidget {
    private final Supplier<List<Component>> entriesSupplier;

    public DropdownListWidget(int x, int y, int width, Component label, Supplier<List<Component>> entriesSupplier) {
        this(x, y, width, label, entriesSupplier, DEFAULT_VISIBLE_ROWS);
    }

    public DropdownListWidget(int x, int y, int width, Component label, Supplier<List<Component>> entriesSupplier, int visibleRows) {
        super(x, y, width, label, visibleRows);
        this.entriesSupplier = entriesSupplier;
    }

    @Override
    protected List<Component> entries() {
        return entriesSupplier.get();
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
