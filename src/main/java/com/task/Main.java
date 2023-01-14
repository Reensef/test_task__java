package com.task;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class Main {
    static HashMap<String, String> params = new HashMap<String, String>() {{
        put("order", "ascending"); // -a ascending -d descending
    }};

    static ArrayList<String> inputfile = new ArrayList<>();

    public static void main(String[] args) {
        if (Objects.equals(args[0], "--help")) {
            //TODO write help information
            System.out.println("This is help information");
            return;
        }

        for (int i = 0; i < 2; i++) {
            // can be 2 params
            if (args[i].length() == 2) {
                switch (args[i]) {
                    case "-d" -> params.put("order", "descending");
                    case "-i" -> params.put("type", "int");
                    case "-s" -> params.put("type", "string");
                }

            } else {
                params.put("outfile", args[i]);
                inputfile.addAll(Arrays.asList(args).subList(i+1, args.length));
                break;
            }
        }

        // Check user input params
        if (!params.containsKey("type")) {
            System.out.println("No required parameter." +
                    "\nUse --help parameter to see help information");
        } else if (!params.containsKey("outfile")) {
            System.out.println("Output file not specified." +
                    "\nUse --help parameter to see help information");
        } else if (inputfile.isEmpty()) {
            System.out.println("Output file not specified." +
                    "\nUse --help parameter to see help information");
        }

        // Existence check
        for (String file : inputfile){
            if (!Files.exists(Path.of(file))) {
                System.out.printf("File %s not found%n", file); // ???? %n
                return;
            }
        }

    }

}



