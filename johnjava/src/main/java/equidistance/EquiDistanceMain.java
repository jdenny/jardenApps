package equidistance;

import java.util.List;

import jarden.equidistance.Group;
import jarden.equidistance.Person;
import jarden.equidistance.Person.Point;

/**
 * Created by john.denny@gmail.com on 03/12/2023.
 */
public class EquiDistanceMain {
    public final static boolean debug = false;

    public static void main(String[] args) {
        Person[] people = {
                new Person("1"),
                new Person("2"),
                new Person("3"),
                new Person("4")
        };
        Group group = new Group(people, 10, 10);
        for (Person bod: people) {
            bod.setGroup(group);
        }
        for (Person bod: people) {
            System.out.println(bod);
        }
        if (debug) {
            for (Person bod : people) {
                List<Point> freeSpaces = bod.getAdjacentFreePositions();
                System.out.println(freeSpaces);
            }
        }
        System.out.println("total discrepancy=" + String.format("%01.3f",
                group.getTotalDiscrepancy()));
        boolean isUsingThreads = false;
        if (isUsingThreads) {
            Thread thread = null;
            for (Person bod: people) {
                thread = new Thread(new Agent(bod));
                thread.start();
            }
            try {
                thread.join(3000); // wait for last thread to finish
            } catch(InterruptedException ex) {
                System.out.println("thread interrupted:" + ex);
            }
            System.out.println("total discrepancy=" + String.format("%01.3f",
                    group.getTotalDiscrepancy()));
        } else {
            boolean someoneMoved = true;
            int i;
            for (i = 0; i < 10 && someoneMoved; i++) {
                System.out.println("About to move everyone - if necessary");
                someoneMoved = false;
                for (Person bod : people) {
                    if (bod.moveIfNecessary()) someoneMoved = true;
                    System.out.println(bod);
                }
                System.out.println("total discrepancy=" + String.format("%01.3f",
                        group.getTotalDiscrepancy()));
            }
            if (i < 10) {
                System.out.println("stopped moving after " + i + " moves");
            }
        }
    }
}
