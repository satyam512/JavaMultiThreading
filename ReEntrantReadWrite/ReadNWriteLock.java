package ReEntrantReadWrite;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadNWriteLock {

    public static final int HIGHEST_PRICE = 1000;
    public static void main(String[] args) throws InterruptedException {
        InventoryDataBase inventoryDataBase = new InventoryDataBase();
        Random random = new Random();
        for(int i=0;i<100000;i++) {
            int price = random.nextInt(HIGHEST_PRICE);
            inventoryDataBase.addItem(price);
        }

        Thread writer = new Thread(()->{
            while (true){
                inventoryDataBase.addItem(HIGHEST_PRICE);
                inventoryDataBase.removeItem(HIGHEST_PRICE);
                try {
                    Thread.sleep(10);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        writer.setDaemon(true);
        writer.start();

        int numOfReaders = 7;
        List<Thread> readers = new ArrayList<>();
        for (int i=0;i<numOfReaders;i++){
            Thread reader = new Thread(()->{
                for(int j=0;j<100000;j++)
                {
                    int upperLimit = random.nextInt(HIGHEST_PRICE);
                    int lowerLimit = upperLimit > 0 ? random.nextInt(upperLimit) : 0;
                    inventoryDataBase.getNoOfItemsInRange(lowerLimit, upperLimit);
                }
            });
            reader.setDaemon(true);
            readers.add(reader);
        }
        long startTime = System.currentTimeMillis();
        for (Thread reader : readers) {
            reader.start();
        }
        for (Thread reader : readers) {
            reader.join();
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Program Ended in : "+ (endTime-startTime));
    }
    public static class InventoryDataBase{
        TreeMap<Integer, Integer> inventory = new TreeMap<>();
        //ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
        ReentrantLock reentrantLock = new ReentrantLock();
        public int getNoOfItemsInRange (int lowerBound, int upperBound){

            //reentrantReadWriteLock.readLock().lock();
            reentrantLock.lock();
            try {
                Integer lowerKey = inventory.ceilingKey(lowerBound);
                Integer upperKey = inventory.floorKey(upperBound);

                NavigableMap<Integer, Integer> snapshot = inventory.subMap(lowerKey, true, upperKey, true);

                int count = 0;
                for(Map.Entry<Integer, Integer> item : snapshot.entrySet()){
                    count = count + item.getValue();
                }
                return count;
            }
            finally {
                //reentrantReadWriteLock.readLock().unlock();
                reentrantLock.unlock();
            }
        }
        public void addItem(int price) {
            //reentrantReadWriteLock.writeLock().lock();
            reentrantLock.lock();
            try {
                Integer numOfItems = inventory.get(price);
                if(numOfItems == null)
                    inventory.put(price, 1);
                else
                    inventory.put(price, numOfItems+1);
            }
            finally {
                //reentrantReadWriteLock.writeLock().unlock();
                reentrantLock.unlock();
            }
        }
        public void removeItem(int price) {
            //reentrantReadWriteLock.writeLock().lock();
            reentrantLock.lock();
            try {
                Integer numOfItems = inventory.get(price);
                if(numOfItems == null || numOfItems == 1)
                    inventory.remove(price);
                else
                    inventory.put(price, numOfItems-1);
            }
            finally {
                //reentrantReadWriteLock.writeLock().unlock();
                reentrantLock.unlock();
            }

        }
    }
}
