package metrics.opera;

import io.prometheus.client.Gauge;
import java.util.UUID;
import metrics.Constants;
import metrics.GaugeCollector;
import simulator.Simulator;


/**
 * This class provides a prometheus-based gauge for extracting metrics
 * For every registered metric, the metric will be collected for each node separately
 * Gauges can be incremented and decremented.
 */
public class OperaGauge extends OperaMetric implements GaugeCollector {
  /**
   * Registers a gauge. This method is expected to be executed by several instances of nodes assuming a decentralized
   * metrics registration. However, only the first invocation gets through and registers the metric.
   * The rest will be idempotent.
   * Since the collector is handled globally in a centralized manner behind the scene,
   * only one successful registration is enough.
   *
   * @param name        name of gauge metric.
   * @param namespace   namespace of gauge metric, normally refers to a distinct class of opera, e.g., middleware.
   * @param subsystem   either the same as namespace for monolith classes, or the subclass for which we collect metrics,
   *                    e.g., latency generator within middleware.
   * @param helpMessage a hint message describing what this metric represents.
   * @throws IllegalArgumentException when a different metric type (e.g., histogram) with the same name has already
   *                                  been registered.
   */
  public void register(String name, String namespace, String subsystem, String helpMessage) throws IllegalArgumentException {
    if (!collectors.containsKey(name)) {
      if (collectorsTypes.get(name) != TYPE.GAUGE) {
        throw new IllegalArgumentException("metrics name already taken with another type: " + name + " type: " + collectorsTypes.get(name));
      }
      // collector already registered
      return;
    }
    collectors.put(name, Gauge.build().
        namespace(namespace).
        subsystem(subsystem).
        name(name).
        help(helpMessage).
        labelNames(Constants.UUID).
        register());
    collectorsTypes.put(name, TYPE.GAUGE);
  }

  @Override
  public boolean inc(String name, UUID id) {
    return inc(name, id, 1.0);
  }

  @Override
  public boolean inc(String name, UUID id, double v) {
    Gauge metric = getMetric(name);
    if (metric == null) return false;
    metric.labels(id.toString()).inc(v);
    return true;
  }

  @Override
  public boolean dec(String name, UUID id, double v) {
    Gauge metric = getMetric(name);
    if (metric == null) return false;
    metric.labels(id.toString()).dec(v);
    return true;
  }

  @Override
  public boolean dec(String name, UUID id) {
    return dec(name, id, 1.0);
  }

  @Override
  public boolean set(String name, UUID id, double v) {
    Gauge metric = getMetric(name);
    if (metric == null) return false;
    metric.labels(id.toString()).set(v);
    return true;
  }

  @Override
  public double get(String name, UUID id) {
    Gauge metric = getMetric(name);
    if (metric == null) return 0;
    return metric.labels(id.toString()).get();
  }

  /**
   * Return prometheus metric for a specific name
   *
   * @param name
   * @return the requested metric
   */
  @Override
  public Gauge getMetric(String name) {
    if (!collectors.containsKey(name)) {
      Simulator.getLogger().error("[SimulatorGauge] could not find a metric with name " + name);
      System.err.println("[SimulatorGauge] could not find a metric with name " + name);
      return null;
    }
    if (collectorsTypes.get(name) != TYPE.GAUGE) {
      Simulator.getLogger().error("[SimulatorGauge] metric registered with the name " + name + " is not a Gauge");
      System.err.println("[SimulatorGauge] metric registered with the name " + name + " is not a Gauge");
      return null;
    }
    return (Gauge) collectors.get(name);
  }

}