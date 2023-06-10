package org.kgs.quicksave;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import static org.kgs.quicksave.FileUtil.copyDir;

public class Save {
    private static MinecraftServer server;
    private static Path saveData;
    private static Path QsPath;

    public static CompletableFuture<Void> currentFuture;

    public static void Init(MinecraftServer inServer)
    {
        server = inServer;
    }

    public static int QSave() {
        saveData = server.getWorldPath(LevelResource.ROOT).toAbsolutePath();
        QsPath = Paths.get(saveData.toFile().getPath(),"..", "Quick_Save");

        QuickSave.LOGGER.info("Start Quick Save");
        server.saveEverything(true,true,true);
        currentFuture = CompletableFuture.runAsync(() -> {
            if (!QsPath.toFile().exists()) {
                try {
                    Files.delete(QsPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            copyDir(saveData.toFile(), QsPath.toFile());
        });
        QuickSave.LOGGER.info("Quick Save Done");

        return 0;
    }
}
