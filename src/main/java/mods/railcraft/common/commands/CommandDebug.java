/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.commands;

import mods.railcraft.api.signals.*;
import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.blocks.machine.wayobjects.boxes.TileBoxBase;
import mods.railcraft.common.carts.CartTools;
import mods.railcraft.common.carts.Train;
import mods.railcraft.common.items.ItemMagnifyingGlass;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.MessageFormatMessageFactory;

import java.util.Collections;
import java.util.List;

/**
 * Commands for assisting with debug operations.
 *
 * Created by CovertJaguar on 3/12/2015.
 */
public class CommandDebug extends SubCommand {
    private static final Level DEBUG_LEVEL = Level.INFO;
    private static final MessageFactory msgFactory = new MessageFormatMessageFactory();

    public CommandDebug() {
        super("debug");
        addChildCommand(new CommandDebugTile());
        addChildCommand(new CommandTrain());
        addChildCommand(new CommandCartNumber());
    }

    static void printLine(ICommandSender sender, String msg, Object... args) {
        Message msgObj;
        if (args.length == 0) {
            msgObj = msgFactory.newMessage(msg);
        } else {
            msgObj = msgFactory.newMessage(msg, args);
        }
        Game.log().msg(DEBUG_LEVEL, msgObj);
        sender.sendMessage(ChatPlugin.makeMessage(msgObj.getFormattedMessage()));
    }

    static void printTarget(ICommandSender sender, World world, BlockPos pos) {
        Block block = WorldPlugin.getBlock(world, pos);
        printLine(sender, "Target block [{0}] = {1}, {2}", shortCoords(sender, pos), block.getClass(), block.getTranslationKey());
        TileEntity t = world.getTileEntity(pos);
        if (t != null)
            printLine(sender, "Target tile [{0}] = {1}", shortCoords(sender, t.getPos()), t.getClass());
        else
            printLine(sender, "Target tile [{0}] = null", shortCoords(sender, pos));
    }

    static String shortCoords(ICommandSender sender, BlockPos coord) {
        String formatString;
        if (sender.getEntityWorld().getGameRules().getBoolean("reducedDebugInfo")) {
            coord = coord.subtract(sender.getPosition());
            formatString = "[~%d, ~%d, ~%d]";
        } else {
            formatString = "[%d, %d, %d]";
        }
        return String.format(formatString, coord.getX(), coord.getY(), coord.getZ());
    }

