package top.diaoyugan.enchanted_ui.api.client.gui.animation;

import java.time.Duration;
import java.util.Objects;
import java.util.function.BooleanSupplier;

/** Frame-time animation clock that preserves progress while paused. */
public final class UIAnimation {
    public enum Mode { ONCE, LOOP, PING_PONG }

    private Mode mode;
    private long durationNanos = Duration.ofMillis(300).toNanos();
    private UIEasing easing = UIEasing.LINEAR;
    private BooleanSupplier running = () -> true;
    private BooleanSupplier reducedMotion = () -> false;
    private long elapsedNanos;
    private long lastFrameNanos = -1;

    private UIAnimation(Mode mode) { this.mode = mode; }

    public static UIAnimation once() { return new UIAnimation(Mode.ONCE); }
    public static UIAnimation loop() { return new UIAnimation(Mode.LOOP); }
    public static UIAnimation pingPong() { return new UIAnimation(Mode.PING_PONG); }

    public UIAnimation duration(Duration duration) {
        durationNanos = Math.max(1, Objects.requireNonNull(duration, "duration").toNanos());
        return this;
    }

    public UIAnimation easing(UIEasing easing) { this.easing = Objects.requireNonNull(easing); return this; }
    public UIAnimation running(BooleanSupplier running) { this.running = Objects.requireNonNull(running); return this; }
    public UIAnimation reducedMotion(BooleanSupplier reducedMotion) { this.reducedMotion = Objects.requireNonNull(reducedMotion); return this; }

    public float value() { return value(System.nanoTime()); }

    public float value(long frameNanos) {
        if (lastFrameNanos < 0) lastFrameNanos = frameNanos;
        long delta = Math.max(0, Math.min(Duration.ofMillis(250).toNanos(), frameNanos - lastFrameNanos));
        lastFrameNanos = frameNanos;
        if (running.getAsBoolean() && !reducedMotion.getAsBoolean()) elapsedNanos += delta;
        double cycles = elapsedNanos / (double) durationNanos;
        float raw = switch (mode) {
            case ONCE -> (float) Math.min(1.0, cycles);
            case LOOP -> (float) (cycles - Math.floor(cycles));
            case PING_PONG -> {
                double phase = cycles - Math.floor(cycles);
                yield (float) (((long) Math.floor(cycles) & 1L) == 0L ? phase : 1.0 - phase);
            }
        };
        return easing.apply(raw);
    }

    public float degrees() { return value() * 360.0F; }
    public float between(float from, float to) { return from + (to - from) * value(); }
    public void reset() { elapsedNanos = 0; lastFrameNanos = -1; }
}
