package equidistance;

/**
 * Created by john.denny@gmail.com on 02/12/2023.
 */
public class Agent implements Runnable {
    private final Person person;

    public Agent(Person person) {
        this.person = person;
    }
    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            person.moveIfNecessary();
            System.out.println(person);
        }
    }
}
