/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Sub-Command
 *
 * Created by CovertJaguar on 3/12/2015.
 */
public abstract class SubCommand implements IModCommand {

    public enum PermLevel {

        EVERYONE(0), ADMIN(2);
        int permLevel;

        PermLevel(int permLevel) {
            this.permLevel = permLevel;
        }

    }

    private final String name;
    private final List<String> aliases = new ArrayList<>();
    private PermLevel permLevel = PermLevel.EVERYONE;
    private IModCommand parent;
    private final NavigableSet<SubCommand> children = new TreeSet<>(SubCommand::compareTo);

    protected SubCommand(String name) {
        this.name = name;
    }

    @Override
    public final String getName() {
        return name;
    }

    public SubCommand addChildCommand(SubCommand child) {
        child.setParent(this);
        children.add(child);
        return this;
    }

    void setParent(IModCommand parent) {
        this.parent = parent;
    }

    @Override
    public NavigableSet<SubCommand> getChildren() {
        return children;
    }

    public SubCommand addAlias(String alias) {
        aliases.add(alias);
        return this;
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return Collections.emptyList();
    }

    @Override
    public final void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!CommandHelpers.executeStandardCommands(server, sender, this, args))
            executeSubCommand(server, sender, args);
    }

    public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        CommandHelpers.throwWrongUsage(sender, this);
    }

    public SubCommand setPermLevel(PermLevel permLevel) {
        this.permLevel = permLevel;
        return this;
    }

    @Override
    public final int getPermissionLevel() {
        return permLevel.permLevel;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return CommandHelpers.checkPermission(server, sender, this);
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + getFullString() + " help";
    }

    @Override
    public void printHelp(ICommandSender sender) {
        CommandHelpers.printHelp(sender, this);
    }

    @Override
    public String getFullString() {
        return parent.getFullString() + " " + getName();
    }

    @Override
    public int compareTo(ICommand command) {
        return getName().compareTo(command.getName());
    }

}