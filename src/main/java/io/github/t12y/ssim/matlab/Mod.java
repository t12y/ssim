package io.github.t12y.ssim.matlab;

public class Mod {
    public static int mod(int x, int y) {
        return x - y * (int)Math.floor((double)x / y);
    }
}