    @Override
    public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 0)
            CommandHelpers.throwWrongUsage(sender, this);

        RayTraceResult rayTraceResult = MiscTools.rayTracePlayerLook((EntityPlayer) sender);
        if (rayTraceResult == null)
            CommandHelpers.throwWrongUsage(sender, this);

        List<String> debug = Collections.emptyList();
        switch (rayTraceResult.typeOfHit) {
            case ENTITY:
                Entity entity = rayTraceResult.entityHit;
                if (entity instanceof EntityMinecart) {
                    debug = CartTools.getDebugOutput((EntityMinecart) entity);
                } else {
                    throw new EntityInvalidException(entity);
                }
                break;
            case BLOCK:
                World world = CommandHelpers.getWorld(sender);
                TileEntity tile = WorldPlugin.getBlockTile(world, rayTraceResult.getBlockPos());
                if (tile instanceof TileRailcraft) {
                    debug = ((TileRailcraft) tile).getDebugOutput();
                } else {
                    throw new BlockNotFoundException();
                }
                break;
        }
        for (String s : debug) {
            printLine(sender, s);
        }
    }

    static final class CommandTrain extends SubCommand {
        CommandTrain() {
            super("train");
            setPermLevel(PermLevel.ADMIN);
            addChildCommand(new Clear());
        }

        static final class Clear extends SubCommand {
            Clear() {
                super("clear");
            }

            @Override
            public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                if (!(sender instanceof EntityLivingBase) || args.length != 0) {
                    CommandHelpers.throwWrongUsage(sender, this);
                }

                Train.Manager.clearTrains();
            }
        }
    }

    static final class CommandCartNumber extends SubCommand {
        CommandCartNumber() {
            super("cartnum");
            setPermLevel(PermLevel.ADMIN);
            addChildCommand(new Set());
            addChildCommand(new Clear());
        }

        static final class Set extends SubCommand {
            Set() {
                super("set");
            }

            @Override
            public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                if (!(sender instanceof EntityLivingBase) || args.length != 1) {
                    CommandHelpers.throwWrongUsage(sender, this);
                }
                int t;
                try {
                    t = Integer.parseInt(args[0]);
                } catch (NumberFormatException ex) {
                    throw CommandHelpers.throwWrongUsage(sender, this); // Doesn't actually throw
                }

                ItemStack stack = ((EntityLivingBase) sender).getHeldItem(EnumHand.MAIN_HAND);
                if (stack.getItem() instanceof ItemMagnifyingGlass) {
                    ((ItemMagnifyingGlass) stack.getItem()).setCartNumber(stack, t);
                }
            }
        }

        static final class Clear extends SubCommand {
            Clear() {
                super("clear");
            }

            @Override
            public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                if (!(sender instanceof EntityLivingBase) || args.length != 0) {
                    CommandHelpers.throwWrongUsage(sender, this);
                }

                ItemStack stack = ((EntityLivingBase) sender).getHeldItem(EnumHand.MAIN_HAND);
                if (stack.getItem() instanceof ItemMagnifyingGlass) {
                    ((ItemMagnifyingGlass) stack.getItem()).clearCartNumber(stack);
                }
            }
        }
    }

    public static class CommandDebugTile extends SubCommand {
        public CommandDebugTile() {
            super("tile");
            addChildCommand(new CommandDebugTileController());
            addChildCommand(new CommandDebugTileReceiver());
        }

        @Override
        public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            ArgDeque argsQueue = ArgDeque.make(args);
            BlockPos pos = CommandHelpers.parseBlockPos(sender, argsQueue);

            World world = CommandHelpers.getWorld(sender);
            TileEntity tile = WorldPlugin.getBlockTile(world, pos);
            if (tile instanceof TileRailcraft) {
                List<String> debug = ((TileRailcraft) tile).getDebugOutput();
                for (String s : debug) {
                    printLine(sender, s);
                }
            } else {
                throw new BlockNotFoundException();
            }
        }
    }

    public static class CommandDebugTileController extends SubCommand {
        public CommandDebugTileController() {
            super("controller");
            addAlias("con");
        }

        @Override
        public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            ArgDeque argsQueue = ArgDeque.make(args);
            BlockPos pos = CommandHelpers.parseBlockPos(sender, argsQueue);

            World world = CommandHelpers.getWorld(sender);
            TileEntity tile = WorldPlugin.getBlockTile(world, pos);
            if (tile instanceof IControllerTile) {
                IControllerTile conTile = (IControllerTile) tile;
                SignalController con = conTile.getController();
                printLine(sender, "Railcraft Controller Debug Start");
                printLine(sender, "Target: {0} = {1}, {2}", shortCoords(sender, con.getCoords()), conTile, con);
                for (BlockPos pair : con.getPairs()) {
                    printLine(sender, "Rec at {0}", shortCoords(sender, pair));
                    printLine(sender, "Con Aspect for Rec = {0}", con.getAspectFor(pair));
                    SignalReceiver rec = con.getReceiverAt(pair);
                    if (rec instanceof SimpleSignalReceiver) {
                        printLine(sender, "Rec Objects = {0}, {1}", rec.getTile(), rec);
                        printLine(sender, "Pre Rec Aspect = {0}", ((SimpleSignalReceiver) rec).getAspect());
                        printLine(sender, "Updating Rec Aspect");
                        rec.onControllerAspectChange(con, con.getAspectFor(pair));
                        printLine(sender, "Post Rec Aspect = {0}", ((SimpleSignalReceiver) rec).getAspect());
                        WorldPlugin.markBlockForUpdate(world, pos);
                    } else if (rec == null) {
                        printLine(sender, "Could not find Rec at {0}", shortCoords(sender, pair));
                        printTarget(sender, tile.getWorld(), pair);
                    }
                    printLine(sender, "Railcraft Controller Debug End");
                }
            } else {
                throw new BlockNotFoundException();
            }
        }
    }

    public static class CommandDebugTileReceiver extends SubCommand {
        public CommandDebugTileReceiver() {
            super("receiver");
            addAlias("rec");
        }

        @Override
        public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            ArgDeque argsQueue = ArgDeque.make(args);
            BlockPos pos = CommandHelpers.parseBlockPos(sender, argsQueue);

            World world = CommandHelpers.getWorld(sender);
            TileEntity tile = WorldPlugin.getBlockTile(world, pos);
            if (tile instanceof IReceiverTile) {
                IReceiverTile recTile = (IReceiverTile) tile;
                SignalReceiver rec = recTile.getReceiver();
                printLine(sender, "Railcraft Receiver Debug Start");
                printLine(sender, "Target: {0} = {1}, {2}", shortCoords(sender, rec.getCoords()), recTile, rec);
                if (recTile instanceof TileBoxBase) {
                    printLine(sender, "Rec Tile Aspect = {0}", ((TileBoxBase) recTile).getBoxSignalAspect(EnumFacing.NORTH));
                }
                for (BlockPos pair : rec.getPairs()) {
                    printLine(sender, "Con at {0}", shortCoords(sender, pair));
                    SignalController con = rec.getControllerAt(pair);
                    if (con != null) {
                        printLine(sender, "Con Aspect for Rec = {0}", con.getAspectFor(rec.getCoords()));
                        printLine(sender, "Con Objects = {0}, {1}", con.getTile(), con);
                        if (rec instanceof SimpleSignalReceiver) {
                            printLine(sender, "Pre Rec Aspect = {0}", ((SimpleSignalReceiver) rec).getAspect());
                            printLine(sender, "Updating Rec Aspect");
                            rec.onControllerAspectChange(con, con.getAspectFor(pair));
                            printLine(sender, "Post Rec Aspect = {0}", ((SimpleSignalReceiver) rec).getAspect());
                            WorldPlugin.markBlockForUpdate(world, pos);
                        }
                    } else {
                        printLine(sender, "Could not find Con at {0}", shortCoords(sender, pair));
                        printTarget(sender, tile.getWorld(), pair);
                    }
                    printLine(sender, "Railcraft Receiver Debug End");
                }
            } else {
                throw new BlockNotFoundException();
            }
        }
    }
}
