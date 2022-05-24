/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.client.util.sounds.JukeboxSound;
import mods.railcraft.common.advancements.criterion.RailcraftAdvancementTriggers;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketBuilder;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import static mods.railcraft.common.util.inventory.InvTools.*;

/**
 * A simple Jukebox cart.
 */
public final class EntityCartJukebox extends EntityRailcraftCart {

    public static final int RECORD_SLOT = 0;
    public static final String RECORD_DISPLAY_NAME = "record";

    @SideOnly(Side.CLIENT)
    public @Nullable JukeboxSound music;

    public EntityCartJukebox(World world) {
        super(world);
    }

    public EntityCartJukebox(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    public ItemStack record() {
        return getStackInSlot(RECORD_SLOT);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.JUKEBOX;
    }

    @Override
    public boolean doInteract(EntityPlayer player, EnumHand hand) {
        if (Game.isHost(world)) {
            if (!InvTools.isEmpty(record())) {
                entityDropItem(record().copy(), 0.5f);
                setInventorySlotContents(RECORD_SLOT, emptyStack());
                PacketBuilder.instance().stopRecord(this);
            }
            ItemStack heldItem = player.getHeldItem(hand);
            if (InvTools.isEmpty(heldItem) || !(heldItem.getItem() instanceof ItemRecord))
                return true;
            ItemStack record = copy(heldItem, 1);
            setInventorySlotContents(RECORD_SLOT, record);
            if (!player.capabilities.isCreativeMode)
                dec(heldItem);
            player.addStat(StatList.RECORD_PLAYED);
            ItemRecord item = (ItemRecord) record.getItem();
            SoundEvent sound = item.sound; // ReflectionHelper.getPrivateValue(ItemRecord.class, item, 1);
            String display = item.displayName; // ReflectionHelper.getPrivateValue(ItemRecord.class, item, 2);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString(RECORD_DISPLAY_NAME, display);
            PacketBuilder.instance().sendMovingSoundPacket(sound, SoundCategory.RECORDS, this, SoundHelper.MovingSoundType.RECORD, tag);
            RailcraftAdvancementTriggers.getInstance().onJukeboxCartPlay((EntityPlayerMP) player, this, sound.soundName);
        }
        return super.doInteract(player, hand);
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return Blocks.JUKEBOX.getDefaultState();
    }
}
