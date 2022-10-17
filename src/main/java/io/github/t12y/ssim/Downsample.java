package io.github.t12y.ssim;

import io.github.t12y.ssim.models.Matrix;
import io.github.t12y.ssim.models.Options;
import io.github.t12y.ssim.models.Shape;

import static io.github.t12y.ssim.Math.divide2d;
import static io.github.t12y.ssim.matlab.IMFilter.imfilter;
import static io.github.t12y.ssim.matlab.Ones.ones;
import static io.github.t12y.ssim.matlab.Skip2d.skip2d;

public class Downsample {
    private static Matrix imageDownsample(Matrix pixels, Matrix filter, int f) {
        Matrix imdown = imfilter(pixels, filter, Shape.SAME);
        return skip2d(imdown, 0, f, imdown.height, 0, f, imdown.width);
    }

    protected static Matrix[] downsample(Matrix[] pixels, Options options) {
        double factor = (double)java.lang.Math.min(pixels[0].width, pixels[1].height) / options.maxSize;
        int f = (int)java.lang.Math.round(factor);

        if (f > 1) {
            Matrix lpf = ones(f);

            lpf = Math.divide2d(lpf, Math.sum2d(lpf));

            pixels[0] = imageDownsample(pixels[0], lpf, f);
            pixels[1] = imageDownsample(pixels[1], lpf, f);
        }

        return new Matrix[]{pixels[0], pixels[1]};
    }
}
