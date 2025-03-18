import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class Field {
    private final String name;
    private final int fieldIndex;  // The index of this physical field
    private int animalCount;
    private final Lock lock = new ReentrantLock(true);
    private final Condition notEmpty = lock.newCondition();
    private final Condition stockingFinished = lock.newCondition();
    private Farmer lastStockingFarmer;
    private boolean isBeingStocked = false;
    
    // Counter for waiting buyers
    private int waitingBuyers = 0;

    public Field(String name, int initialCount, int fieldIndex) {
        this.name = name;
        this.animalCount = initialCount;
        this.fieldIndex = fieldIndex;
    }

    public boolean buyAnimal(int waitTicks) throws InterruptedException {
        lock.lock();
        try {
            // If the field is empty or being stocked, the buyer must wait.
            while (animalCount == 0 || isBeingStocked) {
                waitingBuyers++;  // Record that a buyer is waiting.
                try {
                    stockingFinished.await();
                } finally {
                    waitingBuyers--;  // Ensure decrement even if an exception occurs.
                }
            }
            animalCount--;
            System.out.println("(Ticks :" + Clock.getTickCount() + ") " + name + " (Field " + fieldIndex + ") now has " + animalCount + "/10 capacity.");
            return true;
        } finally {
            lock.unlock();
        }
    }

    public int getAnimalCount() {
        lock.lock();
        try {
            return animalCount;
        } finally {
            lock.unlock();
        }
    }    

    public int addAnimals(int count, Farmer farmer) throws InterruptedException {
        lock.lock();
        try {
            isBeingStocked = true;  // Mark field as being stocked.
            int spaceLeft = Constants.MAX_ANIMALS_PER_FIELD - animalCount;
            int animalsToAdd = Math.min(count, spaceLeft);

            if (animalsToAdd > 0) {
                for (int i = 0; i < animalsToAdd; i++) {
                    sleep(Constants.TICK_DURATION_MS);  // Simulate stocking time.
                    animalCount++;
                    System.out.println("(Ticks :" + Clock.getTickCount() + ") " + name + " (Field " + fieldIndex + ") now has " + animalCount + "/10 capacity.");
                }
                lastStockingFarmer = farmer;
                System.out.println("(Ticks :" + Clock.getTickCount() + ") " + name + " (Field " + fieldIndex + ") was stocked by " + farmer.getName());
            }

            isBeingStocked = false;  // Mark stocking as finished.
            stockingFinished.signalAll();  // Notify waiting buyers.
            return count - animalsToAdd;  // Return number of animals that couldn't be stocked.
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            return count;
        } finally {
            lock.unlock();
        }
    }

    public Farmer getLastStockingFarmer() {
        return lastStockingFarmer;
    }

    public String getName() {
        return name;
    }
    
    public int getFieldIndex() {
        return fieldIndex;
    }

    // Return the number of waiting buyers
    public int getWaitingBuyers() {
        lock.lock();
        try {
            return waitingBuyers;
        } finally {
            lock.unlock();
        }
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
