package ua.cc.server.java;

import java.io.*;
import java.util.Date;

public class ServerLogs {
    private static String filename = "C:\\Users\\Admin\\Documents\\NetBeansProjects\\CharChatApp\\CharChatApp\\src\\ua\\cc\\server\\java\\ServerLogs.txt";
    private static File file = new File(filename);
    private static Date curDate;
    private static StringBuilder sb = new StringBuilder();

    public static void writeLogs(String logs) {
        file.setWritable(true);
        curDate = new Date();
        try (FileWriter writer = new FileWriter(filename, true)) {
            if (!file.exists()) {
                file.createNewFile();
            }
            writer.write(logs + ", server time: " + curDate.toString() + ": " + "\r\n");
            writer.flush();
            file.setWritable(false);
        } catch (IOException e) {
            System.out.println("LOGS NOT WRITEN");
        }
    }

    public static String readLogs() {
        try (BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()))) {
            if (!file.exists()) {
                System.out.println("FileLogs not exists!");
            }
            String s;
            while ((s = in.readLine()) != null) {
                sb.append(s);
                sb.append("\n");
            }
        } catch (FileNotFoundException ex) {
            System.out.println("FileLogs not exists");
        } catch (IOException ex) {
            System.out.println("FileLogs IOException");
        }
        return sb.toString();
    }
}
