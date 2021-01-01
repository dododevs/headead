package revolver.headead.util.misc;

/**
 *
 * The methods floorMod(int, int) and floorDiv(int, int) are just copy-pasted from the Math class,
 * in order to make them available for API < 24
 *
 * */
public final class ForbiddenMath {

    public static int floorMod(int x, int y) {
        return x - floorDiv(x, y) * y;
    }

    public static int floorDiv(int x, int y) {
        int r = x / y;
        if ((x ^ y) < 0 && (r * y != x)) {
            r--;
        }
        return r;
    }

}
