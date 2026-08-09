package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.KeyEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/** A page that directly owns one public {@link UIForm} lifecycle. */
public final class UIFormPage implements UIPage {
    private final int contentWidth;
    private final int startY;
    private final int gap;
    private final UIFormSpec spec;
    private UIForm lastForm;

    public UIFormPage(int contentWidth, UIFormSpec spec) {
        this(contentWidth, 10, 4, spec);
    }

    public UIFormPage(int contentWidth, int startY, int gap, UIFormSpec spec) {
        this.contentWidth = contentWidth;
        this.startY = startY;
        this.gap = gap;
        this.spec = Objects.requireNonNull(spec, "spec");
    }

    /** Returns the most recently built form, or {@code null} before the first build. */
    public @Nullable UIForm lastForm() {
        return lastForm;
    }

    @Override
    public List<AbstractWidget> build(UIBuildContext context) {
        int responsiveWidth = Math.min(contentWidth, Math.max(40, context.availableWidth() - 24));
        UIForm form = new UIForm(context, responsiveWidth, startY, gap);
        spec.build(form);
        lastForm = form;
        return form.widgets();
    }

    @Override
    public boolean onSave() {
        if (lastForm == null) return true;
        if (!lastForm.runSavers()) return false;
        spec.onSave(lastForm);
        lastForm.markClean();
        return true;
    }

    @Override
    public void onOpen() {
        if (lastForm != null) spec.onOpen(lastForm);
    }

    @Override
    public void onClose() {
        if (lastForm != null) spec.onClose(lastForm);
    }

    @Override
    public void onShow() {
        if (lastForm != null) spec.onShow(lastForm);
    }

    @Override
    public void onHide() {
        if (lastForm != null) spec.onHide(lastForm);
    }

    @Override
    public void onPageChanged(int previousPage, int currentPage) {
        if (lastForm != null) spec.onPageChanged(lastForm, previousPage, currentPage);
    }

    @Override
    public void tick() {
        if (lastForm != null) spec.tick(lastForm);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (lastForm == null) return false;
        if (lastForm.keyPressed(event)) return true;
        return spec.keyPressed(lastForm, event);
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        if (lastForm == null) return false;
        if (lastForm.keyReleased(event)) return true;
        return spec.keyReleased(lastForm, event);
    }

    @Override
    public boolean hasUnsavedChanges() {
        return lastForm != null && lastForm.hasUnsavedChanges();
    }

    @Override
    public void reload() {
        if (lastForm != null) lastForm.reload();
    }

    @Override
    public void markClean() {
        if (lastForm != null) lastForm.markClean();
    }
}
