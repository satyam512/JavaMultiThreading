
/*
    In this question we will design a Barrier class.
    When running  tasks by multiple threads concurrently,  sometimes we would like
    to coordinate the work to guarantee that some portion of the work is done by all
    threads before the rest of the work is performed.
    The following task is performed by multiple threads concurrently:
*/

//        private void task() throws InterruptedException {
//
//        // Performing Part 1
//        System.out.println(Thread.currentThread().getName() + " part 1 of the work is finished");
//
//        barrier();
//
//        //Performing Part2
//        System.out.println(Thread.currentThread().getName() + " part 2 of the work is finished");
//        }

/*
        If we have 3 threads, executing this task concurrently, we would like the output to look like this:

        thread1 part 1 of the work is finished
        thread2 part 1 of the work is finished
        thread3 part 1 of the work is finished

        thread2 part 2 of the work is finished
        thread1 part 2 of the work is finished
        thread3 part 2 of the work is finished
        The order of the execution of each part is not important. But we want to make sure that all threads finish part1 before any thread can go ahead and perform part2



        Here is a proposed solution with some blank spot we need to fill in

*/

// **************  You Cannot use join() here, think about it, you can't ************

package InterThreadCommAndSemaphores;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class  ImportantExample {
    public static class Barrier {
        private final int numberOfWorkers;
        private Semaphore semaphore = new Semaphore(0); // so all threads block when barrier is called.
        private int counter = 0;
        private Lock lock = new ReentrantLock();

        public Barrier(int numberOfWorkers) {
            this.numberOfWorkers = numberOfWorkers;
        }

        public void barrier() {
            lock.lock();
            boolean isLastWorker = false;
            try {
                counter++;

                if (counter == numberOfWorkers) {
                    isLastWorker = true;
                }
            } finally {
                lock.unlock();
            }

            if (isLastWorker) {
                semaphore.release(numberOfWorkers-1); // since last thread wasn't blocked, we need to issue only numberOfWorkers - 1 permits
            } else {
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public static class CoordinatedWorkRunner implements Runnable {
        private Barrier barrier;

        public CoordinatedWorkRunner(Barrier barrier) {
            this.barrier = barrier;
        }

        @Override
        public void run() {
            try {
                task();
            } catch (InterruptedException e) {
            }
        }

        private void task() throws InterruptedException {
            // Performing Part 1
            System.out.println(Thread.currentThread().getName() + " part 1 of the work is finished");

            barrier.barrier();

            // Performing Part2
            System.out.println(Thread.currentThread().getName() + " part 2 of the work is finished");
        }
    }

    public static void main(String [] args){
        int numberOfThreads = 20; //or any number you'd like

        List<Thread> threads = new ArrayList<>();

        Barrier barrier = new Barrier(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            threads.add(new Thread(new CoordinatedWorkRunner(barrier)));
        }

        for(Thread thread: threads) {
            thread.start();
        }
    }
}