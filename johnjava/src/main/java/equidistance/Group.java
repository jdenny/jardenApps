package equidistance;

/**
 * Created by john.denny@gmail.com on 01/12/2023.
 */

class Group {
    private Person[] people;
    private int gridX, gridY;
    public Group(Person[] people, int gridX, int gridY) {
        this.people = people;
        this.gridX = gridX;
        this.gridY = gridY;
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

