package quantum;

/**
 * Created by john.denny@gmail.com on 20/01/2025.
 */
public class QClockXY extends QClock {
    private double x, y;
    private double locationX; // locationY, locationZ
    public QClockXY(double x, double y, double locationX) {
        this.x = x;
        this.y = y;
        this.locationX = locationX;
    }
    @Override
    public void addClock(QClock other) {
        if (other instanceof QClockXY) {
            QClockXY otherXY = (QClockXY)other;
            this.x += otherXY.x;
            this.y += otherXY.y;
        }
    }
    /**
     * X2 = xcosA + ysinA
     * Y2 = ycosA - xsinA
     * @param angle
     */
    @Override
    public void rotateClock(double angle) {
        double cosA = Math.cos(-angle);
        double sinA = Math.sin(-angle);
        double x2 = this.x * cosA + this.y * sinA;
        double y2 = this.y * cosA - this.x * sinA;
        this.x = x2;
        this.y = y2;
    }

    @Override
    public void moveClock(double mass, double distanceX, double time) {
        this.locationX += distanceX;
        double turns = calculateTurns(mass, distanceX, time);
        if (debug) System.out.println("turns=" + turns);
        double rotationAngle = Math.PI * 2 * turns;
        rotateClock(rotationAngle);
        //!! convertARToXY();
    }

    public String toString() {
        double degrees = Math.toDegrees(Math.atan(x / y));
        return "(" + x + ", " + y + ", r=" + Math.sqrt(x*x + y*y) +
                ", degrees=" + degrees + ")";
    }

    public static void main(String[] args) {
        testAngularRotation();
    }
    private static void testAngularRotation() {
        QClockXY clockA = new QClockXY(Math.sqrt(3), 1, 0);
        System.out.println(clockA);
        clockA.rotateClock(Math.PI / 6);
        System.out.println(clockA);
        System.out.println("goodnight!");
    }
    /**
     *
     * @param mass
     * @param distanceX
     * @param time
     * @param clockCt - number of clocks
     * @param deltaX - range of uncertainty
     * @return
     */
    public static String moveClocks(double mass, double distanceX, double time,
                                    int clockCt, double deltaX) {
        double clockGap;
        if (debug) System.out.println("moveClocks(...)");
        if (clockCt == 1) {
            clockGap = 0.0;
        } else {
            clockGap = deltaX / (clockCt - 1); // distance between clocks
        }
        double length = Math.sqrt(1.0 / clockCt);
        // assert (length * length * numberOfClocks == 1.0);
        QClock[] clocks = new QClockXY[clockCt];
        for (int i = 0; i < clockCt; i++) {
            double xpos = i * clockGap;
            clocks[i] = new QClockXY(0, length, xpos);
            if (debug) System.out.println(clocks[i]);
            clocks[i].moveClock(mass, distanceX + deltaX - xpos, time);
            if (debug) {
                System.out.println(clocks[i]);
            }
            if (i > 0) {
                clocks[0].addClock(clocks[i]);
                if (debug) System.out.println(clocks[0]);
            }
        }
        return "result=" + clocks[0];
    }
}

