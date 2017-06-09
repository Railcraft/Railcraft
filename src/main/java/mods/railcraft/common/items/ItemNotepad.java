/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items;

import mods.railcraft.api.core.items.IActivationBlockingItem;
import mods.railcraft.api.core.items.InvToolsAPI;
import mods.railcraft.client.render.models.resource.ModelManager;
import mods.railcraft.common.blocks.machine.manipulator.TileFluidManipulator;
import mods.railcraft.common.blocks.machine.manipulator.TileItemManipulator;
import mods.railcraft.common.blocks.machine.manipulator.TileManipulatorCart;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.PhantomInventory;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * An item for copying settings from one block to another.
 *
 * It could use some kind of generic interface perhaps.
 *
 * Created by Forecaster on 09/05/2016 for the Railcraft project.
 */
public class ItemNotepad extends ItemRailcraft implements IActivationBlockingItem {
    public final ModelResourceLocation MODEL_FILLED = new ModelResourceLocation(new ResourceLocation(RailcraftConstants.RESOURCE_DOMAIN, RailcraftItems.NOTEPAD.getBaseTag() + "_filled"), "inventory");
    public final ModelResourceLocation MODEL_EMPTY = new ModelResourceLocation(new ResourceLocation(RailcraftConstants.RESOURCE_DOMAIN, RailcraftItems.NOTEPAD.getBaseTag() + "_empty"), "inventory");

    public ItemNotepad() {
        setMaxStackSize(1);
        setMaxDamage(50);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this),
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
            NBTTagCompound tag = InvToolsAPI.getItemDataRailcraft(stack, false);
            if (tag != null && tag.hasKey("contents")) {
                return MODEL_FILLED;
            }
            return MODEL_EMPTY;
        }, MODEL_EMPTY, MODEL_FILLED);
    }

    private static void setPasteMode(ItemStack stack, PasteMode mode) {
        if (!InvTools.isEmpty(stack) && stack.getItem() instanceof ItemNotepad) {
            NBTTagCompound nbt = InvToolsAPI.getItemDataRailcraft(stack, true);
            nbt.setByte("pasteMode", (byte) mode.ordinal());
        }
    }

    private static PasteMode getPasteMode(ItemStack stack) {
        if (!InvTools.isEmpty(stack) && stack.getItem() instanceof ItemNotepad) {
            NBTTagCompound nbt = InvToolsAPI.getItemDataRailcraft(stack, false);
            return PasteMode.fromOrdinal(nbt != null ? nbt.getByte("pasteMode") : 0);
        }
        return PasteMode.ALL;
    }

    private static PasteMode nextPasteMode(ItemStack stack) {
        if (!InvTools.isEmpty(stack) && stack.getItem() instanceof ItemNotepad) {
            PasteMode pasteMode = getPasteMode(stack);
            pasteMode = pasteMode.next();
            setPasteMode(stack, pasteMode);
            return pasteMode;
        }
        return PasteMode.ALL;
    }

    private static void setContents(ItemStack stack, EnumMap<Contents, NBTTagCompound> contents) {
        if (!InvTools.isEmpty(stack) && stack.getItem() instanceof ItemNotepad) {

            NBTTagCompound contentTag = new NBTTagCompound();
            for (Map.Entry<Contents, NBTTagCompound> entry : contents.entrySet()) {
                contentTag.setTag(entry.getKey().nbtTag, entry.getValue());
            }

            NBTTagCompound nbt = InvToolsAPI.getItemDataRailcraft(stack, true);
            nbt.setTag("contents", contentTag);
        }
    }

    @Nonnull
    private static EnumMap<Contents, NBTTagCompound> getContents(ItemStack stack) {
        EnumMap<Contents, NBTTagCompound> contents = new EnumMap<Contents, NBTTagCompound>(Contents.class);
        if (!InvTools.isEmpty(stack) && stack.getItem() instanceof ItemNotepad) {
            NBTTagCompound nbt = InvToolsAPI.getItemDataRailcraft(stack, false);
            if (nbt != null && nbt.hasKey("contents")) {
                nbt = nbt.getCompoundTag("contents");
                for (Contents content : Contents.VALUES) {
                    if (nbt.hasKey(content.nbtTag)) {
                        contents.put(content, nbt.getCompoundTag(content.nbtTag));
                    }
                }
            }
        }
        return contents;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> info, boolean adv) {
        String contentString;
        EnumMap<Contents, NBTTagCompound> contents = getContents(stack);
        if (contents.isEmpty()) {
            contentString = LocalizationPlugin.translate("item.railcraft.tool.notepad.tips.contents.empty");
        } else {
            List<String> contentTypes = new ArrayList<String>();
            for (Contents content : contents.keySet()) {
                contentTypes.add(LocalizationPlugin.translate(content.locTag));
            }
            contentString = StringUtils.join(contentTypes, ", ");
        }
        info.add(LocalizationPlugin.translate("item.railcraft.tool.notepad.tips.contents", contentString));

        PasteMode pasteMode = getPasteMode(stack);
        info.add(LocalizationPlugin.translate("item.railcraft.tool.notepad.tips.mode", TextFormatting.DARK_PURPLE + pasteMode.toString()));

        super.addInformation(stack, player, info, adv);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (player.isSneaking()) {
            InvToolsAPI.clearItemDataRailcraft(stack, "contents");
        } else {
            PasteMode pasteMode = nextPasteMode(stack);
            if (Game.isClient(world))
                ChatPlugin.sendLocalizedChatFromClient(player, "item.railcraft.tool.notepad.tips.mode", TextFormatting.DARK_PURPLE + pasteMode.toString());
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (Game.isClient(world))
            return EnumActionResult.SUCCESS;
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null) {
            if (player.isSneaking()) // COPY
            {
                EnumMap<Contents, NBTTagCompound> contents = new EnumMap<Contents, NBTTagCompound>(Contents.class);
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
                    boolean pasted = false;
                    for (Map.Entry<Contents, NBTTagCompound> entry : getContents(stack).entrySet()) {
                        if (pasteMode.allows(entry.getKey()))
                            pasted |= entry.getKey().paste(tileEntity, entry.getValue());
                    }
                    if (pasted) {
                        if (tileEntity instanceof IWorldNameable)
                            ChatPlugin.sendLocalizedChatFromServer(player, "item.railcraft.tool.notepad.action.paste", tileEntity.getDisplayName());
                    } else
                        //TODO: Fix in 1.8 to use getDisplayName
                        ChatPlugin.sendLocalizedChatFromServer(player, "item.railcraft.tool.notepad.action.paste.fail");
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
                    PhantomInventory cartFilters = ((TileManipulatorCart) target).getCartFilters();
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
                    PhantomInventory itemFilters = ((TileItemManipulator) target).getItemFilters();
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
                    PhantomInventory itemFilters = ((TileFluidManipulator) target).getFluidFilter();
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

        @Nullable
        abstract NBTTagCompound copy(Object target);

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

        public static PasteMode fromOrdinal(int id) {
            if (id < 0 || id >= VALUES.length)
                return ALL;
            return VALUES[id];
        }

        public boolean allows(Contents content) {
            return allows.contains(content);
        }

        @Override
        public String toString() {
            return LocalizationPlugin.translate(locTag);
        }

        public PasteMode next() {
            return VALUES[(ordinal() + 1) % VALUES.length];
        }

//        public PasteMode previous() {
//            return VALUES[(ordinal() + VALUES.length - 1) % VALUES.length];
//        }

    }

}
