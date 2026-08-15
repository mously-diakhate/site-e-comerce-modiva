package sn.modiva.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AdresseTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Adresse getAdresseSample1() {
        return new Adresse().id(1L).libelle("libelle1").adresse("adresse1").ville("ville1").quartier("quartier1").telephone("telephone1");
    }

    public static Adresse getAdresseSample2() {
        return new Adresse().id(2L).libelle("libelle2").adresse("adresse2").ville("ville2").quartier("quartier2").telephone("telephone2");
    }

    public static Adresse getAdresseRandomSampleGenerator() {
        return new Adresse()
            .id(longCount.incrementAndGet())
            .libelle(UUID.randomUUID().toString())
            .adresse(UUID.randomUUID().toString())
            .ville(UUID.randomUUID().toString())
            .quartier(UUID.randomUUID().toString())
            .telephone(UUID.randomUUID().toString());
    }
}
