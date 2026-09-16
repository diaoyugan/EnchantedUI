package top.diaoyugan.enchanted_ui.api.client.gui.state;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/** Lazily mapped binding; it does not retain listeners after their subscriptions close. */
public final class UIComputed<S, T> implements UIBinding<T> {
    private final UIBinding<S> source;
    private final Function<? super S, ? extends T> mapper;

    private UIComputed(UIBinding<S> source, Function<? super S, ? extends T> mapper) {
        this.source = Objects.requireNonNull(source, "source");
        this.mapper = Objects.requireNonNull(mapper, "mapper");
    }

    public static <S, T> UIComputed<S, T> map(UIBinding<S> source, Function<? super S, ? extends T> mapper) {
        return new UIComputed<>(source, mapper);
    }

    @Override
    public T get() {
        return mapper.apply(source.get());
    }

    @Override
    public UISubscription subscribe(Consumer<? super T> listener) {
        return source.subscribe(value -> listener.accept(mapper.apply(value)));
    }
}
