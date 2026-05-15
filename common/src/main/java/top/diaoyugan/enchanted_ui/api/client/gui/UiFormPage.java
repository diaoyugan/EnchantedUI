package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.KeyEvent;
import top.diaoyugan.enchanted_ui.client.gui.builder.UI;

import java.util.List;

public final class UiFormPage implements UiPage {

    private final UI.FormPage delegate;

    private static UI.FormSpec adapt(UiFormSpec spec) {
        return new UI.FormSpec() {
            private UiForm lastForm;

            private UiForm wrap(UI.Form form) {
                if (lastForm == null || lastForm.delegate() != form) {
                    lastForm = new UiForm(form);
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

    public UiFormPage(int contentWidth, UiFormSpec spec) {
        this(contentWidth, 10, 4, spec);
    }

    public UiFormPage(int contentWidth, int startY, int gap, UiFormSpec spec) {
        this.delegate = new UI.FormPage(contentWidth, startY, gap, adapt(spec));
    }

    UI.Page delegate() {
        return delegate;
    }

    @Override
    public List<AbstractWidget> build(UiBuildContext ctx) {
        return delegate.build(ctx.delegate());
    }

    @Override
    public void onSave() {
        delegate.onSave();
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
}
