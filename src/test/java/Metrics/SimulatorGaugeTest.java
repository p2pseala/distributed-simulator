package Metrics;

import org.apache.commons.math3.random.JDKRandomGenerator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class SimulatorGaugeTest {
    static final int THREAD_CNT = 50;
    static final int ITERATIONS = 2000;
    static JDKRandomGenerator rand = new JDKRandomGenerator();
    CountDownLatch count;
    static final double EPS = 0.01;

    @Test
    void valueTest(){
        assertTrue(SimulatorGauge.register("TestGauge"));
        ArrayList<UUID> allID = new ArrayList<>();
        while(allID.size() != THREAD_CNT)allID.add(UUID.randomUUID());
        count = new CountDownLatch(THREAD_CNT);

        // increment single entry
        UUID id = UUID.randomUUID();
        long tot = 0;
        for(int i = 0;i<ITERATIONS;i++) {
            int v = rand.nextInt(1000);
            tot += v;
            SimulatorGauge.inc("TestGauge", id, v);
        }
        assertEquals(tot, SimulatorGauge.get("TestGauge", id));

        for(UUID nodeID : allID){
            new Thread(){
                @Override
                public void run() {
                    threadTest(nodeID, ITERATIONS);
                }
            }.start();
        }

        try {
            count.await();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        tot = 0;
        for(UUID nodeID : allID){
            tot += SimulatorGauge.get("TestGauge", nodeID);
        }
        assertTrue(Math.abs(tot / (ITERATIONS * THREAD_CNT)) <= EPS);
    }

    void threadTest(UUID nodeID, int iterations){
        while (iterations-- > 0) {
            if(rand.nextBoolean())
                assertTrue(SimulatorGauge.inc("TestGauge", nodeID));
            else
                assertTrue(SimulatorGauge.dec("TestGauge", nodeID));
        }
        count.countDown();
    }

}