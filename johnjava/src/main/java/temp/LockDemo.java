package temp;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by john.denny@gmail.com on 23/02/2017.
 */

public class LockDemo {
    private static final Lock lock = new ReentrantLock();

    private static void doSomething() throws InterruptedException {
        Thread thread = Thread.currentThread();
        System.out.println(thread + " is " + thread.getState());
        lock.lockInterruptibly();
        for (int i = 0; i < 10; i++) {
            System.out.println(thread + " i=" + i);
            Thread.sleep(400);
        }
    }
    public static void main(String[] args) {
        lock.lock();
        Thread t = new Thread(() -> {
            try {
                doSomething();
            } catch (InterruptedException e) { // clears interrupt flag
                Thread thread = Thread.currentThread();
                System.out.println(thread +
                        " has been interrupted and is about to exit");
                System.out.println("interrupted=" + thread.isInterrupted());
                System.out.println("interrupted=" + thread.isInterrupted());
            }
        });
        t.start();
        System.out.println("main(); about to sleep");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("main(); t is " + t.getState());
        lock.unlock();
        System.out.println("main() after unlock; t is " + t.getState());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("main(); t is " + t.getState());
        System.out.println("main(); about to interrupt thread");
        t.interrupt();
        System.out.println("t.isInterrupted()=" + t.isInterrupted());
        System.out.println("main thread about to end");
    }
}
