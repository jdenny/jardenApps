package jarden.life;

/**
 * Created by john.denny@gmail.com on 04/03/2017.
 */

/**
 * Utility class
 */
public class NameCount {
    public String name;
    public int count;
    public NameCount(String name, int count) {
        this.name = name;
        this.count = count;
    }
    public String toString() {
        return count + " " + name;
    }
}

