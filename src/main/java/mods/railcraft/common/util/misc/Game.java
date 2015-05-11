/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.misc;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import mods.railcraft.common.core.Railcraft;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFormatMessage;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class Game {
    public static final boolean IS_OBFUSCATED = !World.class.getSimpleName().equals("World");
    public static final boolean IS_DEBUG = !Railcraft.VERSION.endsWith("0");
    public static final boolean IS_BUKKIT;

    static {
        boolean foundBukkit = false;
        try {
            foundBukkit = Class.forName("org.spigotmc.SpigotConfig") != null;
        } catch (ClassNotFoundException er) {
        }
        IS_BUKKIT = foundBukkit;
        if (IS_BUKKIT)
            log(Level.INFO, "Bukkit detected, disabling Tile Entity caching because Bukkit doesn't seem to invalid Tile Entities properly!");
    }

    public static boolean isHost(final World world) {
        return !world.isRemote;
    }

    public static boolean isNotHost(final World world) {
        return world.isRemote;
    }

    @SideOnly(Side.CLIENT)
    public static World getWorld() {
        Minecraft mc = FMLClientHandler.instance().getClient();
        if (mc != null)
            return mc.theWorld;
        return null;
    }

    public static boolean isObfuscated() {
        return IS_OBFUSCATED;
    }

    public static void log(Level level, String msg, Object... args) {
        if (msg != null)
            log(level, new MessageFormatMessage(msg, args));
    }

    public static void log(Level level, Message msg) {
        LogManager.getLogger(Railcraft.MOD_ID).log(level, msg);
    }

    public static void logTrace(Level level, String msg, Object... args) {
        Game.logTrace(level, 5, msg, args);
    }

    public static void logTrace(Level level, int lines, String msg, Object... args) {
        log(level, msg, args);
        logTrace(level, lines, 2, Thread.currentThread().getStackTrace());
    }

    public static void logTrace(Level level, int lines, int skipLines, StackTraceElement[] stackTrace) {
        for (int i = skipLines; i < stackTrace.length && i < skipLines + lines; i++) {
            log(level, stackTrace[i].toString());
        }
    }

    public static void logThrowable(String msg, Throwable error, Object... args) {
        logThrowable(Level.ERROR, msg, 3, error, args);
    }

    public static void logThrowable(String msg, int lines, Throwable error, Object... args) {
        logThrowable(Level.ERROR, msg, lines, error, args);
    }

    public static void logThrowable(Level level, String msg, int lines, Throwable error, Object... args) {
        log(level, msg, args);
        if (error.getMessage() != null)
            log(level, error.getMessage());
        logTrace(level, lines, 0, error.getStackTrace());
    }

    public static void logDebug(String msg, Object... args) {
        if (!IS_DEBUG)
            return;
        log(Level.DEBUG, msg, args);
    }

    public static void logErrorAPI(String mod, Throwable error, Class... classFiles) {
        StringBuilder msg = new StringBuilder(mod);
        msg.append(" API error, please update your mods. Error: ").append(error);
        logThrowable(Level.ERROR, msg.toString(), 2, error);

        for (Class classFile : classFiles) {
            if (classFile != null) {
                msg = new StringBuilder(mod);
                msg.append(" API error: ").append(classFile.getSimpleName()).append(" is loaded from ").append(classFile.getProtectionDomain().getCodeSource().getLocation());
                log(Level.ERROR, msg.toString());
            }
        }
    }

    public static void logErrorFingerprint(String mod) {
        log(Level.FATAL, "{0} failed validation, terminating. Please re-download {0} from an official source.");
    }
}
