package underlay;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import metrics.NoopCollector;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import underlay.local.LocalUnderlay;
import utils.NoopOrchestrator;

/**
 * Test the communication and termination of every network underlay.
 */

public class UnderlayTest {
  static final int THREAD_CNT = 50;
  static final int SLEEP_DURATION = 10000; // 10 seconds
  private static final ConcurrentHashMap<Integer, Integer> usedPorts = new ConcurrentHashMap();
  private static final HashMap<AbstractMap.SimpleEntry<String, Integer>, Underlay> allUnderlays = new HashMap<>();
  static JDKRandomGenerator rand = new JDKRandomGenerator();

  @AfterAll
  static void terminate() {
    for (Map.Entry<AbstractMap.SimpleEntry<String, Integer>, Underlay> entry : allUnderlays.entrySet()) {
      entry.getValue().terminate(entry.getKey().getKey(), entry.getKey().getValue());
    }
  }

  ArrayList<FixtureNode> initialize(UnderlayType underlayName) {
    ArrayList<FixtureNode> instances = new ArrayList<>();
    ArrayList<UUID> allId = new ArrayList<>();
    HashMap<UUID, AbstractMap.SimpleEntry<String, Integer>> allFullAddresses = new HashMap<>();

    // generate IDs
    for (int i = 0; i < THREAD_CNT; i++) {
      allId.add(UUID.randomUUID());
    }

    try {
      for (int i = 0; i < THREAD_CNT; i++) {
        UUID id = allId.get(i);

        Network network = new Network(id,
              allFullAddresses,
              new NoopOrchestrator(),
              new NoopCollector());
        FixtureNode node = new FixtureNode(id, allId, network);
        network.setOverlay(node);
        Underlay underlay = UnderlayFactory.newUnderlay(underlayName, 0, network);
        int port = underlay.getPort();
        allFullAddresses.put(id, new AbstractMap.SimpleEntry<>(underlay.getAddress(), port));
        network.setUnderlay(underlay);
        instances.add(node);
        allUnderlays.put(new AbstractMap.SimpleEntry<>(underlay.getAddress(), port), underlay);
      }
    } catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
    }


    return instances;
  }

  @Test
  void atestTcp() {
    // generate middle layers
    ArrayList<FixtureNode> tcpNodes = initialize(UnderlayType.TCP_PROTOCOL);
    assure(tcpNodes);

  }

  void assure(ArrayList<FixtureNode> instances) {
    // start all instances
    for (FixtureNode node : instances) {
      new Thread(node::onStart).start();
    }
    try {
      Thread.sleep(SLEEP_DURATION);
    } catch (Exception e) {
      e.printStackTrace();
    }

    // check that all nodes received threadCount - 1 messages
    for (FixtureNode node : instances) {
      assertEquals(THREAD_CNT - 1, node.receivedMessages.get());
    }
  }

  @Test
  void btestUdp() {
    // generate middle layers
    ArrayList<FixtureNode> udpNodes = initialize(UnderlayType.UDP_PROTOCOL);
    assure(udpNodes);
  }

  @Test
  void ctestRmi() {
    // generate middle layers
    ArrayList<FixtureNode> javaRmiNodes = initialize(UnderlayType.JAVA_RMI);
    assure(javaRmiNodes);
  }

  @Test
  void testLocal() {
    HashMap<AbstractMap.SimpleEntry<String, Integer>, LocalUnderlay> allLocalUnderlay = new HashMap<>();
    // generate middle layers
    ArrayList<FixtureNode> instances = new ArrayList<>();
    ArrayList<UUID> allId = new ArrayList<>();
    HashMap<UUID, AbstractMap.SimpleEntry<String, Integer>> allFullAddresses = new HashMap<>();

    // generate IDs
    for (int i = 0; i < THREAD_CNT; i++) {
      allId.add(UUID.randomUUID());
    }

    // generate full addresses
    try {
      String address = Inet4Address.getLocalHost().getHostAddress();
      for (int i = 0; i < THREAD_CNT; i++) {

        allFullAddresses.put(allId.get(i), new AbstractMap.SimpleEntry<>(address, i));
      }

    } catch (UnknownHostException e) {
      e.printStackTrace();
    }

    for (int i = 0; i < THREAD_CNT; i++) {
      UUID id = allId.get(i);
      String address = allFullAddresses.get(id).getKey();
      int port = allFullAddresses.get(id).getValue();

      Network network = new Network(id,
            allFullAddresses,
            new NoopOrchestrator(),
            new NoopCollector());
      FixtureNode node = new FixtureNode(id, allId, network);
      network.setOverlay(node);

      LocalUnderlay underlay = new LocalUnderlay(address, port, allLocalUnderlay);
      underlay.initialize(port, network);

      network.setUnderlay(underlay);
      instances.add(node);
      allLocalUnderlay.put(new AbstractMap.SimpleEntry<>(address, port), underlay);
    }

    assure(instances);
  }
}
