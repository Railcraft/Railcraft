/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.misc;

import mods.railcraft.api.core.ClientAccessException;
import mods.railcraft.common.core.Railcraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFormatMessage;
import org.apache.logging.log4j.message.SimpleMessage;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class Game {
    public static final boolean OBFUSCATED;
    public static final boolean DEVELOPMENT_VERSION;
    public static final boolean BUKKIT;
    /**
     * A logging level dedicated to debug reports. It is more prioritized than errors but less serious than fatal.
     */
    public static final Level DEBUG_REPORT = Level.forName("DEBUG_REPORT", 150);

    static {
        boolean worldFound = false;
        boolean worldObjFound = false;
        try {
            worldFound = Entity.class.getDeclaredField("world") != null;
        } catch (NoSuchFieldException | SecurityException ignored) {
        }
        try {
            //noinspection JavaReflectionMemberAccess
            worldObjFound = Entity.class.getDeclaredField("worldObj") != null;
        } catch (NoSuchFieldException | SecurityException ignored) {
        }
        OBFUSCATED = !worldFound && !worldObjFound;
        DEVELOPMENT_VERSION = Railcraft.getVersion().matches(".*(alpha|beta).*") || !OBFUSCATED;
        boolean foundBukkit = false;
        try {
            foundBukkit = Class.forName("org.spigotmc.SpigotConfig") != null;
        } catch (ClassNotFoundException ignored) {
        }
        BUKKIT = foundBukkit;
        if (BUKKIT)
            Game.log().msg(Level.INFO, "Bukkit detected, disabling Tile Entity caching because Bukkit doesn't seem to invalid Tile Entities properly!");
    }

    public static boolean isHost(final World world) {
        return !world.isRemote;
    }

    public static boolean isClient(final World world) {
        return world.isRemote;
    }

    public static void notClient(final World world) {
        if (isClient(world)) throw new ClientAccessException();
    }

    public static void requiresServerThread() {
        MinecraftServer mcServer = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (!mcServer.isCallingFromMinecraftThread())
            throw new IllegalThreadStateException("Must call from server!");
    }

    public static MinecraftServer getServer() {
        return FMLCommonHandler.instance().getMinecraftServerInstance();
    }

    @SideOnly(Side.CLIENT)
    public static @Nullable WorldClient getWorld() {
        return FMLClientHandler.instance().getWorldClient();
    }

    @SuppressWarnings("SameReturnValue")
    public static boolean isObfuscated() {
        return OBFUSCATED;
    }

    public static ResourceLocation getActiveModResource(String path) {
        ModContainer mod = Loader.instance().activeModContainer();
        String modId = mod != null ? mod.getModId() : "unknown";
        return new ResourceLocation(modId, path);
    }

    private Game() {
    }

    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    private static final Set<String> enabledCategories = new HashSet<>(Arrays.asList(
//                "registry",
//                "models",
            "default"
    ));

    public static ILogger log() {
        return log("default");
    }

    public static ILogger log(String category) {
        if (enabledCategories.contains(category))
            return Logger.INSTANCE;
        return NULL_LOGGER;
    }

    private static ILogger NULL_LOGGER = new ILogger() {};

    public enum Logger implements ILogger {
        INSTANCE;

        private Message getMessage(String msg, Object... args) {
            return new MessageFormatMessage(msg, args);
        }

        @Override
        public void msg(Level level, @Nullable String msg, Object... args) {
            if (msg != null)
                msg(level, getMessage(msg, args));
        }

        @Override
        public void msg(Level level, Message msg) {
            LogManager.getLogger(Railcraft.MOD_ID).log(level, msg);
        }

        @Override
        public void trace(Level level, String msg, Object... args) {
            trace(level, getMessage(msg, args));
        }

        @Override
        public void trace(Level level, Message message) {
            trace(level, 5, message);
        }

        @Override
        public void trace(Level level, int lines, String msg, Object... args) {
            msg(level, getMessage(msg, args));
            trace(level, lines, 2, Thread.currentThread().getStackTrace());
        }

        @Override
        public void trace(Level level, int lines, Message message) {
            msg(level, message);
            trace(level, lines, 2, Thread.currentThread().getStackTrace());
        }

        @Override
        public void trace(Level level, int lines, int skipLines, StackTraceElement[] stackTrace) {
            for (int i = skipLines; i < stackTrace.length && i < skipLines + lines; i++) {
                msg(level, stackTrace[i].toString());
            }
        }

        @Override
        public void throwable(String msg, Throwable error, Object... args) {
            throwable(Level.ERROR, 3, error, msg, args);
        }

        @Override
        public void throwable(String msg, int lines, Throwable error, Object... args) {
            throwable(Level.ERROR, lines, error, msg, args);
        }

        @Override
        public void throwable(Level level, int lines, Throwable error, String msg, Object... args) {
            msg(level, msg, args);
            msg(level, new SimpleMessage(error.toString()));
            trace(level, lines, 0, error.getStackTrace());
        }

        @Override
        public void debug(String msg, Object... args) {
            if (!Game.DEVELOPMENT_VERSION)
                return;
            msg(Level.DEBUG, msg, args);
        }

        @Override
        public void api(String mod, Throwable error, Class<?>... classFiles) {
            StringBuilder msg = new StringBuilder(mod);
            msg.append(" API error, please update your mods. Error: ").append(error);
            throwable(Level.ERROR, 2, error, msg.toString());

            for (Class<?> classFile : classFiles) {
                if (classFile != null) {
                    msg = new StringBuilder(mod);
                    msg.append(" API error: ").append(classFile.getSimpleName()).append(" is loaded from ").append(classFile.getProtectionDomain().getCodeSource().getLocation());
                    msg(Level.ERROR, msg.toString());
                }
            }
        }

        @Override
        public void fingerprint(String mod) {
            msg(Level.FATAL, "{0} failed validation, terminating. Please re-download {0} from an official source.", mod);
        }
    }

    /**
     * Created by CovertJaguar on 1/6/2019 for Railcraft.
     *
     * @author CovertJaguar <http://www.railcraft.info>
     */
    public interface ILogger {
        default void msg(Level level, @Nullable String msg, Object... args) {}

        default void msg(Level level, Message msg) {}

        default void trace(Level level, String msg, Object... args) {}

        default void trace(Level level, Message message) {}

        default void trace(Level level, int lines, String msg, Object... args) {}

        default void trace(Level level, int lines, Message message) {}

        default void trace(Level level, int lines, int skipLines, StackTraceElement[] stackTrace) {}

        default void throwable(String msg, Throwable error, Object... args) {}

        default void throwable(String msg, int lines, Throwable error, Object... args) {}

        default void throwable(Level level, int lines, Throwable error, String msg, Object... args) {}

        default void debug(String msg, Object... args) {}

        default void api(String mod, Throwable error, Class<?>... classFiles) {}

        default void fingerprint(String mod) {}
    }
}
