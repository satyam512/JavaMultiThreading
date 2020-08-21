package exampleVault;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConcurrentHackers {

    public static final int MAX_PASS = 9999;

    public static void main(String[] args) {
        Random random = new Random();
        Vault vault = new Vault(random.nextInt(MAX_PASS));

        List<Thread> threads = new ArrayList<>();

        threads.add(new AscendingHacker(vault));
        threads.add(new DescendingHacker(vault));
        threads.add(new PoliceMan());

        for(Thread task : threads) {
            task.start();
        }
    }

    public static class Vault {
        private final int password;

        public Vault(int password) {

            this.password = password;
        }

        public boolean isCorrect(int pass) {
            //System.out.println(Thread.currentThread().getName() + " ");
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return (this.password == pass);
        }
    }

    public static abstract class HackerThread extends Thread {
        protected Vault vault;

        public HackerThread(Vault vault) {
            this.vault = vault;
            this.setName(this.getClass().getSimpleName());
            this.setPriority(MAX_PRIORITY);
        }

        @Override
        public synchronized void start() {
            System.out.println("Starting thread : " + this.getName());
            super.start();
        }
        // see it's an abstract class so it won't be having any objects, basically we'll have another class extending this class
    }

    public static class AscendingHacker extends HackerThread {

        public AscendingHacker(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for (int i = 0; i <= MAX_PASS; i++) {
                if (vault.isCorrect(i)) {
                    System.out.println("Hacked by : " + this.getName() + " Password is : " + i);
                    System.exit(0);
                }
            }
        }
    }

    public static class DescendingHacker extends HackerThread {

        public DescendingHacker(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for (int i = MAX_PASS; i >= 0; i--) {
                if (vault.isCorrect(i)) {
                    System.out.println("Hacked by : " + this.getName() + " Password is : " + i);
                    System.exit(0);
                }
            }
        }
    }

    public static class PoliceMan extends Thread {
        @Override
        public void run() {
            for (int i=10;i>=0;i--) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(i);
            }
            System.out.println("Game over Hackers");
            System.exit(0);
        }
    }



}

