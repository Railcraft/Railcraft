/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.inventory;

import com.google.common.collect.ForwardingMap;
import mods.railcraft.common.util.collections.StackKey;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by CovertJaguar on 6/22/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class InventoryManifest extends ForwardingMap<StackKey, InventoryManifest.ManifestEntry> {

    private final Map<StackKey, ManifestEntry> entries = new HashMap<>();

    private InventoryManifest() {
    }

    @Override
    protected Map<StackKey, ManifestEntry> delegate() {
        return entries;
    }

    public int count(StackKey key) {
        ManifestEntry entry = get(key);
        if (entry == null)
            return 0;
        return entry.count();
    }

    public Stream<ItemStack> streamValueStacks() {
        return entries.values().stream().flatMap(ManifestEntry::stream);
    }

    public Stream<ItemStack> streamKeyStacks() {
        return entries.keySet().stream().map(StackKey::get);
    }

    public List<ItemStack> keyStacks() {
        return streamKeyStacks().collect(Collectors.toList());
    }

    private static ManifestEntry compute(StackKey key, @Nullable InventoryManifest.ManifestEntry entry, ItemStack stack) {
        if (entry == null)
            entry = new ManifestEntry(key);
        entry.stacks.add(stack.copy());
        return entry;
    }

    /**
     * Returns an InventoryManifest that lists the total
     * number of each type of item in the inventory.
     *
     * @param invs the inventories to generate the manifest for
     * @return A {@code Multiset} that lists how many of each item is in the inventories
     */

    public static InventoryManifest create(IInventoryComposite invs) {
        InventoryManifest manifest = new InventoryManifest();
        invs.streamStacks().forEach(stack -> {
            StackKey key = StackKey.make(stack);
            manifest.compute(key, (k, v) -> compute(k, v, stack));
        });
        return manifest;
    }

    /**
     * Returns an InventoryManifest that lists the total
     * number of each type of item in the inventory.
     *
     * @param invs the inventories to generate the manifest for
     * @param keys The items to list.
     * @return A {@code Multiset} that lists how many of each item is in the inventories
     */

    public static InventoryManifest create(IInventoryComposite invs, Collection<StackKey> keys) {
        InventoryManifest manifest = new InventoryManifest();
        for (StackKey filterKey : keys) {
            Predicate<ItemStack> filter = StackFilters.anyMatch(filterKey.get());
            invs.streamStacks().filter(filter).forEach(stack -> manifest.compute(filterKey, (k, v) -> compute(k, v, stack)));
        }
        return manifest;
    }

    public static class ManifestEntry {
        private final StackKey key;
        final List<ItemStack> stacks = new ArrayList<>();

        public ManifestEntry(StackKey key) {
            this.key = key;
        }

        public StackKey key() {
            return key;
        }

        public int count() {
            return stacks.stream().mapToInt(InvTools::sizeOf).sum();
        }

        public List<ItemStack> stacks() {
            return Collections.unmodifiableList(stacks);
        }

        public Stream<ItemStack> stream() {
            return stacks.stream();
        }
    }
}
