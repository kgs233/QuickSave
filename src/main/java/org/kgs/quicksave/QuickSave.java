package org.kgs.quicksave;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("quticksave")
public class QuickSave {

    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static boolean isQL = false;

    public QuickSave() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // Register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }

    @Mod.EventBusSubscriber
    public static class ServerEvent {
        @SubscribeEvent
        public static void onServerStart(final ServerStartedEvent event)
        {
            Save.Init(event.getServer());
            Load.Init(event.getServer());
        }

        @SubscribeEvent
        public static void onServerStopped(final ServerStoppedEvent event)
        {
            if(isQL)
            {
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
        public static void onKeyboardInput(InputEvent.KeyInputEvent event) {
            if (QUICKSAVE_KEY.isDown()) {
                Save.QSave();
            }
            if (QUICKLOAD_KEY.isDown()) {
                Minecraft.getInstance().player.sendMessage(new TextComponent("WIP, Please input /ql"), Minecraft.getInstance().player.getUUID());
            }
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class KeybindingRegistry {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> ClientRegistry.registerKeyBinding(KeyBoardInput.QUICKSAVE_KEY));
            event.enqueueWork(() -> ClientRegistry.registerKeyBinding(KeyBoardInput.QUICKLOAD_KEY));
        }
    }
}
