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
import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.items.ItemCharge;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.property.PropertyCharacter;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

@BlockMeta.Tile(TileRockCrusher.class)
public final class BlockRockCrusher extends BlockStructureCharge<TileRockCrusher> {

    public static final IProperty<Character> ICON = PropertyCharacter.create("icon", new char[]{'O', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'B', 'D'});
    private static final Map<Charge, ChargeSpec> CHARGE_SPECS = ChargeSpec.make(Charge.distribution, ConnectType.BLOCK, 0.025);

    public BlockRockCrusher() {
        super(Material.IRON, CHARGE_SPECS);
        setDefaultState(getDefaultState().withProperty(ICON, 'O'));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ICON);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(4, 3);
    }

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this, 4);
        CraftingPlugin.addShapedRecipe(stack,
                "DPD",
                "PSP",
                "DMD",
                'D', "gemDiamond",
                'P', new ItemStack(Blocks.PISTON),
                'M', RailcraftItems.CHARGE, ItemCharge.EnumCharge.MOTOR,
                'S', "blockSteel");
    }
}
