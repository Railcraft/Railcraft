/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.commands;

import com.google.gson.JsonParseException;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
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
        sender.sendMessage(new TextComponentTranslation(locTag, args));
    }

    public static void sendLocalizedChatMessage(ICommandSender sender, Style chatStyle, String locTag, Object... args) {
        TextComponentTranslation chat = new TextComponentTranslation(locTag, args);
        chat.setStyle(chatStyle);
        sender.sendMessage(chat);
    }

    /**
     * Avoid using this function if at all possible. Commands are processed on the server,
     * which has no localization information.
     * <p/>
     * StringUtil.localize() is NOT a valid alternative for sendLocalizedChatMessage().
     * Messages will not be localized properly if you use StringUtil.localize().
     */
    public static void sendChatMessage(ICommandSender sender, String message) {
        sender.sendMessage(new TextComponentString(message));
    }

    /**
     * Throws a wrong usage exception.
     *
     * @param sender The command sender
     * @param command The command that throws the exception
     * @return So Java knows this method will throw and does not shout "uninitialized var"
     * @throws WrongUsageException The exception to ask user to look up help
     */
    public static WrongUsageException throwWrongUsage(ICommandSender sender, IModCommand command) throws WrongUsageException {
        throw new WrongUsageException((LocalizationPlugin.translate("command.railcraft.help", command.getUsage(sender))));
    }

    public static boolean checkPermission(MinecraftServer server, ICommandSender sender, IModCommand command) {
        return command.getPermissionLevel() <= 0 || sender.canUseCommand(command.getPermissionLevel(), command.getFullString());
    }

    public static void executeChildCommand(MinecraftServer server, ICommandSender sender, SubCommand child, String[] args) throws CommandException {
        if (!child.checkPermission(server, sender))
            throw new WrongUsageException(LocalizationPlugin.translate("command.railcraft.noperms"));
        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, newArgs.length);
        child.execute(server, sender, newArgs);
    }

    public static void printHelp(ICommandSender sender, IModCommand command) {
        Style header = new Style();
        header.setColor(TextFormatting.BLUE);
        sendLocalizedChatMessage(sender, header, "command.railcraft." + command.getFullString().replace(" ", ".").replace('_', '.') + ".format", command.getFullString());
        Style body = new Style();
        body.setColor(TextFormatting.GRAY);
        sendLocalizedChatMessage(sender, body, "command.railcraft.aliases", command.getAliases().toString().replace("[", "").replace("]", ""));
        sendLocalizedChatMessage(sender, body, "command.railcraft.permlevel", command.getPermissionLevel());
        sendLocalizedChatMessage(sender, body, "command.railcraft." + command.getFullString().replace(" ", ".").replace('_', '.') + ".help");
        if (!command.getChildren().isEmpty()) {
            sendLocalizedChatMessage(sender, "command.railcraft.list");
            for (SubCommand child : command.getChildren()) {
                sendLocalizedChatMessage(sender, "command.railcraft." + child.getFullString().replace(" ", ".").replace('_', '.') + ".desc", child.getName());
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
        if (Objects.equals(commandName, command.getName()))
            return true;
        else if (command.getAliases() != null)
            return command.getAliases().stream().anyMatch(alias -> Objects.equals(commandName, alias));
        return false;
    }

    public static BlockPos parseBlockPos(ICommandSender sender, ArgDeque args) throws CommandException {
        BlockPos pos;
        try {
            pos = CommandBase.parseBlockPos(sender, args.peekArray(3), 0, false);
            args.poll(3);
        } catch (CommandException ex) {
            RayTraceResult rayTraceResult = MiscTools.rayTracePlayerLook((EntityPlayer) sender);
            if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK)
                pos = rayTraceResult.getBlockPos();
            else
                throw ex;
        }
        return pos;
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
