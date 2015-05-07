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
import mods.railcraft.api.signals.*;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.blocks.signals.TileBoxBase;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.MessageFormatMessageFactory;

import java.util.List;

/**
 * Created by CovertJaguar on 3/12/2015.
 */
public class CommandDebug extends SubCommand {
    private static final Level DEBUG_LEVEL = Level.INFO;
    private static final MessageFactory msgFactory = new MessageFormatMessageFactory();

    public CommandDebug() {
        super("debug");
        addChildCommand(new CommandDebugTile());
    }

    private static void printLine(ICommandSender sender, String msg, Object... args) {
        Message msgObj = msgFactory.newMessage(msg, args);
        Game.log(DEBUG_LEVEL, msgObj);
        sender.addChatMessage(ChatPlugin.getMessage(msgObj.getFormattedMessage()));
    }

    private static void printTarget(ICommandSender sender, World world, WorldCoordinate coord) {
        int x = coord.x;
        int y = coord.y;
        int z = coord.z;
        Block block = world.getBlock(x, y, z);
        if (block != null)
            printLine(sender, "Target block [{0}] = {1}, {2}", shortCoords(coord), block.getClass(), block.getUnlocalizedName());
        else
            printLine(sender, "Target block [{0}] = null", shortCoords(coord));
        TileEntity t = world.getTileEntity(x, y, z);
        if (t != null)
            printLine(sender, "Target tile [{0}, {1}, {2}] = {3}", t.xCoord, t.yCoord, t.zCoord, t.getClass());
        else
            printLine(sender, "Target tile [{0}, {1}, {2}] = null", x, y, z);
    }

    private static String shortCoords(WorldCoordinate coord) {
        return String.format("[%d; %d, %d, %d]", coord.dimension, coord.x, coord.y, coord.z);
    }

    public static class CommandDebugTile extends SubCommand {
        public CommandDebugTile() {
            super("tile");
            addChildCommand(new CommandDebugTileController());
            addChildCommand(new CommandDebugTileReceiver());
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
                    printLine(sender, s);
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
                printLine(sender, "Railcraft Controller Debug Start");
                printLine(sender, "Target: {0} = {1}, {2}", shortCoords(con.getCoords()), conTile, con);
                for (WorldCoordinate pair : con.getPairs()) {
                    printLine(sender, "Rec at {0}", shortCoords(pair));
                    printLine(sender, "Con Aspect for Rec = {0}", con.getAspectFor(pair));
                    SignalReceiver rec = con.getReceiverAt(pair);
                    if (rec instanceof SimpleSignalReceiver) {
                        printLine(sender, "Rec Objects = {0}, {1}", rec.getTile(), rec);
                        printLine(sender, "Pre Rec Aspect = {0}", ((SimpleSignalReceiver) rec).getAspect());
                        printLine(sender, "Updating Rec Aspect");
                        rec.onControllerAspectChange(con, con.getAspectFor(pair));
                        printLine(sender, "Post Rec Aspect = {0}", ((SimpleSignalReceiver) rec).getAspect());
                        world.markBlockForUpdate(x, y, z);
                    } else if (rec == null) {
                        printLine(sender, "Could not find Rec at {0}", shortCoords(pair));
                        printTarget(sender, tile.getWorldObj(), pair);
                    }
                    printLine(sender, "Railcraft Controller Debug End");
                }
            } else {
                CommandHelpers.throwWrongUsage(sender, this);
            }
        }
    }

    public static class CommandDebugTileReceiver extends SubCommand {
        public CommandDebugTileReceiver() {
            super("receiver");
            addAlias("rec");
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
            if (tile instanceof IReceiverTile) {
                IReceiverTile recTile = (IReceiverTile) tile;
                SignalReceiver rec = recTile.getReceiver();
                printLine(sender, "Railcraft Receiver Debug Start");
                printLine(sender, "Target: {0} = {1}, {2}", shortCoords(rec.getCoords()), recTile, rec);
                if (recTile instanceof TileBoxBase) {
                    printLine(sender, "Rec Tile Aspect = {0}", ((TileBoxBase) recTile).getBoxSignalAspect(ForgeDirection.NORTH));
                }
                for (WorldCoordinate pair : rec.getPairs()) {
                    printLine(sender, "Con at {0}", shortCoords(pair));
                    SignalController con = rec.getControllerAt(pair);
                    if (con instanceof SignalController) {
                        printLine(sender, "Con Aspect for Rec = {0}", con.getAspectFor(rec.getCoords()));
                        printLine(sender, "Con Objects = {0}, {1}", con.getTile(), con);
                        if (rec instanceof SimpleSignalReceiver) {
                            printLine(sender, "Pre Rec Aspect = {0}", ((SimpleSignalReceiver) rec).getAspect());
                            printLine(sender, "Updating Rec Aspect");
                            rec.onControllerAspectChange(con, con.getAspectFor(pair));
                            printLine(sender, "Post Rec Aspect = {0}", ((SimpleSignalReceiver) rec).getAspect());
                            world.markBlockForUpdate(x, y, z);
                        }
                    } else if (con == null) {
                        printLine(sender, "Could not find Con at {0}", shortCoords(pair));
                        printTarget(sender, tile.getWorldObj(), pair);
                    }
                    printLine(sender, "Railcraft Receiver Debug End");
                }
            } else {
                CommandHelpers.throwWrongUsage(sender, this);
            }
        }
    }
}
