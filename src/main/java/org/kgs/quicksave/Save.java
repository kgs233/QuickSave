package org.kgs.quicksave;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import static org.kgs.quicksave.FileUtil.copyDir;

public class Save {
    public static CompletableFuture<Void> currentFuture;
    private static MinecraftServer server;
    private static Path saveData;
    private static Path QsPath;

    public static void Init(MinecraftServer inServer) {
        server = inServer;
        saveData = server.getWorldPath(LevelResource.ROOT).toAbsolutePath();
        QsPath = Paths.get(server.getServerDirectory().toPath().toString(), "Quick_Save");
    }

    public static int QSave() {
        QuickSave.LOGGER.info("Start Quick Save");
        assert Minecraft.getInstance().player != null;
        Minecraft.getInstance().player.sendMessage(new TextComponent("Start Quick Save"), Minecraft.getInstance().player.getUUID());
        server.saveEverything(true, true, true);
        currentFuture = CompletableFuture.runAsync(() -> {
            FileUtil.deleteFile(QsPath.toFile());
            copyDir(saveData.toFile(), QsPath.toFile());
            QuickSave.LOGGER.info("Quick Save Done");
            Minecraft.getInstance().player.sendMessage(new TextComponent("Quick Save Done"), Minecraft.getInstance().player.getUUID());
        });

        return 0;
    }
}
