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
import mods.railcraft.common.blocks.logic.SteamTurbineLogic;
import mods.railcraft.common.items.ItemCharge;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

@BlockMeta.Tile(TileSteamTurbine.class)
public final class BlockSteamTurbine extends BlockStructureCharge<TileSteamTurbine> {

    public static final IProperty<Boolean> WINDOW = PropertyBool.create("window");
    public static final IProperty<Axis> LONG_AXIS = PropertyEnum.create("long_axis", Axis.class, Axis.X, Axis.Z);
    public static final IProperty<Texture> TEXTURE = PropertyEnum.create("texture", Texture.class);
    private static final Map<Charge, ChargeSpec> CHARGE_SPECS = ChargeSpec.make(Charge.distribution, ConnectType.BLOCK, 0.0,
            new IBatteryBlock.Spec(IBatteryBlock.State.DISABLED,
                    SteamTurbineLogic.CHARGE_OUTPUT, SteamTurbineLogic.CHARGE_OUTPUT, 1.0));

    public BlockSteamTurbine() {
        super(Material.IRON, CHARGE_SPECS);
        setDefaultState(getDefaultState().withProperty(WINDOW, false).withProperty(LONG_AXIS, Axis.X).withProperty(TEXTURE, Texture.NONE));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, WINDOW, LONG_AXIS, TEXTURE);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(3, 3);
    }

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this, 3);
        CraftingPlugin.addShapedRecipe(stack,
                "BPB",
                "PEP",
                "BPB",
                'P', "plateSteel",
                'B', "blockSteel",
                'E', RailcraftItems.CHARGE, ItemCharge.EnumCharge.MOTOR
        );
    }

    enum Texture implements IStringSerializable {

        TOP_LEFT("top_left"),
        TOP_RIGHT("top_right"),
        BOTTOM_LEFT("bottom_left"),
        BOTTOM_RIGHT("bottom_right"),
        NONE("none");

        private final String name;

        Texture(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
