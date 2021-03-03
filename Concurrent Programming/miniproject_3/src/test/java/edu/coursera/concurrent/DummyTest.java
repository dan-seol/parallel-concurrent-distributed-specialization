package edu.coursera.concurrent;

import junit.framework.TestCase;

public class DummyTest extends TestCase {

    public void testDummy() throws InterruptedException {
        final int limit = 5;
        int dummy = new SieveActor().countPrimes(limit); // warmup

        assertTrue(dummy == 3);

    }
}
