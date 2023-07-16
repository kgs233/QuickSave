package org.kgs.quicksave;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.KeyMapping;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("quticksave")
public class QuickSave {

    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogManager.getLogger();
    public static boolean isQL = false;

    public QuickSave() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventBusSubscriber
    public static class ServerEvent {
        @SubscribeEvent
        public static void onServerStart(final ServerStartedEvent event) {
            Save.Init(event.getServer());
            Load.Init(event.getServer());
        }

        @SubscribeEvent
        public static void onServerStopped(final ServerStoppedEvent event) {
            if (isQL) {
                Load.mvSave();
            }
        }
    }

    @Mod.EventBusSubscriber
    public static class CommandEventHandler {
        @SubscribeEvent
        public static void onServerStaring(RegisterCommandsEvent event) {
            CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
            dispatcher.register(
                    Commands.literal("qs")
                            .requires(cs -> cs.hasPermission(0))
                            .executes(cs -> Save.QSave()));
            dispatcher.register(Commands.literal("ql")
                    .requires(cs -> cs.hasPermission(0))
                    .executes(cs -> Load.QLoad()));
        }
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT)
    public static class KeyBoardInput {
        public static final KeyMapping QUICKSAVE_KEY = new KeyMapping("key.quicksave.qs",
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.quicksave");
        public static final KeyMapping QUICKLOAD_KEY = new KeyMapping("key.quicksave.ql",
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "category.quicksave");

        @SubscribeEvent
        public static void onKeyboardInput(InputEvent.Key event) {
            if (QUICKSAVE_KEY.isDown()) {
                Load.server.getCommands().performPrefixedCommand(Load.server.createCommandSourceStack(), "/qs");
            }
            if (QUICKLOAD_KEY.isDown()) {
                Load.server.getCommands().performPrefixedCommand(Load.server.createCommandSourceStack(), "/ql");
            }
        }
    }

    public void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.register(KeyBoardInput.QUICKSAVE_KEY);
        event.register(KeyBoardInput.QUICKLOAD_KEY);
    }
}
