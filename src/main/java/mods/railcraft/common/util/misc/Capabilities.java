/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.misc;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by CovertJaguar on 12/4/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class Capabilities {
    public static <T> Optional<T> get(@Nullable ICapabilityProvider provider, Capability<T> capability) {
        return get(provider, capability, null);
    }

    public static <T> Optional<T> get(@Nullable ICapabilityProvider provider, Capability<T> capability, @Nullable EnumFacing side) {
        if (provider != null && provider.hasCapability(capability, side))
            return Optional.ofNullable(provider.getCapability(capability, side));
        return Optional.empty();
    }

    public static <T> Stream<T> stream(ICapabilityProvider provider, Capability<T> capability, @Nullable EnumFacing side) {
        return get(provider, capability, side).map(Stream::of).orElseGet(Stream::empty);
    }
}
