package DataRaceAndDeadlocks;

public class DataRace {
    public static void main(String []args){

        SharedClass sharedClass = new SharedClass();

        Thread thread1 = new Thread(()->{
            while (true){
                sharedClass.increment();
            }

        });

        Thread thread2 = new Thread(()->{
            while (true){
                sharedClass.checkForDataRace();
            }
        });

        thread1.start();
        thread2.start();
    }
    public static class SharedClass {
        private volatile int x = 0;
        private volatile int y = 0;

        public void increment() {
            x++;
            y++;
        }

        public void checkForDataRace() {
            if(y>x)
                System.out.println("Race condition detected");
        }

    }
}
