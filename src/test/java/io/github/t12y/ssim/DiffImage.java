package io.github.t12y.ssim;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

import io.github.t12y.ssim.models.MSSIMMatrix;
import io.github.t12y.ssim.models.Matrix;
import io.github.t12y.ssim.models.Options;

import static io.github.t12y.ssim.SSIM.ssim;

public class DiffImage {
    private static final String IVC_PATH = "samples/IVC_SubQualityDB/color/";

    public double[] baselinePixels;
    public double[] latestPixels;
    public int height;
    public int width;

    private DiffImage(InputStream baseline, InputStream latest) throws IOException {
        BufferedImage baselineImage = ImageIO.read(baseline);
        BufferedImage latestImage = ImageIO.read(latest);

        this.height = baselineImage.getHeight();
        this.width = baselineImage.getWidth();

        this.baselinePixels = unpackPixels(baselineImage.getRGB(0, 0, width, height, null, 0, width));
        this.latestPixels = unpackPixels(latestImage.getRGB(0, 0, width, height, null, 0, width));
    }

    public static MSSIMMatrix compare(String baselinePath, String latestPath, Options options) throws IOException {
        ClassLoader cl = DiffImage.class.getClassLoader();
        DiffImage d;

        try (
            InputStream baseline = cl.getResource(IVC_PATH + baselinePath + ".bmp").openStream();
            InputStream latest = cl.getResource(IVC_PATH + latestPath + ".bmp").openStream();
        ) {
            d = new DiffImage(baseline, latest);
        }

        return ssim(
                new Matrix(d.height, d.width, d.baselinePixels),
                new Matrix(d.height, d.width, d.latestPixels),
                options);
    }

    private static double[] unpackPixels(int[] packed) {
        int packedLength = packed.length;
        double[] unpacked = new double[packedLength * 4];
        int unpackedIndex;
        int packedPixel;

        for (int i = 0; i < packedLength; i++) {
            packedPixel = packed[i];
            unpackedIndex = i * 4;

            unpacked[unpackedIndex    ] = 0xff & (packedPixel >> 16);
            unpacked[unpackedIndex + 1] = 0xff & (packedPixel >> 8);
            unpacked[unpackedIndex + 2] = 0xff & packedPixel;
            unpacked[unpackedIndex + 3] = 0xff & (packedPixel >>> 24);
        }

        return unpacked;
    }
}