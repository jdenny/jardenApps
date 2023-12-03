package jarden.equidistance;

/**
 * Created by john.denny@gmail.com on 01/12/2023.
 */

public class Group {
    private Person[] people;
    private int gridX, gridY;
    public Group(Person[] people, int gridX, int gridY) {
        this.people = people;
        this.gridX = gridX;
        this.gridY = gridY;
    }
    public double getTotalDiscrepancy() {
        double total = 0.0;
        for (Person person: people) {
            total += person.getDiscrepancy();
        }
        return total;
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

