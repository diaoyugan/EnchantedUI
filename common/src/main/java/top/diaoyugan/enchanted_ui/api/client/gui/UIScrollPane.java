package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UIThemes;

/** Clipped widget container with independent vertical scrolling. */
public final class UIScrollPane extends AbstractWidget {
    private final List<AbstractWidget> children = new ArrayList<>();
    private final Map<AbstractWidget, Integer> originalY = new IdentityHashMap<>();
    private int contentHeight;
    private int scroll;

    public UIScrollPane(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    public UIScrollPane add(AbstractWidget child) {
        children.add(child);
        originalY.put(child, child.getY());
        contentHeight = Math.max(contentHeight, child.getY() + child.getHeight() - getY());
        applyScroll();
        return this;
    }

    public List<AbstractWidget> children() { return List.copyOf(children); }
    public int scrollOffset() { return scroll; }
    public UIScrollPane scrollTo(int offset) { scroll = clamp(offset); applyScroll(); return this; }
    public UIScrollPane scrollTo(AbstractWidget child) {
        Integer y = originalY.get(child);
        if (y == null) return this;
        if (y < getY() + scroll) scrollTo(y - getY());
        else if (y + child.getHeight() > getY() + scroll + height) scrollTo(y + child.getHeight() - getY() - height);
        return this;
    }

    @Override protected void extractWidgetRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        g.enableScissor(getX(), getY(), getX() + width, getY() + height);
        for (AbstractWidget child : children) child.extractRenderState(g, mouseX, mouseY, partialTick);
        g.disableScissor();
        if (contentHeight > height) {
            int thumb = Math.max(12, height * height / contentHeight);
            int y = getY() + (height - thumb) * scroll / Math.max(1, contentHeight - height);
            g.fill(getX() + width - 2, y, getX() + width, y + thumb, UIThemes.current().colors().textMuted());
        }
    }

    @Override public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!isMouseOver(event.x(), event.y())) return false;
        for (int i = children.size() - 1; i >= 0; i--) if (children.get(i).mouseClicked(event, doubleClick)) return true;
        return super.mouseClicked(event, doubleClick);
    }
    @Override public boolean mouseReleased(MouseButtonEvent event) {
        for (AbstractWidget child : children) if (child.mouseReleased(event)) return true;
        return false;
    }
    @Override public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        for (AbstractWidget child : children) if (child.mouseDragged(event, dragX, dragY)) return true;
        return false;
    }
    @Override public boolean mouseScrolled(double mouseX,double mouseY,double horizontal,double vertical) {
        if (!isMouseOver(mouseX, mouseY)) return false;
        for (AbstractWidget child : children) if (child.mouseScrolled(mouseX,mouseY,horizontal,vertical)) return true;
        scrollTo(scroll - (int)Math.round(vertical * 24));
        return true;
    }
    @Override public boolean keyPressed(KeyEvent event) {
        for (AbstractWidget child : children) if (child.isFocused() && child.keyPressed(event)) return true;
        return false;
    }
    private int clamp(int value) { return Math.max(0, Math.min(value, Math.max(0, contentHeight - height))); }
    private void applyScroll() { for (AbstractWidget child : children) child.setY(originalY.get(child) - scroll); }
    @Override protected void updateWidgetNarration(NarrationElementOutput output) {}
}
