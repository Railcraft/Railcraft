/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.commands;

import com.google.gson.JsonParseException;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.blocks.tracks.outfitted.kits.TrackKitMessenger;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

/**
 * Commands for testing, because it was too much effort to find another mod that did them.
 * Created by CovertJaguar on 3/12/2015.
 */
public class CommandTrack extends SubCommand {

    public CommandTrack() {
        super("track");
        addChildCommand(new CommandTrackMessage());
    }

    private static class CommandTrackMessage extends SubCommand {
        private CommandTrackMessage() {
            super("message");
            addChildCommand(new CommandTrackMessageSubtitle());
            addAlias("msg");
        }

        @Override
        public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            ArgDeque argsQueue = ArgDeque.make(args);
            BlockPos pos = CommandHelpers.parseBlockPos(sender, argsQueue);

            TrackKitMessenger track = TrackTools.getTrackInstance(CommandHelpers.getWorld(sender), pos, TrackKitMessenger.class);
            if (track != null) {
                String message = CommandBase.buildString(argsQueue.toArgArray(), 0);
                try {
                    track.setTitle(sender, ITextComponent.Serializer.jsonToComponent(message));
                } catch (JsonParseException ex) {
                    throw CommandHelpers.toSyntaxException(ex);
                }
            } else {
                throw new BlockNotFoundException();
            }
        }
    }

    private static class CommandTrackMessageSubtitle extends SubCommand {
        private CommandTrackMessageSubtitle() {
            super("subtitle");
            addAlias("sub");
        }

        @Override
        public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            ArgDeque argsQueue = ArgDeque.make(args);
            BlockPos pos = CommandHelpers.parseBlockPos(sender, argsQueue);

            TrackKitMessenger track = TrackTools.getTrackInstance(CommandHelpers.getWorld(sender), pos, TrackKitMessenger.class);
            if (track != null) {
                String message = CommandBase.buildString(argsQueue.toArgArray(), 0);

                try {
                    track.setSubtitle(sender, ITextComponent.Serializer.jsonToComponent(message));
                } catch (JsonParseException ex) {
                    throw CommandHelpers.toSyntaxException(ex);
                }
            } else {
                throw new BlockNotFoundException();
            }
        }
    }
}
