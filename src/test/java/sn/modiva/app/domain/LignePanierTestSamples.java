package sn.modiva.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class LignePanierTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static LignePanier getLignePanierSample1() {
        return new LignePanier().id(1L).quantite(1);
    }

    public static LignePanier getLignePanierSample2() {
        return new LignePanier().id(2L).quantite(2);
    }

    public static LignePanier getLignePanierRandomSampleGenerator() {
        return new LignePanier().id(longCount.incrementAndGet()).quantite(intCount.incrementAndGet());
    }
}
