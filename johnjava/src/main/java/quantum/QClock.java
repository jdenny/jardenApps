package quantum;

/**
 * Created by john.denny@gmail.com on 21/01/2025.
 */
public abstract class QClock {
    public final static double h = 6.62607015 * 10e-34; // m^2 kg / s
    public final static boolean debug = true;
    public abstract void addClock(QClock other);
    public abstract void rotateClock(double angle);
    public abstract void moveClock(double mass, double distanceX, double time);
    /**
     *
     * @param mass kg
     * @param distance m
     * @param time s
     * @return
     */
    public static double calculateTurns(double mass, double distance, double time) {
        return mass * distance * distance / (2 * h * time);
    }

}
