/*
 * Copyright (c) 2021, Nelson Gonçalves. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package boofcv.alg.color;

import boofcv.alg.misc.GImageMiscOps;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ColorCmykTest {

    private static final double DOUBLE_DELTA = 1E-10;
    private static final float FLOAT_DELTA = 1E-3F;

    enum TestColors {
        BLACK(0, 0, 0, 0, 0, 0, 100),
        WHITE(255, 255, 255, 0, 0, 0, 0),
        RED(255, 0, 0, 0, 100, 100, 0),
        GREEN(0, 255, 0, 100, 0, 100, 0),
        BLUE(0, 0, 255, 100, 100, 0, 0),
        YELLOW(255, 255, 0, 0, 0, 100, 0),
        CYAN(0, 255, 255, 100, 0, 0, 0),
        MAGENTA(255, 0, 255, 0, 100, 0, 0);

        private final int r, g, b, c, m, y, k;

        TestColors(int r, int g, int b, int c, int m, int y, int k) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.c = c;
            this.m = m;
            this.y = y;
            this.k = k;
        }

        double[] rgb() {
            return new double[]{r, g, b};
        }

        double[] cmyk() {
            return new double[]{c, m, y, k};
        }
    }

    @Test
    public void shouldConvertToRgbInt() {
        double[] cmyk = new double[4];
        ColorCmyk.rgbToCmyk(100, 120, 130, cmyk);
        final int actual = ColorCmyk.cmykToRgb(cmyk[0], cmyk[1], cmyk[2], cmyk[3]);
        assertEquals(100, (actual >> 16) & 0xFF);
        assertEquals(120, (actual >> 8) & 0xFF);
        assertEquals(130, (actual) & 0xFF);
    }

    @Test
    public void shouldConvertBasicColorsDoubles() {
        for (TestColors color : TestColors.values()) {
            double[] actual = new double[3];
            ColorCmyk.cmykToRgb(color.c, color.m, color.y, color.k, actual);
            assertArrayEquals(color.rgb(), actual, DOUBLE_DELTA);
        }

        for (TestColors color : TestColors.values()) {
            double[] actual = new double[4];
            ColorCmyk.rgbToCmyk(color.r, color.g, color.b, actual);
            assertArrayEquals(color.cmyk(), actual, DOUBLE_DELTA);
        }
    }

    @Test
    public void shouldConvertBasicColorsFloats() {
        for (TestColors color : TestColors.values()) {
            float[] actual = new float[3];
            ColorCmyk.cmykToRgb(color.c, color.m, color.y, color.k, actual);
            assertArrayEquals(asFloat(color.rgb()), actual, FLOAT_DELTA);
        }

        for (TestColors color : TestColors.values()) {
            float[] actual = new float[4];
            ColorCmyk.rgbToCmyk(color.r, color.g, color.b, actual);
            assertArrayEquals(asFloat(color.cmyk()), actual, FLOAT_DELTA);
        }
    }

    @Test
    public void shouldConvertBackAndForthDoubles() {
        final Random rand = new Random();
        for (int i = 0; i < 50; i++) {
            double r = rand.nextDouble() * 255;
            double g = rand.nextDouble() * 255;
            double b = rand.nextDouble() * 255;

            double[] expected = {r, g, b};
            double[] rgb = new double[3];
            double[] cmyk = new double[4];
            ColorCmyk.rgbToCmyk(r, g, b, cmyk);
            ColorCmyk.cmykToRgb(cmyk[0], cmyk[1], cmyk[2], cmyk[3], rgb);
            assertArrayEquals(expected, rgb, DOUBLE_DELTA);
        }
    }

    @Test
    public void shouldConvertBackAndForthFloats() {
        final Random rand = new Random();
        for (int i = 0; i < 50; i++) {
            float r = rand.nextFloat() * 255;
            float g = rand.nextFloat() * 255;
            float b = rand.nextFloat() * 255;

            float[] expected = {r, g, b};
            float[] rgb = new float[3];
            float[] cmyk = new float[4];
            ColorCmyk.rgbToCmyk(r, g, b, cmyk);
            ColorCmyk.cmykToRgb(cmyk[0], cmyk[1], cmyk[2], cmyk[3], rgb);
            assertArrayEquals(expected, rgb, FLOAT_DELTA);
        }
    }

    @Test
    public void shouldConvertPlanarFloat() {
        Planar<GrayF32> rgb = new Planar<>(GrayF32.class, 10, 15, 3);
        Planar<GrayF32> cmyk = new Planar<>(GrayF32.class, 10, 15, 4);
        Planar<GrayF32> actual = new Planar<>(GrayF32.class, 10, 15, 3);

        GImageMiscOps.fillUniform(rgb, new Random(), 0, 0);

        ColorCmyk.rgbToCmyk(rgb, cmyk);
        ColorCmyk.cmykToRgb(cmyk, actual);

        float[] tmp = new float[4];

        for (int y = 0; y < rgb.height; y++) {
            for (int x = 0; x < rgb.width; x++) {
                float r = rgb.getBand(0).get(x, y);
                float g = rgb.getBand(1).get(x, y);
                float b = rgb.getBand(2).get(x, y);

                ColorCmyk.rgbToCmyk(r, g, b, tmp);
                for (int i = 0; i < 4; i++) {
                    assertEquals(tmp[i], cmyk.getBand(i).unsafe_get(x, y), FLOAT_DELTA);
                }

                assertEquals(r, actual.getBand(0).get(x, y), FLOAT_DELTA);
                assertEquals(g, actual.getBand(1).get(x, y), FLOAT_DELTA);
                assertEquals(b, actual.getBand(2).get(x, y), FLOAT_DELTA);
            }
        }
    }

    private float[] asFloat(double[] doubles) {
        float[] floats = new float[doubles.length];
        for (int i = 0; i < doubles.length; i++) {
            floats[i] = (float) doubles[i];
        }
        return floats;
    }

}
