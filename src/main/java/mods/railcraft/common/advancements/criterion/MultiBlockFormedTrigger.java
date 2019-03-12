/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.advancements.criterion.MultiBlockFormedTrigger.Instance;
import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.events.MultiBlockEvent;
import mods.railcraft.common.util.json.JsonTools;
import mods.railcraft.common.util.misc.Conditions;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.NBTPredicate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 *
 */
final class MultiBlockFormedTrigger extends BaseTrigger<Instance> {

    static final ResourceLocation ID = RailcraftConstantsAPI.locationOf("multiblock_formed");

    MultiBlockFormedTrigger() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        Class<? extends TileEntity> type;
        if (json.has("type")) {
            String id = JsonUtils.getString(json, "type");
            type = TileEntity.REGISTRY.getObject(new ResourceLocation(id));
        } else {
            type = null;
        }
        NBTPredicate nbt = JsonTools.whenPresent(json, "nbt", NBTPredicate::deserialize, NBTPredicate.ANY);

        return new Instance(type, nbt);
    }

    @SubscribeEvent
    public void onMultiBlockForm(MultiBlockEvent.Form event) {
        TileRailcraft tile = event.getMaster();
        GameProfile owner = tile.getOwner();
        MinecraftServer server = requireNonNull(Game.getServer());
        EntityPlayerMP player = server.getPlayerList().getPlayerByUUID(owner.getId());
        if (player == null) {
            return; // Offline
        }
        PlayerAdvancements advancements = player.getAdvancements();
        Collection<Listener<Instance>> done = manager.get(advancements).stream()
                .filter(listener -> listener.getCriterionInstance().matches(tile))
                .collect(Collectors.toList());
        for (Listener<Instance> listener : done) {
            listener.grantCriterion(advancements);
        }
    }

    static final class Instance implements ICriterionInstance {

        final @Nullable Class<? extends TileEntity> clazz;
        final NBTPredicate nbt;

        Instance(@Nullable Class<? extends TileEntity> type, NBTPredicate nbtPredicate) {
            this.clazz = type;
            this.nbt = nbtPredicate;
        }

        boolean matches(TileRailcraft tile) {
            return Conditions.check(clazz, tile.getClass()) && nbt.test(tile.writeToNBT(new NBTTagCompound()));
        }

        @Override
        public ResourceLocation getId() {
            return ID;
        }
    }
}
