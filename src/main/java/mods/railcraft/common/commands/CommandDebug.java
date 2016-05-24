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
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
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

    private static void printTarget(ICommandSender sender, World world, WorldCoordinate pos) {
        Block block = WorldPlugin.getBlock(world, pos);
        if (block != null)
            printLine(sender, "Target block [{0}] = {1}, {2}", shortCoords(pos), block.getClass(), block.getUnlocalizedName());
        else
            printLine(sender, "Target block [{0}] = null", shortCoords(pos));
        TileEntity t = world.getTileEntity(pos);
        if (t != null)
            printLine(sender, "Target tile [{0}, {1}, {2}] = {3}", t.getPos().getX(), t.getPos().getY(), t.getPos().getZ(), t.getClass());
        else
            printLine(sender, "Target tile [{0}, {1}, {2}] = null", pos.getY(), pos.getY(), pos.getZ());
    }

    private static String shortCoords(WorldCoordinate coord) {
        return String.format("[%d; %d, %d, %d]", coord.getDim(), coord.getX(), coord.getY(), coord.getZ());
    }

    public static class CommandDebugTile extends SubCommand {
        public CommandDebugTile() {
            super("tile");
            addChildCommand(new CommandDebugTileController());
            addChildCommand(new CommandDebugTileReceiver());
        }

        @Override
        public void processSubCommand(ICommandSender sender, String[] args) throws CommandException {
            if (args.length != 3)
                CommandHelpers.throwWrongUsage(sender, this);


            BlockPos pos = CommandHelpers.parseBlockPos(sender, this, args, 0);

            World world = CommandHelpers.getWorld(sender, this);
            TileEntity tile = WorldPlugin.getBlockTile(world, pos);
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
        public void processSubCommand(ICommandSender sender, String[] args) throws CommandException {
            if (args.length != 3)
                CommandHelpers.throwWrongUsage(sender, this);

            BlockPos pos = CommandHelpers.parseBlockPos(sender, this, args, 0);

            World world = CommandHelpers.getWorld(sender, this);
            TileEntity tile = WorldPlugin.getBlockTile(world, pos);
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
                        world.markBlockForUpdate(pos);
                    } else if (rec == null) {
                        printLine(sender, "Could not find Rec at {0}", shortCoords(pair));
                        printTarget(sender, tile.getWorld(), pair);
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
        public void processSubCommand(ICommandSender sender, String[] args) throws CommandException {
            if (args.length != 3)
                CommandHelpers.throwWrongUsage(sender, this);

            BlockPos pos = CommandHelpers.parseBlockPos(sender, this, args, 0);

            World world = CommandHelpers.getWorld(sender, this);
            TileEntity tile = WorldPlugin.getBlockTile(world, pos);
            if (tile instanceof IReceiverTile) {
                IReceiverTile recTile = (IReceiverTile) tile;
                SignalReceiver rec = recTile.getReceiver();
                printLine(sender, "Railcraft Receiver Debug Start");
                printLine(sender, "Target: {0} = {1}, {2}", shortCoords(rec.getCoords()), recTile, rec);
                if (recTile instanceof TileBoxBase) {
                    printLine(sender, "Rec Tile Aspect = {0}", ((TileBoxBase) recTile).getBoxSignalAspect(EnumFacing.NORTH));
                }
                for (WorldCoordinate pair : rec.getPairs()) {
                    printLine(sender, "Con at {0}", shortCoords(pair));
                    SignalController con = rec.getControllerAt(pair);
                    if (con != null) {
                        printLine(sender, "Con Aspect for Rec = {0}", con.getAspectFor(rec.getCoords()));
                        printLine(sender, "Con Objects = {0}, {1}", con.getTile(), con);
                        if (rec instanceof SimpleSignalReceiver) {
                            printLine(sender, "Pre Rec Aspect = {0}", ((SimpleSignalReceiver) rec).getAspect());
                            printLine(sender, "Updating Rec Aspect");
                            rec.onControllerAspectChange(con, con.getAspectFor(pair));
                            printLine(sender, "Post Rec Aspect = {0}", ((SimpleSignalReceiver) rec).getAspect());
                            world.markBlockForUpdate(pos);
                        }
                    } else {
                        printLine(sender, "Could not find Con at {0}", shortCoords(pair));
                        printTarget(sender, tile.getWorld(), pair);
                    }
                    printLine(sender, "Railcraft Receiver Debug End");
                }
            } else {
                CommandHelpers.throwWrongUsage(sender, this);
            }
        }
    }
}
