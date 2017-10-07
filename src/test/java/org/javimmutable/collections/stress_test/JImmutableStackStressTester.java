///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2017, Burton Computer Corporation
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
//     Redistributions of source code must retain the above copyright
//     notice, this list of conditions and the following disclaimer.
//
//     Redistributions in binary form must reproduce the above copyright
//     notice, this list of conditions and the following disclaimer in
//     the documentation and/or other materials provided with the
//     distribution.
//
//     Neither the name of the Burton Computer Corporation nor the names
//     of its contributors may be used to endorse or promote products
//     derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.javimmutable.collections.stress_test;

import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableStack;
import org.javimmutable.collections.cursors.StandardCursorTest;
import org.javimmutable.collections.util.JImmutables;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Test program for implementations of JImmutableStack. Divided into three
 * sections: growing (adds values to the stack), shrinking (removes values),
 * and cleanup (empties the stack of all values).
 */
public class JImmutableStackStressTester
        extends AbstractStressTestable
{
    private final JImmutableStack<String> stack;

    public JImmutableStackStressTester(JImmutableStack<String> stack)
    {
        this.stack = stack;
    }

    @Override
    public JImmutableList<String> getOptions()
    {
        JImmutableList<String> options = JImmutables.list();
        return options.insert("stack");
    }

    @Override
    public void execute(Random random,
                        JImmutableList<String> tokens)
    {
        JImmutableStack<String> stack = this.stack;
        LinkedList<String> expected = new LinkedList<String>();
        int size = 1 + random.nextInt(100000);

        System.out.printf("JImmutableStackStressTest on %s of size %d%n", "JImmutableStack", size);
        for (SizeStepCursor.Step step : SizeStepCursor.steps(6, size, random)) {
            System.out.printf("growing %d%n", expected.size());
            while (expected.size() < step.growthSize()) {
                String value = RandomKeyManager.makeValue(tokens, random);
                stack = stack.insert(value);
                expected.addFirst(value);
            }
            verifyContents(stack, expected);

            System.out.printf("shrinking %d%n", expected.size());
            while (expected.size() > step.shrinkSize()) {
                stack = stack.remove();
                expected.removeFirst();
            }
            verifyContents(stack, expected);
            verifyCursor(stack, expected);
        }
        System.out.printf("cleanup %d%n", expected.size());
        for (String value : expected) {
            if (!value.equals(stack.getHead())) {
                throw new RuntimeException(String.format("value mismatch - expected %s found %s", value, stack.getHead()));
            }
            stack = stack.getTail();
        }
        if (!stack.isEmpty()) {
            throw new RuntimeException("expected stack to be empty but found more values%n");
        }
        System.out.printf("JImmutableStackStressTest on %s completed without errors%n", "JImmutableStack");
    }

    private void verifyCursor(JImmutableStack<String> stack,
                              List<String> expected)
    {
        System.out.printf("checking cursor of size %d%n", expected.size());
        ArrayList<String> list = new ArrayList<String>(expected);   //ArrayList instead of LinkedList for speed
        StandardCursorTest.listCursorTest(list, stack.cursor());
        StandardCursorTest.listIteratorTest(list, stack.iterator());
    }

    private void verifyContents(JImmutableStack<String> stack,
                                List<String> expected)
    {
        if (stack.isEmpty() != expected.isEmpty()) {
            throw new RuntimeException(String.format("isEmpty mismatch - expected %b found %b%n", expected.isEmpty(), stack.isEmpty()));
        }

        JImmutableStack<String> checkStack = stack;
        for (String value : expected) {
            if (!value.equals(checkStack.getHead())) {
                throw new RuntimeException(String.format("value mismatch - expected %s found %s", value, checkStack.getHead()));
            }
            checkStack = checkStack.getTail();
        }

        if (!expected.equals(stack.makeList())) {
            throw new RuntimeException("makeList() method call failed\n");
        }
        stack.checkInvariants();
    }
}