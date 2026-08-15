package sn.modiva.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ClienteProfileTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static ClienteProfile getClienteProfileSample1() {
        return new ClienteProfile().id(1L).telephone("telephone1");
    }

    public static ClienteProfile getClienteProfileSample2() {
        return new ClienteProfile().id(2L).telephone("telephone2");
    }

    public static ClienteProfile getClienteProfileRandomSampleGenerator() {
        return new ClienteProfile().id(longCount.incrementAndGet()).telephone(UUID.randomUUID().toString());
    }
}
