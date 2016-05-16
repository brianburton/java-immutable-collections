package org.javimmutable.collections.StressTestTool;

import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.util.JImmutables;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

class StressTestUtil
{
    static JImmutableList<String> loadTokens(Iterable<String> filenames)
            throws IOException
    {
        JImmutableSet<String> tokens = JImmutables.set();
        for (String filename : filenames) {
            tokens = addTokensFromFile(tokens, filename);
        }
        return JImmutables.list(tokens);
    }

    static JImmutableList<String> loadTokens(String filename)
            throws IOException
    {
        JImmutableSet<String> tokens = JImmutables.set();
        tokens = addTokensFromFile(tokens, filename);
        return JImmutables.list(tokens);
    }

    static JImmutableSet<String> addTokensFromFile(JImmutableSet<String> tokens,
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
