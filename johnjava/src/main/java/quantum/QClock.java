package quantum;

/**
 * Quantum Clock.
 * Created by john.denny@gmail.com on 28/12/2024.
 * length^2 is probability of finding a particle at the position of the clock.
 * Clock angles: hour times 30 degrees
 */
public class QClock {
    private final static double angle60 = Math.PI / 3; // 1.0472
    private final static double angle90 = Math.PI * 0.5; // 1.5708
    private final static double angle270 = Math.PI * 1.5; // 4.7124
    private final static double angle360 = Math.PI * 2; // 6.2832
    private final static double h = 6.62607015 * 10e-34; // m^2 kg / s

    private double angle;
    private double length;
    private double handX, handY; // position of clock hand
    /**
     * Position of the clock in 3-dimensional space.
     * Initially, we will use only positionX, i.e. a one-dimensional world.
     */
    private double positionX /*, positionY, positionZ*/;

    /**
     * C^2 = A^2 + B^2 - 2AB cos(c)
     * The following 2 methods not used as replaced by xy coordinates.
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
            handX = - length * Math.cos(angle - angle270);
            handY = length * Math.sin(angle - angle270);
        } else if (angle >= Math.PI) {
            handX = - length * Math.sin(angle - Math.PI);
            handY = - length * Math.cos(angle - Math.PI);
        } else if (angle >= angle90) {
            handX = length * Math.cos(angle - angle90);
            handY = - length * Math.sin(angle - angle90);
        } else {
            handX = length * Math.sin(angle);
            handY = length * Math.cos(angle);
        }
    }
    private void convertXYtoAngleLength() {
        length = Math.sqrt(handX * handX + handY * handY);
        if (handY < 0.0) {
            if (handX < 0.0) {
                angle = Math.PI + Math.asin((-handX) / length);
            } else {
                angle = angle90 + Math.acos(handX / length);
            }
        } else {
            if (handX < 0.0) {
                angle = angle270 + Math.acos((-handX) / length);
            } else {
                angle = Math.asin(handX / length);
            }
        }
    }

    /**
     *
     * @param mass kg
     * @param distance m
     * @param time s
     * @return
     */
    public static double calculateWindAngle(double mass, double distance, double time) {
        return mass * distance * distance / 2 * h * time;
    }

    public QClock(double angle, double length, double positionX) {
        this.angle = angle;
        this.length = length;
        this.positionX = positionX;
        convertAngleLengthToXY();
    }
    public QClock(double x, double y, boolean xy) {
        this.handX = x;
        this.handY = y;
        convertXYtoAngleLength();
    }

    public void addQClock(QClock other) {
        handX += other.handX;
        handY += other.handY;
        convertXYtoAngleLength();
    }
    private void rotateClock(double ang) {
        this.angle -= ang;
        this.angle %= angle360;
        if (this.angle < 0) {
            this.angle = angle360 + this.angle;
        }
    }

    /**
     *
     * If lots of clocks are separated by small amounts all move to a point nearby, they
     * will not tend to cancel out; whereas if they move to a point far away, they will tend
     * to cancel out. E.g. separated by 0.01 units, move to 0.01 unit beyond furthest:
     * pos   x    x^2   rotation (x^2 * 360 % 360) degrees
     *  0   .04  .0016  .576
     * .01  .03  .0009  .324
     * .02  .02  .0004  .144
     * .03  .01  .0001  .036
     * now move to point 10 units beyond furthest:
     * pos   x     x^2       rotation (x^2 * 360 % 360)
     *  0   10.03  100.6009  216.324
     * .01  10.02  100.4004  144.144
     * .02  10.01  100.2001  72.036
     * .03  10     100       0
     * Note: the 100 means 100 complete rotations of the clock, leaving it where it was.
     * So the important thing: much more distribution of the second: 0 to 216 degrees,
     * compared with the first: about 0 to half a degree.
     *
     * @param x
     */
    public void moveClock(double x) {
        this.positionX += x;
        double rotationAngle = angle360 * x * x;
        rotateClock(rotationAngle);
        convertAngleLengthToXY();
    }
    public String toString() {
        return "(" + (Math.round(Math.toDegrees(angle) * 1000.0) / 1000.0) +
                ", " + (Math.round(length * 1000.0) / 1000.0) +
                ", " + (Math.round(handX * 1000.0) / 1000.0) +
                ", " + (Math.round(handY * 1000.0) / 1000.0) +
                ", " + (Math.round(positionX * 1000.0) / 1000.0) +")";
    }
    public static void main(String[] args) {
        double[] data = {
                0.0016, 0.0009, 0.0004, 0.0001,
                100.6009, 100.4004, 100.2001, 100.0
        };
        for (int i = 0; i < data.length; i++) {
            System.out.println("i=" + i + ", " +
                    ((data[i] * 360) % 360));
        }
        /*
        testConvertToXY();
        testConvertFromXY();
        testAddClocks();
        testRotateClock();
        testMoveClock();
         */
        // bCoxP58();
    }

    private static void testRotateClock() {
        for (int i = 0; i < 8; i++) {
            QClock qc = new QClock(0, 1, 0);
            qc.rotateClock(angle60 * i);
            System.out.println(qc);
        }
    }
    private static void testMoveClock() {
        for (int i = 0; i <= 5; i++) {
            QClock qc = new QClock(0, 1, 0);
            qc.moveClock(0.4 * i);
            System.out.println(qc);
        }
    }

    private static void bCoxP58() {
        int numberOfClocks = 12;
        double deltaX = 0.2 / (numberOfClocks - 1);
        double length = Math.sqrt(1.0 / numberOfClocks);
        // assert (length * length * numberOfClocks == 1.0);
        QClock[] clocks = new QClock[numberOfClocks];
        for (int i = 0; i < numberOfClocks; i++) {
            double xpos = i * deltaX;
            clocks[i] = new QClock(0, length, xpos);
            System.out.print(clocks[i] + "\t");
            clocks[i].moveClock(10.2 - xpos);
            System.out.print(clocks[i] + "\t");
            if (i > 0) {
                clocks[0].addQClock(clocks[i]);
            }
            System.out.println(clocks[0]);
        }
    }

    private static void testConvertToXY() {
        System.out.println("testConvertToXY");
        double angle15 = Math.PI / 12; // 1.0472
        for (int i = 0; i <= 24; i++) {
            QClock qc = new QClock(angle15 * i, 1, 0);
            System.out.print(qc);
            System.out.println("; length=" + Math.sqrt(qc.handX * qc.handX + qc.handY * qc.handY));
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

    // Sample data:
    /*
        double angle45 = Math.PI * 0.25; // 0.7854
        double angle135 = Math.PI * 0.75; // 2.356
        double angle180 = Math.PI; // 3.1416
        double angle225 = Math.PI * 1.25; // 3.927
    270,0 + 0,1 = ?,1
     */
    private static void testAddClocks() {
        System.out.println("testAddClocks");
        double increment = Math.PI / 6.0;
        double length = 1.0; // vary this later!
        for (int b=0; b<=12; b++) {
            QClock qca = new QClock(0.0, length, 0);
            QClock qcb = new QClock(increment * b, length, 0);
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
                QClock qca = new QClock(increment * a, length, 0);
                QClock qcb = new QClock(increment * b, length, 0);
                System.out.print(qca + ", " + qcb + ", ");
                qca.addQClock(qcb);
                System.out.println(qca);
            }
            System.out.println();
        }
    }
}
