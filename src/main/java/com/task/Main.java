package com.task;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {
    static FileOutputStream fos;
    static ArrayList<String> arrayList = new ArrayList<String>(
            Arrays.asList(
                    "q", "w", "e", "r", "t", "y", "u", "i", "o", "p",
                    "a", "s", "d", "f", "g", "h", "j", "k", "l",
                    "z", "x", "c", "v", "b", "n", "m",
                    "1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
                    "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "-", "+",
                    "/", "<", ">", ",", ".", "?", ";", "G", "T"
            )
    );


    public static void main(String[] args) throws IOException {
        fos = new FileOutputStream("input_3.txt");
        Random random = new Random();
        for (int i = 0; i < 1000000; i++) {
            int r = random.nextInt(8);
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j < r; j++) {
                int g = random.nextInt(57);
                stringBuilder.append(arrayList.get(g));
            }
            fos.write((stringBuilder.toString() + "\n").getBytes());
        }

        fos.close();
    }

}



