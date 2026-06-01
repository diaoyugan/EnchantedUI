package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.KeyEvent;
import top.diaoyugan.enchanted_ui.client.gui.builder.UI;

import java.util.List;

public final class UIFormPage implements UIPage {

    private final UI.FormPage delegate;

    private static UI.FormSpec adapt(UIFormSpec spec) {
        return new UI.FormSpec() {
            private UIForm lastForm;

            private UIForm wrap(UI.Form form) {
                if (lastForm == null || lastForm.delegate() != form) {
                    lastForm = new UIForm(form);
                }
                return lastForm;
            }

            @Override
            public void build(UI.Form form) {
                spec.build(wrap(form));
            }

            @Override
            public void onOpen(UI.Form form) {
                spec.onOpen(wrap(form));
            }

            @Override
            public void onClose(UI.Form form) {
                spec.onClose(wrap(form));
            }

            @Override
            public void onShow(UI.Form form) {
                spec.onShow(wrap(form));
            }

            @Override
            public void onHide(UI.Form form) {
                spec.onHide(wrap(form));
            }

            @Override
            public void onPageChanged(UI.Form form, int previousPage, int currentPage) {
                spec.onPageChanged(wrap(form), previousPage, currentPage);
            }

            @Override
            public void onSave(UI.Form form) {
                spec.onSave(wrap(form));
            }

            @Override
            public void tick(UI.Form form) {
                spec.tick(wrap(form));
            }

            @Override
            public boolean keyPressed(UI.Form form, KeyEvent event) {
                return spec.keyPressed(wrap(form), event);
            }
        };
    }

    public UIFormPage(int contentWidth, UIFormSpec spec) {
        this(contentWidth, 10, 4, spec);
    }

    public UIFormPage(int contentWidth, int startY, int gap, UIFormSpec spec) {
        this.delegate = new UI.FormPage(contentWidth, startY, gap, adapt(spec));
    }

    @Override
    public List<AbstractWidget> build(UIBuildContext ctx) {
        return delegate.build(ctx.delegate());
    }

    @Override
    public boolean onSave() {
        return delegate.onSave();
    }

    @Override
    public void onOpen() {
        delegate.onOpen();
    }

    @Override
    public void onClose() {
        delegate.onClose();
    }

    @Override
    public void onShow() {
        delegate.onShow();
    }

    @Override
    public void onHide() {
        delegate.onHide();
    }

    @Override
    public void onPageChanged(int previousPage, int currentPage) {
        delegate.onPageChanged(previousPage, currentPage);
    }

    @Override
    public void tick() {
        delegate.tick();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        return delegate.keyPressed(event);
    }

    @Override
    public boolean hasUnsavedChanges() {
        return delegate.hasUnsavedChanges();
    }

    @Override
    public void reload() {
        delegate.reload();
    }

    @Override
    public void markClean() {
        delegate.markClean();
    }
}
