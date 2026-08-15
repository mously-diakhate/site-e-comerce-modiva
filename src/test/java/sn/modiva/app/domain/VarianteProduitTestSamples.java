package sn.modiva.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class VarianteProduitTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static VarianteProduit getVarianteProduitSample1() {
        return new VarianteProduit().id(1L).stock(1);
    }

    public static VarianteProduit getVarianteProduitSample2() {
        return new VarianteProduit().id(2L).stock(2);
    }

    public static VarianteProduit getVarianteProduitRandomSampleGenerator() {
        return new VarianteProduit().id(longCount.incrementAndGet()).stock(intCount.incrementAndGet());
    }
}
