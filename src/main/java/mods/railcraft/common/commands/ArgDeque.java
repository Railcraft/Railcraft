/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.commands;

import net.minecraft.command.CommandException;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by CovertJaguar on 6/4/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ArgDeque extends ArrayDeque<String> {

    private ArgDeque(Collection<? extends String> c) {
        super(c);
    }

    public static ArgDeque make(String[] args) {
        return new ArgDeque(Arrays.asList(args));
    }

    @Override
    public String[] toArray() {
        return toArray(new String[size()]);
    }

    public String[] peekArray(int entries) throws CommandException {
        if (entries > size())
            throw new CommandException("commands.generic.syntax");
        return stream().limit(entries).toArray(String[]::new);
    }

    public void poll(int entries) {
        for (int i = 0; i < entries; i++) {
            poll();
        }
    }
}
