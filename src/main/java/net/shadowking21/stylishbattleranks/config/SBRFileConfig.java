package net.shadowking21.stylishbattleranks.config;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.stream.Stream;

public class SBRFileConfig {
    public static void createDirectories() {
        Path configDirectory = Paths.get(FMLPaths.GAMEDIR.get().toAbsolutePath().toString(), "config", "stylishbattleranks", "music");
        if (!Files.exists(configDirectory)) {
            try {
                Files.createDirectories(configDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
