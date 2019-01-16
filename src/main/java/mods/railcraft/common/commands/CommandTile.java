/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.commands;

import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Commands for testing, because it was too much effort to find another mod that did them.
 * Created by CovertJaguar on 3/12/2015.
 */
public class CommandTile extends SubCommand {

    public CommandTile() {
        super("tile");
        addChildCommand(new CommandOwner());
    }

    private static class CommandOwner extends SubCommand {
        private CommandOwner() {
            super("owner");
            addChildCommand(new CommandOwnerClear());
        }

        @Override
        public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            ArgDeque argsQueue = ArgDeque.make(args);
            BlockPos pos = CommandHelpers.parseBlockPos(sender, argsQueue);

            TileEntity tile = WorldPlugin.getBlockTile(sender.getEntityWorld(), pos);
            if (tile instanceof TileRailcraft) {
                sender.sendMessage(ChatPlugin.translateMessage("command.railcraft.railcraft.tile.owner.message", ((TileRailcraft) tile).getOwner().getName()));
            } else {
                throw new BlockNotFoundException();
            }
        }
    }

    private static class CommandOwnerClear extends SubCommand {
        private CommandOwnerClear() {
            super("clear");
            setPermLevel(PermLevel.ADMIN);
        }

        @Override
        public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            ArgDeque argsQueue = ArgDeque.make(args);
            BlockPos pos = CommandHelpers.parseBlockPos(sender, argsQueue);

            TileEntity tile = WorldPlugin.getBlockTile(sender.getEntityWorld(), pos);
            if (tile instanceof TileRailcraft) {
                ((TileRailcraft) tile).clearOwner();
                sender.sendMessage(ChatPlugin.translateMessage("command.railcraft.railcraft.tile.owner.message", ((TileRailcraft) tile).getOwner().getName()));
            } else {
                throw new BlockNotFoundException();
            }
        }
    }
}
