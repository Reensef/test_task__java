package com.task;

import picocli.CommandLine;
import picocli.CommandLine.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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


    private String[] row; // ex. [2, null, 4], null if file is finished
    private FileInputStream[] allFIS;
    private Scanner[] allScanners;
    private FileOutputStream fos;

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

        row = new String[inputfiles.length];
        allFIS = new FileInputStream[inputfiles.length];
        allScanners = new Scanner[inputfiles.length];

        try {
            openAllFisAndScanners();
            fos = new FileOutputStream(outfile);
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found");
            return;
        }

        System.out.println("Merge sort started");

        readFilesInFirstTime();

        try {
            while (true) {
                boolean hasLine = false;
                for(String r : row) {
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

        } catch (IOException e) {
            System.out.println("Write error");
        }

        // TODO close fis and scanners
        System.out.println("Success");

        System.out.println("!".compareTo("&"));

    }

    private void readFilesInFirstTime() {
        for(int i = 0; i < inputfiles.length; i++) {
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

    private int writeIntLine() throws IOException {
        /**
         * @return file index in inputfiles for next read
         */
        int min = Integer.MAX_VALUE;
        int minId = 0;
        boolean isThereRightLine = false;
        for (int i = 0; i < row.length; i++) {
            if (row[i] == null) {
                continue;
            }
            try {
                int temp = Integer.parseInt(row[i]);

                if (temp < min) {
                    min = temp;
                    minId = i;
                }

                isThereRightLine = true;
            } catch (NumberFormatException e){
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

        if (isThereRightLine) {
            fos.write((min + "\n").getBytes());
        }

        return minId;
    }

    private int writeStringLine() throws IOException {
        /**
         * @return file index in inputfiles for next read
         */
        String min = "z";
        int minId = 0;
        boolean isThereRightLine = false;
        for (int i = 0; i < row.length; i++) {
            if (row[i] == null) {
                continue;
            }

            String temp = row[i];
            if (!temp.contains(" ")) { // is right line
                // lexicographic comparison
                if (temp.compareTo(min) < 0) {
                    min = temp;
                    minId = i;
                }
                isThereRightLine = true;
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

        if (isThereRightLine) {
            fos.write((min + "\n").getBytes());
        }

        return minId;
    }
}
