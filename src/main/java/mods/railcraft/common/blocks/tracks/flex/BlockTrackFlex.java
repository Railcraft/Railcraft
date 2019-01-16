/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks.flex;

import mods.railcraft.api.tracks.TrackType;
import mods.railcraft.common.blocks.tracks.BlockTrackStateless;
import mods.railcraft.common.blocks.tracks.TrackConstants;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.BlockRail;
import net.minecraft.block.properties.IProperty;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

/**
 * Created by CovertJaguar on 8/2/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockTrackFlex extends BlockTrackStateless {

    public BlockTrackFlex(TrackType trackType) {
        super(trackType);
    }

    @Override
    public IProperty<EnumRailDirection> getShapeProperty() {
        return BlockRail.SHAPE;
    }

    @Override
    public final void defineRecipes() {
        defineTrackRecipe();
        if (!RailcraftConfig.vanillaTrackRecipes()) {
            Object[] tracks = new Object[Math.round((float) Math.ceil(((float) TrackConstants.FLEX_RECIPE_OUTPUT) / 6F))];
            Arrays.fill(tracks, getStack());
            CraftingPlugin.addShapelessRecipe(getTrackType().getRail().getStack(), tracks);
        }
    }

    protected void defineTrackRecipe() {
        CraftingPlugin.addShapedRecipe(getRecipeOutput(),
                "I I",
                "I#I",
                "I I",
                'I', getTrackType().getRail(),
                '#', getTrackType().getRailbed());
    }

    public ItemStack getRecipeOutput() {
        return getStack(TrackConstants.FLEX_RECIPE_OUTPUT);
    }
}
