package examples.bullyalgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;
import metrics.MetricsCollector;
import node.BaseNode;
import org.apache.log4j.Logger;
import simulator.Simulator;
import underlay.MiddleLayer;
import underlay.packets.Event;

/**
 * This is a node class with selfId. Coordinater is determined by the UUID's.
 */
public class MyNode implements BaseNode {
  public static Logger log = Logger.getLogger(Simulator.class.getName());

  public Simulator simulator;
  private UUID selfId;
  public ArrayList<UUID> allId;
  private MiddleLayer network;
  public UUID coordinatorId;
  private MetricsCollector metricsCollector;

  public MyNode() {
  }

  MyNode(UUID selfId, MiddleLayer network, MetricsCollector metricsCollector) {
    this.selfId = selfId;
    this.network = network;
    this.metricsCollector = metricsCollector;

  }

  /**
   * Compares selfID with maxID on the allID list.
   *
   * @return whether this node has maximum UUID or not
   */
  public boolean isMax() {
    UUID maxUuid = Collections.max(allId);
    return maxUuid == this.selfId;
  }

  /**
   * Send Message
   * Sends "Victory" message to all other nodes if this node has maxID and the coordinator is not determined yet.
   * Else sends "Election" message to nodes which has bigger ID than selfID.
   */
  public void sendMessage() {
    if (this.isMax()) {
      log.info("CoordinatorID: " + selfId);
      for (UUID targetId : allId) {
        log.info(selfId + " sends to" + targetId + " " + "Victory Message.");
        Message victoryMassage = new Message("Victory", selfId, targetId);
        network.send(targetId, victoryMassage);
      }
    } else {
      for (UUID targetId : allId) {
        if (targetId.compareTo(selfId) == 1) {
          log.info(selfId + " sends to" + targetId + " " + "Election Message.");
          Message electionMassage = new Message("Election", selfId, targetId);
          network.send(targetId, electionMassage);
        }
      }
    }
  }

  /**
   * Set allID.
   *
   * @param allIds list of all ID's
   */
  public void setAllId(ArrayList<UUID> allIds) {
    this.allId = allIds;
  }

  /**
   * Get MaxID.
   *
   * @return maxID in the allID list
   */
  public UUID getMaxId() {
    return (Collections.max(allId));
  }

  /**
   * Get Coordinator's ID.
   *
   * @return ID of the coordinator
   */
  public UUID getCoordinatorId() {
    return (this.coordinatorId);
  }

  /**
   * Get SelfID.
   *
   * @return ID of this node
   */
  public UUID getUuid() {
    return (this.selfId);
  }

  /**
   * set UUID.
   *
   * @param uuid to change uuid of the node
   */
  public void setUuid(UUID uuid) {
    this.selfId = uuid;
  }

  /**
   * Set CoordinatorID.
   *
   * @param uuid to set the coordinator ID
   */
  public void setCoordinatorId(UUID uuid) {
    this.coordinatorId = uuid;
  }

  /**
   * Sets the Simulation.
   *
   * @param simulator to set the simulator
   */
  public void setSimulation(Simulator simulator) {
    this.simulator = simulator;
    allId = simulator.getAllId();
    coordinatorId = Collections.max(allId);
  }


  @Override
  public void onCreate(ArrayList<UUID> allId) {
    this.allId = allId;
    network.ready();
  }

  @Override
  public void onStart() {
    if (this.coordinatorId == null) {
      this.sendMessage();
    }
  }

  @Override
  public void onStop() {
  }

  @Override
  public void onNewMessage(UUID originId, Event msg) {
    coordinatorId = this.getMaxId();
    try {
      Random rand = new Random();
      Thread.sleep(rand.nextInt(1000));
    } catch (InterruptedException e) {
      log.error("Interruption");
    }
    msg.actionPerformed(this);


  }

  @Override
  public BaseNode newInstance(UUID selfId, MiddleLayer network, MetricsCollector metrics) {
    return new MyNode(selfId, network, metrics);
  }


}