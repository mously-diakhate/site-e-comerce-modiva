package sn.modiva.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LivraisonTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Livraison getLivraisonSample1() {
        return new Livraison().id(1L).adresse("adresse1").ville("ville1").quartier("quartier1").telephone("telephone1");
    }

    public static Livraison getLivraisonSample2() {
        return new Livraison().id(2L).adresse("adresse2").ville("ville2").quartier("quartier2").telephone("telephone2");
    }

    public static Livraison getLivraisonRandomSampleGenerator() {
        return new Livraison()
            .id(longCount.incrementAndGet())
            .adresse(UUID.randomUUID().toString())
            .ville(UUID.randomUUID().toString())
            .quartier(UUID.randomUUID().toString())
            .telephone(UUID.randomUUID().toString());
    }
}
