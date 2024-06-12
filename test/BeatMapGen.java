import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class BeatMapGen {
    public static void main(String[] args) {
        String pythonExecutable = "C:/ProgramData/anaconda3/python.exe";

        String scriptPath = "./beatMapGen.py";


        ProcessBuilder processBuilder = new ProcessBuilder(pythonExecutable, scriptPath);

        try {

            Process process = processBuilder.start();

            // 获取进程的标准输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // 获取进程的标准错误输出
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }

            // 等待进程结束并获取退出状态
            int exitCode = process.waitFor();
            System.out.println("Exited with code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

