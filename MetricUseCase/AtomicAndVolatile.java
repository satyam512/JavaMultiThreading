package MetricUseCase;

import java.util.Random;

public class AtomicAndVolatile {
    public static void main(String[] args) {

        long mini = Long.MAX_VALUE;
        long maxi = Long.MIN_VALUE;

        System.out.println(Long.max(mini, maxi));
        Metrices metrices = new Metrices();
        BussinessLogic bussinessLogic1 = new BussinessLogic(metrices);
        BussinessLogic bussinessLogic2 = new BussinessLogic(metrices);
        MetricesPrinter metricesPrinter = new MetricesPrinter(metrices);

        bussinessLogic1.start();
        bussinessLogic2.start();
        metricesPrinter.start();

    }

    public static class MetricesPrinter extends Thread {
        public Metrices metrices;
        public MetricesPrinter(Metrices metrices) {
            this.metrices = metrices;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Current average is : " + metrices.getAverage());
            }
        }

    }
    public static class BussinessLogic extends Thread {
        private Random random = new Random();;
        private Metrices metrices;

        public BussinessLogic(Metrices metrices) {
            this.metrices = metrices;
        }

        @Override
        public void run() {

            while (true) {
                long startTime = System.currentTimeMillis();
                try {
                    Thread.sleep(random.nextInt(9));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long endTime = System.currentTimeMillis();
                this.metrices.addSample(endTime - startTime);
            }
        }

    }
    public static class Metrices {
        private long count = 0;
        private volatile double average = 0.0;

        public synchronized void addSample(long sample) { // since only one thread should access it at a time
            double current = this.count * this.average;
            this.count++;
            this.average = (current + sample)/this.count;
        }

        public double getAverage(){
            return this.average;   // this is atomic as getters and setters are atomic, but since using double we need volatile
        }
    }

}
