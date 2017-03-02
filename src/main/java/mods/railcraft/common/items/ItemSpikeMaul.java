/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import com.google.common.collect.Iterators;
import ic2.api.item.IBoxable;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.api.tracks.TrackToolsAPI;
import mods.railcraft.api.tracks.TrackType;
import mods.railcraft.common.blocks.charge.ChargeManager;
import mods.railcraft.common.blocks.tracks.TrackShapeHelper;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.blocks.tracks.flex.BlockTrackFlex;
import mods.railcraft.common.blocks.tracks.outfitted.BlockTrackOutfitted;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.*;

@Optional.InterfaceList({
        @Optional.Interface(iface = "ic2.api.item.IBoxable", modid = "IC2API")
})
public abstract class ItemSpikeMaul extends ItemTool implements IBoxable, IRailcraftItemSimple {

    public static final String ORE_TAG = "toolSpikeMaul";

    protected ItemSpikeMaul(ItemMaterials.Material material, ToolMaterial vanillaMaterial) {
        this(
                ItemMaterials.Tool.SPIKE_MAUL.getAttributeF(material, ItemMaterials.Attribute.ATTACK_DAMAGE),
                ItemMaterials.Tool.SPIKE_MAUL.getAttributeF(material, ItemMaterials.Attribute.ATTACK_SPEED),
                vanillaMaterial
        );
    }

    protected ItemSpikeMaul(float attackDamageIn, float attackSpeedIn, ToolMaterial vanillaMaterial) {
        super(
                attackDamageIn,
                attackSpeedIn,
                vanillaMaterial,
                Collections.emptySet()
        );
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    @Override
    public String getUnlocalizedName() {
        return LocalizationPlugin.convertTag(super.getUnlocalizedName());
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getUnlocalizedName();
    }

    @Override
    public Item getObject() {
        return this;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    enum TrackTarget {
        JUNCTION {
            @Override
            public boolean matches(World world, BlockPos pos, IBlockState state) {
                return state.getBlock() instanceof BlockTrackOutfitted
                        && ((BlockTrackOutfitted) state.getBlock()).getTrackKit(world, pos) == TrackKits.JUNCTION.getTrackKit();
            }

            @Override
            public boolean setToTarget(World world,
                                       BlockPos pos,
                                       IBlockState state,
                                       EntityPlayer player,
                                       BlockRailBase.EnumRailDirection shape,
                                       TrackType trackType) {
                return BlockTrackOutfitted.placeTrack(world, pos, player, shape, trackType, TrackKits.JUNCTION.getTrackKit());
            }
        },
        FLEX {
            @Override
            public boolean matches(World world, BlockPos pos, IBlockState state) {
                return state.getBlock() instanceof BlockTrackFlex || state.getBlock() == Blocks.RAIL;
            }

            @Override
            public boolean setToTarget(World world,
                                       BlockPos pos,
                                       IBlockState state,
                                       EntityPlayer player,
                                       BlockRailBase.EnumRailDirection shape,
                                       TrackType trackType) {
                IBlockState newState = TrackToolsAPI.makeTrackState(trackType.getBaseBlock(), TrackTools.getTrackDirectionRaw(state));
                ChargeManager.getNetwork(world).deregisterChargeNode(pos);
                return WorldPlugin.setBlockState(world, pos, newState);
            }
        };

        public abstract boolean matches(World world, BlockPos pos, IBlockState state);

        public abstract boolean setToTarget(World world, BlockPos pos, IBlockState state, EntityPlayer player, BlockRailBase.EnumRailDirection shape, TrackType trackType);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        playerIn.swingArm(hand);
        IBlockState oldState = WorldPlugin.getBlockState(worldIn, pos);
        if (!TrackTools.isRailBlock(oldState))
            return EnumActionResult.PASS;
        TrackType trackType = TrackTools.getTrackTypeAt(worldIn, pos, oldState);
        BlockRailBase.EnumRailDirection shape = TrackTools.getTrackDirectionRaw(oldState);
        if (!TrackShapeHelper.isAscending(shape)) {
            Iterator<TrackTarget> it = Iterators.cycle(TrackTarget.values());
            Set<TrackTarget> tried = new HashSet<>();
            while (true) {
                TrackTarget target = it.next();
                if (tried.contains(target))
                    break;
                tried.add(target);
                if (target.matches(worldIn, pos, oldState)) {
                    target = it.next();
                    if (Game.isClient(worldIn))
                        return EnumActionResult.SUCCESS;
                    if (target.setToTarget(worldIn, pos, oldState, playerIn, shape, trackType)) {
                        SoundHelper.playPlaceSoundForBlock(worldIn, pos);
                        stack.damageItem(1, playerIn);
                        return EnumActionResult.SUCCESS;
                    }
                }
            }
        }
        return EnumActionResult.PASS;
    }

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        stack.damageItem(3, attacker);
        return true;
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> info, boolean advInfo) {
        info.add(LocalizationPlugin.translate("item.railcraft.tool.spike.maul.tips"));
    }

    @Override
    public Object getRecipeObject(@Nullable IVariantEnum meta) {
        return ORE_TAG;
    }

    @Override
    public void initializeDefinintion() {
        OreDictionary.registerOre(ORE_TAG, new ItemStack(this, 1, OreDictionary.WILDCARD_VALUE));
    }
}