/*
 * Copyright (c) CovertJaguar, 2015 http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.commands;

import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.command.ICommandSender;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import java.util.List;

/**
 * Created by CovertJaguar on 3/12/2015.
 */
public class CommandDebug extends SubCommand {
    public CommandDebug() {
        super("debug");
        addChildCommand(new CommandDebugTile());
    }

    public static class CommandDebugTile extends SubCommand {
        public CommandDebugTile() {
            super("tile");
        }

        @Override
        public void processSubCommand(ICommandSender sender, String[] args) {
            if (args.length != 3)
                CommandHelpers.throwWrongUsage(sender, this);

            int x = 0, y = 0, z = 0;
            try {
                x = Integer.parseInt(args[0]);
                y = Integer.parseInt(args[1]);
                z = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                CommandHelpers.throwWrongUsage(sender, this);
            }

            World world = CommandHelpers.getWorld(sender, this);
            TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
            if (tile instanceof RailcraftTileEntity) {
                List<String> debug = ((RailcraftTileEntity) tile).getDebugOutput();
                for (String s : debug) {
                    Game.log(Level.INFO, s);
                }
            } else {
                CommandHelpers.throwWrongUsage(sender, this);
            }
        }
    }
}
