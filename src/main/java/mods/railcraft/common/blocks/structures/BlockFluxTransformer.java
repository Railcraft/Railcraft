/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.structures;

import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.charge.IBatteryBlock;
import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.items.ItemCharge;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.item.ItemStack;

import java.util.Map;

@BlockMeta.Tile(TileFluxTransformer.class)
public final class BlockFluxTransformer extends BlockStructureCharge<TileFluxTransformer> {
    public static final PropertyInteger ICON = PropertyInteger.create("icon", 0, 1);
    private static final Map<Charge, ChargeSpec> CHARGE_SPECS = ChargeSpec.make(Charge.distribution, ConnectType.BLOCK, 0.5,
            new IBatteryBlock.Spec(IBatteryBlock.State.DISABLED, 500, 500, 1.0));

    public BlockFluxTransformer() {
        super(Material.IRON, CHARGE_SPECS);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ICON);
    }

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this, 2);
        CraftingPlugin.addShapedRecipe("railcraft:flux_transformer",
                stack,
                "CGC",
                "GRG",
                "CTC",
                'G', RailcraftItems.PLATE, Metal.GOLD,
                'C', RailcraftItems.CHARGE, ItemCharge.EnumCharge.SPOOL_SMALL,
                'T', RailcraftItems.CHARGE, ItemCharge.EnumCharge.TERMINAL,
                'R', "blockRedstone");
    }
}
