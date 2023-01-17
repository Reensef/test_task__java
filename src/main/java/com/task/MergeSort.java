package com.task;

import picocli.CommandLine;
import picocli.CommandLine.*;

import java.io.*;
import java.util.Scanner;


@Command
public class MergeSort implements Runnable {
    @Option(names = {"-i", "--integer"}, description = "if input files contains integers")
    private boolean isParamTypeInteger;
    @Option(names = {"-s", "--string"}, description = "if input files contains strings")
    private boolean isParamTypeString;
    @Option(names = {"-a", "--ascending"}, description = "(default) if input files are sorted in ascending order")
    private boolean isParamOrderAscending;
    @Option(names = {"-d", "--descending"}, description = "if input files are sorted in descending order")
    private boolean isParamOrderDescending;
    @Parameters(index = "0", defaultValue = "null", description="one output file")
    private String outfile;
    @Parameters(index = "1..*", description = "one or many input file")
    private String[] inputfiles;
    private String[] row; // ex. [2, null, 4], null if file is finished
    private FileInputStream[] allFIS;
    private Scanner[] allScanners;
    private FileOutputStream fos;

    public static void main(String[] args) {
        CommandLine.run(new MergeSort(), args);
    }

    @Command(name = "--help", description = "for more information")
    public void help() {
        System.out.println("Order params:");
        System.out.println("-a if input files are sorted in ascending order (default)");
        System.out.println("-d if input files are sorted in descending order");
        System.out.println("Type params:");
        System.out.println("-s if input files contains strings");
        System.out.println("-i if input files contains integers");
        System.out.println("After the params to be one OUTPUT file and many INPUT files");
        System.out.println("Example: -d -i out.txt in_1.txt in_2.txt");
    }

    @Override
    public void run() {
        if (!checkParams()) {
            // if params isn't correct that program stop
            return;
        }

        row = new String[inputfiles.length];
        allFIS = new FileInputStream[inputfiles.length];
        allScanners = new Scanner[inputfiles.length];

        try {
            openAllFisAndScanners();
            fos = new FileOutputStream(outfile);
        } catch (FileNotFoundException e) {
            System.out.printf("File not found: %s\n", e.getMessage());
            return;
        }

        displayStartInfo();

        readFilesInFirstTime();

        try {
            while (true) {
                boolean hasLine = false;
                for (String r : row) {
                    hasLine = hasLine || r != null;
                }
                if (!hasLine) {
                    // if all files are finished
                    break;
                }

                int idNextFile;
                if (isParamTypeInteger) {
                    idNextFile = writeIntLine();
                } else {
                    idNextFile = writeStringLine();
                }

                readLine(idNextFile);

            }

            fos.close();
            closeAllFisAndScanners();

        } catch (IOException e) {
            System.out.printf("Error: %s\n", e.getMessage());
        }

        System.out.println("Success");
    }

    private void displayStartInfo() {
        System.out.print("Merge sort started ");
        System.out.print("with params: ");
        if (isParamTypeInteger) {
            System.out.print("--integer ");
        } else if (isParamTypeString) {
            System.out.print("--string ");
        }

        if (isParamOrderAscending) {
            System.out.print("--ascending ");
        } else if (isParamOrderDescending) {
            System.out.print("--descending ");
        }
        System.out.print("\n");
    }

    private void readFilesInFirstTime() {
        for (int i = 0; i < inputfiles.length; i++) {
            readLine(i);
        }
    }

    private void readLine(int fileId) {
        Scanner scanner = allScanners[fileId];
        if (scanner.hasNextLine()) {
            row[fileId] = scanner.nextLine();
        } else {
            row[fileId] = null;
        }
    }

    private void openAllFisAndScanners() throws FileNotFoundException {
        for (int i = 0; i < inputfiles.length; i++) {
            FileInputStream inputStream = new FileInputStream(inputfiles[i]);
            allFIS[i] = inputStream;
            allScanners[i] = new Scanner(inputStream, "UTF-8");
        }
    }

    private void closeAllFisAndScanners() throws IOException {
        for (int i = 0; i < inputfiles.length; i++) {
            allFIS[i].close();
            allScanners[i].close();
        }
    }

    private boolean checkParams() {
        /**
         @return: true if the user gave the parameters correctly
         */
        if (!isParamOrderAscending && !isParamOrderDescending) {
            isParamOrderAscending = true; // set default value
        }
        if (!isParamTypeString && !isParamTypeInteger) {
            System.out.println("No required parameters --strings or --integer.");
            return false;
        }
        if (outfile == null) {
            System.out.println("Output file not specified.");
            return false;
        }
        if (inputfiles == null) {
            System.out.println("Input file not specified.");
            return false;
        }

        return true;
    }

    private int writeIntLine() throws IOException {
        /**
         * @return file index in inputfiles for next read
         */
        int target = 0;
        if (isParamOrderAscending) {
            target = Integer.MAX_VALUE;
        } else if (isParamOrderDescending) {
            target = Integer.MIN_VALUE;
        }
        int targetId = -1;

        for (int i = 0; i < row.length; i++) {
            if (row[i] == null) {
                continue;
            }
            try {
                int temp = Integer.parseInt(row[i]);

                if (temp < target && isParamOrderAscending) {
                    target = temp;
                    targetId = i;
                } else if (temp > target && isParamOrderDescending) {
                    target = temp;
                    targetId = i;
                }
            } catch (NumberFormatException e) {
                // skip wrong lines
                Scanner scanner = allScanners[i];
                if (scanner.hasNextLine()) {
                    row[i] = scanner.nextLine();
                    i--;
                } else {
                    row[i] = null;
                }
            }
        }
        if (targetId != -1) {
            fos.write((target + "\n").getBytes());
        } else {
            targetId = 0;
        }

        return targetId;
    }

    private int writeStringLine() throws IOException {
        /**
         * @return file index in inputfiles for next read
         */
        String target = "";
        if (isParamOrderAscending) {
            target = "~";
        } else if (isParamOrderDescending) {
            target = "";
        }
        int targetId = -1;

        for (int i = 0; i < row.length; i++) {
            if (row[i] == null) {
                continue; // if file is finished
            }

            String temp = row[i];
            if (!temp.contains(" ") && !temp.equals("")) { // is right line
                // lexicographic comparison
                if (temp.compareTo(target) < 0 && isParamOrderAscending) {
                    target = temp;
                    targetId = i;
                } else if (temp.compareTo(target) > 0 &&
                        isParamOrderDescending) {
                    target = temp;
                    targetId = i;
                }
            } else {
                // skip wrong lines
                Scanner scanner = allScanners[i];
                if (scanner.hasNextLine()) {
                    row[i] = scanner.nextLine();
                    i--;
                } else {
                    row[i] = null;
                }
            }
        }

        if (targetId != -1) {
            fos.write((target + "\n").getBytes());
        } else {
            targetId = 0;
        }

        return targetId;
    }
}
