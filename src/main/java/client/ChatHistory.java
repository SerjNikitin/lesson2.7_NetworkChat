package client;

import java.io.*;
import java.util.ArrayList;

public class ChatHistory {
    private ArrayList<String> strings = new ArrayList<>();

    public void writeHistory(String massage) {
        File file = new File("D:\\учеба\\JAVA\\gb2and3\\2.7\\src\\main\\java\\client\\History");
        try (FileWriter fileWriter = new FileWriter(file, true)) {
            fileWriter.write(massage);
            fileWriter.write("\n");
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> readeHistory() {
        File file = new File("D:\\учеба\\JAVA\\gb2and3\\2.7\\src\\main\\java\\client\\History");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                strings.add(br.readLine() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strings;
    }
}