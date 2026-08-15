package sn.modiva.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CommandeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Commande getCommandeSample1() {
        return new Commande().id(1L).numero("numero1").modePaiement("modePaiement1");
    }

    public static Commande getCommandeSample2() {
        return new Commande().id(2L).numero("numero2").modePaiement("modePaiement2");
    }

    public static Commande getCommandeRandomSampleGenerator() {
        return new Commande()
            .id(longCount.incrementAndGet())
            .numero(UUID.randomUUID().toString())
            .modePaiement(UUID.randomUUID().toString());
    }
}
