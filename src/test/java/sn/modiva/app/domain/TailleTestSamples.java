package sn.modiva.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TailleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Taille getTailleSample1() {
        return new Taille().id(1L).libelle("libelle1");
    }

    public static Taille getTailleSample2() {
        return new Taille().id(2L).libelle("libelle2");
    }

    public static Taille getTailleRandomSampleGenerator() {
        return new Taille().id(longCount.incrementAndGet()).libelle(UUID.randomUUID().toString());
    }
}
