package underlay.packets;

import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a serializable request packet what wrap an event.
 */
public class Request implements Serializable {

  private final Event event;
  private final UUID originalId;
  private final UUID destinationId;

  /**
   * Constructor for the Request.
   *
   * @param event Event of the request
   * @param originalId sender of the request
   * @param destinationId reciever of the request
   */
  public Request(Event event, UUID originalId, UUID destinationId) {
    this.event = event;
    this.originalId = originalId;
    this.destinationId = destinationId;
  }

  public Event getEvent() {
    return event;
  }

  public UUID getOriginalId() {
    return originalId;
  }

  public UUID getDestinationId() {
    return destinationId;
  }
}
