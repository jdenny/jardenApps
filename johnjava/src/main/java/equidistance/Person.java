package equidistance;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by john.denny@gmail.com on 30/11/2023.
 * Equidistance game. Everyone in the group randomly chooses two people, and on the word 'go'
 * each one moves so that he or she is equidistant from his chosen two who in turn - frustratingly -
 * will have also moved.
 */
public class Person {
    private final static boolean verbose = false;
    private final static double discrepancyTolerance = 0.02f;
    private final int number;
    private int x = -1, y = -1;
    private Person boda, bodb;
    private Group group;
    private int gridWidth, gridHeight;
    private final Random random = new Random();
    private double discrepancy = -1;

    public Person(int number) {
        this.number = number;
    }
    public String toString() {
        double distanceA = getDistance(boda);
        double distanceB = getDistance(bodb);
        double discrepancy = getDiscrepancy();
        StringBuilder builder = new StringBuilder(
                "Person " + number + " (" + x + ", " + y + ") chosen Persons(" +
                        boda.number + ", " + bodb.number + "); discrepancy: " +
                        String.format("%01.3f",discrepancy));
        if (verbose) {
            builder.append(" distances: (" + String.format("%01.3f",distanceA) +
                ", " + String.format("%01.3f",distanceA));
        }
        return builder.toString();
    }
    public void setGroup(Group group) {
        this.group = group;
        gridWidth = group.getGridWidth();
        gridHeight = group.getGridHeight();
        this.x = random.nextInt(gridWidth);
        this.y = random.nextInt(gridHeight);
        if (this.isPositionTaken()) {
            Point point = getNextFreePosition(this.x, this.y);
            if (point == null) {
                System.out.println("cannot find a free position for Person" + this.number);
                System.exit(1);
            } else {
                this.x = point.x;
                this.y = point.y;
            }
        }
        this.chooseTwo();
    }
    // starting from current (x, y), find next free position.
    // Return null if no free positions - which should only happen if there are too many
    // Persons for the size of the grid.
    private Point getNextFreePosition(int currentX, int currentY) {
        int nextX = currentX;
        int nextY = currentY;
        do {
            nextX++;
            if (nextX >= gridWidth) {
                nextX = 0;
                nextY++;
                if (nextY >= gridHeight) {
                    nextY = 0;
                }
            }
            if (nextX == this.x && nextY == this.y) {
                if (verbose) System.out.println("no free positions found!");
                return null;
            }
        } while (isPositionTaken());
        if (verbose) System.out.println("next free position for Person" + this.number + ": (" + nextX + ", " + nextY + ")");
        return new Point(nextX, nextY);
    }
    public List<Point> getAdjacentFreePositions() {
        List<Point> listPoint = new ArrayList<>();
        int gridWidth = group.getGridWidth();
        int gridHeight = group.getGridHeight();
        int newX, newY;
        for (int xincr = -1; xincr <= 1; xincr++ ) {
            for (int yincr = -1; yincr <= 1; yincr++) {
                newX = xincr + this.x;
                newY = yincr + this.y;
                if (newX >= 0 && newX < gridWidth && newY >= 0 && newY < gridHeight &&
                        !(xincr==0 && yincr == 0) && !isPositionTaken(newX, newY)) {
                    listPoint.add(new Point(newX, newY));
                }
            }
        }
        return listPoint;
    }

    /**
     * Choose two people who you are going to distance yourself from.
     * They can't be the same, and they can't me me!
     */
    public void chooseTwo() {
        int a = random.nextInt(group.getLength());
        boda = group.getPerson(a);
        if (boda == this) { // check it's not yourself
            a = (a+1) % group.getLength();
            boda = group.getPerson(a);
        }
        bodb = getNextPerson(a);
    }
    private Person getNextPerson(int a) {
        int b = random.nextInt(group.getLength());
        boolean found = false;
        while (!found) {
            if (b == a) { // check you haven't chosen the same person twice
                b++;
                if (b >= group.getLength()) b = 0;
            }
            bodb = group.getPerson(b);
            if (bodb != this) { // check it's not yourself
                found = true;
            } else {
                b++;
                if (b >= group.getLength()) b = 0;
            }
        }
        return bodb;
    }
    /** Check there is no one currently in that position
     *  Return true if position is free, i.e no other persons at the same location as this.
     */
    public boolean isPositionTaken() {
        for (int i = 0; i < group.getLength(); i++) {
            Person bod = group.getPerson(i);
            if (this != bod &&bod.x != -1 && bod.x == this.x && bod.y == this.y) {
                if (verbose) System.out.println("someone already at (" + this.x + ", " + this.y + ")");
                return true;
            }
        }
        return false;
    }
    public boolean isPositionTaken(int posX, int posY) {
        for (int i = 0; i < group.getLength(); i++) {
            Person bod = group.getPerson(i);
            if (bod.x != -1 && bod.x == posX && bod.y == posY) {
                if (verbose) System.out.println("someone already at (" + posX + ", " + posY + ")");
                return true;
            }
        }
        return false;
    }

