package org.kgs.quicksave;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;

//TODO: Add copied auto load world

public class Load {
    public static String LevelName;

    public static MinecraftServer server;
    private static Path saveData;
    private static Path QsPath;

    public static void Init(MinecraftServer inServer) {
        server = inServer;
        saveData = Path.of(server.getWorldPath(LevelResource.ROOT).toString());
        QsPath = Path.of(Paths.get(server.getServerDirectory().toPath().toString(), "Quick_Save").toString());
        LevelName = server.getWorldData().getLevelName();
    }

    public static int QLoad() {
        Class<? extends MinecraftServer> pubSever = server.getClass();

        QuickSave.LOGGER.info("Start Quick Load");
        assert Minecraft.getInstance().player != null;
        //Minecraft.getInstance().player.sendSystemMessage(new Component("Start Quick Load"), Minecraft.getInstance().player.getUUID());
        QuickSave.isQL = true;
        if (server.getConnection() != null) {
            server.getConnection().running = false;
        }

        server.getPlayerList().removeAll();

        for (ServerLevel serverlevel : server.getAllLevels()) {
            if (serverlevel != null) {
                serverlevel.noSave = false;
            }
        }

        Field field;
        try {
            field = pubSever.getDeclaredField("isSaving");
            field.setAccessible(true);
            field.set(server, false);
            server.getServerResources().close();
            QuickSave.LOGGER.info("Resources unload");
            field = pubSever.getDeclaredField("storageSource");
            field.setAccessible(true);
            LevelStorageSource.LevelStorageAccess storage;
            storage = (LevelStorageSource.LevelStorageAccess) field.get(server);
            storage.close();
            field.set(server, storage);
        } catch (IOException | IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public static void mvSave() {
        FileUtil.deleteFile(saveData.toFile());
        FileUtil.copyDir(QsPath.toFile(), saveData.toFile());
        QuickSave.isQL = false;
        QuickSave.LOGGER.info("Quick Load Done");
    }
}
