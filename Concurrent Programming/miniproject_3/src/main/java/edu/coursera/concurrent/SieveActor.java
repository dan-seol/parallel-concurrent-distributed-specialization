package edu.coursera.concurrent;

import edu.rice.pcdp.Actor;

import java.util.Objects;

import static edu.rice.pcdp.PCDP.finish;

/**
 * An actor-based implementation of the Sieve of Eratosthenes.
 *
 * TODO Fill in the empty SieveActorActor actor class below and use it from
 * countPrimes to determin the number of primes <= limit.
 */
public final class SieveActor extends Sieve {

    /**
     * {@inheritDoc}
     *
     * TODO Use the SieveActorActor class to calculate the number of primes <=
     * limit in parallel. You might consider how you can model the Sieve of
     * Eratosthenes as a pipeline of actors, each corresponding to a single
     * prime number.
     */
    @Override
    public int countPrimes(final int limit) {
        SieveActorActor actor = new SieveActorActor();
        finish(() -> {
            for (int i = 3; i <= limit; i+= 2) {
                actor.send(i);
            }
            actor.send(2);
        });

        // Sum up the number of local primes from each actor in the chain.
        int totalPrimes = 1;
        SieveActorActor currentActor = actor;
        while (currentActor != null) {
            totalPrimes += currentActor.numPrimes;
            currentActor = currentActor.nextActor;
        }

        return totalPrimes;
    }

    /**
     * An actor class that helps implement the Sieve of Eratosthenes in
     * parallel.
     */
    public static final class SieveActorActor extends Actor {
        private static final int MAX_LOCAL_PRIMES = 1000;

        private int numPrimes;
        private int[] localPrimes;
        private SieveActorActor nextActor = null;

        public SieveActorActor() {
            this.numPrimes = 0;
            this.localPrimes = new int[MAX_LOCAL_PRIMES];
        }

        /**
         * Process a single message sent to this actor.
         * <p>
         * TODO complete this method.
         *
         * @param msg Received message
         */
        @Override
        public void process(final Object msg) {
            final int candidate = (Integer) msg;

            if (candidate < 3 && this.nextActor == null) {
                return;
            }

            if (candidate < 3 && this.nextActor != null) {
                this.nextActor.process(candidate);
            }

            if (isCoprime(candidate)) {

                if (numPrimes < MAX_LOCAL_PRIMES) {
                    localPrimes[numPrimes++] = candidate;
                } else if (this.nextActor != null) {
                    this.nextActor.send(candidate);
                } else {
                    this.nextActor = new SieveActorActor();
                    this.nextActor.send(candidate);
                }

            }
        }

        private boolean isCoprime(final int candidate) {
            for (int i = 0; i < numPrimes; i++) {
                if (candidate % this.localPrimes[i] == 0) {
                    return false;
                }
            }
            return true;
        }
    }
}
