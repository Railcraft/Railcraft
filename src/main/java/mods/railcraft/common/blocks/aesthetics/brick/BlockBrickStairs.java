/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.aesthetics.brick;

import mods.railcraft.common.blocks.IRailcraftBlock;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.crafting.RockCrusherCrafter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;

import java.util.Objects;

import static mods.railcraft.common.blocks.aesthetics.brick.BrickVariant.BRICK;

/**
 * Created by CovertJaguar on 7/29/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockBrickStairs extends BlockStairs implements IRailcraftBlock {
    public final BrickTheme brickTheme;
    public final BrickVariant brickVariant;

    public BlockBrickStairs(BrickTheme brickTheme, BrickVariant brickVariant) {
        super(Objects.requireNonNull(brickTheme.getState(BRICK)));
        this.brickTheme = brickTheme;
        this.brickVariant = brickVariant;
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(getStack(8),
                "I  ",
                "II ",
                "III",
                'I', brickTheme, brickVariant);
        CraftingPlugin.addShapedRecipe(brickTheme.getStack(3, brickVariant),
                "II",
                "II",
                'I', getStack());
        RockCrusherCrafter.INSTANCE.makeRecipe(getStack())
                .addOutput(brickTheme.getStack(BrickVariant.COBBLE), 0.75F).register();
    }

    @Override
    public Block getObject() {
        return this;
    }
}
