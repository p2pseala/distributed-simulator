package Utils;

import Node.BaseNode;
import Underlay.packets.Event;

public class FixtureEvent implements Event {
    @Override
    public boolean actionPerformed(BaseNode hostNode) {
        return true;
    }

    @Override
    public String logMessage() {
        return null;
    }
}
