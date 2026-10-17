package com.libreconnect.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class ConfigManager {

    private static final File FILE = new File("config/libreconnect.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static ConfigData data = new ConfigData();

    public static void load() {
        try {
            if (!FILE.exists()) {
                save();
                return;
            }

            FileReader reader = new FileReader(FILE);
            data = GSON.fromJson(reader, ConfigData.class);
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            FILE.getParentFile().mkdirs();

            FileWriter writer = new FileWriter(FILE);
            GSON.toJson(data, writer);
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
