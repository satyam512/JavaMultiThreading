package DataRaceAndDeadlocks;
import java.util.Random;

public class Deadlocks {

    public static void main(String[] args) {
        RailRoadCrossing railRoadCrossing = new RailRoadCrossing();
        Thread thread1 = new Thread(new TrainA(railRoadCrossing));
        Thread thread2 = new Thread(new TrainB(railRoadCrossing));

        thread1.start();
        thread2.start();
    }

    public static class TrainA implements Runnable {
        private final RailRoadCrossing railRoadCrossing;
        private final Random random = new Random();

        public TrainA(RailRoadCrossing railRoadCrossing) {
            this.railRoadCrossing = railRoadCrossing;
        }

        @Override
        public void run() {
            while (true) {
                long passingTime = random.nextInt(10);

                try {
                    Thread.sleep(passingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    railRoadCrossing.TakeRoadA();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public static class TrainB implements Runnable {
        private final RailRoadCrossing railRoadCrossing;
        private final Random random = new Random();
        public TrainB(RailRoadCrossing railRoadCrossing) {
            this.railRoadCrossing = railRoadCrossing;
        }

        @Override
        public void run() {

            while (true) {
                long passingTime = random.nextInt(10);

                try {
                    Thread.sleep(passingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    railRoadCrossing.TakeRoadB();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public static class RailRoadCrossing{
        private Object lockA = new Object();
        private Object lockB = new Object();

        public void TakeRoadA() throws InterruptedException {
//            synchronized (lockA) {
//                System.out.println("Road A is acquired by " + Thread.currentThread().getName());
//                synchronized (lockB) {
//                    System.out.println("Train at road A");
//                    try {
//                        Thread.sleep(1);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }


            // to avoid 4th condition i.e circular wait , changing order of lock acquiring
            synchronized (lockB) {
                System.out.println("Road A is acquired by " + Thread.currentThread().getName());
                synchronized (lockA) {
                    System.out.println("Train at road A");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void TakeRoadB() throws InterruptedException {
            synchronized (lockB) {
                System.out.println("Road B is acquired by " + Thread.currentThread().getName());
                synchronized (lockA) {
                    System.out.println("Train at road B");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
