package top.diaoyugan.enchanted_ui.api.client.gui.animation;

import java.time.Duration;
import java.util.Objects;
import java.util.function.BooleanSupplier;

/** Reversible state transition suitable for opacity, offset, scale, or rotation bindings. */
public final class UITransition {
    private final BooleanSupplier target;
    private long durationNanos = Duration.ofMillis(200).toNanos();
    private UIEasing easing = UIEasing.EASE_IN_OUT;
    private BooleanSupplier reducedMotion = () -> false;
    private float value;
    private long lastFrame = -1;
    public UITransition(BooleanSupplier target) { this.target=Objects.requireNonNull(target); }
    public static UITransition when(BooleanSupplier target) { return new UITransition(target); }
    public UITransition duration(Duration value){durationNanos=Math.max(1,value.toNanos());return this;}
    public UITransition easing(UIEasing value){easing=Objects.requireNonNull(value);return this;}
    public UITransition reducedMotion(BooleanSupplier value){reducedMotion=Objects.requireNonNull(value);return this;}
    public float value(){return value(System.nanoTime());}
    public float value(long now){
        if(lastFrame<0)lastFrame=now;
        long delta=Math.max(0,Math.min(Duration.ofMillis(250).toNanos(),now-lastFrame));lastFrame=now;
        float destination=target.getAsBoolean()?1F:0F;
        if(reducedMotion.getAsBoolean())value=destination;
        else {float step=delta/(float)durationNanos;value=destination>value?Math.min(destination,value+step):Math.max(destination,value-step);}
        return easing.apply(value);
    }
    public float between(float from,float to){return from+(to-from)*value();}
}
