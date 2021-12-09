package underlay.local;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;

import underlay.Underlay;
import underlay.packets.Request;


/**
 * Serves as the LocalUnderlay layer of the simulator.
 */

public class LocalUnderlay extends Underlay {


  private final String selfAddress;
  private final int port;
  private HashMap<SimpleEntry<String, Integer>, LocalUnderlay> allUnderlay;

  /**
   * Constructor of LocalUnderlay.
   *
   * @param selfAddress Address of the underlay
   * @param port        port of the Underlay
   * @param allUnderlay hashmap of all underlays
   */
  public LocalUnderlay(String selfAddress, int port, HashMap<SimpleEntry<String, Integer>, LocalUnderlay> allUnderlay) {
    this.selfAddress = selfAddress;
    this.port = port;
    this.allUnderlay = allUnderlay;
  }

  public void setAllUnderlay(HashMap<SimpleEntry<String, Integer>, LocalUnderlay> allUnderlay) {
    this.allUnderlay = allUnderlay;
  }

  @Override
  public boolean terminate(String address, int port) {
    allUnderlay.remove(new SimpleEntry<>(address, port));
    return true;
  }

  @Override
  public int getPort() {
    return this.port;
  }

  @Override
  public String getAddress() {
    return this.selfAddress;
  }

  @Override
  protected boolean initUnderlay(int port) {
    // the underlay to the underlay cluster
    allUnderlay.put(new SimpleEntry<>(this.selfAddress, this.port), this);
    return true;
  }

  /**
   * Sends a request to Underlay. Return true if no errors.
   *
   * @param address address of the remote server.
   * @param port    port of the remote server.
   * @param request the request.
   * @return response for the given request. Null in case of failure
   */
  @Override
  public boolean sendMessage(String address, int port, Request request) {
    SimpleEntry fullAddress = new SimpleEntry<>(address, port);
    if (!allUnderlay.containsKey(fullAddress)) {
      log.error("[LocalUnderlay] " + address + ": Node is not found");
      return false;
    }
    try {
      Underlay destinationUnderlay = allUnderlay.get(fullAddress);

      // handle the request in a separated thread
      new Thread() {
        @Override
        public void run() {
          destinationUnderlay.dispatchRequest(request);
        }
      }.start();
      return true;
    } catch (NullPointerException e) {
      log.error("[LocalUnderlay] Middle layer instance not found ");
      return false;
    }
  }

  /**
   * associate a middle layer to a specific node.
   *
   * @param address  address of the node
   * @param port     ID of the node
   * @param underlay underlay to add.
   * @return true if ID was found and instance was added successfully. False, otherwise.
   */
  public boolean addInstance(String address, int port, LocalUnderlay underlay) {
    allUnderlay.put(new SimpleEntry<>(address, port), underlay);
    return true;
  }
}
