package sn.modiva.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PaiementTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Paiement getPaiementSample1() {
        return new Paiement().id(1L).moyenPaiement("moyenPaiement1").referenceTransaction("referenceTransaction1");
    }

    public static Paiement getPaiementSample2() {
        return new Paiement().id(2L).moyenPaiement("moyenPaiement2").referenceTransaction("referenceTransaction2");
    }

    public static Paiement getPaiementRandomSampleGenerator() {
        return new Paiement()
            .id(longCount.incrementAndGet())
            .moyenPaiement(UUID.randomUUID().toString())
            .referenceTransaction(UUID.randomUUID().toString());
    }
}
