/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.multi;

import mods.railcraft.api.charge.IBatteryBlock;
import mods.railcraft.common.blocks.charge.Charge;
import mods.railcraft.common.items.ItemCharge;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 */
public final class BlockSteamTurbine extends BlockMultiBlockCharge {

    public static final IProperty<Boolean> WINDOW = PropertyBool.create("window");
    public static final IProperty<Axis> LONG_AXIS = PropertyEnum.create("long_axis", Axis.class, Axis.X, Axis.Z);
    public static final IProperty<Texture> TEXTURE = PropertyEnum.create("texture", Texture.class);
    private static final ChargeDef DEFINITION = new ChargeDef(ConnectType.BLOCK, 0.0,
            new IBatteryBlock.Spec(IBatteryBlock.State.DISABLED, TileSteamTurbine.IC2_OUTPUT, TileSteamTurbine.IC2_OUTPUT, 1.0));

    public BlockSteamTurbine() {
        super(Material.IRON);
        setSoundType(SoundType.METAL);
        setDefaultState(getDefaultState().withProperty(WINDOW, false).withProperty(LONG_AXIS, Axis.X).withProperty(TEXTURE, Texture.NONE));
        setHarvestLevel("pickaxe", 1);
    }

    @Override
    public ChargeDef getChargeDef(IBlockState state, IBlockAccess world, BlockPos pos) {
        return DEFINITION;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, WINDOW, LONG_AXIS, TEXTURE);
    }

    @Override
    public TileMultiBlock createTileEntity(World world, IBlockState state) {
        return new TileSteamTurbine();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public Class<TileSteamTurbine> getTileClass(IBlockState state) {
        return TileSteamTurbine.class;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        registerNode(state, worldIn, pos);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        deregisterNode(worldIn, pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(3, 3);
    }

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this, 3);
        CraftingPlugin.addRecipe(stack,
                "BPB",
                "PEP",
                "BPB",
                'P', "plateSteel",
                'B', "blockSteel",
                'E', RailcraftItems.CHARGE, ItemCharge.EnumCharge.MOTOR
        );
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World worldIn, BlockPos pos) {
        return Charge.distribution.network(worldIn).grid(pos).getComparatorOutput();
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
