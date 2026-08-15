package sn.modiva.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class PanierTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Panier getPanierSample1() {
        return new Panier().id(1L);
    }

    public static Panier getPanierSample2() {
        return new Panier().id(2L);
    }

    public static Panier getPanierRandomSampleGenerator() {
        return new Panier().id(longCount.incrementAndGet());
    }
}
