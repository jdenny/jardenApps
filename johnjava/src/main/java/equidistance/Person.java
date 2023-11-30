package equidistance;

import java.util.Random;
import java.util.logging.Logger;


/**
 * Created by john.denny@gmail.com on 30/11/2023.
 * Equidistance game. Everyone in the group randomly chooses two people, and on the word 'go'
 * each one moves so that he or she is equidistant from his chosen two who in turn - frustratingly -
 * will have also moved.
 */
public class Person {
    private static Logger logger = Logger.getLogger("johnjava.equidistance");

    private int number;
    private int x = -1, y = -1;
    private Person boda, bodb;
    private Group group;
    private int gridWidth, gridHeight;
    private Random random = new Random();

    public Person(int number) {
        this.number = number;
    }
    public String toString() {
        return "Person " + number + " (" + x + ", " + y + ") chosen: (" +
                boda.number + ": " + getDistance(boda) + ", " +
                bodb.number + ": " + getDistance(bodb) +")";
    }
    public void setGroup(Group group) {
        this.group = group;
        gridWidth = group.getGridWidth();
        gridHeight = group.getGridHeight();
        this.x = random.nextInt(gridWidth);
        this.y = random.nextInt(gridHeight);
        if (!this.isPositionFree()) {
            moveToNextFreePosition();
        }
        this.chooseTwo();
    }
    private boolean moveToNextFreePosition() {
        int oldX = x;
        int oldY = y;
        do {
            x++;
            if (x >= gridWidth) {
                x = 0;
                y++;
                if (y >= gridHeight) {
                    y = 0;
                }
            }
            if (x == oldX && y == oldY) {
                System.out.println("no free positions found!");
                return false;
            }
        } while (!isPositionFree());
        return true;
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
    public boolean isPositionFree() {
        for (int i = 0; i < group.getLength(); i++) {
            Person bod = group.getPerson(i);
            if (bod != this && bod.x != -1 && bod.x == this.x && bod.y == this.y) {
                System.out.println("someone already at (" + this.x + ", " + this.y + ")");
                return false;
            }
        }
        return true;
    }

    public boolean moveIfNecessary() {
        int distanceA = this.getDistance(boda);
        int distanceB = this.getDistance(bodb);

        boolean moved;
        int discrepancy;
        int minDiscrepancy = Math.abs(distanceA - distanceB);
        int oldX = x;
        int oldY = y;

        while ((minDiscrepancy > 0) &&
                (moved = moveToNextFreePosition())) { // returns false if no free position found
            distanceA = this.getDistance(boda);
            distanceB = this.getDistance(bodb);
            discrepancy = Math.abs(distanceA - distanceB);
            if (discrepancy < minDiscrepancy) {
                minDiscrepancy = discrepancy;
            }
        }
        moved = this.x != oldX || this.y != oldY;
        if (moved) {
            System.out.println("moved to (" + this.x + ", " + this.y + ")");
        } else {
            System.out.println("stayed in same place: (" + this.x + ", " + this.y + ")");
        }
        return moved;
    }
    public int getDistance(Person other) {
        double xd = Math.abs(this.x - other.x);
        double yd = Math.abs(this.y - other.y);
        return (int)Math.sqrt(xd * xd + yd * yd);
    }
    public static void main(String[] args) {
        Person[] people = {
                new Person(1),
                new Person(2),
                new Person(3),
                new Person(4)
        };
        Group group = new Group(people, 4, 4);
        for (Person bod: people) {
            bod.setGroup(group);
        }
        for (Person bod: people) {
            System.out.println(bod);
        }
        System.out.println("About to move everyone!");
        for (Person bod: people) {
            bod.moveIfNecessary();
            System.out.println(bod);
        }
    }
}

class Group {
    private Person[] people;
    private int gridX, gridY;
    public Group(Person[] people, int gridX, int gridY) {
        this.people = people;
        this.gridX = gridX;
        this.gridY = gridY;
    }
    private boolean checkPosition(Person person) {
        return true;
    }

    public int getLength() {
        return people.length;
    }
    public int getGridWidth() {
        return this.gridX;
    }
    public int getGridHeight() {
        return this.gridY;
    }

    public Person getPerson(int a) {
        return people[a];
    }
}
