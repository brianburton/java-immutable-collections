package org.javimmutable.collections.StressTestTool;


import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.util.JImmutables;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

public class StressTesterTest
        extends TestCase
{
    public void test()
            throws IOException
    {
        JImmutableList<String> tokens = loadTokens("src/site/apt/index.apt");
        testStandard(tokens);
    }

    public void testStandard(JImmutableList<String> tokens)
    {
        Random random = new Random();
        AbstractStressTestable testable = new JImmutableArrayStressTester(JImmutables.<String>array());
        testMakeInsertList(testable, tokens, random);
        testMakeInsertJList(testable, tokens, random);
    }


    public void testMakeInsertList(AbstractStressTestable testable,
                                   JImmutableList<String> tokens,
                                   Random random)
    {
        int times = 100000;
        int total = 0;
        for (int i = 0; i < times; ++i) {
            List<String> list = testable.makeInsertList(tokens, random);
            total = total + list.size();
        }
        double average = (double)total / (double)times;
        assertTrue(average >= 0.9998);
        assertTrue(average <= 1.0001);
    }

    public void testMakeInsertJList(AbstractStressTestable testable,
                                    JImmutableList<String> tokens,
                                    Random random)
    {
        int times = 100000;
        int total = 0;
        for (int i = 0; i < times; ++i) {
            JImmutableList<String> list = testable.makeInsertJList(tokens, random);
            total = total + list.size();
        }
        double average = (double)total / (double)times;
        assertTrue(average >= 0.9998);
        assertTrue(average <= 1.0001);
    }

    private JImmutableList<String> loadTokens(String filename)
            throws IOException
    {
        JImmutableSet<String> tokens = JImmutables.set();
        tokens = addTokensFromFile(tokens, filename);
        return JImmutables.list(tokens);
    }

    private JImmutableSet<String> addTokensFromFile(JImmutableSet<String> tokens,
                                                    String filename)
            throws IOException
    {
        BufferedReader inp = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
        try {
            for (String line = inp.readLine(); line != null; line = inp.readLine()) {
                StringTokenizer tokenizer = new StringTokenizer(line);
                while (tokenizer.hasMoreTokens()) {
                    tokens = tokens.insert(tokenizer.nextToken());
                }
            }
        } finally {
            inp.close();
        }
        return tokens;
    }
}
