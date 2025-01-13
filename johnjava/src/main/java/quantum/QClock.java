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

    private final static boolean debug = true;
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
                angle = Math.asin(handX / length); // if length is zero, angle is NaN - correct!
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
    public static double calculateTurns(double mass, double distance, double time) {
        return mass * distance * distance / (2 * h * time);
    }
    public QClock() {
        this(0, 1.0, 0);
    }
    public QClock(double angle, double length, double positionX) {
        this.angle = angle;
        this.length = length;
        this.positionX = positionX;
        convertAngleLengthToXY();
    }
    private void setXY(double x, double y) {
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
     * pos   x     x^2       rot degs, rotation rads, new angle from 0 degs, new from 0 rads
     *  0   .04   .0016      -0.576, -0.010053096491487338, 359.424, 6.2731322106880985
     * .01  .03   .0009      -0.324, -0.005654866776461627, 359.676, 6.277530440403124
     * .02  .02   .0004      -0.144, -0.0025132741228718345, 359.856, 6.280672033056715
     * .03  .01   .0001      -0.036, -0.0006283185307179586, 359.964, 6.282556988648868
     * now move to point 10 units beyond furthest:
     *  0   10.03  100.6009  -216.324, -3.775566051084212, 143.676, 2.5076192560953743
     * .01  10.02  100.4004  -144.144, -2.515787396994739, 215.856, 3.7673979101848474
     * .02  10.01  100.2001  -72.036, -1.2572653799666398, 287.964, 5.0259199272129464
     * .03  10     100       -0.0, -2.8421709430404007E-14, 360.0, 6.283185307179558
     * Note: the 100 means 100 complete rotations of the clock, leaving it where it was.
     * So the important thing: much more distribution of the second: 0 to 216 degrees,
     * compared with the first: about 0 to about half a degree.
     *
     * @param dx distance to move.
     *           In this 1-dimensional world so far, distance along x axis
     */
    public void moveClock(double dx) {
        this.positionX += dx;
        double rotationAngle = angle360 * dx * dx;
        rotateClock(rotationAngle);
        convertAngleLengthToXY();
    }
    public void moveClock2(double mass, double distanceX, double time) {
        this.positionX += distanceX;
        double turns = calculateTurns(mass, distanceX, time);
        if (debug) System.out.println("turns=" + turns);
        double rotationAngle = angle360 * turns;
        rotateClock(rotationAngle);
        convertAngleLengthToXY();
    }
    public String toStringRounded() {
        return "(" + (Math.round(Math.toDegrees(angle) * 1000.0) / 1000.0) +
                ", " + (Math.round(length * 1000.0) / 1000.0) +
                ", " + (Math.round(handX * 1000.0) / 1000.0) +
                ", " + (Math.round(handY * 1000.0) / 1000.0) +
                ", " + (Math.round(positionX * 1000.0) / 1000.0) +")";
    }
    public String toString() {
        return "(" + angle + ", " + length + ", " + positionX + ")";
    }
    public static void main(String[] args) {
        // testConvertToXY();
        // testConvertFromXY();
        // testAddClocks();
        // testRotateClock();
        // produceRotationTestResults();
        // testMoveClock();
        testCancellingClocks();
        bCoxP58();
    }
    /*
    To reduce length of clock to zero (or close, due to rounding errors):
        (distance + span)^2 = 1
    if distance is integer (whole turns) and span significantly smaller than distance:
        2 * distance * span = 1; hence span = 0.5 / distance
    Examples:
        distance  span     noOfClocks  finalLength  finalLength/noc
        10          2
        10        0.025    2           0.002777     0.00138
        10        0.0375   4           0.004385     0.00110
        10        0.04375  8           0.006704     0.000838
        10        0.05     1000        0.113244     0.000113244
        Best result: distance = 10; span = 0.024968827881711 (Math.sqrt(402) - 20) / 2
        finalLength = 7.285229008292718E-14
     */
    private static void testCancellingClocks() {
        double rootHalf = Math.sqrt(0.5); // probability if 2 clocks
        QClock qc1, qc2;
        /*
        qc1 = new QClock(angle270, rootHalf, 0);
        qc2 = new QClock(angle90, rootHalf, 0);
        System.out.println(qc1);
        System.out.println(qc2);
        qc1.addQClock(qc2);
        System.out.println(qc1);
        System.out.println();
         */
        qc1 = new QClock(0.75, rootHalf, 0);
        qc2 = new QClock(0.75, rootHalf, 0);
        qc1.moveClock(Math.sqrt(0.75));
        qc2.moveClock(Math.sqrt(0.25));
        System.out.println(qc1);
        System.out.println(qc2);
        qc1.addQClock(qc2);
        System.out.println(qc1);
    }
    /* how close to zero with just 2 clocks? Best result is
        solving x^2 + 20x - 0.5 = 0
       deltaX = (Math.sqrt(402) - 20) / 2; deltaX = 0.024968827881711
       finalLength = 7.285229008292718E-14
     */
    private static void bCoxP58() {
        System.out.println("bCoxP58()");
        double distance = 10.0;
        double deltaX = (Math.sqrt(402) - 20) / 2; //0.5 / distance; // range of uncertainty
        int numberOfClocks = 2;
        double clockGap = deltaX / (numberOfClocks - 1); // distance between clocks
        double length = Math.sqrt(1.0 / numberOfClocks);
        // assert (length * length * numberOfClocks == 1.0);
        QClock[] clocks = new QClock[numberOfClocks];
        for (int i = 0; i < numberOfClocks; i++) {
            double xpos = i * clockGap;
            clocks[i] = new QClock(0, length, xpos);
            System.out.println(clocks[i]);
            clocks[i].moveClock(distance + deltaX - xpos);
            System.out.print(clocks[i]);
            System.out.println(Math.toDegrees(clocks[i].angle));
            if (i > 0) {
                clocks[0].addQClock(clocks[i]);
                System.out.println(clocks[0]);
            }
        }
        // System.out.println(clocks[0]);
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
        QClock[] clocks = new QClock[clockCt];
        for (int i = 0; i < clockCt; i++) {
            double xpos = i * clockGap;
            clocks[i] = new QClock(0, length, xpos);
            if (debug) System.out.println(clocks[i]);
            clocks[i].moveClock2(mass, distanceX + deltaX - xpos, time);
            if (debug) {
                System.out.print(clocks[i]);
                System.out.println(Math.toDegrees(clocks[i].angle));
            }
            if (i > 0) {
                clocks[0].addQClock(clocks[i]);
                if (debug) System.out.println(clocks[0]);
            }
        }
        return "result=" + clocks[0];
    }
    private static void testRotateClock() {
        for (int i = 0; i < 8; i++) {
            QClock qc = new QClock(0, 1, 0);
            qc.rotateClock(angle60 * i);
            System.out.println(qc);
        }
    }
    private static double[] rotationTestData = {
            0.04, 0.03, 0.02, 0.01,
            10.03, 10.02, 10.01, 10
    };
    private static void testMoveClock() {
        for (int i = 0; i <= 5; i++) {
            QClock qc = new QClock(0, 1, 0);
            qc.moveClock(0.4 * i);
            System.out.println(qc);
        }
        System.out.println();
        for (int i = 0; i < rotationTestData.length; i++) {
            QClock qc = new QClock(0, 1, 0);
            qc.moveClock(rotationTestData[i]);
            System.out.println(qc + ", angle(Rads)=" + qc.angle);
        }
    }
    private static void produceRotationTestResults() {
        System.out.println("produceRotationTestResults");
        System.out.println(
                "i, rot degs, rotation rads, new from 0 degs, new from 0 rads");
        for (int i = 0; i < rotationTestData.length; i++) {
            double x = rotationTestData[i];
            x *= x;
            double rotDegs = - ((x * 360.0) % 360.0);
            double angleDegs = 360 + rotDegs;
            double rotRads = - ((x * angle360) % angle360);
            double angleRads = angle360 + rotRads;
            System.out.println("i=" + i + ", " + rotDegs + ", " +
                    rotRads + ", " + angleDegs + ", " + angleRads);
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
                QClock qc = new QClock();
                qc.setXY(1 - 0.5 * i, 1 - 0.5 * j);
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