    /**
     find the next free space from
     * @return
     */
    public boolean moveIfNecessary() {
        double distanceA = this.getDistance(boda);
        double distanceB = this.getDistance(bodb);
        int bestX = this.x;
        int bestY = this.y;

        boolean moved = false;
        double discrepancy = Math.abs(distanceA - distanceB); // difference between the 2 distances; the value we want to minimise
        if (discrepancy < discrepancyTolerance) {
            if (verbose) System.out.println("discrepancy < " + discrepancyTolerance + ", so not moving");
            return false; // that's close enough!
        }
        double bestDiscrepancy = discrepancy;
        List<Point> freePoints = getAdjacentFreePositions();
        for (Point point: freePoints) {
            distanceA = this.getDistance(point.x, point.y, boda);
            distanceB = this.getDistance(point.x, point.y, bodb);
            discrepancy = Math.abs(distanceA - distanceB);
            if (discrepancy < bestDiscrepancy) {
                bestDiscrepancy = discrepancy;
                bestX = point.x;
                bestY = point.y;
                moved = true;
            }
        }
        this.x = bestX;
        this.y = bestY;
        this.discrepancy = bestDiscrepancy;
        return moved;
    }
    public boolean moveIfNecessaryOld() {
        double distanceA = this.getDistance(boda);
        double distanceB = this.getDistance(bodb);
        int bestX = this.x;
        int bestY = this.y;
        int currentX = this.x;
        int currentY = this.y;

        boolean moved = false;
        double discrepancy; // difference between the 2 distances; the value we want to minimise
        double bestDiscrepancy = Math.abs(distanceA - distanceB);
        Point point;

        while ((point = getNextFreePosition(currentX, currentY)) != null) { // returns false if no free position found
            currentX = point.x;
            currentY = point.y;
            distanceA = this.getDistance(point.x, point.y, boda);
            distanceB = this.getDistance(point.x, point.y, bodb);
            discrepancy = Math.abs(distanceA - distanceB);
            if (discrepancy < bestDiscrepancy) {
                bestDiscrepancy = discrepancy;
                bestX = point.x;
                bestY = point.y;
                moved = true;
            }
        }
        this.x = bestX;
        this.y = bestY;
        this.discrepancy = bestDiscrepancy;
        return moved;
    }
    public double getDistance(Person other) {
        return getDistance(this.x, this.y, other);
    }
    public double getDistance(int thisX, int thisY, Person other) {
        double xd = Math.abs(thisX - other.x);
        double yd = Math.abs(thisY - other.y);
        return Math.sqrt(xd * xd + yd * yd);
    }
    public double getDiscrepancy() {
        if (this.discrepancy < 0) {
            double distanceA = getDistance(boda);
            double distanceB = getDistance(bodb);
            this.discrepancy = Math.abs(distanceA - distanceB);
        }
        return this.discrepancy;
    }

    public static void main(String[] args) {
        Person[] people = {
                new Person(1),
                new Person(2),
                new Person(3),
                new Person(4)
        };
        Group group = new Group(people, 10, 10);
        for (Person bod: people) {
            bod.setGroup(group);
        }
        for (Person bod: people) {
            System.out.println(bod);
        }
        for (Person bod: people) {
            List<Point> freeSpaces = bod.getAdjacentFreePositions();
            System.out.println(freeSpaces);
        }
        System.out.println("total discrepancy=" + String.format("%01.3f",
                group.getTotalDiscrepancy()));
        for (int i = 0; i < 10; i++){
            System.out.println("About to move everyone - if necessary");
            for (Person bod : people) {
                bod.moveIfNecessary();
                System.out.println(bod);
            }
            System.out.println("total discrepancy=" + String.format("%01.3f",
                    group.getTotalDiscrepancy()));
        }
    }
}

