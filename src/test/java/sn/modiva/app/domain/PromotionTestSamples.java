package sn.modiva.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PromotionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Promotion getPromotionSample1() {
        return new Promotion().id(1L).nom("nom1");
    }

    public static Promotion getPromotionSample2() {
        return new Promotion().id(2L).nom("nom2");
    }

    public static Promotion getPromotionRandomSampleGenerator() {
        return new Promotion().id(longCount.incrementAndGet()).nom(UUID.randomUUID().toString());
    }
}
