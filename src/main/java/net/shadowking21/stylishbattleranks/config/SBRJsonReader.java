package net.shadowking21.stylishbattleranks.config;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.Minecraft;
import net.shadowking21.stylishbattleranks.utils.BattleUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SBRJsonReader {
    private static final Gson GSON = new Gson();

    public static ConfigData read() {
        ConfigData config = new ConfigData();
        try {
            File jsonFile = BattleUtils.MusicPath.resolve(SBRConfig.selectedTrack.get()+".json").toFile();

            try (Reader reader = Files.newBufferedReader(jsonFile.toPath(), StandardCharsets.UTF_8)) {
                config = GSON.fromJson(reader, ConfigData.class);
            }

            if (config.controlpoints == null || config.styleranks == null) {
                throw new IllegalArgumentException("JSON must contain 'controlpoints' (array) and 'styleranks' (map).");
            }

            else if (!config.styleranks.keySet().containsAll(List.of("D", "C", "B", "A", "S", "SS", "SSS"))) {
                throw new IllegalArgumentException("Map must contain all style rank keys! (D, C, B, A, S, SS, SSS)");
            }

        }
        catch (JsonSyntaxException e) {
            System.err.println("JSON syntax error: " + e.getMessage());
        }

        catch (IOException e) {
            e.printStackTrace();
        }
        config.styleranks.put("NOTHING", 0f);
        config.controlpoints.add(config.styleranks.get("NOTHING"));
        config.controlpoints.add(config.styleranks.get("D"));
        config.controlpoints.add(config.styleranks.get("C"));
        config.controlpoints.add(config.styleranks.get("B"));
        config.controlpoints.add(config.styleranks.get("A"));
        config.controlpoints.add(config.styleranks.get("S"));
        config.controlpoints.add(config.styleranks.get("SS"));
        config.controlpoints.add(config.styleranks.get("SSS"));
        return config;
    }

}
