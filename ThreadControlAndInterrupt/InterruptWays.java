package ThreadControlAndInterrupt;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InterruptWays {
    public static void main(String[] args) throws InterruptedException {

//        //Thread longCalc = new Thread(new PowerCalculator(new BigInteger("2"), new BigInteger("1000000")), "Thread_for_2");
//        //longCalc.start();
//        Thread longCalc = new Thread(new PowerCalculator2(new BigInteger("2"), new BigInteger("1000000")), "Thread_for_2");
//        longCalc.setDaemon(true);
//        longCalc.start();
//        System.out.println("Well I guess, this is it ....");
//        //longCalc.interrupt();

        List<Long> values = Arrays.asList(10L, 2005L, 200L, 5658L, 82000L, 1000L);
        List<FactorialCalculator> tasks = new ArrayList<>();

        for( long value : values) {
            tasks.add(new FactorialCalculator(value));
        }
        for (FactorialCalculator thread : tasks) {
            thread.start();
            //thread.join(2000); this would have been very very catastrophic ....
        }
        /*

        "Then, how can it execute thread.join() for rest of the threads in the for loop ?"

        It doesn't, until the first thread.join() returns.
        So if we have 2 threads that the main thread is waiting on, and the main thread calls
        thread.join() in a loop on each of those threads, the main thread blocks on the first
        thread.join() then when the first thread terminates then the main thread wakes up and
        proceeds to call thread.join() on the next thread (next iteration).

        If by that time the second thread already terminated, then thread.join() returns
        immediately, otherwise the main thread waits.



         */
        for (FactorialCalculator thread : tasks) {
            thread.join(2000); // there's a reason why this is in a separate loop; // question Well if it sets the 2000 on next thread also so does it start counting from 0 itself, i.e. this thread might be say 5th in list so it has been technically running for 10 seconds ???
        }
        /*
        Answer : All the instructions that any single thread (including the main thread) executes are sequential.
                 So yes t2.join() is executed only after t1.join(..) finishes, t3.join() is called only after t2.join(..) returns.
                 Now there's nothing unfair about it, because Thread.join() does not stop the thread, it just means that the main
                 thread will wait specifically for that thread. And yes, calling join on one thread has nothing to do with
                 any other thread. However if you want to be "fair" and give each thread a maximum of about 2000ms.
                Then you could write the code that roughly looks like this:

        long timeLeft = 2000;
        for(Thread thread : threads) {
            long before = System.currentTimeMillis();
            thread.join(timeLeft);
            long after = System.currentTimeMillis();
            timeLeft = timeLeft - (after - before);
            if (timeLeft < = 0 ) {
                break;
            }
        }

        for (Thread thread : threads) {
            if (thread.isAlive()) {
                thread.interrupt();
            }
        }
    */
        for (FactorialCalculator task : tasks) {
            if(task.isDone()){
                System.out.println("Factorial of " + task.getNumber() + " is " + task.getResult());
            }
            else {
                task.interrupt();
                System.out.println("Calculation for "+task.getNumber() + " is still in progress ..."+task.isDone());
            }
        }

        System.out.println("I guess this is it then ... ");
    }
//    public static class PowerCalculator implements Runnable{
//        private BigInteger base;
//        private BigInteger power;
//
//        public PowerCalculator(BigInteger base, BigInteger power) {
//            this.base = base;
//            this.power = power;
//        }
//
//        public BigInteger pow(BigInteger base, BigInteger power) {
//            BigInteger result = BigInteger.ONE;
//            for(BigInteger i = BigInteger.ZERO;i.compareTo(power)!=0 ;i = i.add(BigInteger.ONE)) {
//                if(Thread.currentThread().isInterrupted()) {
//                    System.out.println("Returning partial result due to termination ");
//                    return result;
//                }
//                result = result.multiply(base);
//            }
//            return result;
//        }
//
//        @Override
//        public void run() {
//            System.out.println(base+"^"+power+" = "+pow(base, power));
//        }
//    }
//    public static class PowerCalculator2 implements Runnable{
//        private BigInteger base;
//        private BigInteger power;
//
//        public PowerCalculator2(BigInteger base, BigInteger power) {
//            this.base = base;
//            this.power = power;
//        }
//
//        public BigInteger pow(BigInteger base, BigInteger power) {
//            BigInteger result = BigInteger.ONE;
//            for(BigInteger i = BigInteger.ZERO;i.compareTo(power)!=0 ;i = i.add(BigInteger.ONE))
//                result = result.multiply(base);
//            return result;
//        }
//
//        @Override
//        public void run() {
//            System.out.println(base+"^"+power+" = "+pow(base, power));
//        }
//    } // is a daemon thread

    public static class FactorialCalculator extends Thread {

        private final long number;
        private BigInteger result;
        private boolean isDone;

        public FactorialCalculator(long number) {
            this.number = number;
            this.isDone = false;
        }
        public BigInteger getResult() {
            return result;
        }

        public boolean isDone() {
            return isDone;
        }

        public long getNumber() {
            return number;
        }

        public BigInteger factorial() {
            BigInteger tempResult = BigInteger.ONE;
            for (long i = 1;i<=this.number;i++) {
                if(Thread.currentThread().isInterrupted()){
                    this.isDone = false;
                    return BigInteger.ZERO; // will take less memory
                }
                tempResult = tempResult.multiply(new BigInteger(Long.toString(i)));
            }
            return tempResult;
        }
        @Override
        public void run() {

            this.result = this.factorial();
            this.isDone = true;
            //System.out.println(this.isDone);
        }
    }
}

