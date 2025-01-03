package pal;

import java.io.*;
import java.util.*;

public class TestRunner {

    private static final String DATA_DIR = "data";
    private static final String INPUT_PREFIX = "pub";
    private static final String INPUT_SUFFIX = ".in";
    private static final String OUTPUT_SUFFIX = ".out";

    public static void main(String[] args) throws IOException {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists() || !dataDir.isDirectory()) {
            System.err.println("Data directory not found: " + DATA_DIR);
            return;
        }

        int testsRun = 0;
        int testsPassed = 0;

        // Loop through each input file in the data directory
        for (File inputFile : dataDir.listFiles((dir, name) -> name.endsWith(INPUT_SUFFIX))) {
            String testName = inputFile.getName().replace(INPUT_SUFFIX, "");
            File expectedOutputFile = new File(DATA_DIR + "/" + testName + OUTPUT_SUFFIX);

            if (!expectedOutputFile.exists()) {
                System.err.println("Missing expected output file for " + testName);
                continue;
            }

            // Read input from the input file
            List<String> inputLines = readFile(inputFile);
            List<String> expectedOutputLines = readFile(expectedOutputFile);

            // Run the LakeDistrictInspection with input lines
            String programOutput = runLakeDistrictInspection(inputLines);

            // Compare program output with expected output
            String expectedOutput = String.join("\n", expectedOutputLines).trim();
            boolean testPassed = programOutput.trim().equals(expectedOutput);

            // Output result of the test
            System.out.println("Test " + testName + ": " + (testPassed ? "PASSED" : "FAILED"));
            if (!testPassed) {
                System.out.println("Expected:\n" + expectedOutput);
                System.out.println("Found:\n" + programOutput);
            }

            testsRun++;
            if (testPassed) testsPassed++;
        }

        // Print summary
        System.out.println("\nSummary:");
        System.out.println("Tests run: " + testsRun);
        System.out.println("Tests passed: " + testsPassed);
        System.out.println("Tests failed: " + (testsRun - testsPassed));
    }

    private static List<String> readFile(File file) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    private static String runLakeDistrictInspection(List<String> inputLines) {
        try {
            // Prepare to read input as if it was from System.in
            ByteArrayInputStream inputStream = new ByteArrayInputStream(String.join("\n", inputLines).getBytes());
            System.setIn(inputStream);

            // Capture System.out output
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));

            // Run the main method from LakeDistrictInspection
            Main.main(new String[0]);

            // Restore System.in and System.out
            System.setIn(System.in);
            System.setOut(originalOut);

            return outputStream.toString().trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}

