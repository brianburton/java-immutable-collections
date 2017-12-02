package org.javimmutable.collections.common;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class StandardSerializableTests
    extends TestCase
{
    public static void verifySerializable(Object source)
        throws IOException, ClassNotFoundException
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(source);
        }
        Object dest;
        try (ObjectInputStream inp = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            dest = inp.readObject();
        }
        assertEquals(source.getClass().getName(), dest.getClass().getName());
        assertEquals(source, dest);
    }
}
