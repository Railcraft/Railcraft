/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.charge.IChargeBlock;
import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.post.BlockPostBase;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.items.ItemCharge;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.EnumTools;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Created by CovertJaguar on 7/22/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockWire extends BlockCharge implements IPostConnection {

    public static final PropertyEnum<Addon> ADDON = PropertyEnum.create("addon", Addon.class);
    public static final PropertyEnum<Connection> DOWN = PropertyEnum.create("down", Connection.class);
    public static final PropertyEnum<Connection> UP = PropertyEnum.create("up", Connection.class);
    public static final PropertyEnum<Connection> NORTH = PropertyEnum.create("north", Connection.class);
    public static final PropertyEnum<Connection> SOUTH = PropertyEnum.create("south", Connection.class);
    public static final PropertyEnum<Connection> WEST = PropertyEnum.create("west", Connection.class);
    public static final PropertyEnum<Connection> EAST = PropertyEnum.create("east", Connection.class);
    @SuppressWarnings("unchecked")
    public static final PropertyEnum<Connection>[] connectionProperties = new PropertyEnum[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
    private static final EnumMap<EnumFacing, EnumSet<IChargeBlock.ConnectType>> connectionMatcher = new EnumMap<>(EnumFacing.class);
    private static final Map<Charge, ChargeSpec> CHARGE_SPECS = ChargeSpec.make(Charge.distribution, ConnectType.WIRE, 0.02);

    static {
        for (EnumFacing side : EnumFacing.VALUES) {
            connectionMatcher.put(side, EnumSet.of(IChargeBlock.ConnectType.BLOCK, IChargeBlock.ConnectType.WIRE));
        }
        connectionMatcher.put(EnumFacing.UP, EnumSet.allOf(IChargeBlock.ConnectType.class));
    }

    public BlockWire() {
        super(Material.CIRCUITS, MapColor.BLUE);
        IBlockState defaultState = blockState.getBaseState().withProperty(ADDON, Addon.NONE);
        for (PropertyEnum<Connection> connection : connectionProperties) {
            defaultState = defaultState.withProperty(connection, Connection.NONE);
        }
        setDefaultState(defaultState);
        setResistance(1F);
        setHardness(1F);
    }

    @Override
    public void initializeDefinition() {
//                HarvestPlugin.setStateHarvestLevel(instance, "crowbar", 0);
        HarvestPlugin.setBlockHarvestLevel("pickaxe", 1, this);

        ForestryPlugin.addBackpackItem("forestry.builder", this);
    }

    @Override
    public void defineRecipes() {
        Crafters.rollingMachine().newRecipe(
                getStack(8, null)).shaped(
                "LPL",
                "PCP",
                "LPL",
                'C', RailcraftItems.CHARGE, ItemCharge.EnumCharge.SPOOL_LARGE,
                'P', Items.PAPER,
                'L', "ingotLead");

        Crafters.rollingMachine().newRecipe(
                getStack(8, null)).shaped(
                "LPL",
                "PCP",
                "LPL",
                'C', RailcraftItems.CHARGE, ItemCharge.EnumCharge.SPOOL_LARGE,
                'P', Items.PAPER,
                'L', "ingotElectricalSteel");
    }

    @Override
    public Map<Charge, ChargeSpec> getChargeSpecs(IBlockState state, IBlockAccess world, BlockPos pos) {
        return CHARGE_SPECS;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (rand.nextInt(50) == 25) {
            IBlockState state = getActualState(stateIn, worldIn, pos);
            int numConnection = (int) Arrays.stream(connectionProperties)
                    .map(state::getValue)
                    .filter(c -> c != Connection.NONE)
                    .count();
            if (numConnection > 2)
                Charge.effects().zapEffectPoint(worldIn, pos);
        }
    }

    @Override
    public IBlockState getItemRenderState(@Nullable IVariantEnum variant) {
        IBlockState state = getDefaultState();
        for (PropertyEnum<Connection> connection : connectionProperties) {
            state = state.withProperty(connection, Connection.WIRE);
        }
        return state;
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        state = super.getActualState(state, worldIn, pos);
        Connection[] connections = new Connection[6];
        Arrays.fill(connections, Connection.NONE);
        for (EnumFacing side : EnumFacing.VALUES) {
            BlockPos neighborPos = pos.offset(side);
            IBlockState neighborState = WorldPlugin.getBlockState(worldIn, pos.offset(side));
            Block neighborBlock = neighborState.getBlock();
            if (neighborBlock instanceof IChargeBlock) {
                ChargeSpec chargeSpec = ((IChargeBlock) neighborBlock).getChargeSpecs(neighborState, worldIn, neighborPos).get(Charge.distribution);
                if (chargeSpec != null) {
                    IChargeBlock.ConnectType connectType = chargeSpec.getConnectType();
                    if (connectionMatcher.get(side).contains(connectType)) {
                        connections[side.ordinal()] = connectType == IChargeBlock.ConnectType.WIRE ? Connection.WIRE : Connection.PLUG;
                    }
                }
            } else if (side.getAxis() == EnumFacing.Axis.Y && neighborBlock instanceof BlockPostBase) {
                connections[side.ordinal()] = Connection.PLUG;
            }
        }
        for (EnumFacing side : EnumFacing.VALUES) {
            state = state.withProperty(connectionProperties[side.ordinal()], connections[side.ordinal()]);
        }
        return state;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(ADDON, EnumTools.fromOrdinal(meta, Addon.VALUES));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ADDON).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ADDON, DOWN, UP, NORTH, SOUTH, WEST, EAST);
    }

    public Addon getAddon(IBlockState state) {
        return state.getValue(ADDON);
    }

    public boolean setAddon(World worldIn, BlockPos pos, IBlockState state, Addon addon) {
        Addon existing = getAddon(state);
        //TODO: drop stuff
        return existing != addon && WorldPlugin.setBlockState(worldIn, pos, state.withProperty(ADDON, addon));
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        Addon addon = getAddon(state);
        return addon.boundingBox;
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        List<ItemStack> drops = super.getDrops(world, pos, state, fortune);
        Addon addon = getAddon(state);
        if (addon.addonObject != null)
            drops.add(addon.addonObject.getStack());
        return drops;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = playerIn.getHeldItem(hand);
        if (InvTools.isStackEqualToBlock(heldItem, RailcraftBlocks.FRAME.block()))
            if (setAddon(worldIn, pos, state, Addon.FRAME)) {
                if (!playerIn.capabilities.isCreativeMode)
                    playerIn.setHeldItem(hand, InvTools.depleteItem(heldItem));
                return true;
            }
        return false;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return getAddon(base_state) == Addon.FRAME && side == EnumFacing.UP;
    }

    @Override
    public ConnectStyle connectsToPost(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
        if (getAddon(state) == Addon.FRAME)
            return IPostConnection.ConnectStyle.TWO_THIN;
        return IPostConnection.ConnectStyle.NONE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        return getAddon(blockState).hardness;
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        IBlockState blockState = WorldPlugin.getBlockState(world, pos);
        return getAddon(blockState).resistance * 0.6F;
    }

    public enum Addon implements IStringSerializable {

        NONE(1, 1, null, AABBFactory.start().box().grow(-0.25).build()),
        FRAME(5, 10, RailcraftBlocks.FRAME, FULL_BLOCK_AABB),
        ;
        //        PYLON(null, FULL_BLOCK_AABB);
        public static final Addon[] VALUES = values();
        private final IRailcraftObjectContainer addonObject;
        private final AxisAlignedBB boundingBox;
        private final float hardness, resistance;
        private final String name;

        Addon(float hardness, float resistance, @Nullable IRailcraftObjectContainer addonObject, AxisAlignedBB boundingBox) {
            this.hardness = hardness;
            this.resistance = resistance;
            this.addonObject = addonObject;
            this.boundingBox = boundingBox;
            name = name().toLowerCase(Locale.ROOT);
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum Connection implements IStringSerializable {

        NONE,
        WIRE,
        PLUG;
        public static final Connection[] VALUES = values();
        private final String name;

        Connection() {
            name = name().toLowerCase(Locale.ROOT);
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
