package top.diaoyugan.enchanted_ui.client.gui.builder;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Internal
public final class FormStateController {
    private final List<Runnable> savers = new ArrayList<>();
    private final List<BooleanSupplier> validators = new ArrayList<>();
    private final List<BooleanSupplier> dirtyTrackers = new ArrayList<>();
    private final List<Runnable> resetters = new ArrayList<>();
    private final List<Runnable> cleanMarkers = new ArrayList<>();

    public FormStateController() {}

    public boolean validate() {
        boolean valid = true;
        for (BooleanSupplier validator : validators) {
            valid &= validator.getAsBoolean();
        }
        return valid;
    }

    public boolean runSavers() {
        if (!validate()) {
            return false;
        }
        for (Runnable saver : savers) {
            saver.run();
        }
        return true;
    }

    public boolean save() {
        if (!runSavers()) {
            return false;
        }
        markClean();
        return true;
    }

    public boolean hasUnsavedChanges() {
        for (BooleanSupplier tracker : dirtyTrackers) {
            if (tracker.getAsBoolean()) {
                return true;
            }
        }
        return false;
    }

    public void reload() {
        for (Runnable resetter : resetters) {
            resetter.run();
        }
    }

    public void markClean() {
        for (Runnable marker : cleanMarkers) {
            marker.run();
        }
    }

    public void addValidator(BooleanSupplier validator) {
        validators.add(validator);
    }

    public void addSaver(Runnable saver) {
        savers.add(saver);
    }

    public <T> void trackModelValue(
            Supplier<T> currentValue,
            Consumer<T> resetAction,
            Function<T, T> copy,
            @Nullable Runnable afterReset
    ) {
        Snapshot<T> snapshot = new Snapshot<>(copy.apply(currentValue.get()));
        dirtyTrackers.add(() -> !Objects.equals(snapshot.value, copy.apply(currentValue.get())));
        resetters.add(() -> {
            resetAction.accept(copy.apply(snapshot.value));
            if (afterReset != null) {
                afterReset.run();
            }
        });
        cleanMarkers.add(() -> snapshot.value = copy.apply(currentValue.get()));
    }

    public <T> void trackWidgetValue(
            Supplier<T> widgetValue,
            Consumer<T> resetAction,
            Function<T, T> copy
    ) {
        Snapshot<T> snapshot = new Snapshot<>(copy.apply(widgetValue.get()));
        dirtyTrackers.add(() -> !Objects.equals(snapshot.value, copy.apply(widgetValue.get())));
        resetters.add(() -> resetAction.accept(copy.apply(snapshot.value)));
        cleanMarkers.add(() -> snapshot.value = copy.apply(widgetValue.get()));
    }

    private static final class Snapshot<T> {
        private T value;

        private Snapshot(T value) {
            this.value = value;
        }
    }
}
