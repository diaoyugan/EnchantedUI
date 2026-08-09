package top.diaoyugan.enchanted_ui.client.gui.screen.base;

import top.diaoyugan.enchanted_ui.api.client.gui.UIUnsavedChangesPrompt;

/** Coordinates guarded screen closure without owning screen lifecycle details. */
final class CloseCoordinator {
    private UIUnsavedChangesPrompt prompt = UIUnsavedChangesPrompt.defaults();
    private boolean confirmed;

    void prompt(UIUnsavedChangesPrompt prompt) {
        this.prompt = prompt;
    }

    UIUnsavedChangesPrompt prompt() {
        return prompt;
    }

    void request(BaseTabbedScreen screen) {
        if (confirmed) {
            screen.forceClose();
            return;
        }
        if (screen.hasOpenModal()) {
            return;
        }
        if (!screen.hasUnsavedChanges()) {
            screen.forceClose();
            return;
        }
        screen.showDialog(
                prompt.title(),
                prompt.lines(),
                new BaseTabbedScreen.DialogAction(prompt.discardLabel(), () -> {
                    confirmed = true;
                    screen.forceClose();
                }, true),
                new BaseTabbedScreen.DialogAction(prompt.cancelLabel(), () -> {
                }, true)
        );
    }

    void reset() {
        confirmed = false;
    }
}
