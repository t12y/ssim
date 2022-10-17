package io.github.t12y.ssim;

import io.github.t12y.ssim.models.MSSIMMatrix;
import io.github.t12y.ssim.models.Matrix;
import io.github.t12y.ssim.models.Options;

import static io.github.t12y.ssim.Downsample.downsample;
import static io.github.t12y.ssim.FastSSIM.fastSSIM;
import static io.github.t12y.ssim.IgnoredBoxes.adjustMSSIM;
import static io.github.t12y.ssim.matlab.RBG2Gray.rgb2gray;
import static io.github.t12y.ssim.matlab.RBG2Gray.rgb2grayInteger;
import static io.github.t12y.ssim.models.Options.Defaults;

public class SSIM {
    private static void validateOptions(Options options) {
        if (options.k1 < 0) {
            throw new RuntimeException("Invalid k1 value. Default is " + Defaults().k1);
        }

        if (options.k2 < 0) {
            throw new RuntimeException("Invalid k2 value. Default is " + Defaults().k2);
        }
    }

    private static void validateDimensions(Matrix pixels1, Matrix pixels2) {
        if (pixels1.width != pixels2.width || pixels1.height != pixels2.height) {
            throw new RuntimeException("Image dimensions do not match");
        }
    }

    public static void toGrayScale(Matrix pixels1, Matrix pixels2, Options options) {
        if (options.rgb2grayVersion == Options.RGB2Gray.ORIGINAL) {
            pixels1.set(rgb2gray(pixels1));
            pixels2.set(rgb2gray(pixels2));
        } else {
            pixels1.set(rgb2grayInteger(pixels1));
            pixels2.set(rgb2grayInteger(pixels2));
        }
    }

    private static void toResize(Matrix pixels1, Matrix pixels2, Options options) {
        Matrix[] pixels = downsample(new Matrix[]{pixels1, pixels2}, options);
        pixels1.set(pixels[0]);
        pixels2.set(pixels[1]);
    }

    private static Matrix comparison(Matrix pixels1, Matrix pixels2, Options options) {
        if (options.ssim == Options.SSIMImpl.WEBER) {
            return WeberSSIM.weberSSIM(pixels1, pixels2, options);
        }

        return fastSSIM(pixels1, pixels2, options);
    }

    public static MSSIMMatrix ssim(Matrix image1, Matrix image2, Options options) {
        int height = image1.height;
        int width = image1.width;

        validateOptions(options);
        validateDimensions(image1, image2);
        toGrayScale(image1, image2, options);
        toResize(image1, image2, options);
        Matrix ssimMap = comparison(image1, image2, options);

        MSSIMMatrix mssimMatrix;
        if (ssimMap instanceof MSSIMMatrix) {
            mssimMatrix = (MSSIMMatrix)ssimMap;
        } else {
            mssimMatrix = new MSSIMMatrix(ssimMap, Math.mean2d(ssimMap));
        }

        adjustMSSIM(height, width, mssimMatrix, options);

        return mssimMatrix;
    }

    public static MSSIMMatrix ssim(Matrix image1, Matrix image2) {
        return ssim(image1, image2, Defaults());
    }
}
