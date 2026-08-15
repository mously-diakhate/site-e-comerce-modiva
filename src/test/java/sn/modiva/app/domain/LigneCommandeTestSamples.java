package sn.modiva.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class LigneCommandeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static LigneCommande getLigneCommandeSample1() {
        return new LigneCommande().id(1L).quantite(1);
    }

    public static LigneCommande getLigneCommandeSample2() {
        return new LigneCommande().id(2L).quantite(2);
    }

    public static LigneCommande getLigneCommandeRandomSampleGenerator() {
        return new LigneCommande().id(longCount.incrementAndGet()).quantite(intCount.incrementAndGet());
    }
}
