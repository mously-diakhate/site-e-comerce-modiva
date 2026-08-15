package sn.modiva.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CouleurTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Couleur getCouleurSample1() {
        return new Couleur().id(1L).libelle("libelle1").codeHex("codeHex1");
    }

    public static Couleur getCouleurSample2() {
        return new Couleur().id(2L).libelle("libelle2").codeHex("codeHex2");
    }

    public static Couleur getCouleurRandomSampleGenerator() {
        return new Couleur().id(longCount.incrementAndGet()).libelle(UUID.randomUUID().toString()).codeHex(UUID.randomUUID().toString());
    }
}
