/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.common.items.ItemCharge.EnumCharge;
import mods.railcraft.common.items.ItemDust.EnumDust;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Map;

/**
 * Created by CovertJaguar on 7/22/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockBatteryEmpty extends BlockCharge {
    private static final Map<Charge, ChargeSpec> CHARGE_SPECS = ChargeSpec.make(Charge.distribution, ConnectType.BLOCK, 0.4);

    public BlockBatteryEmpty() {
        super(Material.CIRCUITS);
        setResistance(10F);
        setHardness(5F);
    }

    @Override
    public void defineRecipes() {
        super.defineRecipes();
        Crafters.rockCrusher().makeRecipe(getStack())
                .addOutput(RailcraftItems.CHARGE.getStack(2, EnumCharge.TERMINAL))
                .addOutput(RailcraftItems.CHARGE.getStack(1, EnumCharge.SPOOL_MEDIUM))
                .addOutput(RailcraftItems.DUST.getStack(4, EnumDust.SLAG))
                .addOutput(RailcraftItems.DUST.getStack(2, EnumDust.SLAG), 0.5F)
                .register();
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void initializeDefinition() {
        HarvestPlugin.setBlockHarvestLevel("pickaxe", 1, this);
        ForestryPlugin.addBackpackItem("forestry.builder", this);
    }

    @Override
    public Map<Charge, ChargeSpec> getChargeSpecs(IBlockState state, IBlockAccess world, BlockPos pos) {
        return CHARGE_SPECS;
    }
}
