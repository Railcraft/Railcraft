/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.commands;

import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.api.crafting.IBlastFurnaceCrafter;
import mods.railcraft.api.crafting.ICokeOvenCrafter;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;

/**
 *
 */
public final class CommandCrafting extends SubCommand {

    public CommandCrafting() {
        super("crafting");
        setPermLevel(PermLevel.EVERYONE);
        addChildCommand(new CokeOven());
        addChildCommand(new BlastFurnace());
        addChildCommand(new BlastFurnaceFuel());
    }

    private static final class CokeOven extends SubCommand {
        CokeOven() {
            super("coke_oven");
        }

        @Override
        public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            if (!(sender instanceof EntityPlayerMP)) {
                CommandHelpers.throwWrongUsage(sender, this);
            }
            EntityPlayerMP player = (EntityPlayerMP) sender;
            ItemStack input = player.getHeldItem(EnumHand.MAIN_HAND);
            ItemStack output = Crafters.cokeOven().getRecipe(input).map(ICokeOvenCrafter.IRecipe::getOutput).orElse(ItemStack.EMPTY);
            player.sendMessage(new TextComponentTranslation("command.railcraft.railcraft.crafting.coke.oven.message",
                    input.getTextComponent(), output.getTextComponent()));
        }
    }

    private static final class BlastFurnace extends SubCommand {
        BlastFurnace() {
            super("blast_furnace");
        }

        @Override
        public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            if (!(sender instanceof EntityPlayerMP)) {
                CommandHelpers.throwWrongUsage(sender, this);
            }
            EntityPlayerMP player = (EntityPlayerMP) sender;
            ItemStack input = player.getHeldItem(EnumHand.MAIN_HAND);
            ItemStack output = Crafters.blastFurnace().getRecipe(input)
                    .map(IBlastFurnaceCrafter.IRecipe::getOutput).orElse(ItemStack.EMPTY);
            player.sendMessage(new TextComponentTranslation("command.railcraft.railcraft.crafting.blast.furnace.message",
                    input.getTextComponent(), output.getTextComponent()));
        }
    }

    private static final class BlastFurnaceFuel extends SubCommand {
        BlastFurnaceFuel() {
            super("blast_furnace_fuel");
        }

        @Override
        public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            if (!(sender instanceof EntityPlayerMP)) {
                CommandHelpers.throwWrongUsage(sender, this);
            }
            EntityPlayerMP player = (EntityPlayerMP) sender;
            ItemStack input = player.getHeldItem(EnumHand.MAIN_HAND);
            int time = Crafters.blastFurnace().getFuel(input).map(f -> f.getTickTime(input)).orElse(0);
            player.sendMessage(new TextComponentTranslation("command.railcraft.railcraft.crafting.blast.furnace.fuel.message",
                    input.getTextComponent(), time));
        }
    }
}
