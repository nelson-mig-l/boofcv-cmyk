# boofcv-cmyk

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/ae5f2db0b519404980fbb67b5ca3a71f)](https://app.codacy.com/gh/nelson-mig-l/boofcv-cmyk?utm_source=github.com&utm_medium=referral&utm_content=nelson-mig-l/boofcv-cmyk&utm_campaign=Badge_Grade_Settings)

BoofCV CMYK color space.

## How to use

```xml
<dependency>
  <groupId>nelson.boofcv</groupId>
  <artifactId>boofcv-cmyk</artifactId>
  <version>1.0</version>
</dependency> 
```

```java
Planar<GrayF32> rgb = ...
Planar<GrayF32> cmyk = new Planar<>(GrayF32.class, rgb.getWidth(), rgb.getHeight(), 4);
ColorCmyk.rgbToCmyk(rgb, cmyk);
GrayF32 c = cmyk.getBand(0);
GrayF32 m = cmyk.getBand(1);
GrayF32 y = cmyk.getBand(2);
GrayF32 k = cmyk.getBand(3);
```

## Why to use

For fun and profit.
