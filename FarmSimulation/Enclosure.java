import java.util.*;

public class Enclosure {
    private final Map<String, Integer> animals = new HashMap<>();

    public synchronized void addAnimals(Map<String, Integer> delivery) {
        boolean addedAnimals = false;
        for (Map.Entry<String, Integer> entry : delivery.entrySet()) {
            if (entry.getValue() > 0) {
                animals.put(entry.getKey(), animals.getOrDefault(entry.getKey(), 0) + entry.getValue());
                addedAnimals = true;
            }
        }
        if (addedAnimals) {
            System.out.println("(Ticks :" + Clock.getTickCount() + ") " + "New delivery arrived: " + animals);
            notifyAll();  // Wakes up all waiting farmers
        }
    }
    

    public synchronized Map<String, Integer> collectAnimals(int maxCapacity) {
        while (animals.isEmpty()) {
            try {
                System.out.println("(Ticks :" + Clock.getTickCount() + ") " + Thread.currentThread().getName() + " is waiting for animals in enclosure.");
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    
        Map<String, Integer> collected = new HashMap<>();
        int count = 0;
    
        Iterator<Map.Entry<String, Integer>> iterator = animals.entrySet().iterator();
        while (iterator.hasNext() && count < maxCapacity) {
            Map.Entry<String, Integer> entry = iterator.next();
            int toTake = Math.min(entry.getValue(), maxCapacity - count);
            collected.put(entry.getKey(), toTake);
            count += toTake;
            entry.setValue(entry.getValue() - toTake);
            if (entry.getValue() == 0) {
                iterator.remove();
            }
        }
    
        System.out.println("(Ticks :" + Clock.getTickCount() + ") " + Thread.currentThread().getName() + " collected animals: " + collected);
        return collected;
    }
}
