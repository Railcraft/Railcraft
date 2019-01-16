/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items;

import mods.railcraft.api.items.ActivationBlockingItem;
import mods.railcraft.api.items.InvToolsAPI;
import mods.railcraft.client.render.models.resource.ModelManager;
import mods.railcraft.common.blocks.machine.manipulator.TileFluidManipulator;
import mods.railcraft.common.blocks.machine.manipulator.TileItemManipulator;
import mods.railcraft.common.blocks.machine.manipulator.TileManipulatorCart;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.util.inventory.InventoryAdvanced;
import mods.railcraft.common.util.misc.Code;
import mods.railcraft.common.util.misc.EnumTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An item for copying settings from one block to another.
 *
 * It could use some kind of generic interface perhaps.
 *
 * Created by Forecaster on 09/05/2016 for the Railcraft project.
 */
@ActivationBlockingItem
public class ItemNotepad extends ItemRailcraft {
    public static final ModelResourceLocation MODEL_FILLED = new ModelResourceLocation(new ResourceLocation(RailcraftConstants.RESOURCE_DOMAIN, RailcraftItems.NOTEPAD.getBaseTag() + "_filled"), "inventory");
    public static final ModelResourceLocation MODEL_EMPTY = new ModelResourceLocation(new ResourceLocation(RailcraftConstants.RESOURCE_DOMAIN, RailcraftItems.NOTEPAD.getBaseTag() + "_empty"), "inventory");
    public static final String NBT_CONTENTS = "contents";
    public static final String NBT_PASTE_MODE = "pasteMode";

