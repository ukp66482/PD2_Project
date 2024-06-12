package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class BeatMapGen {
    public static void beatMapGenerator(){
        String pythonExecutable = "C:/ProgramData/anaconda3/python.exe";

        String scriptPath = "src/main/resources/beatMapGen.py";


        ProcessBuilder processBuilder = new ProcessBuilder(pythonExecutable, scriptPath);

        try {

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("Exited with code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

