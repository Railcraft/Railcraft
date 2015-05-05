/*
 * Copyright (c) CovertJaguar, 2015 http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.commands;

import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.api.signals.IControllerTile;
import mods.railcraft.api.signals.SignalController;
import mods.railcraft.api.signals.SignalReceiver;
import mods.railcraft.api.signals.SimpleSignalReceiver;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import java.util.List;

/**
 * Created by CovertJaguar on 3/12/2015.
 */
public class CommandDebug extends SubCommand {
    private static final Level DEBUG_LEVEL = Level.INFO;

    public CommandDebug() {
        super("debug");
        addChildCommand(new CommandDebugTile());
    }

    public static class CommandDebugTile extends SubCommand {
        public CommandDebugTile() {
            super("tile");
            addChildCommand(new CommandDebugTileController());
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
                    Game.log(DEBUG_LEVEL, s);
                }
            } else {
                CommandHelpers.throwWrongUsage(sender, this);
            }
        }
    }

    public static class CommandDebugTileController extends SubCommand {
        public CommandDebugTileController() {
            super("controller");
            addAlias("con");
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
            if (tile instanceof IControllerTile) {
                IControllerTile conTile = (IControllerTile) tile;
                SignalController con = conTile.getController();
                Game.log(DEBUG_LEVEL, "Railcraft Controller Debug Start [{0}]", con.getCoords());
                for (WorldCoordinate pair : con.getPairs()) {
                    Game.log(DEBUG_LEVEL, "Con Aspect for Rec at [{0}] = {1}", pair, con.getAspectFor(pair));
                    SignalReceiver rec = con.getReceiverAt(pair);
                    if (rec instanceof SimpleSignalReceiver) {
                        Game.log(DEBUG_LEVEL, "Pre Rec Aspect at [{0}] = {1}", pair, ((SimpleSignalReceiver) rec).getAspect());
                        Game.log(DEBUG_LEVEL, "Updating Rec at [{0}]", pair);
                        rec.onControllerAspectChange(con, con.getAspectFor(pair));
                        Game.log(DEBUG_LEVEL, "Post Rec Aspect at [{0}] = {1}", pair, ((SimpleSignalReceiver) rec).getAspect());
                    } else if (rec == null) {
                        Game.log(DEBUG_LEVEL, "Could not find Rec at [{0}]", pair);
                        printTarget(tile.getWorldObj(), pair);
                    }
                    Game.log(DEBUG_LEVEL, "Railcraft Controller Debug End", con.getCoords());
                }
            } else {
                CommandHelpers.throwWrongUsage(sender, this);
            }
        }

        private void printTarget(World world, WorldCoordinate coord) {
            int x = coord.x;
            int y = coord.y;
            int z = coord.z;
            Block block = world.getBlock(x, y, z);
            if (block != null)
                Game.log(DEBUG_LEVEL, "Target block [{0}, {1}, {2}] = {3}, {4}", x, y, z, block.getClass(), block.getUnlocalizedName());
            else
                Game.log(DEBUG_LEVEL, "Target block [{0}, {1}, {2}] = null", x, y, z);
            TileEntity t = world.getTileEntity(x, y, z);
            if (t != null)
                Game.log(DEBUG_LEVEL, "Target tile [{0}, {1}, {2}] = {3}", t.xCoord, t.yCoord, t.zCoord, t.getClass());
            else
                Game.log(DEBUG_LEVEL, "Target tile [{0}, {1}, {2}] = null", x, y, z);
        }
    }
}
