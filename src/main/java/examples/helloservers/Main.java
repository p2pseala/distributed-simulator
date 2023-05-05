package examples.helloservers;

import network.NetworkProtocol;
import simulator.Factory;
import simulator.Recipe;
import simulator.Simulator;
import utils.churn.UniformGenerator;

/**
 * Hello Servers is a simulator example which consist of 5 nodes.
 * When the simulation started nodes sends "Hello" message to other nodes.
 * If a node receives a "Hello" message it sends "Thank You" else sends "Hello" message back to that node.
 * Simulation goes like this infinitely.
 */
public class Main {
   /**
   * creates a new helloservers simulation for 5 nodes.
   *
   * @param args nothing
   */
  public static void main(String[] args) {
    final String nameSpace = "example-helloservers";
    Factory factory = new Factory();
    factory.addRecipe(new Recipe(new MyNode(), nameSpace, 5));
    Simulator simulator = new Simulator(factory, NetworkProtocol.MOCK_NETWORK);
    simulator.churnSimulation(100000, new UniformGenerator(1000, 2000), new UniformGenerator(2000,
        3000));
  }
}
