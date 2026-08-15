package sn.modiva.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ImageProduitTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ImageProduit getImageProduitSample1() {
        return new ImageProduit().id(1L).url("url1").ordre(1);
    }

    public static ImageProduit getImageProduitSample2() {
        return new ImageProduit().id(2L).url("url2").ordre(2);
    }

    public static ImageProduit getImageProduitRandomSampleGenerator() {
        return new ImageProduit().id(longCount.incrementAndGet()).url(UUID.randomUUID().toString()).ordre(intCount.incrementAndGet());
    }
}
