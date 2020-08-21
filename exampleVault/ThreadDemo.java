package exampleVault;

import java.util.ArrayList;
import java.util.List;

public class ThreadDemo {
    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Really ?? .... ");
            }
            System.out.println(Thread.currentThread().getName()+" Bat");
        }, "Detective");

        thread1.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println(new StringBuilder().append("A critical error ").append(e.getMessage()).append(" has happened in ").append(t.getName()).toString());
            }
        });

        System.out.println("Before calling start, in thread " + Thread.currentThread().getName());
        thread1.start();
        System.out.println("Currently in thread : " + Thread.currentThread().getName());
        System.out.println("After calling ");

    }
}
