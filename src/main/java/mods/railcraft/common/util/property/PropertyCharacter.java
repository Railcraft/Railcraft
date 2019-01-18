/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.property;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyHelper;

import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A character property. Useful for multiblocks.
 * Stupid things happen because vanilla doesn't allow upper case letter names!
 */
public final class PropertyCharacter extends PropertyHelper<Character> {

    private final Set<Character> allowed;
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");

    private PropertyCharacter(String name, Set<Character> allowed) {
        super(name, Character.class);
        this.allowed = allowed;
    }

    public static IProperty<Character> create(String name, Iterable<Character> characters) {
        ImmutableSet.Builder<Character> builder = ImmutableSet.builder();
        builder.addAll(characters);
        return new PropertyCharacter(name, builder.build());
    }

    public static IProperty<Character> create(String name, Character... characters) {
        ImmutableSet.Builder<Character> builder = ImmutableSet.builder();
        builder.add(characters);
        return new PropertyCharacter(name, builder.build());
    }

    public static IProperty<Character> create(String name, char... characters) {
        ImmutableSet.Builder<Character> builder = ImmutableSet.builder();
        for (char c : characters) {
            builder.add(c);
        }
        return new PropertyCharacter(name, builder.build());
    }

    @Override
    public Collection<Character> getAllowedValues() {
        return allowed;
    }

    @Override
    public Optional<Character> parseValue(String value) {
        if (value.length() == 1) {
            char c = value.charAt(0);
            if (allowed.contains(c))
                return Optional.of(c);
        }
        try {
            return Optional.of((char) Integer.parseUnsignedInt(value, 16));
        } catch (NumberFormatException ex) {
            return Optional.absent();
        }
    }

    @Override
    public String getName(Character value) {
        String s = value.toString();
        return NAME_PATTERN.matcher(s).matches() ? s : Integer.toHexString(value);
    }
}
