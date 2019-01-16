/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.detector.types;

import mods.railcraft.common.blocks.detector.Detector;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static mods.railcraft.common.plugins.forge.PowerPlugin.FULL_POWER;
import static mods.railcraft.common.plugins.forge.PowerPlugin.NO_POWER;

public class DetectorVillager extends Detector {


    private VillagerRegistry.VillagerProfession profession = VillagerRegistry.FARMER;
    private Mode mode = Mode.ANY;

    @Override
    public EnumDetector getType() {
        return EnumDetector.VILLAGER;
    }

    private boolean cartHasVillager(List<EntityMinecart> carts) {
        for (EntityMinecart cart : carts) {
            if (cart.getPassengers().stream().anyMatch(entity -> entity instanceof EntityVillager))
                return true;
        }
        return false;
    }

    private boolean cartHasProfession(List<EntityMinecart> carts) {
        for (EntityMinecart cart : carts) {
            if (cart.getPassengers().stream().filter(e -> e instanceof EntityVillager).map(entity -> (EntityVillager) entity)
                    .anyMatch(villager -> villager.getProfessionForge() == profession))
                return true;
        }
        return false;
    }

    @Override
    public int testCarts(List<EntityMinecart> carts) {
        switch (mode) {
            case ANY:
                return cartHasVillager(carts) ? FULL_POWER : NO_POWER;
            case NONE:
                return !cartHasVillager(carts) ? FULL_POWER : NO_POWER;
            case EQUALS:
                return cartHasProfession(carts) ? FULL_POWER : NO_POWER;
            case NOT:
                return !cartHasProfession(carts) ? FULL_POWER : NO_POWER;
            default:
                return NO_POWER;
        }
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        openGui(EnumGui.DETECTOR_VILLAGER, player);
        return true;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public VillagerRegistry.VillagerProfession getProfession() {
        return profession;
    }

    public void setProfession(VillagerRegistry.VillagerProfession profession) {
        this.profession = profession;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setString("ProfessionName", checkNotNull(profession.getRegistryName()).toString());
        data.setByte("mode", (byte) mode.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        if (data.hasKey("ProfessionName")) {
            VillagerRegistry.VillagerProfession p =
                    ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation(data.getString("ProfessionName")));
            if (p == null)
                p = VillagerRegistry.FARMER;
            setProfession(p);
        }
        mode = Mode.values()[data.getByte("mode")];
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeUTF(checkNotNull(profession.getRegistryName()).toString());
        data.writeByte((byte) mode.ordinal());
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);

        profession = checkNotNull(ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation(data.readUTF())));
        mode = Mode.values()[data.readByte()];
    }

    @Override
    public void writeGuiData(RailcraftOutputStream data) throws IOException {
        data.writeUTF(checkNotNull(profession.getRegistryName()).toString());
        data.writeByte(mode.ordinal());
    }

    @Override
    public void readGuiData(RailcraftInputStream data, @Nullable EntityPlayer sender) throws IOException {
        profession = checkNotNull(ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation(data.readUTF())));
        mode = Mode.values()[data.readByte()];
    }

    public enum Mode {

        ANY, NONE, NOT, EQUALS
    }

}
