/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.single;

import com.google.common.collect.Lists;
import mods.railcraft.api.items.IMetalsChestCondenseRule;
import mods.railcraft.client.render.tesr.TESRChest;
import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Collection;
import java.util.List;

@BlockMeta.Tile(TileChestMetals.class)
public class BlockChestMetals extends BlockChestRailcraft<TileChestMetals> {
    public static final Collection<String> PREFIXES = Lists.newArrayList("nugget", "ingot", "dust", "gem", "fuel");

    @SideOnly(Side.CLIENT)
    @Override
    public void initializeClient() {
        super.initializeClient();
        ClientRegistry.bindTileEntitySpecialRenderer(TileChestMetals.class, new TESRChest(this));
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(new ItemStack(this),
                "GPG",
                "PAP",
                "GPG",
                'A', new ItemStack(Blocks.ANVIL),
                'P', new ItemStack(Blocks.PISTON),
                'G', "gearSteel");
    }

    @Override
    public void finalizeDefinition() {
        super.finalizeDefinition();
        initOreCondensationRules();
        initHardcodedRules();
    }

    private void initHardcodedRules() {
        IMetalsChestCondenseRule bone = IMetalsChestCondenseRule.of(stack ->
                        stack.getItem() == Items.DYE && EnumDyeColor.byDyeDamage(stack.getItemDamage()) == EnumDyeColor.WHITE,
                9, new ItemStack(Blocks.BONE_BLOCK));
        IMetalsChestCondenseRule wheat = IMetalsChestCondenseRule.of(stack -> stack.getItem() == Items.WHEAT, 9,
                new ItemStack(Blocks.HAY_BLOCK));
        IMetalsChestCondenseRule slime = IMetalsChestCondenseRule.of(stack -> stack.getItem() == Items.SLIME_BALL, 9,
                new ItemStack(Blocks.SLIME_BLOCK));

        IMetalsChestCondenseRule.rules.add(bone);
        IMetalsChestCondenseRule.rules.add(wheat);
        IMetalsChestCondenseRule.rules.add(slime);
    }

    private void initOreCondensationRules() {
        final Container dummyCallback = new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer playerIn) {
                return false;
            }
        };
        final InventoryCrafting playerCrafting = new InventoryCrafting(dummyCallback, 2, 2);
        final InventoryCrafting blockCrafting = new InventoryCrafting(dummyCallback, 3, 3);

        for (String ore : OreDictionary.getOreNames()) {
            List<ItemStack> stacks = OreDictionary.getOres(ore);
            if (stacks.isEmpty())
                continue;
            ItemStack stack = stacks.get(0);
//            Game.log().msg(Game.DEBUG_REPORT, "Found ore {0} for trial", ore);

            if (PREFIXES.stream().noneMatch(prefix -> ore.startsWith(prefix) && Character.isUpperCase(ore.charAt(prefix.length()))))
                continue;

            final ItemStack nineCompressionResult = checkCompression(blockCrafting, stack);
            if (!nineCompressionResult.isEmpty()) {
                IMetalsChestCondenseRule.rules.add(oreCondenseRule(ore, nineCompressionResult, 9));
                continue;
            }

            final ItemStack fourCompressionResult = checkCompression(playerCrafting, stack);
            if (!fourCompressionResult.isEmpty()) {
                IMetalsChestCondenseRule.rules.add(oreCondenseRule(ore, fourCompressionResult, 4));
            }
        }
    }

    private static ItemStack checkCompression(InventoryCrafting crafting, ItemStack stack) {
        crafting.clear();
        for (int i = 0; i < crafting.getSizeInventory(); i++)
            crafting.setInventorySlotContents(i, stack);
        ItemStack compressed = CraftingManager.findMatchingResult(crafting, null);

        if (compressed.getCount() != 1) // some other stuff may produce more than 1 output which we don't want
            return ItemStack.EMPTY;

        crafting.clear();
        crafting.setInventorySlotContents(0, compressed);
        ItemStack decompressed = CraftingManager.findMatchingResult(crafting, null);

        if (decompressed.isEmpty())
            return ItemStack.EMPTY;

        if (InvTools.isItemEqual(stack, decompressed) && decompressed.getCount() == crafting.getSizeInventory()) {
            return compressed;
        }
        return ItemStack.EMPTY;
    }

    private static IMetalsChestCondenseRule oreCondenseRule(String dict, ItemStack compressed, int count) {
        final int oreId = OreDictionary.getOreID(dict);
        return IMetalsChestCondenseRule.of(stack -> ArrayUtils.contains(OreDictionary.getOreIDs(stack), oreId), count, compressed);
    }
}
