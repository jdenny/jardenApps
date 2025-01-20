package quantum;

/**
 * Created by john.denny@gmail.com on 20/01/2025.
 */
public class Clock2 {
    private double x, y;
    public Clock2(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public void addClock(Clock2 other) {
        this.x += other.x;
        this.y += other.y;
    }

    /**
     * X2 = xcosA + ysinA
     * Y2 = ycosA - xsinA
     * @param angle
     */
    public void rotateClock(double angle) {
        double cosA = Math.cos(-angle);
        double sinA = Math.sin(-angle);
        double x2 = this.x * cosA + this.y * sinA;
        double y2 = this.y * cosA - this.x * sinA;
        this.x = x2;
        this.y = y2;
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
        Clock2 clockA = new Clock2(Math.sqrt(3), 1);
        System.out.println(clockA);
        clockA.rotateClock(Math.PI / 6);
        System.out.println(clockA);
        System.out.println("goodnight!");
    }
}

