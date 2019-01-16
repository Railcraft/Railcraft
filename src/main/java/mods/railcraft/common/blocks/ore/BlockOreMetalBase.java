/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.ore;

import mods.railcraft.common.blocks.BlockRailcraftSubtyped;
import mods.railcraft.common.blocks.IVariantEnumBlock;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.oredict.OreDictionary;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class BlockOreMetalBase<V extends Enum<V> & IVariantEnumBlock<V>> extends BlockRailcraftSubtyped<V> {
    protected BlockOreMetalBase() {
        super(Material.ROCK);
        setResistance(5);
        setHardness(3);
        setSoundType(SoundType.STONE);
    }

    @Override
    public void initializeDefinition() {
        EntityTunnelBore.addMineableBlock(this);
        HarvestPlugin.setBlockHarvestLevel("pickaxe", 1, this);

        for (IVariantEnumBlock<V> ore : getVariants()) {
            ForestryPlugin.addBackpackItem("forestry.miner", ore.getStack());
            if (ore.isEnabled())
                OreDictionary.registerOre(ore.getOreTag(), ore.getStack());
        }
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
