package org.javimmutable.collections.serialization;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.javimmutable.collections.Option;

/**
 * Serialization proxy class to safely serialize immutable {@link Option} instances.
 */
public class OptionProxy
    implements Externalizable
{
    private static final long serialVersionUID = -71022;
    private static final short EMPTY_VERSION = 9998;
    private static final short FILLED_VERSION = 9999;

    private Option<?> option;

    public OptionProxy()
    {
        option = Option.of();
    }

    public OptionProxy(Option<?> option)
    {
        this.option = option;
    }

    @Override
    public void writeExternal(ObjectOutput out)
        throws IOException
    {
        if (option.isEmpty()) {
            out.writeShort(EMPTY_VERSION);
        } else {
            out.writeShort(FILLED_VERSION);
            out.writeObject(option.getValue());
        }
    }

    @Override
    public void readExternal(ObjectInput in)
        throws IOException, ClassNotFoundException
    {
        final short version = in.readShort();
        switch (version) {
            case EMPTY_VERSION:
                option = Option.of();
                break;
            case FILLED_VERSION:
                option = Option.of(in.readObject());
                break;
            default:
                throw new IOException("unexpected version number: expected " + EMPTY_VERSION + " or " + FILLED_VERSION + " found " + version);
        }
    }

    private Object readResolve()
    {
        return option;
    }
}
