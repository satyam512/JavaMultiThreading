import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringJoiner;

public class MainApplication {

    private static final String INPUT_FILE = "./out/matrices";
    private static final String OUTPUT_FILE = "./out/matricesResult";
    private static final int N = 10;

    private static final int CAPACITY = 5;

    public static void main(String[] args) throws IOException {

        File inputFile = new File(INPUT_FILE);
        File outputFile = new File(OUTPUT_FILE);

        ThreadSafeQueue threadSafeQueue = new ThreadSafeQueue();

        MatricesWriterConsumer consumer = new MatricesWriterConsumer(new FileWriter(outputFile), threadSafeQueue);
        MatricesReaderProducer producer = new MatricesReaderProducer(new FileReader(inputFile), threadSafeQueue);

        consumer.start();
        producer.start();

    }

    private static class MatricesWriterConsumer extends Thread {
        private final FileWriter fileWriter;
        private final ThreadSafeQueue threadSafeQueue;

        public MatricesWriterConsumer(FileWriter fileWriter, ThreadSafeQueue threadSafeQueue) {
            this.threadSafeQueue = threadSafeQueue;
            this.fileWriter = fileWriter;
        }

        @Override
        public void run() {
            while (true) {

                MatricesPair matricesPair = threadSafeQueue.remove();
                if (matricesPair == null) {
                    System.out.println("No more matrices to read from the queue, consumer is terminating");
                    break;
                }
                float [][] matRes = matMul(matricesPair.mat1, matricesPair.mat2);

                try {
                    saveMatrixToFile(fileWriter, matRes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                fileWriter.flush();
                fileWriter.close();
            }catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        private static void saveMatrixToFile(FileWriter fileWriter, float[][] matrix) throws IOException {
            for (int r = 0; r < N; r++) {
                StringJoiner stringJoiner = new StringJoiner(", ");
                for (int c = 0; c < N; c++) {
                    stringJoiner.add(String.format("%.2f", matrix[r][c]));
                }
                fileWriter.write(stringJoiner.toString());
                fileWriter.write('\n');
            }
            fileWriter.write('\n');
        }

        float [][] matMul(float [][]mat1 , float [][]mat2) {
            float [][] result = new float[N][N];

            for (int i = 0;i<N; i++)
                for (int j = 0; j<N ; j++)
                    for (int k= 0; k<N ; k++)
                        result[i][j] += mat1[i][k] * mat2[k][j];
            return result;
        }

    }

    private static class MatricesReaderProducer extends Thread {
        private final Scanner scanner;
        private final ThreadSafeQueue queue;



        public MatricesReaderProducer(FileReader reader, ThreadSafeQueue queue) {
            this.scanner = new Scanner(reader);
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                float[][] matrix1 = readMatrix();
                float[][] matrix2 = readMatrix();
                if (matrix1 == null || matrix2 == null) {
                    queue.terminate();
                    System.out.println("No more matrices to read. Producer Thread is terminating");
                    return;
                }

                MatricesPair matricesPair = new MatricesPair();
                matricesPair.mat1 = matrix1;
                matricesPair.mat2 = matrix2;

                queue.add(matricesPair);
            }
        }

        private float[][] readMatrix() {
            float[][] matrix = new float[N][N];
            for (int r = 0; r < N; r++) {
                if (!scanner.hasNext()) {
                    return null;
                }
                String[] line = scanner.nextLine().split(",");
                for (int c = 0; c < N; c++) {
                    matrix[r][c] = Float.parseFloat(line[c]);
                }
            }
            scanner.nextLine();
            return matrix;
        }
    }

    public static class ThreadSafeQueue {

        public Queue<MatricesPair> queue = new LinkedList<>();
        public boolean isEmpty = true;
        public boolean isTerminate = false;

        public synchronized void add(MatricesPair matricesPair) { // sync basically locks the ThreadSafeQueue object from being accessed by any other thread
            // 1st no back pressure so producer can produce without worrying about consumer speed.
            // next adding this loop to block the producer as soon as queue.size is CAPACITY and keep it blocked
            while (queue.size() == CAPACITY) {  // telling producer that in case producer is still producing but currently no produced item availabl
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.add(matricesPair);
            isEmpty = false;
            notify(); // notifying(waking up) all consumer threads that queue has some pair to process
        }

        public synchronized MatricesPair remove() { // called by consumer to obtain a matrix pair to consume
            while (isEmpty && !isTerminate) {  // telling producer that in case producer is still producing but currently no produced item availabl
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(queue.size()==1) // will reach here after thread wakes up and one of above loop conditions is false
                isEmpty = true;  // i.e in case we got an object on queue after we consume it, our queue will be empty()
            if(queue.size() == 0 && isTerminate)
                return null;

            System.out.println("Queue size : " + queue.size());

            //notifyAll(); /// telling all sleeping producers that one item has been consumed but see in case it is already less then we would be waking threads in vain
            MatricesPair result =  queue.remove();
            if(queue.size()==CAPACITY-1)
                notifyAll(); // now this will be only called when queue size gets 4

            return result;
        }
        public synchronized void terminate() {
            isTerminate = true;
            notifyAll(); // this is important as since no more items will be produced we need to wake all sleeping consumers that 2nd condition is false now and they'll terminate
        }

    }

    public static class MatricesPair {
        public float [][] mat1;
        public float [][] mat2;
    }

}
