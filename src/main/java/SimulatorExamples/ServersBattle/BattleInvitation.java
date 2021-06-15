package SimulatorExamples.ServersBattle;

import Node.BaseNode;
import Underlay.packets.Event;

import java.util.UUID;

public class BattleInvitation implements Event {
    UUID host, opponent;
    int duration;

    public BattleInvitation(UUID host, UUID opponent, int duration) {
        this.host = host;
        this.opponent = opponent;
        this.duration = duration;
    }

    @Override
    public boolean actionPerformed(BaseNode hostNode) {
        Contestant node = (Contestant) hostNode;
        node.onNewFightInvitation(host, duration);
        return true;
    }

    @Override
    public String logMessage() {
        return this.host + " Invitation is pending " + this.opponent + " Confirmation";
    }

    @Override
    public int size() {
        // TODO: return number of encoded bytes
        return 1;
    }
}