    public ItemNotepad() {
        setMaxStackSize(1);
        setMaxDamage(50);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(new ItemStack(this),
                "IF",
                "XP",
                'I', new ItemStack(Items.DYE, 1, 0),
                'F', Items.FEATHER,
                'X', RailcraftItems.MAG_GLASS,
                'P', Items.PAPER);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initializeClient() {
        ModelManager.registerComplexItemModel(this, stack -> {
            if (InvToolsAPI.getRailcraftDataSubtag(stack, NBT_CONTENTS, false).isPresent())
                return MODEL_FILLED;
            return MODEL_EMPTY;
        }, MODEL_EMPTY, MODEL_FILLED);
    }

    private static void setPasteMode(ItemStack stack, PasteMode mode) {
        Code.assertInstance(ItemNotepad.class, stack.getItem());
        InvToolsAPI.getRailcraftData(stack, true)
                .ifPresent(nbt -> NBTPlugin.writeEnumName(nbt, NBT_PASTE_MODE, mode));
    }

    private static PasteMode getPasteMode(ItemStack stack) {
        Code.assertInstance(ItemNotepad.class, stack.getItem());
        return InvToolsAPI.getRailcraftData(stack, false)
                .map(nbt -> NBTPlugin.readEnumName(nbt, NBT_PASTE_MODE, PasteMode.ALL))
                .orElse(PasteMode.ALL);
    }

    private static PasteMode nextPasteMode(ItemStack stack) {
        Code.assertInstance(ItemNotepad.class, stack.getItem());
        PasteMode pasteMode = getPasteMode(stack);
        pasteMode = EnumTools.next(pasteMode, PasteMode.VALUES);
        setPasteMode(stack, pasteMode);
        return pasteMode;
    }

    private static void setContents(ItemStack stack, EnumMap<Contents, NBTTagCompound> contents) {
        Code.assertInstance(ItemNotepad.class, stack.getItem());
        InvToolsAPI.getRailcraftData(stack, true).ifPresent(nbt -> {
            NBTTagCompound contentTag = new NBTTagCompound();
            for (Map.Entry<Contents, NBTTagCompound> entry : contents.entrySet()) {
                contentTag.setTag(entry.getKey().nbtTag, entry.getValue());
            }
            nbt.setTag(NBT_CONTENTS, contentTag);
        });
    }

    private static EnumMap<Contents, NBTTagCompound> getContents(ItemStack stack) {
        Code.assertInstance(ItemNotepad.class, stack.getItem());
        EnumMap<Contents, NBTTagCompound> contents = new EnumMap<>(Contents.class);
        InvToolsAPI.getRailcraftData(stack, false)
                .filter(nbt -> nbt.hasKey(NBT_CONTENTS))
                .map(nbt -> nbt.getCompoundTag(NBT_CONTENTS))
                .ifPresent(nbt -> {
                    for (Contents content : Contents.VALUES) {
                        if (nbt.hasKey(content.nbtTag)) {
                            contents.put(content, nbt.getCompoundTag(content.nbtTag));
                        }
                    }
                });
        return contents;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> info, ITooltipFlag adv) {
        String contentString;
        EnumMap<Contents, NBTTagCompound> contents = getContents(stack);
        if (contents.isEmpty()) {
            contentString = LocalizationPlugin.translate("item.railcraft.tool.notepad.tips.contents.empty");
        } else {
            List<String> contentTypes = contents.keySet().stream()
                    .map(content -> LocalizationPlugin.translate(content.locTag))
                    .collect(Collectors.toList());
            contentString = StringUtils.join(contentTypes, ", ");
        }
        info.add(LocalizationPlugin.translate("item.railcraft.tool.notepad.tips.contents", contentString));

        PasteMode pasteMode = getPasteMode(stack);
        info.add(LocalizationPlugin.translate("item.railcraft.tool.notepad.tips.mode", TextFormatting.DARK_PURPLE + pasteMode.toString()));

        super.addInformation(stack, world, info, adv);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            InvToolsAPI.clearRailcraftDataSubtag(stack, NBT_CONTENTS);
        } else {
            PasteMode pasteMode = nextPasteMode(stack);
            if (Game.isClient(world))
                ChatPlugin.sendLocalizedChatFromClient(player, "item.railcraft.tool.notepad.tips.mode", TextFormatting.DARK_PURPLE + pasteMode.toString());
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (Game.isClient(world))
            return EnumActionResult.SUCCESS;
        ItemStack stack = player.getHeldItem(hand);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null) {
            if (player.isSneaking()) // COPY
            {
                EnumMap<Contents, NBTTagCompound> contents = new EnumMap<>(Contents.class);
                for (Contents contentType : Contents.VALUES) {
                    NBTTagCompound data = contentType.copy(tileEntity);
                    if (data != null)
                        contents.put(contentType, data);
                }
                if (contents.isEmpty()) {
                    //TODO: Fix in 1.8 to use getDisplayName
                    ChatPlugin.sendLocalizedChatFromServer(player, "item.railcraft.tool.notepad.action.copy.fail");
                } else {
                    setContents(stack, contents);
                    if (tileEntity instanceof IWorldNameable)
                        ChatPlugin.sendLocalizedChatFromServer(player, "item.railcraft.tool.notepad.action.copy", tileEntity.getDisplayName());
                    stack.damageItem(1, player);
                }
            } else // PASTE
            {
                EnumMap<Contents, NBTTagCompound> contents = getContents(stack);
                if (contents.isEmpty()) {
                    ChatPlugin.sendLocalizedChatFromServer(player, "item.railcraft.tool.notepad.action.empty");
                } else {
                    PasteMode pasteMode = getPasteMode(stack);
                    boolean pasted = getContents(stack).entrySet().stream()
                            .filter(entry -> pasteMode.allows(entry.getKey()))
                            .map(entry -> entry.getKey().paste(tileEntity, entry.getValue()))
                            .reduce(false, (a, b) -> a || b);
                    if (pasted) {
                        if (tileEntity instanceof IWorldNameable)
                            ChatPlugin.sendLocalizedChatFromServer(player, "item.railcraft.tool.notepad.action.paste", tileEntity.getDisplayName());
                    } else
                        ChatPlugin.sendLocalizedChatFromServer(player, "item.railcraft.tool.notepad.action.paste.fail", tileEntity.getDisplayName());
                }
            }
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    private enum Contents {
        FILTER_CART("filter.cart", "item.railcraft.tool.notepad.tips.contents.filter.cart") {
            @Override
            NBTTagCompound copy(Object target) {
                if (target instanceof TileManipulatorCart) {
                    InventoryAdvanced cartFilters = ((TileManipulatorCart) target).getCartFilters();
                    NBTTagCompound nbt = new NBTTagCompound();
                    cartFilters.writeToNBT("inv", nbt);
                    return nbt;
                }
                return null;
            }

            @Override
            boolean paste(Object target, NBTTagCompound nbt) {
                if (target instanceof TileManipulatorCart) {
                    ((TileManipulatorCart) target).getCartFilters().readFromNBT("inv", nbt);
                    return true;
                }
                return false;
            }
        },
        FILTER_ITEMS("filter.items", "item.railcraft.tool.notepad.tips.contents.filter.items") {
            @Override
            NBTTagCompound copy(Object target) {
                if (target instanceof TileItemManipulator) {
                    InventoryAdvanced itemFilters = ((TileItemManipulator) target).getItemFilters();
                    NBTTagCompound nbt = new NBTTagCompound();
                    itemFilters.writeToNBT("inv", nbt);
                    return nbt;
                }
                return null;
            }

            @Override
            boolean paste(Object target, NBTTagCompound nbt) {
                if (target instanceof TileItemManipulator) {
                    ((TileItemManipulator) target).getItemFilters().readFromNBT("inv", nbt);
                    return true;
                }
                return false;
            }
        },
        FILTER_FLUID("filter.fluid", "item.railcraft.tool.notepad.tips.contents.filter.fluid") {
            @Override
            NBTTagCompound copy(Object target) {
                if (target instanceof TileFluidManipulator) {
                    InventoryAdvanced itemFilters = ((TileFluidManipulator) target).getFluidFilter();
                    NBTTagCompound nbt = new NBTTagCompound();
                    itemFilters.writeToNBT("inv", nbt);
                    return nbt;
                }
                return null;
            }

            @Override
            boolean paste(Object target, NBTTagCompound nbt) {
                if (target instanceof TileFluidManipulator) {
                    ((TileFluidManipulator) target).getFluidFilter().readFromNBT("inv", nbt);
                    return true;
                }
                return false;
            }
        };
        public static final Contents[] VALUES = values();
        private final String nbtTag;
        private final String locTag;

        Contents(String nbtTag, String locTag) {
            this.nbtTag = nbtTag;
            this.locTag = locTag;
        }

        abstract @Nullable NBTTagCompound copy(Object target);

        abstract boolean paste(Object target, NBTTagCompound nbt);
    }

    private enum PasteMode {
        ALL("item.railcraft.tool.notepad.tips.mode.all", Contents.VALUES),
        CART_FILTER("item.railcraft.tool.notepad.tips.mode.cart.filter", Contents.FILTER_CART),
        LOADER_FILTERS("item.railcraft.tool.notepad.tips.mode.loader.filters", Contents.FILTER_ITEMS, Contents.FILTER_ITEMS);
        public static final PasteMode[] VALUES = values();
        private final String locTag;
        private final EnumSet<Contents> allows;

        PasteMode(String locTag, Contents... allows) {
            this.locTag = locTag;
            this.allows = EnumSet.copyOf(Arrays.asList(allows));
        }

        public boolean allows(Contents content) {
            return allows.contains(content);
        }

        @Override
        public String toString() {
            return LocalizationPlugin.translate(locTag);
        }
    }

}
