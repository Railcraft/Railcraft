/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
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
import java.util.Iterator;

/**
 * Created by CovertJaguar on 6/4/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ArgDeque extends ArrayDeque<String> {
    public ArgDeque() {
    }

    public ArgDeque(int numElements) {
        super(numElements);
    }

    public ArgDeque(Collection<? extends String> c) {
        super(c);
    }

    public static ArgDeque make(String[] args) {
        return new ArgDeque(Arrays.asList(args));
    }

    public String[] toArgArray() {
        return toArray(new String[size()]);
    }

    public String[] peekArray(int entries) throws CommandException {
        if (entries > size())
            throw new CommandException("commands.generic.syntax");
        String[] args = new String[entries];
        Iterator<String> it = iterator();
        for (int i = 0; i < args.length; i++) {
            args[i] = it.next();
        }
        return args;
    }

    public void poll(int entries) {
        for (int i = 0; i < entries; i++) {
            poll();
        }
    }
}
