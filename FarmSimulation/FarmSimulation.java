import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class FarmSimulation {
    public static void main(String[] args) {
        Enclosure enclosure = new Enclosure();
        // Map animal types to a list of fields.
        Map<String, List<Field>> fields = new HashMap<>();
        Scanner scanner = new Scanner(System.in);

        // Input number of farmers.
        int numFarmers = Constants.DEFAULT_FARMERS;
        while (true) {
            System.out.print("Input the number of farmers working on the farm (default " + Constants.DEFAULT_FARMERS + "): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                break;
            }
            try {
                numFarmers = Integer.parseInt(input);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number or press enter for default (" + Constants.DEFAULT_FARMERS + ").");
            }
        }

        // Input number of fields per animal type.
        int numFieldsPerAnimal = Constants.DEFAULT_FIELDS_PER_ANIMAL;
        while (true) {
            System.out.print("Input the number of fields per animal type (default " + Constants.DEFAULT_FIELDS_PER_ANIMAL + "): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                break;
            }
            try {
                numFieldsPerAnimal = Integer.parseInt(input);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number or press enter for default (" + Constants.DEFAULT_FIELDS_PER_ANIMAL + ").");
            }
        }

        // Input animals types.
        System.out.println("Input the types of animals. When done, type 'DONE' or type 'default' to add default animals:");
        while (true) {
            System.out.print("Enter animal type (or 'DONE' to finish, or 'default' to add defaults): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty() || input.equalsIgnoreCase("DONE")) {
                break;
            } else if (input.toLowerCase().contains("default")) {
                String[] defaultAnimals = {"Pigs", "Cows", "Sheep", "Llamas", "Chickens"};
                for (String defAnimal : defaultAnimals) {
                    String normalized = normalizeAnimal(defAnimal);
                    if (!fields.containsKey(normalized)) {
                        List<Field> fieldList = new ArrayList<>();
                        for (int i = 1; i <= numFieldsPerAnimal; i++) {
                            fieldList.add(new Field(normalized, 5, i));
                        }
                        fields.put(normalized, fieldList);
                    }
                }
                System.out.print("Default animals added. Would you like to add more animals? (yes/no): ");
                String response = scanner.nextLine().trim();
                if (response.equalsIgnoreCase("no") || response.equalsIgnoreCase("n")) {
                    break;
                } else {
                    continue;
                }
            } else {
                String normalized = normalizeAnimal(input);
                if (fields.containsKey(normalized)) {
                    System.out.println(normalized + " already exists. Skipping duplicate.");
                } else {
                    List<Field> fieldList = new ArrayList<>();
                    for (int i = 1; i <= numFieldsPerAnimal; i++) {
                        fieldList.add(new Field(normalized, 5, i));
                    }
                    fields.put(normalized, fieldList);
                }
            }
        }
        
        // If no animal types were entered, add default animals.
        if (fields.isEmpty()) {
            String[] defaultAnimals = {"Pigs", "Cows", "Sheep", "Llamas", "Chickens"};
            for (String animal : defaultAnimals) {
                String normalized = normalizeAnimal(animal);
                List<Field> fieldList = new ArrayList<>();
                for (int i = 1; i <= numFieldsPerAnimal; i++) {
                    fieldList.add(new Field(normalized, 5, i));
                }
                fields.put(normalized, fieldList);
            }
        }
        
        // Input to adjust delivery probabilities for animal types.
        Map<String, Double> deliveryProbs = new HashMap<>();
        System.out.print("Would you like to adjust delivery probabilities? (yes/no): ");
        String adjustProbs = scanner.nextLine().trim();
        if (adjustProbs.equalsIgnoreCase("yes") || adjustProbs.equalsIgnoreCase("y")) {
            for (String animal : fields.keySet()) {
                while (true) {
                    System.out.print("Enter probability (0 to 1) for " + animal + " delivery: ");
                    try {
                        double prob = Double.parseDouble(scanner.nextLine().trim());
                        if (prob < 0 || prob > 1) {
                            System.out.println("Please enter a value between 0 and 1.");
                        } else {
                            deliveryProbs.put(animal, prob);
                            break;
                        }
                    } catch (NumberFormatException nfe) {
                        System.out.println("Invalid input. Please enter a numerical value between 0 and 1.");
                    }
                }
            }
        } else {
            // Use equal probabilities.
            int count = fields.keySet().size();
            double equalProb = 1.0 / count;
            for (String animal : fields.keySet()) {
                deliveryProbs.put(animal, equalProb);
            }
        }
        
        // Input to adjust buyer probabilities.
        Map<String, Double> buyerProbs = new HashMap<>();
        System.out.print("Would you like to adjust buyer probabilities? (yes/no): ");
        String adjustBuyer = scanner.nextLine().trim();
        if (adjustBuyer.equalsIgnoreCase("yes") || adjustBuyer.equalsIgnoreCase("y")) {
            for (String animal : fields.keySet()) {
                while (true) {
                    System.out.print("Enter probability (0 to 1) for " + animal + " buyer: ");
                    try {
                        double prob = Double.parseDouble(scanner.nextLine().trim());
                        if (prob < 0 || prob > 1) {
                            System.out.println("Please enter a value between 0 and 1.");
                        } else {
                            buyerProbs.put(animal, prob);
                            break;
                        }
                    } catch (NumberFormatException nfe) {
                        System.out.println("Invalid input. Please enter a numerical value between 0 and 1.");
                    }
                }
            }
        } else {
            // Use default probabilities from Constants.
            for (String animal : fields.keySet()) {
                double defaultProb;
                if (animal.equalsIgnoreCase("Chickens")) {
                    defaultProb = Constants.PROB_CHICKENS;
                } else if (animal.equalsIgnoreCase("Cows")) {
                    defaultProb = Constants.PROB_COWS;
                } else if (animal.equalsIgnoreCase("Sheep")) {
                    defaultProb = Constants.PROB_SHEEP;
                } else if (animal.equalsIgnoreCase("Llamas")) {
                    defaultProb = Constants.PROB_LLAMAS;
                } else if (animal.equalsIgnoreCase("Pigs")) {
                    defaultProb = Constants.PROB_PIGS;
                } else {
                    defaultProb = Constants.PROB_CUSTOM;
                }
                buyerProbs.put(animal, defaultProb);
            }
        }
        
        // Input to set the average delivery wait time (in ticks).
        int avgDeliveryWait = Constants.DEFAULT_AVG_DELIVERY_WAIT;
        System.out.print("Enter average delivery wait time in ticks (default " + Constants.DEFAULT_AVG_DELIVERY_WAIT + "): ");
        String avgWaitInput = scanner.nextLine().trim();
        if (!avgWaitInput.isEmpty()) {
            try {
                avgDeliveryWait = Integer.parseInt(avgWaitInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Using default average wait time of " + Constants.DEFAULT_AVG_DELIVERY_WAIT + " ticks.");
                avgDeliveryWait = Constants.DEFAULT_AVG_DELIVERY_WAIT;
            }
        }
        
        // Input to set the number of buyers (must be >1 and <16).
        int numBuyers = Constants.DEFAULT_BUYERS;
        while (true) {
            System.out.print("Input the number of buyers who can visit the farm (default " + Constants.DEFAULT_BUYERS + ", must be >1 and <16) (Check CPU Threads before deciding, else use default): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                break;
            }
            try {
                numBuyers = Integer.parseInt(input);
                if (numBuyers > 1 && numBuyers < 16) {
                    break;
                } else {
                    System.out.println("Please enter a number greater than 1 and less than 16.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        printInitialAnimalCount(fields);

        new Delivery(enclosure, fields, deliveryProbs, avgDeliveryWait).start();
        for (int i = 0; i < numFarmers; i++) {
            new Farmer(enclosure, fields).start();
        }
        for (int i = 0; i < numBuyers; i++) {
            new Buyer(fields, buyerProbs).start();
        }
        new LoggingThread().start();
    }

    // Normalizes an animal name: first letter uppercase, rest lowercase;
    // if not "Sheep" and not already plural, appends an "s".
    private static String normalizeAnimal(String animal) {
        if (animal.isEmpty()) {
            return animal;
        }
        String normalized = animal.substring(0, 1).toUpperCase() + animal.substring(1).toLowerCase();
        if (!normalized.equals("Sheep") && !normalized.endsWith("s")) {
            normalized = normalized + "s";
        }
        return normalized;
    }

    // Prints initial animal counts across all fields.
    private static void printInitialAnimalCount(Map<String, List<Field>> fields) {
        System.out.println("(Ticks :" + Clock.getTickCount() + ") Initial animal counts in fields:");
        for (Map.Entry<String, List<Field>> entry : fields.entrySet()) {
            int total = 0;
            for (Field f : entry.getValue()) {
                total += f.getAnimalCount();
            }
            System.out.println("(Ticks :" + Clock.getTickCount() + ") " + entry.getKey() + " = " + total);
        }
    }
}
