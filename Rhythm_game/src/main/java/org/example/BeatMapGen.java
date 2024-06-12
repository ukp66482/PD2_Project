package org.example;

import java.io.IOException;

public class BeatMapGen {
    public static void beatMapGenerator(){
        String exePath = "src/main/resources/beatMapGen.exe";
        try {

            Process process = Runtime.getRuntime().exec(exePath);

            int exitCode = process.waitFor();
            System.out.println("Process exited with code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

