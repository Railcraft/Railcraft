/*
 * Copyright (c) CovertJaguar, 2015 http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */

package mods.railcraft.common.commands;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

import java.util.List;
import java.util.SortedSet;

/**
 * Created by CovertJaguar on 3/12/2015.
 */
public interface IModCommand extends ICommand {

    String getFullCommandString();

    @Override
    List<String> getCommandAliases();

    int getRequiredPermissionLevel();

    SortedSet<SubCommand> getChildren();

    void printHelp(ICommandSender sender);
}