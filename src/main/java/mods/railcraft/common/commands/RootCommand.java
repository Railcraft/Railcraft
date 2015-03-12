/*
 * Copyright (c) CovertJaguar, 2015 http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import java.util.*;

/**
 * Created by CovertJaguar on 3/12/2015.
 */
public class RootCommand extends CommandBase implements IModCommand {
    public static final String ROOT_COMMAND_NAME = "railcraft";
    private final SortedSet<SubCommand> children = new TreeSet<SubCommand>(new Comparator<SubCommand>() {
        @Override
        public int compare(SubCommand o1, SubCommand o2) {
            return o1.compareTo(o2);
        }
    });

    public void addChildCommand(SubCommand child) {
        child.setParent(this);
        children.add(child);
    }

    @Override
    public SortedSet<SubCommand> getChildren() {
        return children;
    }

    @Override
    public String getCommandName() {
        return ROOT_COMMAND_NAME;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public List<String> getCommandAliases() {
        List<String> aliases = new ArrayList<String>();
        aliases.add("rc");
        aliases.add("rail");
        return aliases;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + this.getCommandName() + " help";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!CommandHelpers.processStandardCommands(sender, this, args))
            CommandHelpers.throwWrongUsage(sender, this);
    }


    @Override
    public String getFullCommandString() {
        return getCommandName();
    }

    @Override
    public void printHelp(ICommandSender sender) {
        CommandHelpers.printHelp(sender, this);
    }
}