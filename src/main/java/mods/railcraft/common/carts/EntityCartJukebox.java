/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
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
import net.minecraft.util.DamageSource;
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
public final class EntityCartJukebox extends CartBase {

    public static final String RECORD_DISPLAY_NAME = "record";
    private static final String RECORD_ITEM_KEY = "RecordItem";

    private ItemStack record = emptyStack();

    @SideOnly(Side.CLIENT)
    public @Nullable JukeboxSound music;

    public EntityCartJukebox(World world) {
        super(world);
    }

    public EntityCartJukebox(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.JUKEBOX;
    }

    @Override
    public boolean doInteract(EntityPlayer player, EnumHand hand) {
        if (Game.isHost(world)) {
            if (!isEmpty(record)) {
                entityDropItem(record.copy(), 0.5f);
                record = emptyStack();
                PacketBuilder.instance().stopRecord(this);
            }
            ItemStack heldItem = player.getHeldItem(hand);
            if (isEmpty(heldItem) || !(heldItem.getItem() instanceof ItemRecord))
                return true;
            record = heldItem.copy();
            InvTools.setSize(record, 1);
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
        return true;
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return Blocks.JUKEBOX.getDefaultState();
    }

    @Override
    public void killMinecart(DamageSource par1DamageSource) {
        super.killMinecart(par1DamageSource);
        if (!isEmpty(record))
            entityDropItem(record.copy(), 0);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        record = new ItemStack(compound.getCompoundTag(RECORD_ITEM_KEY));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        NBTTagCompound tag = isEmpty(record) ? new NBTTagCompound() : record.writeToNBT(new NBTTagCompound());
        compound.setTag(RECORD_ITEM_KEY, tag);
    }
}
