package mods.railcraft.common.commands;

import mods.railcraft.api.crafting.IBlastFurnaceRecipe;
import mods.railcraft.api.crafting.ICokeOvenRecipe;
import mods.railcraft.common.util.crafting.BlastFurnaceCraftingManager;
import mods.railcraft.common.util.crafting.CokeOvenCraftingManager;
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
            ICokeOvenRecipe recipe = CokeOvenCraftingManager.getInstance().getRecipe(input);
            ItemStack output = recipe == null ? ItemStack.EMPTY : recipe.getOutput();
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
            IBlastFurnaceRecipe recipe = BlastFurnaceCraftingManager.getInstance().getRecipe(input);
            ItemStack output = recipe == null ? ItemStack.EMPTY : recipe.getOutput();
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
            int time = BlastFurnaceCraftingManager.getInstance().getCookTime(input);
            player.sendMessage(new TextComponentTranslation("command.railcraft.railcraft.crafting.blast.furnace.fuel.message",
                    input.getTextComponent(), time));
        }
    }
}
