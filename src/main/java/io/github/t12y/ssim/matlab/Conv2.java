package io.github.t12y.ssim.matlab;

import io.github.t12y.ssim.models.Matrix;
import io.github.t12y.ssim.models.Shape;

import static io.github.t12y.ssim.Math.multiply2d;
import static io.github.t12y.ssim.matlab.Ones.ones;

public class Conv2 {

    private static Matrix mxConv2(Matrix ref, Matrix b, Shape shape) {
        int cWidth = ref.width + b.width - 1;
        int cHeight = ref.height + b.height - 1;
        double[] data = Zeros.zeros(cHeight, cWidth).data;

        for (int r1 = 0; r1 < b.height; r1++) {
            for (int c1 = 0; c1 < b.width; c1++) {
                double br1c1 = b.data[r1 * b.width + c1];

                if (br1c1 != 0) {
                    for (int i = 0; i < ref.height; i++) {
                        for (int j = 0; j < ref.width; j++) {
                            data[(i + r1) * cWidth + j + c1] += ref.data[i * ref.width + j] * br1c1;
                        }
                    }
                }
            }
        }

        Matrix c = new Matrix(cHeight, cWidth, data);

        return reshape(c, shape, ref.height, b.height, ref.width, b.width);
    }

    private static Matrix boxConv(Matrix a, Matrix m, Shape shape) {
        Matrix b1 = ones(m.height, 1);
        Matrix b2 = ones(1, m.width);
        Matrix out = convn(a, b1, b2, shape);

        return multiply2d(out, m.data[0]);
    }

    private static boolean isBoxKernel(Matrix m){
        double expected = m.data[0];

        for (int i = 1; i < m.data.length; i++) {
            if (m.data[i] != expected) {
                return false;
            }
        }

        return true;
    }

    private static Matrix convn(Matrix a, Matrix b1, Matrix b2, Shape shape) {
        int mb = Math.max(b1.height, b1.width);
        int nb = Math.max(b2.height, b2.width);
        Matrix temp = mxConv2(a, b1, Shape.FULL);
        Matrix c = mxConv2(temp, b2, Shape.FULL);

        return reshape(c, shape, a.height, mb, a.width, nb);
    }

    private static Matrix reshape(Matrix c, Shape shape, int ma, int mb, int na, int nb) {
        if (shape == Shape.FULL) {
            return c;
        }

        if (shape == Shape.SAME) {
            int rowStart = (int)Math.ceil(((double)(c.height - ma)) / 2);
            int colStart = (int)Math.ceil(((double)(c.width - na)) / 2);

            return Sub.sub(c, rowStart, ma, colStart, na);
        }

        return Sub.sub(c, mb - 1, ma - mb + 1, nb - 1, na - nb + 1);
    }

    public static Matrix conv2(Matrix a, Matrix b, Shape shape) {
        if (isBoxKernel(b)) {
            return boxConv(a, b, shape);
        }
        return mxConv2(a, b, shape);
    }

    public static Matrix conv2(Matrix a, Matrix b1, Matrix b2, Shape shape) {
        return convn(a, b1, b2, shape);
    }
}
