package org.example;

import java.io.IOException;

public class BeatMapGen {
    public static boolean beatMapGenerator(){
        String exePath = "src/main/resources/beatMapGen.exe";
        try {

            Process process = Runtime.getRuntime().exec(exePath);

            int exitCode = process.waitFor();
            System.out.println("Process exited with code: " + exitCode);
            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}

