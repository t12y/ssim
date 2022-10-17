package io.github.t12y.ssim.models;

public class Options {
    public static Options Defaults() {
        Options defaultOptions = new Options();
        defaultOptions.windowSize = 11;
        defaultOptions.k1 = 0.01;
        defaultOptions.k2 = 0.03;
        defaultOptions.bitDepth = 8;
        defaultOptions.ssim = SSIMImpl.WEBER;
        defaultOptions.maxSize = 256;
        defaultOptions.rgb2grayVersion = RGB2Gray.INTEGER;
        return defaultOptions;
    }

    public RGB2Gray rgb2grayVersion;
    public double k1;
    public double k2;
    public SSIMImpl ssim;
    public int windowSize;
    public int bitDepth;
    public int maxSize;
    public int[][] ignoredBoxes; // array of boxes like [left, right, top, bottom]

    public enum RGB2Gray {
        ORIGINAL,
        INTEGER
    }

    public enum SSIMImpl {
        FAST,
        WEBER
    }
}
