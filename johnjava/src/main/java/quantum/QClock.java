package quantum;

/**
 * Created by john.denny@gmail.com on 28/12/2024.
 */
public class QClock {
    /**
     * C^2 = A^2 + B^2 - 2AB cos(c)
     */
    public static double calculateLength(QClock cA, QClock cB) {
        return Math.sqrt(cA.length * cA.length + cB.length * cB.length -
                2 * cA.length * cB.length * Math.cos(cA.angle + cB.angle));
    }
    /**
     * c = acos((A^2 + B^2 - C^2)/2AB)
     */
    public static double calculateAngle(double a, double b, double c) {
        return Math.acos((a*a + b*b - c*c)/(2 * a * b));
    }
    private double angle;
    private double length;

    public QClock(double angle, double length) {
        this.angle = angle;
        this.length = length;
    }
    public void addQClock(QClock other) {
        double newLength = calculateLength(this, other);
        double newAngle = calculateAngle(other.length, newLength, this.length);
        this.length = newLength;
        this.angle = newAngle;
    }
    public String toString() {
        return "length=" + length + "; angle=" + angle;
    }
    public static void main(String[] args) {
        double angle45 = Math.PI * 0.25; // 0.7854
        double angle60 = Math.PI / 3; // 1.0472
        double angle90 = Math.PI * 0.5; // 1.5708
        double angle135 = Math.PI * 0.75; // 2.356
        double angle180 = Math.PI; // 3.1416
        double angle225 = Math.PI * 1.25; // 3.927
        double angle270 = Math.PI * 1.5; // 4.7124

        double angle = calculateAngle(1.0, Math.sqrt(2.0), 1.0);
        System.out.println("angle=" + angle + "; degrees=" + Math.toDegrees(angle));
        System.out.println("cos(0)=" + Math.cos(0)); // should be 1
        System.out.println("cos(45)=" + Math.cos(angle45)); // should be 1/sqrt(2); 0.707ish
        System.out.println("cos(60)=" + Math.cos(angle60)); // should be 1/2
        System.out.println("acos(0)=" + Math.acos(0)); // should be PI/2
        System.out.println("acos(1/sqrt(2))=" + Math.acos(1/Math.sqrt(2.0))); // should be 45
        System.out.println("acos(1/2)=" + Math.acos(0.5)); // should be 60
        QClock cA = new QClock(0, 1);
        QClock cB = new QClock(angle90, 1);
        double length = calculateLength(cA, cB);
        System.out.println("length=" + length);
        cA.addQClock(cB);
        System.out.println("cA=" + cA);

    }
}
