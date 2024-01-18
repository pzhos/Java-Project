package com.example.semproj;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class TextHandlingStrategy implements HandlingStrategy {
    @Override
    public ArrayList<String> getStringArray(File file) throws FileNotFoundException {
        ArrayList<String> text = new ArrayList<>();
        Scanner scanner = new Scanner(file);
        while (scanner.hasNext()) {
            text.add(scanner.nextLine());
        }
        return text;
    }
}
