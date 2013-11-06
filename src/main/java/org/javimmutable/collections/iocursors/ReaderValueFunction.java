package org.javimmutable.collections.iocursors;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;

import java.io.IOException;
import java.io.Reader;

/**
 * Abstract base class for CloseableValueFunctions that read values from a Reader.
 *
 * @param <R>
 * @param <T>
 */
public abstract class ReaderValueFunction<R extends Reader, T>
        implements CloseableValueFunction<T>
{
    private final R reader;
    private boolean closed;

    protected ReaderValueFunction(R reader)
    {
        this.reader = reader;
    }

    protected abstract Holder<T> readValue(R reader);

    @Override
    public Holder<T> nextValue()
    {
        if (closed) {
            throw new Cursor.NoValueException();
        }
        return readValue(reader);
    }

    @Override
    public void close()
    {
        if (!closed) {
            closed = true;
            try {
                reader.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
