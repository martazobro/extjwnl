package net.didion.jwnl.util;

import java.util.StringTokenizer;

/**
 * A <code>StringTokenizer</code> with extensions to retrieve the values of numeric tokens, as well as strings.
 */
public class TokenizerParser extends StringTokenizer {
    public TokenizerParser(String string, String delimiters) {
        super(string, delimiters);
    }

    /**
     * Convert the next token into a byte
     */
    public int nextByte() {
        return Byte.parseByte(nextToken());
    }

    /**
     * Convert the next token into a short
     */
    public int nextShort() {
        return Short.parseShort(nextToken());
    }

    /**
     * Convert the next token into an int
     */
    public int nextInt() {
        return Integer.parseInt(nextToken());
    }

    /**
     * Convert the next token into an int with base <var>radix</var>
     *
     * @param radix the base into which to convert the next token
     */
    public int nextInt(int radix) {
        return Integer.parseInt(nextToken(), radix);
    }

    /**
     * Convert the next token into a base 16 int
     */
    public int nextHexInt() {
        return nextInt(16);
    }

    /**
     * Convert the next token into a long
     */
    public long nextLong() {
        return Long.parseLong(nextToken());
    }
}