package mods.railcraft.common.util.property;

import com.google.common.base.Optional;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A character property. Useful for multiblocks.
 */
public final class PropertyCharacter extends PropertyHelper<Character> {

    private final Set<Character> allowed;
    private final Set<Character> view;

    PropertyCharacter(String name) {
        super(name, Character.class);
        allowed = new HashSet<>();
        view = Collections.unmodifiableSet(allowed);
    }

    public static IProperty<Character> create(String name, Iterable<Character> characters) {
        PropertyCharacter ret = new PropertyCharacter(name);
        if (characters instanceof Collection) {
            ret.allowed.addAll((Collection<Character>) characters);
        } else {
            for (Character c : characters)
                ret.allowed.add(c);
        }
        return ret;
    }

    public static IProperty<Character> create(String name, char... characters) {
        PropertyCharacter ret = new PropertyCharacter(name);
        for (Character c : characters)
            ret.allowed.add(c);
        return ret;
    }

    @Override
    public Collection<Character> getAllowedValues() {
        return view;
    }

    @Override
    public Optional<Character> parseValue(String value) {
        if (value.length() != 1)
            return Optional.absent();
        return Optional.of(value.charAt(0));
    }

    @Override
    public String getName(Character value) {
        return value.toString();
    }
}
