package io.github.t12y.ssim.matlab;

import io.github.t12y.ssim.models.Matrix;
import io.github.t12y.ssim.models.Shape;

import static io.github.t12y.ssim.matlab.Conv2.conv2;

public class Filter2 {

    private static Matrix rotate1802d(Matrix ref) {
        Matrix result = new Matrix(ref);

        for (int i = 0; i < ref.height; i++) {
            for (int j = 0; j < ref.width; j++) {
                result.data[i * ref.width + j] = ref.data[(ref.height - 1 - i) * ref.width + ref.width - 1 - j];
            }
        }

        return result;
    }

    public static Matrix filter2(Matrix h, Matrix X, Shape shape) {
        return Conv2.conv2(X, rotate1802d(h), shape);
    }

    public static Matrix filter2(Matrix h, Matrix X) {
        return filter2(h, X, Shape.SAME);
    }
}
