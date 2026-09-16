package top.diaoyugan.enchanted_ui.api.client.gui.time;

import java.time.Duration;
import java.util.Objects;
import java.util.function.LongSupplier;

/** Frame-time, tick-independent timer supporting count-up and count-down operation. */
public final class UITimer {
    private final UITimerMode mode;
    private final Duration duration;
    private final LongSupplier clock;
    private long elapsedNanos;
    private long lastNanos=-1;
    private boolean running;

    private UITimer(UITimerMode mode,Duration duration,LongSupplier clock){
        this.mode=Objects.requireNonNull(mode);this.duration=nonNegative(duration);this.clock=Objects.requireNonNull(clock);
    }
    public static UITimer countUp(){return new UITimer(UITimerMode.COUNT_UP,Duration.ZERO,System::nanoTime);}
    public static UITimer countUp(Duration limit){return new UITimer(UITimerMode.COUNT_UP,limit,System::nanoTime);}
    public static UITimer countDown(Duration duration){return new UITimer(UITimerMode.COUNT_DOWN,duration,System::nanoTime);}
    static UITimer withClock(UITimerMode mode,Duration duration,LongSupplier clock){return new UITimer(mode,duration,clock);}

    public UITimer start(){if(!running){running=true;lastNanos=clock.getAsLong();}return this;}
    public UITimer pause(){advance();running=false;lastNanos=-1;return this;}
    public UITimer toggle(){return running?pause():start();}
    public UITimer reset(){elapsedNanos=0;lastNanos=running?clock.getAsLong():-1;return this;}
    public UITimer restart(){reset();return start();}
    public UITimer seek(Duration elapsed){elapsedNanos=clampElapsed(nonNegative(elapsed).toNanos());lastNanos=running?clock.getAsLong():-1;return this;}
    public UITimer add(Duration amount){advance();elapsedNanos=clampElapsed(elapsedNanos+amount.toNanos());return this;}
    public UITimerMode mode(){return mode;}
    public Duration duration(){return duration;}
    public boolean running(){advance();return running;}
    public boolean finished(){advance();return (mode==UITimerMode.COUNT_DOWN&&duration.isZero())||(!duration.isZero()&&elapsedNanos>=duration.toNanos());}
    public Duration elapsed(){advance();return Duration.ofNanos(elapsedNanos);}
    public Duration remaining(){advance();return duration.isZero()?Duration.ZERO:Duration.ofNanos(Math.max(0,duration.toNanos()-elapsedNanos));}
    public Duration value(){return mode==UITimerMode.COUNT_DOWN?remaining():elapsed();}
    public double progress(){advance();return duration.isZero()?0.0D:Math.max(0.0D,Math.min(1.0D,elapsedNanos/(double)duration.toNanos()));}

    private void advance(){
        if(!running)return;long now=clock.getAsLong();if(lastNanos>=0)elapsedNanos=clampElapsed(elapsedNanos+Math.max(0,now-lastNanos));lastNanos=now;
        if(!duration.isZero()&&elapsedNanos>=duration.toNanos()){elapsedNanos=duration.toNanos();running=false;lastNanos=-1;}
    }
    private long clampElapsed(long value){long nonNegative=Math.max(0,value);return duration.isZero()?nonNegative:Math.min(duration.toNanos(),nonNegative);}
    private static Duration nonNegative(Duration value){Objects.requireNonNull(value);return value.isNegative()?Duration.ZERO:value;}
}
