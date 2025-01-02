package quantum;

/**
 * Quantum Clock.
 * Created by john.denny@gmail.com on 28/12/2024.
 */
public class QClock {
    private double angle270 = Math.PI * 1.5; // 4.7124
    private double angle360 = Math.PI * 2; // 6.2832
    private double angle90 = Math.PI * 0.5; // 1.5708



    /**
     * C^2 = A^2 + B^2 - 2AB cos(c)
     */
    public static double calculateLength(QClock cA, QClock cB) {
        return Math.sqrt(cA.length * cA.length + cB.length * cB.length -
                2 * cA.length * cB.length * Math.cos(cA.angle + Math.PI - cB.angle));
    }
    /**
     * c = acos((A^2 + B^2 - C^2)/2AB)
     */
    public static double calculateAngle(double a, double b, double c) {
        return Math.acos((a*a + b*b - c*c)/(2 * a * b));
    }
    private void convertAngleLengthToXY() {
        if (angle >= angle270) {
            x = - length * Math.cos(angle - angle270);
            y = length * Math.sin(angle - angle270);
        } else if (angle >= Math.PI) {
            x = - length * Math.sin(angle - Math.PI);
            y = - length * Math.cos(angle - Math.PI);
        } else if (angle >= angle90) {
            x = length * Math.cos(angle - angle90);
            y = - length * Math.sin(angle - angle90);
        } else {
            x = length * Math.sin(angle);
            y = length * Math.cos(angle);
        }
    }
    private void convertXYtoAngleLength() {
        length = Math.sqrt(x * x + y * y);
        if (y < 0.0) {
            if (x < 0.0) {
                angle = Math.PI + Math.asin((-x) / length);
            } else {
                angle = angle90 + Math.acos(x / length);
            }
        } else {
            if (x < 0.0) {
                angle = angle270 + Math.acos((-x) / length);
            } else {
                angle = Math.asin(x / length);
            }
        }
    }
    private double angle;
    private double length;
    private double x, y;

    public QClock(double angle, double length) {
        this.angle = angle;
        this.length = length;
        convertAngleLengthToXY();
    }
    public QClock(double x, double y, boolean xy) {
        this.x = x;
        this.y = y;
        convertXYtoAngleLength();
    }

    public void addQClock(QClock other) {
        x += other.x;
        y += other.y;
        convertXYtoAngleLength();
    }
    public void rotateClock(double angle) {
        this.angle += angle;
        if (this.angle >= angle360) {
            this.angle = angle % angle360;
        }
    }
    public String toStringFull() {
        return "(" + angle + ", " + length + ")";
    }
    public String toString() {
        return "(" + (Math.round(Math.toDegrees(angle) * 1000.0) / 1000.0) +
                ", " + (Math.round(length * 1000.0) / 1000.0) +
                ", " + (Math.round(x * 1000.0) / 1000.0) + ", " +
                (Math.round(y * 1000.0) / 1000.0) + ")";
    }
    public static void main(String[] args) {
        /*
        double angle45 = Math.PI * 0.25; // 0.7854

        double angle135 = Math.PI * 0.75; // 2.356
        double angle180 = Math.PI; // 3.1416
        double angle225 = Math.PI * 1.25; // 3.927
         */
        testConvertToXY();
        testConvertFromXY();
        testAddClocks();
    }
    private static void testConvertToXY() {
        System.out.println("testConvertToXY");
        double angle15 = Math.PI / 12; // 1.0472
        for (int i = 0; i <= 24; i++) {
            QClock qc = new QClock(angle15 * i, 1);
            System.out.print(qc);
            System.out.println("; length=" + Math.sqrt(qc.x * qc.x + qc.y * qc.y));
        }
    }
    private static void testConvertFromXY() {
        System.out.println("testConvertFromXY");
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                QClock qc = new QClock(1 - 0.5 * i, 1 - 0.5 * j, true);
                System.out.println(qc);
            }
        }
    }

    private static void testAddClocks() {
        System.out.println("testAddClocks");
        double increment = Math.PI / 6.0;
        double length = 1.0; // vary this later!
        for (int b=0; b<=12; b++) {
            QClock qca = new QClock(0.0, length);
            QClock qcb = new QClock(increment * b, length);
            System.out.print(qca + ", " + qcb + ", ");
            qca.addQClock(qcb);
            System.out.println(qca);
        }
    }
    private double[][] addClockData = {
            {0, 1, 0, 1, 0, 2},
            {0, 1, 30, 1, 15, 1.932}
    };
    private static void testAddClocks2() {
        double increment = Math.PI / 3.0;
        double length = 1.0; // vary this later!
        for (int a=1; a<=6; a++) {
            for (int b=1; b<=6; b++) {
                QClock qca = new QClock(increment * a, length);
                QClock qcb = new QClock(increment * b, length);
                System.out.print(qca + ", " + qcb + ", ");
                qca.addQClock(qcb);
                System.out.println(qca);
            }
            System.out.println();
        }
    }
}
