import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.List;

public class Delivery extends Thread {
    private final Enclosure enclosure;
    private final Random random;
    private final Random randgen; // Used to compute delivery wait time with a fixed seed.
    private final Map<String, List<Field>> fields;
    private final Map<String, Double> deliveryProbs;  // Custom delivery probabilities for each animal type
    private final int avgDeliveryWaitTicks; // Average wait time (in ticks) for a delivery

    public Delivery(Enclosure enclosure, Map<String, List<Field>> fields, Map<String, Double> deliveryProbs, int avgDeliveryWaitTicks) {
        this.enclosure = enclosure;
        this.fields = fields;
        this.deliveryProbs = deliveryProbs;
        this.avgDeliveryWaitTicks = avgDeliveryWaitTicks;
        this.random = new Random();
        this.randgen = new Random(1234);  // Set a seed to replicate behavior.
        this.setName("Delivery Thread");
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Calculate wait ticks using the formula:
                // your_wait_ticks = 2 * randgen.nextDouble() * avgDeliveryWaitTicks
                long waitTicks = (long)(2 * randgen.nextDouble() * avgDeliveryWaitTicks);
                sleep(waitTicks * Constants.TICK_DURATION_MS);

                // Delivery logic begins.
                Map<String, Integer> delivery = new HashMap<>();
                // Initialize delivery counts for each animal type.
                for (String animal : fields.keySet()) {
                    delivery.put(animal, 0);
                }
                int totalAnimals = 10;
                // Compute total weight from the delivery probabilities.
                double totalWeight = 0.0;
                for (String animal : fields.keySet()) {
                    totalWeight += deliveryProbs.getOrDefault(animal, 0.0);
                }
                // For each of 10 animals, select an animal type based on the weighted probabilities.
                for (int i = 0; i < totalAnimals; i++) {
                    double r = random.nextDouble() * totalWeight;
                    double cumulative = 0.0;
                    for (String animal : fields.keySet()) {
                        cumulative += deliveryProbs.getOrDefault(animal, 0.0);
                        if (r < cumulative) {
                            delivery.put(animal, delivery.get(animal) + 1);
                            break;
                        }
                    }
                }

                System.out.println("(Ticks :" + Clock.getTickCount() + ") Delivery: Buyer waiting counts per animal type:");
                String prioritizedAnimal = null;
                int maxWaiting = -1;
                for (Map.Entry<String, List<Field>> entry : fields.entrySet()) {
                    String animalType = entry.getKey();
                    int totalWaiting = 0;
                    for (Field field : entry.getValue()) {
                        totalWaiting += field.getWaitingBuyers();
                    }
                    System.out.println("   " + animalType + ": " + totalWaiting + " waiting buyers.");
                    if (totalWaiting > maxWaiting) {
                        maxWaiting = totalWaiting;
                        prioritizedAnimal = animalType;
                    }
                }
                if (prioritizedAnimal != null) {
                    System.out.println("(Ticks :" + Clock.getTickCount() + ") Farmer will prioritize: " + prioritizedAnimal);
                } else {
                    System.out.println("(Ticks :" + Clock.getTickCount() + ") No animal is prioritized (no waiting buyers).");
                }

                enclosure.addAnimals(delivery);
                System.out.println("(Ticks :" + Clock.getTickCount() + ") " + getName() + " Delivery of: " + delivery + " has arrived.");
                System.out.println("(Ticks :" + Clock.getTickCount() + ") Current animal counts per field:");
                for (Map.Entry<String, List<Field>> entry : fields.entrySet()) {
                    for (Field field : entry.getValue()) {
                        System.out.println("   " + field.getName() + " (Field " + field.getFieldIndex() + ") has " + field.getAnimalCount() + " animals.");
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
