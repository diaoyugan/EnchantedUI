package top.diaoyugan.enchanted_ui.api.client.gui.state;

import java.util.function.Consumer;
import java.util.function.Function;

/** Read-only observable value used by widgets and computed state. */
public interface UIBinding<T> {
    static <T> UIBinding<T> constant(T value) {
        return new UIBinding<>() {
            @Override public T get() { return value; }
            @Override public UISubscription subscribe(Consumer<? super T> listener) { return UISubscription.EMPTY; }
        };
    }

    T get();

    UISubscription subscribe(Consumer<? super T> listener);

    default UISubscription subscribeNow(Consumer<? super T> listener) {
        listener.accept(get());
        return subscribe(listener);
    }

    default <R> UIBinding<R> map(Function<? super T, ? extends R> mapper) {
        return UIComputed.map(this, mapper);
    }
}
