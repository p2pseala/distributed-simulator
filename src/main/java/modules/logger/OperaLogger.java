package modules.logger;

import org.slf4j.LoggerFactory;

/**
 * Encapsulates logger for opera project.
 */
public class OperaLogger {
  /**
   * Logger prefix that is used to filter out logs generated by this project.
   */
  private static final String LOGGER_NAME = "io.github.yhassanzadeh13";

  /**
   * Creates a logger for the given class name prefixed by the logger name.
   *
   * @param className class name for which a logger is created.
   * @return logger for the class.
   */
  public static Logger getLogger(String className) {
    return new Logger(LoggerFactory.getLogger(LOGGER_NAME + "." + className));
  }
}