/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.single;

import mods.railcraft.client.render.tesr.TESRChest;
import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@BlockMeta.Tile(TileChestMetals.class)
public class BlockChestMetals extends BlockChestRailcraft<TileChestMetals> {
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
}
