import java.util.Map;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Farmer extends Thread {
    private static int farmerCounter = 0;
    public static final List<String> farmerNames = new ArrayList<>(); 
    private final Enclosure enclosure;
    private final Map<String, List<Field>> fields;
    private int ticksWorked = 0;
    private final Random random = new Random();

    public Farmer(Enclosure enclosure, Map<String, List<Field>> fields) {
        this.enclosure = enclosure;
        this.fields = fields;
        farmerCounter++;
        this.setName("Farmer " + farmerCounter);
        farmerNames.add(this.getName());
    }

    private void processAnimalType(String animalType, int quantity) {
        int remaining = quantity;
        List<Field> fieldList = fields.get(animalType);
        if (fieldList == null) return;
        // Sort fields in decending order by waiting buyer count for prioritisation
        List<Field> sortedFields = new ArrayList<>(fieldList);
        Collections.sort(sortedFields, Comparator.comparingInt(Field::getWaitingBuyers).reversed());
        for (Field field : sortedFields) {
            if (remaining <= 0) break;
            try {
                // Simulate movement time: 10 ticks plus 1 tick per animal being moved.
                sleep((10 + remaining) * Constants.TICK_DURATION_MS);
                System.out.println("(Ticks :" + Clock.getTickCount() + ") " + getName() + " reached " + field.getName() +
                                   " (Field " + field.getFieldIndex() + ")");
                int leftover = field.addAnimals(remaining, this);
                int added = remaining - leftover;
                remaining = leftover;
                ticksWorked += added;
                System.out.println("(Ticks :" + Clock.getTickCount() + ") " + getName() + " stocked " + added + " animals in " +
                                   field.getName() + " (Field " + field.getFieldIndex() + "). Total work time: " + ticksWorked + " ticks.");
                Statistics.logFarmerWorkTime(ticksWorked);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            if (ticksWorked >= Constants.FARMER_BREAK_INTERVAL) {
                try {
                    System.out.println("(Ticks :" + Clock.getTickCount() + ") " + getName() + " is taking a break for " + Constants.FARMER_BREAK_DURATION + " ticks.");
                    sleep(Constants.FARMER_BREAK_DURATION * Constants.TICK_DURATION_MS);
                    ticksWorked = 0;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

          // After collecting animals from the enclosure:
            Map<String, Integer> collectedAnimals;
            do {
                collectedAnimals = enclosure.collectAnimals(Constants.FARMER_CAPACITY);
                if (collectedAnimals.isEmpty()) {
                    try {
                        System.out.println("(Ticks :" + Clock.getTickCount() + ") " + getName() + " is waiting for animals in enclosure...");
                        sleep(200);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            } while (collectedAnimals.isEmpty());

            // Determine prioritized animal type (based on total waiting buyers) from the collected animals.
            String prioritizedAnimal = null;
            int maxWaiting = -1;
            for (String animal : collectedAnimals.keySet()) {
                int totalWaiting = fields.get(animal).stream().mapToInt(Field::getWaitingBuyers).sum();
                if (totalWaiting > maxWaiting) {
                    maxWaiting = totalWaiting;
                    prioritizedAnimal = animal;
                }
            }

            if (prioritizedAnimal != null && collectedAnimals.containsKey(prioritizedAnimal)) {
                System.out.println("(Ticks :" + Clock.getTickCount() + ") " + getName() + " will process prioritized animal type: " + prioritizedAnimal);
                // Process the prioritized animal type first.
                processAnimalType(prioritizedAnimal, collectedAnimals.get(prioritizedAnimal));
                // Remove it from the collected map so it’s not processed again.
                collectedAnimals.remove(prioritizedAnimal);
            }

            // Process remaining animal types (sorted by waiting buyer count descending).
            List<String> remainingAnimalTypes = new ArrayList<>(collectedAnimals.keySet());
            Collections.sort(remainingAnimalTypes, (a, b) -> {
                int waitingA = fields.get(a).stream().mapToInt(Field::getWaitingBuyers).sum();
                int waitingB = fields.get(b).stream().mapToInt(Field::getWaitingBuyers).sum();
                return Integer.compare(waitingB, waitingA);
            });
            for (String animalType : remainingAnimalTypes) {
                processAnimalType(animalType, collectedAnimals.get(animalType));
            }

                }
            }
        
    }

