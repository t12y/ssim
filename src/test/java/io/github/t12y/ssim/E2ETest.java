package io.github.t12y.ssim;

import io.github.t12y.ssim.models.Options;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class E2ETest {
    private Properties loadScores(String name) throws IOException {
        Properties scores = new Properties();
        scores.load(getClass().getClassLoader().getResourceAsStream(name + ".properties"));
        return scores;
    }

    private static double round(double d) {
        return java.lang.Math.round(d * 1000.0) / 1000.0;
    }

    private static void compareAll(Properties scores, Options options) throws IOException, URISyntaxException {
        for (String latest : scores.stringPropertyNames()) {
            String baseline = latest.split("_")[0];
            double expectedScore = round(Double.parseDouble(scores.getProperty(latest)));
            double actualScore = round(DiffImage.compare(baseline, latest, options).mssim);
            assertEquals(expectedScore, actualScore);
        }
    }

    @Test
    void testSSIMFast() throws IOException, URISyntaxException {
        Properties scores = loadScores("IVC_color");

        Options options = Options.Defaults();
        options.ssim = Options.SSIMImpl.FAST;
        options.rgb2grayVersion = Options.RGB2Gray.ORIGINAL;

        compareAll(scores, options);
    }

    @Test
    void testSSIMWeber() throws IOException, URISyntaxException {
        Properties scores = loadScores("IVC_color-weber");

        Options options = Options.Defaults();
        options.ssim = Options.SSIMImpl.WEBER;
        options.rgb2grayVersion = Options.RGB2Gray.ORIGINAL;

        compareAll(scores, options);
    }

    @Test
    void testSSIMFastRGBInt() throws IOException, URISyntaxException {
        Properties scores = loadScores("IVC_color_rgb_int");

        Options options = Options.Defaults();
        options.ssim = Options.SSIMImpl.FAST;
        options.rgb2grayVersion = Options.RGB2Gray.INTEGER;

        compareAll(scores, options);
    }

    @Test
    void testSSIMWeberRGBInt() throws IOException, URISyntaxException {
        Properties scores = loadScores("IVC_color-weber_rgb_int");

        Options options = Options.Defaults();
        options.ssim = Options.SSIMImpl.WEBER;
        options.rgb2grayVersion = Options.RGB2Gray.INTEGER;

        compareAll(scores, options);
    }
}
