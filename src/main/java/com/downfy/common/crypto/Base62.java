package com.downfy.common.crypto;

import org.apache.commons.codec.binary.Base64;

/**
 * Static methods for "Base62" encoding and decoding. A Base62 encoded string is
 * very similar to Base64, but eliminates the '/', '+', and '='. Having only the
 * characters A-Za-z0-9 helps prevent MUAs from putting linebreaks in the middle
 * of longish encryption tokens.
 *
 * @author Jeff Schnitzer
 */
public class Base62 {

    /**
     * default constructor prevents util class from being created.
     */
    private Base62() {
    }

    /**
     * Encodes bytes as a Base62 string.
     *
     * Having tokens that are a seamless string of letters and numbers means
     * that MUAs are less likely to linebreak a long token.
     */
    public static String encode(byte[] data) {
        String base64 = new String(Base64.encodeBase64(data));
        return base64ToBase62(base64);
    }

    public static String encode(long data) {
        String base64 = new String(Base64.encodeBase64((data + "").getBytes()));
        return base64ToBase62(base64);
    }

    /**
     * Returns a Base62 encoded string to it's original state.
     */
    public static String decode(String base62) {
        String base64 = base62ToBase64(base62);
        return new String(Base64.decodeBase64(base64.getBytes()));
    }

    /**
     * Takes a base64 encoded string and eliminates the '+' and '/'. Also
     * eliminates any CRs.
     *
     * Having tokens that are a seamless string of letters and numbers means
     * that MUAs are less likely to linebreak a long token.
     */
    protected static String base64ToBase62(String base64) {
        StringBuilder buf = new StringBuilder(base64.length() * 2);

        for (int i = 0; i < base64.length(); i++) {
            char ch = base64.charAt(i);
            switch (ch) {
                case 'i':
                    buf.append("ii");
                    break;

                case '+':
                    buf.append("ip");
                    break;

                case '/':
                    buf.append("is");
                    break;

                case '=':
                    buf.append("ie");
                    break;

                case '\n':
                    // Strip out
                    break;

                default:
                    buf.append(ch);
            }
        }

        return buf.toString();
    }

    /**
     * Returns a string encoded with encodeBase62 to its original (base64
     * encoded) state.
     */
    protected static String base62ToBase64(String base62) {
        StringBuilder buf = new StringBuilder(base62.length());

        int i = 0;
        while (i < base62.length()) {
            char ch = base62.charAt(i);

            if (ch == 'i') {
                i++;
                char code = base62.charAt(i);
                switch (code) {
                    case 'i':
                        buf.append('i');
                        break;

                    case 'p':
                        buf.append('+');
                        break;

                    case 's':
                        buf.append('/');
                        break;

                    case 'e':
                        buf.append('=');
                        break;

                    default:
                        throw new IllegalStateException("Illegal code in base62 encoding");
                }
            } else {
                buf.append(ch);
            }

            i++;
        }

        return buf.toString();
    }
}
