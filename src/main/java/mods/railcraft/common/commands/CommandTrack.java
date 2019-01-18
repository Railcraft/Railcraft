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
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.blocks.tracks.outfitted.kits.TrackKitMessenger;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

import java.util.Objects;

/**
 * Commands for tracks.
 * Created by CovertJaguar on 3/12/2015.
 */
public class CommandTrack extends SubCommand {

    public CommandTrack() {
        super("track");
        SubCommand commandTrackMessage = new CommandTrackMessage("message", TrackKitMessenger::setTitle).addAlias("msg");
        commandTrackMessage.addChildCommand(new CommandTrackMessage("title", TrackKitMessenger::setTitle));
        commandTrackMessage.addChildCommand(new CommandTrackMessage("subtitle", TrackKitMessenger::setSubtitle).addAlias("sub"));
        commandTrackMessage.addChildCommand(new CommandTrackMessage("actionbar", TrackKitMessenger::setActionbar).addAlias("bar"));
        addChildCommand(commandTrackMessage);
    }

    private static class CommandTrackMessage extends SubCommand {
        private final IMessageConsumer consumer;

        private interface IMessageConsumer {
            void accept(TrackKitMessenger track, ICommandSender sender, ITextComponent message);
        }

        private CommandTrackMessage(String name, IMessageConsumer consumer) {
            super(name);
            this.consumer = consumer;
        }

        @Override
        public final void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            ArgDeque argsQueue = ArgDeque.make(args);
            BlockPos pos = CommandHelpers.parseBlockPos(sender, argsQueue);

            TrackKitMessenger track = TrackTools.getTrackInstance(CommandHelpers.getWorld(sender), pos, TrackKitMessenger.class);
            if (track != null) {
                String message = CommandBase.buildString(argsQueue.toArray(), 0);
                try {
                    consumer.accept(track, sender, Objects.requireNonNull(ITextComponent.Serializer.jsonToComponent(message)));
                } catch (JsonParseException ex) {
                    throw CommandHelpers.toSyntaxException(ex);
                }
            } else {
                throw new BlockNotFoundException();
            }
        }
    }
}
