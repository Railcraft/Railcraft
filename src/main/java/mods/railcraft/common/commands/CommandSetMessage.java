package mods.railcraft.common.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 *
 */
public class CommandSetMessage extends SubCommand {

    public CommandSetMessage() {
        super("message");
    }

    @Override
    public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        CommandHelpers.throwWrongUsage(sender, this);
    }
}
