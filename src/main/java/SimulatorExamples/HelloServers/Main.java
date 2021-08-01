package SimulatorExamples.HelloServers;

import Simulator.Simulator;
import Underlay.UnderlayType;
import Utils.Generator.UniformGenerator;
import Utils.Generator.WeibullGenerator;

public class Main {

    public static void main(String[] args) {
        myNode fixtureNode = new myNode();
        Simulator<myNode> simulation = new Simulator<myNode>(fixtureNode, 5, UnderlayType.MOCK_NETWORK);
        simulation.churnSimulation(100000, new UniformGenerator(500, 1000), new UniformGenerator(2000, 3000));
    }
}
