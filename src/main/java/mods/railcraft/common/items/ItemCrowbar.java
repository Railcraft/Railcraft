/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import buildcraft.api.tools.IToolWrench;
import com.google.common.collect.Sets;
import ic2.api.item.IBoxable;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.blocks.tracks.elevator.BlockTrackElevator;
import mods.railcraft.common.items.enchantment.RailcraftEnchantments;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Optional.InterfaceList({
        @Optional.Interface(iface = "ic2.api.item.IBoxable", modid = "IC2API"),
        @Optional.Interface(iface = "buildcraft.api.tools.IToolWrench", modid = "BuildCraftAPI|tools")
})
public abstract class ItemCrowbar extends ItemTool implements IToolCrowbar, IBoxable, IToolWrench, IRailcraftItemSimple {

    private static final int BOOST_DAMAGE = 1;
    private final Set<Class<? extends Block>> shiftRotations = new HashSet<>();
    private final Set<Class<? extends Block>> bannedRotations = new HashSet<>();

    protected ItemCrowbar(ItemMaterials.Material material, ToolMaterial vanillaMaterial) {
        this(
                ItemMaterials.Tool.CROWBAR.getAttributeF(material, ItemMaterials.Attribute.ATTACK_DAMAGE),
                ItemMaterials.Tool.CROWBAR.getAttributeF(material, ItemMaterials.Attribute.ATTACK_SPEED),
                vanillaMaterial
        );
    }

    protected ItemCrowbar(float attackDamageIn, float attackSpeedIn, ToolMaterial vanillaMaterial) {
        super(
                attackDamageIn,
                attackSpeedIn,
                vanillaMaterial,
                Sets.newHashSet(Blocks.RAIL, Blocks.DETECTOR_RAIL, Blocks.GOLDEN_RAIL, Blocks.ACTIVATOR_RAIL)
        );
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        shiftRotations.add(BlockLever.class);
        shiftRotations.add(BlockButton.class);
        shiftRotations.add(BlockChest.class);
        bannedRotations.add(BlockRailBase.class);

        setHarvestLevel("crowbar", 2);
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
    public float getStrVsBlock(ItemStack stack, IBlockState state) {
        if (TrackTools.isRailBlock(state))
            return efficiencyOnProperMaterial;
        return super.getStrVsBlock(stack, state);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    private boolean isShiftRotation(Class<? extends Block> cls) {
        for (Class<? extends Block> shift : shiftRotations) {
            if (shift.isAssignableFrom(cls))
                return true;
        }
        return false;
    }

    private boolean isBannedRotation(Class<? extends Block> cls) {
        for (Class<? extends Block> banned : bannedRotations) {
            if (banned.isAssignableFrom(cls))
                return true;
        }
        return false;
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState blockState = WorldPlugin.getBlockState(world, pos);

        if (WorldPlugin.isBlockAir(world, pos, blockState))
            return EnumActionResult.PASS;

        if (player.isSneaking() != isShiftRotation(blockState.getBlock().getClass()))
            return EnumActionResult.PASS;

        if (isBannedRotation(blockState.getBlock().getClass()))
            return EnumActionResult.PASS;

        if (blockState.getBlock().rotateBlock(world, pos, facing)) {
            player.swingArm(hand);
            stack.damageItem(1, player);
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        if (!world.isRemote)
            if (entityLiving instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entityLiving;
                if (!player.isSneaking()) {
                    int level = RailcraftEnchantments.DESTRUCTION.getLevel(stack) * 2 + 1;
                    if (level > 1)
                        checkBlocks(world, level, pos, player);
                }
            }
        return super.onBlockDestroyed(stack, world, state, pos, entityLiving);
    }

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        stack.damageItem(1, attacker);
        return true;
    }
//    @Override
//    public EnumAction getItemUseAction(ItemStack stack) {
//        return EnumAction.BLOCK;
//    }

//    @Override
//    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
//        return 72000;
//    }

//    @Override
//    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
//        player.setActiveHand(hand);
//        return ActionResult.newResult(EnumActionResult.SUCCESS, itemStack);
//    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }

    @Override
    public boolean canWrench(EntityPlayer player, EnumHand hand, ItemStack wrench, RayTraceResult rayTrace) {
        return true;
    }

    @Override
    public void wrenchUsed(EntityPlayer player, EnumHand hand, ItemStack wrench, RayTraceResult rayTrace) {
        wrench.damageItem(1, player);
        player.swingArm(hand);
    }

    @Override
    public boolean canWhack(EntityPlayer player, EnumHand hand, ItemStack crowbar, BlockPos pos) {
        return true;
    }

    @Override
    public void onWhack(EntityPlayer player, EnumHand hand, ItemStack crowbar, BlockPos pos) {
        crowbar.damageItem(1, player);
        player.swingArm(hand);
    }

    @Override
    public boolean canLink(EntityPlayer player, EnumHand hand, ItemStack crowbar, EntityMinecart cart) {
        return player.isSneaking();
    }

    @Override
    public void onLink(EntityPlayer player, EnumHand hand, ItemStack crowbar, EntityMinecart cart) {
        crowbar.damageItem(1, player);
        player.swingArm(hand);
    }

    @Override
    public boolean canBoost(EntityPlayer player, EnumHand hand, ItemStack crowbar, EntityMinecart cart) {
        return !player.isSneaking();
    }

    @Override
    public void onBoost(EntityPlayer player, EnumHand hand, ItemStack crowbar, EntityMinecart cart) {
        crowbar.damageItem(BOOST_DAMAGE, player);
        player.swingArm(hand);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> info, boolean adv) {
        addToolTips(stack, player, info, adv);
        info.add(LocalizationPlugin.translate("item.railcraft.tool.crowbar.tips"));
    }

    private void removeExtraBlocks(World world, int level, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (level > 0) {
            WorldPlugin.playerRemoveBlock(world, pos, player);
            checkBlocks(world, level, pos, player);
        }
    }

    private void checkBlock(World world, int level, BlockPos pos, EntityPlayer player) {
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        if (TrackTools.isRailBlock(state) || state.getBlock() instanceof BlockTrackElevator || state.getBlock().isToolEffective("crowbar", state))
            removeExtraBlocks(world, level - 1, pos, state, player);
    }

    private void checkBlocks(World world, int level, BlockPos pos, EntityPlayer player) {
        //NORTH
        checkBlock(world, level, pos.add(0, 0, -1), player);
        checkBlock(world, level, pos.add(0, 1, -1), player);
        checkBlock(world, level, pos.add(0, -1, -1), player);
        //SOUTH
        checkBlock(world, level, pos.add(0, 0, 1), player);
        checkBlock(world, level, pos.add(0, 1, 1), player);
        checkBlock(world, level, pos.add(0, -1, 1), player);
        //EAST
        checkBlock(world, level, pos.add(1, 0, 0), player);
        checkBlock(world, level, pos.add(1, 1, 0), player);
        checkBlock(world, level, pos.add(1, -1, 0), player);
        //WEST
        checkBlock(world, level, pos.add(-1, 0, 0), player);
        checkBlock(world, level, pos.add(-1, 1, 0), player);
        checkBlock(world, level, pos.add(-1, -1, 0), player);
        //UP_DOWN
        checkBlock(world, level, pos.up(), player);
        checkBlock(world, level, pos.down(), player);
    }

    @Nullable
    @Override
    public String getOreTag(@Nullable IVariantEnum variant) {
        return ORE_TAG;
    }

    @Override
    public void initializeDefinintion() {
        OreDictionary.registerOre(ORE_TAG, new ItemStack(this, 1, OreDictionary.WILDCARD_VALUE));
    }
}
