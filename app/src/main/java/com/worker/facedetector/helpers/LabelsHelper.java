package com.worker.facedetector.helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class LabelsHelper {
    public static Map<Integer, String> readLabels() {
        Map<Integer, String> result = new HashMap<>();
        File file = new File(ConstantsHelper.LABELS_PATH);
        if (file.isFile()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line;
                while ((line = reader.readLine()) != null) {
                    StringTokenizer tokens = new StringTokenizer(line, ",");
                    String userName = tokens.nextToken();
                    String label = tokens.nextToken();
                    result.put(Integer.parseInt(label), userName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static void saveLabels(Map<String, Integer> labels) {
        File file = new File(ConstantsHelper.LABELS_PATH);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for (String userName : labels.keySet()) {
                int label = labels.get(userName);
                bw.write(userName + "," + label);
                bw.newLine();
            }
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
