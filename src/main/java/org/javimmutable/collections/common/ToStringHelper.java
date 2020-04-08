package org.javimmutable.collections.common;

public class ToStringHelper
{
    public static <K, V> void addToString(StringBuilder sb,
                                          int prefixLength,
                                          K key,
                                          V value)
    {
        if (sb.length() > prefixLength) {
            sb.append(",");
        }
        sb.append("(");
        sb.append(key);
        sb.append(",");
        sb.append(value);
        sb.append(")");
    }

    public static <V> void addToString(StringBuilder sb,
                                       int prefixLength,
                                       V value)
    {
        if (sb.length() > prefixLength) {
            sb.append(",");
        }
        sb.append(value);
    }

    public static <V> String arrayToString(V[] values)
    {
        StringBuilder sb = new StringBuilder("[");
        for (V value : values) {
            addToString(sb, 1, value);
        }
        sb.append("]");
        return sb.toString();
    }
}
