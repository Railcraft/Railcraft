/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.crafting;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@SuppressWarnings("UnnecessaryThis")
public class CompoundIngredient extends Ingredient {
    private Collection<Ingredient> children;
    private ItemStack[] stacks;
    private IntList itemIds;
    private final boolean isSimple;

    protected CompoundIngredient(Collection<Ingredient> children) {
        super(0);
        this.children = children;

        this.isSimple = children.stream().map(Ingredient::isSimple).reduce(true, (a, b) -> a && b);
    }

    @Override
    @Nonnull
    public ItemStack[] getMatchingStacks() {
        if (stacks == null) {
            stacks = children.stream().flatMap(child -> Arrays.stream(child.getMatchingStacks())).toArray(ItemStack[]::new);
        }
        return stacks;
    }

    @Override
    @Nonnull
    public IntList getValidItemStacksPacked() {
        //TODO: Add a child.isInvalid()?
        if (this.itemIds == null) {
            this.itemIds = new IntArrayList();
            for (Ingredient child : children)
                this.itemIds.addAll(child.getValidItemStacksPacked());
            this.itemIds.sort(IntComparators.NATURAL_COMPARATOR);
        }

        return this.itemIds;
    }

    @Override
    public boolean apply(@Nullable ItemStack target) {
        if (target == null)
            return false;

        return children.stream().anyMatch(child -> child.apply(target));
    }

    @Override
    protected void invalidate() {
        this.itemIds = null;
        this.stacks = null;
        //Shouldn't need to invalidate children as this is only called form invalidateAll..
    }

    @Override
    public boolean isSimple() {
        return isSimple;
    }

    @Nonnull
    public Collection<Ingredient> getChildren() {
        return Collections.unmodifiableCollection(this.children);
    }
}