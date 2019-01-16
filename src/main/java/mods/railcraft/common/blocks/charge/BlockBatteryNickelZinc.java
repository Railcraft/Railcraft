/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import mods.railcraft.api.charge.IBatteryBlock;
import mods.railcraft.common.items.ItemCharge;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by CovertJaguar on 11/8/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockBatteryNickelZinc extends BlockBattery {
    private static final ChargeSpec CHARGE_SPEC = new ChargeSpec(ConnectType.BLOCK, 0.2,
            new IBatteryBlock.Spec(IBatteryBlock.State.RECHARGEABLE, 150_000, 16.0, 0.7));

    @Override
    public void initializeDefinition() {
        super.initializeDefinition();
        OreDictionary.registerOre(RECHARGEABLE_BATTERY_ORE_TAG, getStack());
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(getStack(),
                "TWT",
                "NSZ",
                "NBZ",
                'T', RailcraftItems.CHARGE, ItemCharge.EnumCharge.TERMINAL,
                'N', RailcraftItems.CHARGE, ItemCharge.EnumCharge.ELECTRODE_NICKEL,
                'Z', RailcraftItems.CHARGE, ItemCharge.EnumCharge.ELECTRODE_ZINC,
                'W', RailcraftItems.CHARGE, ItemCharge.EnumCharge.SPOOL_MEDIUM,
                'S', "dustSaltpeter",
                'B', Items.WATER_BUCKET);
    }

    @Override
    protected ChargeSpec getChargeSpec(IBlockState state) {
        return CHARGE_SPEC;
    }
}
