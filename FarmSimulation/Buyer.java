import java.util.Map;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class Buyer extends Thread {
    private static int buyerCounter = 0;
    private final Map<String, List<Field>> fields;
    private final Map<String, Double> buyerProbabilities;
    private final Random random = new Random();
    private final int buyerId;

    public Buyer(Map<String, List<Field>> fields, Map<String, Double> buyerProbabilities) {
        this.fields = fields;
        this.buyerProbabilities = buyerProbabilities;
        buyerCounter++;
        this.buyerId = buyerCounter;
        this.setName("Buyer " + buyerId);
    }

    @Override
    public void run() {
        while (true) {
            try {
                int preWait = random.nextInt(10);
                sleep(preWait * Constants.TICK_DURATION_MS);

                // Use weighted random selection for animal type.
                String selectedAnimalType = selectAnimalType();
                List<Field> fieldList = fields.get(selectedAnimalType);
                if (fieldList == null || fieldList.isEmpty()) {
                    continue;
                }
                // Pick one field randomly from the list.
                Field field = fieldList.get(random.nextInt(fieldList.size()));

                long startTick = Clock.getTickCount();
                // Buyer waits if field is being stocked or empty.
                if (field.buyAnimal(preWait)) {
                    long waited = Clock.getTickCount() - startTick;
                    Farmer responsibleFarmer = field.getLastStockingFarmer();
                    String sellerName;
                    if (responsibleFarmer != null) {
                        sellerName = responsibleFarmer.getName();
                    } else if (!Farmer.farmerNames.isEmpty()) {
                        sellerName = Farmer.farmerNames.get(random.nextInt(Farmer.farmerNames.size()));
                    } else {
                        sellerName = "Unknown Farmer";
                    }
                    System.out.println("(Ticks :" + Clock.getTickCount() + ") " + getName() + " bought 1 " + selectedAnimalType + " from " + sellerName);
                    Statistics.logBuyerWaitTime((int)waited);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // Weighted random selection based on input buyer probabilities.
    private String selectAnimalType() {
        List<String> animalTypes = new ArrayList<>(fields.keySet());
        double totalWeight = 0;
        double[] weights = new double[animalTypes.size()];
        for (int i = 0; i < animalTypes.size(); i++) {
            String type = animalTypes.get(i);
            double weight = buyerProbabilities.getOrDefault(type, defaultBuyerProb(type));
            weights[i] = weight;
            totalWeight += weight;
        }
        double r = random.nextDouble() * totalWeight;
        double cumulative = 0;
        for (int i = 0; i < weights.length; i++) {
            cumulative += weights[i];
            if (r < cumulative) {
                return animalTypes.get(i);
            }
        }
        return animalTypes.get(animalTypes.size() - 1);
    }
    
    // If no input fallback to default probabilities defined in Constants.
    private double defaultBuyerProb(String type) {
        if (type.equalsIgnoreCase("Chickens")) {
            return Constants.PROB_CHICKENS;
        } else if (type.equalsIgnoreCase("Cows")) {
            return Constants.PROB_COWS;
        } else if (type.equalsIgnoreCase("Sheep")) {
            return Constants.PROB_SHEEP;
        } else if (type.equalsIgnoreCase("Llamas")) {
            return Constants.PROB_LLAMAS;
        } else if (type.equalsIgnoreCase("Pigs")) {
            return Constants.PROB_PIGS;
        } else {
            return Constants.PROB_CUSTOM;
        }
    }
}
