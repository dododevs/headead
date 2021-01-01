package revolver.headead.util.misc;

import android.util.SparseIntArray;

import java.util.Locale;

public final class Base32 {

    private static final SparseIntArray TABLE = new SparseIntArray(32);

    static {
        TABLE.append((int) '0', 0);
        TABLE.append((int) '1', 1);
        TABLE.append((int) '2', 2);
        TABLE.append((int) '3', 3);
        TABLE.append((int) '4', 4);
        TABLE.append((int) '5', 5);
        TABLE.append((int) '6', 6);
        TABLE.append((int) '7', 7);
        TABLE.append((int) '8', 8);
        TABLE.append((int) '9', 9);
        TABLE.append((int) 'B', 10);
        TABLE.append((int) 'C', 11);
        TABLE.append((int) 'D', 12);
        TABLE.append((int) 'F', 13);
        TABLE.append((int) 'G', 14);
        TABLE.append((int) 'H', 15);
        TABLE.append((int) 'J', 16);
        TABLE.append((int) 'K', 17);
        TABLE.append((int) 'L', 18);
        TABLE.append((int) 'M', 19);
        TABLE.append((int) 'N', 20);
        TABLE.append((int) 'P', 21);
        TABLE.append((int) 'Q', 22);
        TABLE.append((int) 'R', 23);
        TABLE.append((int) 'S', 24);
        TABLE.append((int) 'T', 25);
        TABLE.append((int) 'U', 26);
        TABLE.append((int) 'V', 27);
        TABLE.append((int) 'W', 28);
        TABLE.append((int) 'X', 29);
        TABLE.append((int) 'Y', 30);
        TABLE.append((int) 'Z', 31);
    }

    public static String decode(final String in) {
        int total = 0;
        char[] cc = in.toCharArray();
        for (int i = cc.length - 1; i >= 0; i--) {
            total += ((int) Math.pow(32, cc.length - i - 1)) * TABLE.get(cc[i]);
        }
        return String.format(Locale.ITALY, "%09d", total);
    }

}
