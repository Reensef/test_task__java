package com.task;

import picocli.CommandLine;
import picocli.CommandLine.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;


@Command
public class CommandHandler implements Runnable {
    @Option(names = {"-i" })
    private boolean isParamTypeInteger;
    @Option(names = {"-s" })
    private boolean isParamTypeString;
    @Option(names = {"-a" })
    private boolean isParamOrderAscending;
    @Option(names = {"-d" })
    private boolean isParamOrderDescending;
    @Parameters(index = "0")
    private String outfile;
    @Parameters(index = "1..*")
    private String[] inputfiles;


    String[] rows;
    FileInputStream[] allFIS;
    Scanner[] allScanners;


    public static void main(String[] args) {
        CommandLine.run(new CommandHandler(), args);
    }

    @Command(name = "--help")
    public void help() {
        System.out.println("This is help information");
    }

    @Override
    public void run() {
        if (!checkParams()) {
            // if params isn't correct that program stop
            return;
        }

        initWorkDataStructures();

        try {
            openAllFisAndScanners();
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found");
            return;
        }

        System.out.println("Merge sort started");

        readFilesInFirstTime();

        while (true) {
            boolean hasLine = false;
            for(Scanner scanner : allScanners) {
                hasLine = hasLine || scanner.hasNextLine();
            }
            if (!hasLine) {
                break;
            }

            int idNextFile;
            if (isParamTypeInteger) {
                idNextFile = findIdMinIntegerElem(rows);
            } else {
                idNextFile = findIdMinStringElem(rows);
            }

            // ascending or descending
            // write string in file

            readLineInFile(idNextFile);

        }

        System.out.println("End");


    }

    private void readFilesInFirstTime() {
        for(int i = 0; i < inputfiles.length; i++) {
            readLineInFile(i);
        }
    }

    private void readLineInFile(int fileId) {
        Scanner scanner = allScanners[fileId];
        if (scanner.hasNextLine()) {
            rows[fileId] = scanner.nextLine();
        } else {
            rows[fileId] = "";
        }
        System.out.println(rows[fileId]);
    }



    private void openAllFisAndScanners() throws FileNotFoundException {
        for (int i = 0; i < inputfiles.length; i++) {
            FileInputStream inputStream = new FileInputStream(inputfiles[i]);
            allFIS[i] = inputStream;
            allScanners[i] = new Scanner(inputStream, "UTF-8");
        }
    }

    private void initWorkDataStructures() {
        rows = new String[inputfiles.length];
        allFIS = new FileInputStream[inputfiles.length];
        allScanners = new Scanner[inputfiles.length];

    }

    private boolean checkParams() {
        /**
          @return: true if the user gave the parameters correctly
         */
        if (!isParamOrderAscending && !isParamOrderDescending) {
            isParamOrderAscending = true; // set default value
        }
        if (!isParamTypeString && !isParamTypeInteger) {
            System.out.println("No required parameters -s or -i.");
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

    private int findIdMinStringElem(String[] strings) {
        return 0;
    }

    private int findIdMinIntegerElem(String[] ints) {
        int min = Integer.MAX_VALUE;
        int minId = 0;
        for (int i = 0; i < ints.length; i++) {
            try {
                int temp = Integer.parseInt(ints[i]);

                if (temp < min) {
                    min = temp;
                    minId = i;
                }
            } catch (NumberFormatException e){
                // skip wrong lines
                Scanner scanner = allScanners[i];
                if (scanner.hasNextLine()) {
                    scanner.nextLine();
                    i--;
                }
            }

        }

        return minId;
    }
}
