package top.diaoyugan.enchanted_ui.api.client.gui.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/** Small synchronous observable state holder. */
public final class UIState<T> implements UIBinding<T> {
    private T value;
    private final List<Consumer<? super T>> listeners = new ArrayList<>();

    private UIState(T value) {
        this.value = value;
    }

    public static <T> UIState<T> of(T value) {
        return new UIState<>(value);
    }

    @Override
    public T get() {
        return value;
    }

    public void set(T value) {
        if (Objects.equals(this.value, value)) return;
        this.value = value;
        for (Consumer<? super T> listener : List.copyOf(listeners)) listener.accept(value);
    }

    public void update(java.util.function.UnaryOperator<T> update) {
        set(update.apply(value));
    }

    @Override
    public UISubscription subscribe(Consumer<? super T> listener) {
        Objects.requireNonNull(listener, "listener");
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }
}
