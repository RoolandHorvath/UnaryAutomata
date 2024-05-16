import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Main {

    public static void main(String[] args) {
        System.out.println("Select operation:");
        System.out.println("0) Output DFAs with a specified number of end states");
        System.out.println("1) Concatenate automata");
        System.out.println("2) Square automata");
        System.out.println("3) Positive closure operation on automata");
        System.out.println("4) Process a specific alldfahsf file");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 0:
                handleOutputDFAsWithEndStates(scanner);
                break;
            case 1:
            case 2:
            case 3:
                handleOperations(scanner, choice);
                break;
            case 4:
                System.out.println("Enter the specific file name (e.g., alldfahsf_10.txt):");
                String filename = scanner.nextLine();
                handleSpecificFile(filename);
                break;
            default:
                System.out.println("Invalid choice.");
                break;
        }
        scanner.close();
    }

    private static void handleSpecificFile(String filename) {
        String path = "UnaryAutomata\\output\\" + filename;
        System.out.println("Processing file: " + filename);
        handleOperationsOnFile(path);
    }

    private static void handleOperations(Scanner scanner, int operationType) {
        System.out.println("Enter the input file path:");
        String inputPath = scanner.nextLine();
        if (operationType == 1) {
            handleConcatenateOperation(inputPath);
        } else {
            handleUnaryOrBinaryOperation(inputPath, operationType);
        }
    }

    // Commented due to no usage - uncomment otherwise

    // Generate and concatenate DFA files
    // private static void generateAndConcatenateDFAFiles() throws IOException {
    //     String inputPath = new File("D:/upjs/ThesisAutomata/UnaryAutomata/output").getAbsolutePath();
    //     String outputPath = new File(inputPath).getAbsolutePath();

    //     new File(outputPath).mkdirs();

    //     for (int n = 2; n <= 16; n++) {
    //         StringBuilder allDfaContent = new StringBuilder();
    //         boolean isFirstOneEndStateFileIncluded = false;

    //         int currentS = n;
    //         int currentF = currentS / 2;

    //         while (currentS >= 2 && currentF >= 1) {
    //             String fileName = String.format("unarydfa%d_filtered_%d.txt", currentS, currentF);
    //             File file = new File(inputPath, fileName);
    //             if (file.exists()) {
    //                 String content = new String(Files.readAllBytes(Paths.get(inputPath, fileName)));
    //                 allDfaContent.append(content).append("\n");
    //                 System.out.println("Including: " + fileName);

    //                 if (currentF == 1) {
    //                     isFirstOneEndStateFileIncluded = true;
    //                 }
    //             } else {
    //                 System.out.println("File not found: " + fileName);
    //             }

    //             currentS--;
    //             if (currentF > 1 || !isFirstOneEndStateFileIncluded) {
    //                 currentF--;
    //             }

    //             if (isFirstOneEndStateFileIncluded && currentF == 1) {
    //                 break;
    //             }
    //         }

    //         if (allDfaContent.length() > 0) {
    //             String outputFile = Paths.get(outputPath, "alldfahsf_" + n + ".txt").toString();
    //             writeFile(outputFile, allDfaContent.toString());
    //             System.out.println("Concatenated file alldfahsf_" + n + " created.");
    //         } else {
    //             System.out.println("No content for alldfahsf_" + n);
    //         }
    //     }
    // }

    // Commented due to no usage - uncomment otherwise

    // Write content to a file
    // private static void writeFile(String filePath, String content) throws IOException {
    //     try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
    //         writer.write(content);
    //     } catch (IOException e) {
    //         System.err.println("Error writing to file: " + e.getMessage());
    //     }
    // }
    
    // Commented due to no usage - uncomment otherwise

    // Automate operations on all "alldfahsf" files
    // private static void automateOperationsOnAlldfahsfFiles() {
    //     String outputPath = new File("UnaryAutomata/output").getAbsolutePath();
    //     File dir = new File(outputPath);
    //     File[] files = dir.listFiles((d, name) -> name.startsWith("alldfahsf_") && name.endsWith(".txt"));

    //     if (files != null) {
    //         for (File file : files) {
    //             System.out.println("Processing file: " + file.getName());
    //             handleOperationsOnFile(file.getAbsolutePath());
    //         }
    //     } else {
    //         System.out.println("No files found to process.");
    //     }
    // }

    // Handle operations on a specific file
    private static void handleOperationsOnFile(String inputFilePath) {
        AutomatonLoader loader = new AutomatonLoader();
        try {
            List<String> automatonDefinitions = loader.loadAutomatonDefinitionsFromFile(inputFilePath);
            List<Automaton> automata = loader.parseAutomata(automatonDefinitions);

            performUnaryOperations(automata, inputFilePath, 2); // Square
            performUnaryOperations(automata, inputFilePath, 3); // Positive closure
            performConcatenation(automata, inputFilePath); // Concatenation

        } catch (IOException e) {
            System.err.println("Error processing file " + inputFilePath + ": " + e.getMessage());
        }
    }

    // Perform unary operations on automata (square or positive closure)
    private static void performUnaryOperations(List<Automaton> automata, String inputFilePath, int operationType) throws IOException {
        Map<String, List<String>> complexityMap = new HashMap<>();

        for (Automaton automaton : automata) {
            Automaton result = null;
            if (operationType == 2) { // Square
                result = AutomatonOperations.square(automaton);
            } else if (operationType == 3) { // positive closure
                result = AutomatonOperations.plus(automaton);
            }

            if (result != null) {
                Automaton dfa = AutomatonOperations.convertToDFA(result);
                Automaton minimized = AutomatonOperations.minimizeDFA(dfa);
                String key = "(" + minimized.getTotalStates() + "," + minimized.getEndStates().size() + ")";
                complexityMap.computeIfAbsent(key, k -> new ArrayList<>()).add(minimized.toString());
            }
        }

        String operationName = operationType == 2 ? "square" : "plus";
        String outputFilename = inputFilePath.replace(".txt", "_" + operationName + ".txt");
        saveComplexityResults(complexityMap, outputFilename, operationName);
    }

    // Commented due to no usage - uncomment otherwise

    // Update the complexity map with an automaton
    // private static void updateComplexityMap(Map<String, List<String>> complexityMap, Automaton automaton) {
    //     String key = "(" + automaton.getTotalStates() + "," + automaton.getEndStates().size() + ")";
    //     String value = automaton.toCompactString();
    //     complexityMap.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    // }

    // Perform concatenation of automata
    private static void performConcatenation(List<Automaton> automata, String inputFilePath) throws IOException {
        Map<String, List<String>> complexityMap = new HashMap<>();

        for (int i = 0; i < automata.size(); i++) {
            for (int j = 0; j < automata.size(); j++) {
                Automaton concatenated = AutomatonOperations.concatenate(automata.get(i), automata.get(j));
                if (concatenated != null) {
                    Automaton dfa = AutomatonOperations.convertToDFA(concatenated);
                    Automaton minimized = AutomatonOperations.minimizeDFA(dfa);
                    String key = "(" + minimized.getTotalStates() + "," + minimized.getEndStates().size() + ")";
                    complexityMap.computeIfAbsent(key, k -> new ArrayList<>()).add(minimized.toString());
                }
            }
        }

        String concatenateOutputFilename = inputFilePath.replace(".txt", "_concatenate.txt");
        saveComplexityResults(complexityMap, concatenateOutputFilename, "concatenation");
    }

    // Save the complexity results to a file
    private static void saveComplexityResults(Map<String, List<String>> complexityMap, String outputFile, String operationName) throws IOException {
        // Sort the complexity map
        Map<String, List<String>> sortedMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String key1, String key2) {
                String[] parts1 = key1.substring(1, key1.length() - 1).split(",");
                String[] parts2 = key2.substring(1, key2.length() - 1).split(",");
                int s1 = Integer.parseInt(parts1[0].trim());
                int f1 = Integer.parseInt(parts1[1].trim());
                int s2 = Integer.parseInt(parts2[0].trim());
                int f2 = Integer.parseInt(parts2[1].trim());
                if (s1 != s2) {
                    return Integer.compare(s2, s1);
                } else {
                    return Integer.compare(f2, f1);
                }
            }
        });
        sortedMap.putAll(complexityMap);

        // Write the results to a file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write("Summary of Results for " + operationName + " on unary DFAs:\n");
            for (Map.Entry<String, List<String>> entry : sortedMap.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue().size() + "\n");
            }
            writer.write("\nDetailed Automata:\n");

            for (Map.Entry<String, List<String>> entry : sortedMap.entrySet()) {
                writer.write("Details for " + entry.getKey() + ":\n");
                for (String automaton : entry.getValue()) {
                    writer.write(automaton + "\n");
                }
                writer.newLine();
            }
        }
        System.out.println(operationName.substring(0, 1).toUpperCase() + operationName.substring(1) + " results saved to: " + outputFile);
    }

    // Handle output of DFAs with a specified number of end states
    private static void handleOutputDFAsWithEndStates(Scanner scanner) {
        System.out.println("Enter the filename from which to load automata:");
        String filename = scanner.next();
        System.out.println("Enter the desired number of end states:");
        int endStates = scanner.nextInt();

        String basePath = new File("").getAbsolutePath();
        String baseInputPath = basePath + "/UnaryAutomata/UnaryAutomataList/";
        String baseOutputPath = basePath + "/UnaryAutomata/output/";

        String inputFilePath = baseInputPath + "/" + filename;
        String outputFilePath = baseOutputPath + "/" + filename.replace(".txt", "_filtered_" + endStates + ".txt");
        System.out.println("Input path: " + inputFilePath);
        System.out.println("Output path: " + outputFilePath);

        AutomatonLoader loader = new AutomatonLoader();
        try {
            List<Automaton> automata = loader.loadAutomataFromFile(inputFilePath);
            List<Automaton> filteredAutomata = loader.filterAutomataByEndStates(automata, endStates);

            File outputDir = new File(baseOutputPath);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            loader.saveAutomataToFile(filteredAutomata, outputFilePath);
            System.out.println("Filtered automata saved to: " + outputFilePath);
        } catch (IOException e) {
            System.err.println("An error occurred while loading and saving DFA: " + e.getMessage());
        }
    }

    private static void handleConcatenateOperation(String inputPath) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the first input file path:");
        String firstFilename = inputPath;

        System.out.println("Enter the second input file path:");
        String secondFilename = scanner.nextLine();

        try {
            AutomatonLoader loader = new AutomatonLoader();
            List<Automaton> firstAutomata = loader.loadAutomataFromFile(firstFilename);
            List<Automaton> secondAutomata = loader.loadAutomataFromFile(secondFilename);

            System.out.println("Loaded " + firstAutomata.size() + " automata from the first file.");
            System.out.println("Loaded " + secondAutomata.size() + " automata from the second file.");

            Map<String, List<String>> complexityMap = new HashMap<>();
            List<Automaton> concatenatedAutomata = new ArrayList<>();

            for (Automaton automaton1 : firstAutomata) {
                for (Automaton automaton2 : secondAutomata) {
                    Automaton concatenated = AutomatonOperations.concatenate(automaton1, automaton2);
                    if (concatenated != null) {
                        Automaton dfa = AutomatonOperations.convertToDFA(concatenated);
                        Automaton minimized = AutomatonOperations.minimizeDFA(dfa);
                        concatenatedAutomata.add(minimized);

                        String key = "(" + minimized.getTotalStates() + "," + minimized.getEndStates().size() + ")";
                        String value = automaton1.toCompactString() + "\t" + automaton2.toCompactString();
                        complexityMap.computeIfAbsent(key, k -> new ArrayList<>()).add(value);

                        System.out.println("Concatenated and processed a pair: " + key);
                    }
                }
            }

            if (concatenatedAutomata.isEmpty()) {
                System.out.println("No automata were processed.");
            } else {
                String outputFile = firstFilename.replace(".txt", "_concatenation_results.txt");
                saveResultsAndDisplaySummary(complexityMap, outputFile, "concatenation");
                System.out.println("Concatenation results saved to " + outputFile);
            }
        } catch (IOException e) {
            System.err.println("Error during concatenation operation: " + e.getMessage());
        }
        scanner.close();
    }

    // Handle unary or binary operations on automata
    private static void handleUnaryOrBinaryOperation(String inputFilePath, int operationType) {
        AutomatonLoader loader = new AutomatonLoader();
        try {
            List<Automaton> automata = loader.loadAutomataFromFile(inputFilePath);
            if (automata.isEmpty()) {
                System.out.println("No automata found in: " + inputFilePath);
                return;
            }

            List<Automaton> processedAutomata = new ArrayList<>();
            for (Automaton automaton : automata) {
                Automaton result = null;
                if (operationType == 2) { // Square
                    result = AutomatonOperations.square(automaton);
                } else if (operationType == 3) { // Positive closure
                    result = AutomatonOperations.plus(automaton);
                }

                if (result != null) {
                    processedAutomata.add(result);
                }
            }

            if (!processedAutomata.isEmpty()) {
                String outputFilename = inputFilePath.replace(".txt", "_processed_" + operationType + ".txt");
                loader.saveAutomataToFile(processedAutomata, outputFilename);
                System.out.println("Processed " + processedAutomata.size() + " automata into " + outputFilename);
            } else {
                System.out.println("No processed automata for " + inputFilePath);
            }
        } catch (IOException e) {
            System.err.println("Error processing file " + inputFilePath + ": " + e.getMessage());
        }
    }

    // Save results and display summary
    private static void saveResultsAndDisplaySummary(Map<String, List<String>> complexityMap, String outputFile, String operationName) throws IOException {
        // Sort the complexity map
        Map<String, List<String>> sortedMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String key1, String key2) {
                String[] parts1 = key1.substring(1, key1.length() - 1).split(",");
                String[] parts2 = key2.substring(1, key2.length() - 1).split(",");
                int s1 = Integer.parseInt(parts1[0].trim());
                int f1 = Integer.parseInt(parts1[1].trim());
                int s2 = Integer.parseInt(parts2[0].trim());
                int f2 = Integer.parseInt(parts2[1].trim());
                if (s1 != s2) {
                    return Integer.compare(s2, s1);
                } else {
                    return Integer.compare(f2, f1);
                }
            }
        });
        sortedMap.putAll(complexityMap);

        // Write the results to a file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write("Results for " + operationName + " on unary DFAs:\n");
            for (Map.Entry<String, List<String>> entry : sortedMap.entrySet()) {
                writer.write(entry.getKey() + ":\n");
                for (String value : entry.getValue()) {
                    writer.write(value + "\n");
                }
                writer.newLine();

                System.out.println(entry.getKey() + ": " + entry.getValue().size());
            }
        }
        System.out.println(operationName.substring(0, 1).toUpperCase() + operationName.substring(1) + " results saved to: " + outputFile);
    }
}