# CSC1101-Concurrency
-Farm Simulation
  A Java-based simulation that models a dynamic, concurrent farm environment operating 24/7. This project showcases advanced concurrency concepts by simulating various farm activities—such as animal deliveries, field stocking, and animal purchases—using multiple threads and configurable parameters.
  
  Overview
  The simulation uses ticks to represent time (with 1000 ticks per day) and simulates a farm that has dedicated fields for pigs, cows, sheep, llamas, and chickens. Key features include:
  
  Animal Deliveries:
  A delivery thread brings in 10 animals at random intervals (on average every 100 ticks) using weighted probabilities that can be configured via user input or default values.
  
  Farmers:
  Multiple farmer threads collect animals from a shared enclosure and stock the corresponding fields. Farmers prioritize stocking fields with the highest demand (i.e., more waiting buyers) and take scheduled breaks to simulate real-world workload balancing.
  
  Buyers:
  Buyer threads attempt to purchase animals from fields. If a field is empty or currently being restocked, buyers wait until the animal becomes available. Buyer purchase probabilities are configurable, ensuring varied demand across animal types.
  
  Real-Time Logging:
  A dedicated logging thread periodically prints performance statistics (e.g., average wait times for buyers and work times for farmers) to the terminal.
  
  Concurrency Controls:
  The simulation leverages Java’s threading, synchronization, ReentrantLocks (with fairness enabled), and condition variables to ensure smooth operation, prevent resource starvation, and maintain data consistency.
  
  Features
  Dynamic Configuration:
  User inputs allow for the dynamic setting of key parameters such as the number of farmers, fields per animal type, buyers, average delivery wait time, and probability values for both deliveries and purchases. Default values are centralized in the Constants.java file.
  
  Weighted Random Selection:
  Both the delivery and buyer selection processes use weighted random algorithms to determine the distribution of animals, providing a realistic and unpredictable simulation of farm operations.
  
  Fairness and Starvation Prevention:
  Careful design of locking and waiting mechanisms ensures that buyers and farmers are served in a fair manner, reducing prolonged wait times and resource monopolization.
  
  Modular and Extensible Design:
  The code is organized into distinct classes (e.g., Delivery, Farmer, Buyer, Field, Enclosure, LoggingThread, and Statistics), making it easy to understand, maintain, and extend.
  
  Getting Started
  Prerequisites
  Java SE 21 (LTS) or later
  Linux/Ubuntu environment recommended for execution
  Compiling and Running
  A bash script (run.sh) is provided for easy compilation and execution:
  
  bash
  #!/bin/bash
  # Compile all Java source files
  echo "Compiling Java files..."
  javac *.java
  
  # Check if compilation succeeded
  if [ $? -ne 0 ]; then
    echo "Compilation failed. Please fix errors and try again."
    exit 1
  fi
  
  # Run the FarmSimulation program
  echo "Running FarmSimulation..."
  java FarmSimulation
  Make sure to give the script execution permission:
  
  bash
  chmod +x run.sh
  ./run.sh
  Alternatively, you can compile manually:
  javac *.java
  java FarmSimulation
  Project Structure
  Constants.java: Centralized default configuration values.
  FarmSimulation.java: Main entry point; handles user input for dynamic configuration.
  Delivery.java: Manages animal deliveries with a configurable, probabilistic wait time.
  Enclosure.java: Shared resource where delivered animals are stored.
  Field.java: Represents individual animal fields with limited capacity, synchronized for safe concurrent access.
  Farmer.java: Farmer threads that collect animals and stock fields based on demand.
  Buyer.java: Buyer threads that purchase animals and wait if necessary.
  LoggingThread.java & Statistics.java: Collect and display performance statistics.
  
  Contributing:
  Contributions are welcome! Feel free to fork the repository, make enhancements (e.g., additional statistical analysis, alternative concurrency strategies), and submit pull requests.
