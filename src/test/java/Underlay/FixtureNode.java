package Underlay;

import Node.BaseNode;
import Underlay.packets.Event;

import java.util.ArrayList;
import java.util.UUID;

public class FixtureNode implements BaseNode {
    private UUID selfID;
    private ArrayList<UUID> allID;
    private MiddleLayer network;
    public int receivedMessages = 0;

    FixtureNode(){}

    FixtureNode(UUID selfID, ArrayList<UUID> allID, MiddleLayer network){
        this.selfID = selfID;
        this.network = network;
        this.allID = allID;
    }


    @Override
    public void onCreate(ArrayList<UUID> allID) {
    }

    @Override
    public void onStart() {
        for(UUID id : allID){
            if(id != selfID)
                network.send(id, new FixtureEvent());
        }
    }

    @Override
    public void onStop() {
    }

    @Override
    public BaseNode newInstance(UUID ID, MiddleLayer network) {
        return null;
    }

    @Override
    public void onNewMessage(UUID originID, Event msg){
        this.receivedMessages++;
    }
}
