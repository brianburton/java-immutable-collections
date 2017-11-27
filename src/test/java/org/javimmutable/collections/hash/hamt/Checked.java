package org.javimmutable.collections.hash.hamt;

import javax.annotation.concurrent.Immutable;

@Immutable
class Checked
{
    final int hashCode;
    final int value;

    Checked(int hashCode,
            int value)
    {
        this.hashCode = hashCode;
        this.value = value;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Checked checked = (Checked)o;
        return hashCode == checked.hashCode &&
               value == checked.value;
    }

    @Override
    public int hashCode()
    {
        return hashCode;
    }
}
