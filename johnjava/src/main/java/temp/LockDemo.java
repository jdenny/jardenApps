package temp;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by john.denny@gmail.com on 23/02/2017.
 */

public class LockDemo {
    private static final Lock lock = new ReentrantLock();
    private static final ReentrantLock rlock = new ReentrantLock();

    private static void doSomething() throws InterruptedException {
        Thread thread = Thread.currentThread();
        System.out.println(thread + " is " + thread.getState());
        lock.lockInterruptibly();
        for (int i = 0; i < 10; i++) {
            System.out.println(thread + " i=" + i);
            Thread.sleep(400);
        }
    }
    public static void main(String[] args) throws InterruptedException {
        // test1();
        test2();
    }
    private static void test2() throws InterruptedException {
        rlock.lock();
        System.out.println("1 locked once; holdCount=" + rlock.getHoldCount());
        rlock.lock();
        System.out.println("2 locked twice; holdCount=" + rlock.getHoldCount());
        rlock.unlock();
        System.out.println("3 unlocked once; holdCount=" + rlock.getHoldCount());
        System.out.println("4 held by current thread=" + rlock.isHeldByCurrentThread());
        Thread t = new Thread(() -> {
            System.out.println("6 **thread started");
            rlock.lock();
            try {
                System.out.println("9 **thread has got the lock");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("12 **thread about to release the lock");
                rlock.unlock();
            }
        });
        t.start();
        System.out.println("5 started thread; about to sleep");
        Thread.sleep(2000);
        System.out.println("7 sleep finished; about to unlock");
        rlock.unlock();
        System.out.println("8 unlocked twice; holdCount=" + rlock.getHoldCount());
        Thread.yield();
        System.out.println("10 held by current thread=" + rlock.isHeldByCurrentThread());
        System.out.println("11 now let's try locking again");
        rlock.lock();
        System.out.println("13 got the lock again");
        rlock.unlock();
        /*
        What I think will happen
         */
    }
    private static void test1() {
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
