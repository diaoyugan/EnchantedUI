package top.diaoyugan.enchanted_ui.api.client.gui.state;

/** A detachable UI listener. Subscriptions are idempotent and intended to be closed with a screen. */
@FunctionalInterface
public interface UISubscription extends AutoCloseable {
    UISubscription EMPTY = () -> {};

    @Override
    void close();
}
