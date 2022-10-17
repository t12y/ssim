package io.github.t12y.ssim.matlab;

import io.github.t12y.ssim.models.Matrix;

import static io.github.t12y.ssim.matlab.internal.Numbers.numbers;

public class Zeros {
    public static Matrix zeros(int height, int width) {
        return numbers(height, width, 0);
    }
}
