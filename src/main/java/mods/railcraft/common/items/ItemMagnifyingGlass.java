/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.api.core.IOwnable;
import mods.railcraft.api.items.ActivationBlockingItem;
import mods.railcraft.api.signals.DualLamp;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.common.blocks.TileLogic;
import mods.railcraft.common.blocks.logic.StructureLogic;
import mods.railcraft.common.blocks.machine.wayobjects.signals.IDualHeadSignal;
import mods.railcraft.common.blocks.machine.wayobjects.signals.TileSignalBase;
import mods.railcraft.common.blocks.structures.StructurePattern;
import mods.railcraft.common.blocks.structures.StructurePattern.State;
import mods.railcraft.common.blocks.structures.TileMultiBlock;
import mods.railcraft.common.plugins.forge.*;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@ActivationBlockingItem
public class ItemMagnifyingGlass extends ItemRailcraft {

    private static final String CART_NUMBERING_KEY = "cartNumber";

    public ItemMagnifyingGlass() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setFull3D();

        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    public void setCartNumber(ItemStack stack, int number) {
        stack.setTagInfo(CART_NUMBERING_KEY, new NBTTagInt(number));
    }

    public void clearCartNumber(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null) {
            tag.removeTag(CART_NUMBERING_KEY);
        }
    }

    public int getCartNumber(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey(CART_NUMBERING_KEY, NBT.TAG_INT)) {
            return tag.getInteger(CART_NUMBERING_KEY);
        }
        return 0;
    }

    @Override
    public void initializeDefinition() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(new ItemStack(this),
                " G",
                "S ",
                'S', "stickWood",
                'G', "paneGlassColorless"
        );
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        EntityPlayer thePlayer = event.getEntityPlayer();

        Entity entity = event.getTarget();

        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof ItemMagnifyingGlass)
            thePlayer.swingArm(event.getHand());

        if (Game.isClient(thePlayer.world))
            return;

        if (stack.getItem() instanceof ItemMagnifyingGlass) {
            if (entity instanceof EntityMinecart) {
                EntityMinecart cart = (EntityMinecart) entity;
                ChatPlugin.sendLocalizedChatFromServer(thePlayer, "gui.railcraft.mag.glass.placedby", LocalizationPlugin.getEntityLocalizationTag(cart), CartToolsAPI.getCartOwner(cart));
                event.setCanceled(true);
            }
            if (entity instanceof IMagnifiable) {
                ((IMagnifiable) entity).onMagnify(thePlayer);
            }
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        int t = getCartNumber(stack);
        if (t > 0 && entity instanceof EntityMinecart) {
            entity.setCustomNameTag("Cart " + t);
            setCartNumber(stack, t + 1);
            return true;
        }
        return false;
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (Game.isClient(world))
            return EnumActionResult.PASS;
        TileEntity t = WorldPlugin.getBlockTile(world, pos);
        EnumActionResult returnValue = EnumActionResult.PASS;
        if (t instanceof IOwnable) {
            IOwnable ownable = (IOwnable) t;
            ChatPlugin.sendLocalizedChatFromServer(player, "gui.railcraft.mag.glass.placedby", ownable.getDisplayName(), ownable.getOwner());
            returnValue = EnumActionResult.SUCCESS;
        }

        if (t instanceof TileMultiBlock) {
            TileMultiBlock tile = (TileMultiBlock) t;
            if (tile.isStructureValid()) {
                ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.multiblock.state.valid");
                ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.multiblock.state.master." + (tile.isValidMaster() ? "true" : "false"));
            } else
                for (State returnState : EnumSet.complementOf(EnumSet.of(State.VALID))) {
                    List<StructurePattern> pats = tile.patternStates.get(returnState);
                    if (!pats.isEmpty()) {
                        List<Integer> indexList = pats.stream().map(map -> tile.getPatterns().indexOf(map)).collect(Collectors.toList());
                        ChatPlugin.sendLocalizedChatFromServer(player, returnState.message, indexList.toString());
                    }
                }
            returnValue = EnumActionResult.SUCCESS;
        }

        if (t instanceof TileLogic) {
            TileLogic tile = (TileLogic) t;
            Optional<StructureLogic> optLogic = tile.getLogic(StructureLogic.class);
            if (optLogic.isPresent()) {
                StructureLogic logic = optLogic.get();
                if (logic.isStructureValid()) {
                    ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.multiblock.state.valid");
                    ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.multiblock.state.master." + (logic.isValidMaster() ? "true" : "false"));
                } else
                    for (State returnState : EnumSet.complementOf(EnumSet.of(State.VALID))) {
                        List<StructurePattern> pats = logic.patternStates.get(returnState);
                        if (!pats.isEmpty()) {
                            List<Integer> indexList = pats.stream().map(map -> logic.getPatterns().indexOf(map)).collect(Collectors.toList());
                            ChatPlugin.sendLocalizedChatFromServer(player, returnState.message, indexList.toString());
                        }
                    }
                returnValue = EnumActionResult.SUCCESS;
            }
        }
        if (t instanceof IDualHeadSignal) {
            IDualHeadSignal signal = (IDualHeadSignal) t;
            SignalAspect top = signal.getSignalAspect(DualLamp.TOP);
            SignalAspect bottom = signal.getSignalAspect(DualLamp.BOTTOM);
            ChatPlugin.sendLocalizedChatFromServer(player, "gui.railcraft.mag.glass.aspect.dual", top.getLocalizationTag(), bottom.getLocalizationTag());
            returnValue = EnumActionResult.SUCCESS;
        } else if (t instanceof TileSignalBase) {
            ChatPlugin.sendLocalizedChatFromServer(player, "gui.railcraft.mag.glass.aspect", ((TileSignalBase) t).getSignalAspect().getLocalizationTag());
            returnValue = EnumActionResult.SUCCESS;
        }
        if (t instanceof IMagnifiable) {
            ((IMagnifiable) t).onMagnify(player);
        }
        return returnValue;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> info, ITooltipFlag adv) {
        super.addInformation(stack, world, info, adv);
        int t = getCartNumber(stack);
        if (t > 0) {
            info.add(LocalizationPlugin.translate("gui.railcraft.mag.glass.cart.number", t));
        }
    }
}
