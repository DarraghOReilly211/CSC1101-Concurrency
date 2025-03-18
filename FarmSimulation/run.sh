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
