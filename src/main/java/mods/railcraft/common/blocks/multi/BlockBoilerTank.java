package mods.railcraft.common.blocks.multi;

import net.minecraft.block.material.Material;

/**
 *
 */
public abstract class BlockBoilerTank extends BlockMultiBlock {

    //TODO connection states

    protected BlockBoilerTank() {
        super(Material.ROCK);
        setHarvestLevel("pickaxe", 1);
    }
}
