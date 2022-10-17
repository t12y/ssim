package io.github.t12y.ssim.matlab;

import io.github.t12y.ssim.models.Matrix;
import io.github.t12y.ssim.models.Shape;
import io.github.t12y.ssim.Math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.github.t12y.ssim.matlab.Filter2.filter2;

public class IMFilter {
    private static Matrix padMatrix(Matrix A, int frows, int fcols) {
        int[] padHeightWidth = Math.floor(new double[]{(double)frows / 2, (double)fcols / 2});
        A = PadArray.padarray(A, padHeightWidth[0], padHeightWidth[1]);

        if (Mod.mod(frows, 2) == 0) {
            A.data = Arrays.copyOf(A.data, A.data.length-1);
            A.height--;
        }

        if (Mod.mod(fcols, 2) == 0) {
            List<Double> data = new ArrayList<>();

            for (int x = 0; x < A.data.length; x++) {
                if ((x + 1) % A.width != 0) {
                    data.add(A.data[x]);
                }
            }

            A.data = data.stream().mapToDouble(Double::doubleValue).toArray();
            A.width--;
        }

        return A;
    }

    private static Shape getConv2Size(Shape resSize) {
        if (resSize == Shape.SAME) {
            resSize = Shape.VALID;
        }
        return resSize;
    }

    public static Matrix imfilter(Matrix A, Matrix f, Shape resSize) {
        A = padMatrix(A, f.width, f.height);
        resSize = getConv2Size(resSize);
        return filter2(f, A, resSize);
    }
}
