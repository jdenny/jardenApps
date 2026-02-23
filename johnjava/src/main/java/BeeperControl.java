/**
 * Created by john.denny@gmail.com on 23/02/2026.
 */

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;
class BeeperControl {
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public void beepForNSeconds(int n) {
        final Runnable beeper = new Runnable() {
            public void run() { System.out.println("beep"); }
        };
        final ScheduledFuture<?> beeperHandle =
                scheduler.scheduleAtFixedRate(beeper, 1, 1, SECONDS);
        scheduler.schedule(new Runnable() {
            public void run() { beeperHandle.cancel(true); }
        }, n, SECONDS);
    }
    public static void main(String[] args) {
        new BeeperControl().beepForNSeconds(5);
    }
}
