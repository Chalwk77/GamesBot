/* Copyright (c) 2023, GamesBot. Jericho Crosby <jericho.crosby227@gmail.com> */

package com.chalwk.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

public class FileIO {

    private static final String programPath = getProgramPath();


    public static String getProgramPath() {
        String currentDirectory = System.getProperty("user.dir");
        currentDirectory = currentDirectory.replace("\\", "/");
        return currentDirectory + "/";
    }

    private static void checkExists(File file) throws IOException {
        boolean exists = file.exists();
        if (!exists) {
            boolean created = file.createNewFile();
            if (!created) {
                throw new IOException("Failed to create file: " + file);
            }
        }
    }

    private static String readFile(String fileName) throws IOException {

        File f = new File(programPath + fileName);
        checkExists(f);

        BufferedReader reader = new BufferedReader(new FileReader(f));
        String line = reader.readLine();
        StringBuilder stringBuilder = new StringBuilder();

        while (line != null) {
            stringBuilder.append(line);
            line = reader.readLine();
        }
        reader.close();

        return stringBuilder.toString();
    }

    public static <T> void writeJSON(T json, String fileName) throws IOException {
        FileWriter fileWriter = new FileWriter(programPath + fileName);
        fileWriter.write(json.toString());
        fileWriter.flush();
        fileWriter.close();
    }

    public static JSONObject readJSONObject(String fileName) throws IOException {
        String content = readFile(fileName);
        return content.isEmpty() ? new JSONObject() : new JSONObject(content);
    }

    public static JSONArray readJSONArray(String fileName) throws IOException {
        String content = readFile(fileName);
        return content.isEmpty() ? new JSONArray() : new JSONArray(content);
    }
}
