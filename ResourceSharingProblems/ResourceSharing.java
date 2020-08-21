package ResourceSharingProblems;

public class ResourceSharing {
    public static void main(String[] args) throws InterruptedException {

        Inventory inventory = new Inventory();
        IncrementThread adder = new IncrementThread(inventory);
        DecrementThread remover = new DecrementThread(inventory);

        adder.start();
        remover.start();

        adder.join();
        remover.join();

        System.out.println(inventory.getItems());
    }
    public static class Inventory{
        private int items;
        public Inventory(){
            this.items = 0;
        }

        public void increment(){
            this.items++;
        }

        public void decrement(){
            this.items--;
        }

        public int getItems(){
            return this.items;
        }
    }

    public static class IncrementThread extends Thread {

        private Inventory inventory;

        public IncrementThread(Inventory inventory) {
            this.inventory = inventory;
        }

        @Override
        public void run() {
            for (int i=0;i<500000;i++) {
                inventory.increment();
           }
        }
    }
    public static class DecrementThread extends Thread {

        private Inventory inventory;

        public DecrementThread(Inventory inventory) {
            this.inventory = inventory;
        }

        @Override
        public void run() {
            for (int i=0;i<500000;i++) {
                inventory.decrement();
            }
        }
    }
}
