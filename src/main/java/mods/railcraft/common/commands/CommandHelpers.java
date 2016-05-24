/*
 * Copyright (c) CovertJaguar, 2015 http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */

package mods.railcraft.common.commands;

import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

/**
 * Created by CovertJaguar on 3/12/2015.
 */
public class CommandHelpers {

    public static World getWorld(ICommandSender sender, IModCommand command, String[] args, int worldArgIndex) throws WrongUsageException {
        // Handle passed in world argument
        if (worldArgIndex < args.length)
            try {
                int dim = Integer.parseInt(args[worldArgIndex]);
                World world = MinecraftServer.getServer().worldServerForDimension(dim);
                if (world != null)
                    return world;
            } catch (Exception ex) {
                throwWrongUsage(sender, command);
            }
        return getWorld(sender, command);
    }

    public static World getWorld(ICommandSender sender, IModCommand command) {
        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            return player.worldObj;
        }
        return MinecraftServer.getServer().worldServerForDimension(0);
    }

    public static String[] getPlayers() {
        return MinecraftServer.getServer().getAllUsernames();
    }

    public static void sendLocalizedChatMessage(ICommandSender sender, String locTag, Object... args) {
        sender.addChatMessage(new TextComponentTranslation(locTag, args));
    }

    public static void sendLocalizedChatMessage(ICommandSender sender, ChatStyle chatStyle, String locTag, Object... args) {
        TextComponentTranslation chat = new TextComponentTranslation(locTag, args);
        chat.setChatStyle(chatStyle);
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

    public static void processChildCommand(ICommandSender sender, SubCommand child, String[] args) throws CommandException {
        if (!sender.canCommandSenderUseCommand(child.getRequiredPermissionLevel(), child.getFullCommandString()))
            throw new WrongUsageException(LocalizationPlugin.translate("command.railcraft.noperms"));
        String[] newargs = new String[args.length - 1];
        System.arraycopy(args, 1, newargs, 0, newargs.length);
        child.processCommand(sender, newargs);
    }

    public static void printHelp(ICommandSender sender, IModCommand command) {
        ChatStyle header = new ChatStyle();
        header.setColor(TextFormatting.BLUE);
        sendLocalizedChatMessage(sender, header, "command.railcraft." + command.getFullCommandString().replace(" ", ".") + ".format", command.getFullCommandString());
        ChatStyle body = new ChatStyle();
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

    public static boolean processStandardCommands(ICommandSender sender, IModCommand command, String[] args) throws CommandException {
        if (args.length >= 1) {
            if (args[0].equals("help")) {
                command.printHelp(sender);
                return true;
            }
            for (SubCommand child : command.getChildren()) {
                if (matches(args[0], child)) {
                    processChildCommand(sender, child, args);
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean matches(String commandName, IModCommand command) {
        if (commandName.equals(command.getCommandName()))
            return true;
        else if (command.getCommandAliases() != null)
            for (String alias : command.getCommandAliases()) {
                if (commandName.equals(alias))
                    return true;
            }
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
}
