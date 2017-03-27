/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.commands;

import com.google.gson.JsonParseException;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Objects;

/**
 * Command utilities.
 *
 * Created by CovertJaguar on 3/12/2015.
 */
public class CommandHelpers {

    public static World getWorld(ICommandSender sender, IModCommand command, String[] args, int worldArgIndex) throws WrongUsageException {
        // Handle passed in world argument
        if (worldArgIndex < args.length)
            try {
                int dim = Integer.parseInt(args[worldArgIndex]);
                World world = DimensionManager.getWorld(dim);
                if (world != null)
                    return world;
            } catch (Exception ex) {
                throwWrongUsage(sender, command);
            }
        return getWorld(sender);
    }

    public static World getWorld(ICommandSender sender) {
        return sender.getEntityWorld();
    }

    public static void sendLocalizedChatMessage(ICommandSender sender, String locTag, Object... args) {
        sender.addChatMessage(new TextComponentTranslation(locTag, args));
    }

    public static void sendLocalizedChatMessage(ICommandSender sender, Style chatStyle, String locTag, Object... args) {
        TextComponentTranslation chat = new TextComponentTranslation(locTag, args);
        chat.setStyle(chatStyle);
        sender.addChatMessage(chat);
    }

    /**
     * Avoid using this function if at all possible. Commands are processed on the server,
     * which has no localization information.
     * <p/>
     * StringUtil.localize() is NOT a valid alternative for sendLocalizedChatMessage().
     * Messages will not be localized properly if you use StringUtil.localize().
     */
    public static void sendChatMessage(ICommandSender sender, String message) {
        sender.addChatMessage(new TextComponentString(message));
    }

    public static void throwWrongUsage(ICommandSender sender, IModCommand command) throws WrongUsageException {
        throw new WrongUsageException((LocalizationPlugin.translate("command.railcraft.help", command.getCommandUsage(sender))));
    }

    public static void executeChildCommand(MinecraftServer server, ICommandSender sender, SubCommand child, String[] args) throws CommandException {
        if (!sender.canCommandSenderUseCommand(child.getRequiredPermissionLevel(), child.getFullCommandString()))
            throw new WrongUsageException(LocalizationPlugin.translate("command.railcraft.noperms"));
        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, newArgs.length);
        child.execute(server, sender, newArgs);
    }

    public static void printHelp(ICommandSender sender, IModCommand command) {
        Style header = new Style();
        header.setColor(TextFormatting.BLUE);
        sendLocalizedChatMessage(sender, header, "command.railcraft." + command.getFullCommandString().replace(" ", ".") + ".format", command.getFullCommandString());
        Style body = new Style();
        body.setColor(TextFormatting.GRAY);
        sendLocalizedChatMessage(sender, body, "command.railcraft.aliases", command.getCommandAliases().toString().replace("[", "").replace("]", ""));
        sendLocalizedChatMessage(sender, body, "command.railcraft.permlevel", command.getRequiredPermissionLevel());
        sendLocalizedChatMessage(sender, body, "command.railcraft." + command.getFullCommandString().replace(" ", ".") + ".help");
        if (!command.getChildren().isEmpty()) {
            sendLocalizedChatMessage(sender, "command.railcraft.list");
            for (SubCommand child : command.getChildren()) {
                sendLocalizedChatMessage(sender, "command.railcraft." + child.getFullCommandString().replace(" ", ".") + ".desc", child.getCommandName());
            }
        }
    }

    public static boolean executeStandardCommands(MinecraftServer server, ICommandSender sender, IModCommand command, String[] args) throws CommandException {
        if (args.length >= 1) {
            if (Objects.equals(args[0], "help")
                    || Objects.equals(args[0], "?")) {
                command.printHelp(sender);
                return true;
            }
            for (SubCommand child : command.getChildren()) {
                if (matches(args[0], child)) {
                    executeChildCommand(server, sender, child, args);
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean matches(String commandName, IModCommand command) {
        if (Objects.equals(commandName, command.getCommandName()))
            return true;
        else if (command.getCommandAliases() != null)
            return command.getCommandAliases().stream().anyMatch(alias -> Objects.equals(commandName, alias));
        return false;
    }

    public static BlockPos parseBlockPos(ICommandSender sender, IModCommand command, String[] args, int start) throws WrongUsageException {
        if (args.length <= start + 3)
            CommandHelpers.throwWrongUsage(sender, command);

        int x = 0, y = 0, z = 0;
        try {
            x = Integer.parseInt(args[start]);
            y = Integer.parseInt(args[start + 1]);
            z = Integer.parseInt(args[start + 2]);
        } catch (NumberFormatException ex) {
            throwWrongUsage(sender, command);
        }

        return new BlockPos(x, y, z);
    }

    public static SyntaxErrorException toSyntaxException(JsonParseException e) {
        Throwable throwable = ExceptionUtils.getRootCause(e);
        String s = "";

        if (throwable != null) {
            s = throwable.getMessage();

            if (s.contains("setLenient")) {
                s = s.substring(s.indexOf("to accept ") + 10);
            }
        }

        return new SyntaxErrorException("commands.tellraw.jsonException", s);
    }
}
