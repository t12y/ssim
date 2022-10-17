package io.github.t12y.ssim.matlab;

import io.github.t12y.ssim.models.Matrix;

import static io.github.t12y.ssim.matlab.internal.Numbers.numbers;

public class Ones {
    public static Matrix ones(int height, int width) {
        return numbers(height, width, 1);
    }

    public static Matrix ones(int height) {
        return numbers(height, height, 1);
    }
}
