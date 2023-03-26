package org.javimmutable.collections;

import java.util.Iterator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Holders
{
    private Holders()
    {
    }

    /**
     * Returns an empty Holder. All empty Holder share a common instance.
     */
    @Nonnull
    public static <T> Holder<T> none()
    {
        return Holder.none();
    }

    /**
     * Returns a Holder containing the (possibly null) value.
     */
    @Nonnull
    public static <T> Holder<T> nullable(@Nullable T value)
    {
        return Holder.some(value);
    }

    /**
     * Returns an empty Holder if value is null, otherwise a Holder containing
     * the value is returned.
     */
    @Nonnull
    public static <T> Holder<T> notNull(@Nullable T value)
    {
        return value != null ? Holder.some(value) : Holder.none();
    }

    /**
     * Determine if the object is an instance of the specified Class or a subclass.
     * If the object is null, returns a Holder containing null.
     * If the object is not null but not of the correct class, returns an empty Holder.
     * Otherwise returns a Holder containing the value cast to the target type.
     *
     * @param klass       class to cast the object to
     * @param valueOrNull object to be case
     * @param <T>         type of the class
     * @return a Holder
     */
    public static <T> Holder<T> cast(@Nonnull Class<T> klass,
                                     @Nullable Object valueOrNull)
    {
        if (valueOrNull == null) {
            return Holder.some(null);
        } else if (klass.isInstance(valueOrNull)) {
            return Holder.some(klass.cast(valueOrNull));
        } else {
            return Holder.none();
        }
    }

    /**
     * Returns a Holder containing the first value of the collection.  If the collection
     * is empty  an empty Holder is returned.
     */
    @Nonnull
    public static <T> Holder<T> first(@Nonnull Iterable<? extends T> collection)
    {
        final Iterator<? extends T> i = collection.iterator();
        return i.hasNext() ? nullable(i.next()) : Holder.none();
    }

    /**
     * Returns a Holder containing the first value of the collection
     * for which the predicate returns true.  If the collection
     * is empty or predicate always
     * returns false an empty Holder is returned.
     */
    @Nonnull
    public static <T> Holder<T> first(@Nonnull Iterable<? extends T> collection,
                                      @Nonnull Func1<? super T, Boolean> predicate)
    {
        for (T value : collection) {
            if (predicate.apply(value)) {
                return Holder.some(value);
            }
        }
        return Holder.none();
    }
}
