package mod.adrenix.nostalgic.util.common.log;

//import dev.architectury.platform.Platform;

import mod.adrenix.nostalgic.services.NostalgicServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

//import mod.adrenix.nostalgic.tweak.config.ModTweak;

/**
 * The mod's custom logger wrapper.
 *
 * @param prefix The prefix that is attached to each logging statement.
 */
public record ModLogger(String prefix) {
    /**
     * The current {@link LogManager} instance.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Whether the mod is in debugging mode.
     */
    private static boolean isDebugging = NostalgicServices.PLATFORM.isDevEnvironment();

    /**
     * Checks if the mod is in debug mode.
     *
     * @return The state of this record's {@code isDebugging} flag.
     */
    public boolean isDebugMode() {
        return /*NostalgicTweaks.isServer() ? ModTweak.SERVER_DEBUG.get() : */isDebugging; //TODO
    }

    /**
     * Change this record's {@code isDebugging} flag.
     *
     * @param state The new state.
     */
    public void setDebug(boolean state) {
        isDebugging = state;
    }

    /**
     * Toggle the logger's debugging mode.
     */
    public void setDebug() {
        this.setDebug(!this.isDebugMode());
    }

    /**
     * Some consoles will put a red color on the error message which will cause visual glitches for this logger. This
     * assures the starting bracket of the message is reset before the rest of the message is printed.
     *
     * @return A left bracket that has a reset ansi code applied to it.
     */
    private String getStartBracket() {
        return String.format("%s", LogColor.apply(LogColor.RESET, "["));
    }

    /**
     * Get the prefix of this record's {@code prefix}.
     *
     * @return A colored square-bracketed prefix with this record's {@code prefix}.
     */
    private String getPrefix() {
        return String.format("%s%s] ", this.getStartBracket(), LogColor.apply(LogColor.BLUE, this.prefix));
    }

    /**
     * Changes known strings to specific colors, such as applying a green color to instances of 'true' within a string.
     * Any logging colors are converted to ANSI in development environments and are removed in production environments.
     *
     * @param input The logging statement.
     * @return The logging statement modified with appropriate color changes based on mod environment.
     */
    private String getOutput(String input) {
        input = input.replaceAll("true", LogColor.apply(LogColor.GREEN, "true"));
        input = input.replaceAll("false", LogColor.apply(LogColor.RED, "false"));

        for (LogColor color : LogColor.values())
            input = color.convert(input);

        return input;
    }

    /* Logging Statement Utilities */

    /**
     * Create an informative logging statement.
     *
     * @param message The info to log.
     */
    public void info(String message) {
        String input = String.format(this.getPrefix() + "[%s] ", LogColor.apply(LogColor.GREEN, "INFO")) + message;
        LOGGER.info(getOutput(input));
    }

    /**
     * Convenience overload method to output an info message as a formatted string.
     *
     * @param message The info message to log.
     * @param args    String formatting arguments.
     */
    public void info(String message, Object... args) {
        this.info(String.format(message, args));
    }

    /**
     * Create a warning logging statement.
     *
     * @param message The warning to log.
     */
    public void warn(String message) {
        String input = String.format(this.getPrefix() + "[%s] ", LogColor.apply(LogColor.GOLD, "WARN")) + message;
        LOGGER.warn(getOutput(input));
    }

    /**
     * Convenience overload method to output a warning message as a formatted string.
     *
     * @param message The warning message to log.
     * @param args    String formatting arguments.
     */
    public void warn(String message, Object... args) {
        this.warn(String.format(message, args));
    }

    /**
     * Create an error logging statement.
     *
     * @param message The error to log.
     */
    public void error(String message) {
        String input = String.format(this.getPrefix() + "[%s] ", LogColor.apply(LogColor.RED, "ERROR")) + message;
        LOGGER.error(getOutput(input));
    }

    /**
     * Convenience overload method to output an error message as a formatted string. If an exception is included in the
     * var args, then it will automatically have its stacktrace printed.
     *
     * @param message The error message to log.
     * @param args    String formatting arguments.
     */
    public void error(String message, Object... args) {
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];

            if (arg instanceof Throwable exception) {
                StringWriter writer = new StringWriter();
                PrintWriter printer = new PrintWriter(writer);

                exception.printStackTrace(printer);

                args[i] = writer.toString();
            }
        }

        this.error(String.format(message, args));
    }

    /**
     * Create a debugging logging statement.
     *
     * @param message The debug message to log.
     */
    public void debug(String message) {
        if (this.isDebugMode()) {
            String input = String.format(this.getPrefix() + "[%s] ", LogColor.apply(LogColor.LIGHT_PURPLE, "DEBUG")) + message;
            LOGGER.info(getOutput(input));
        }
    }

    /**
     * Convenience overload method to output a debug message as a formatted string.
     *
     * @param message The debug message to log.
     * @param args    String formatting arguments.
     */
    public void debug(String message, Object... args) {
        if (this.isDebugMode())
            this.debug(String.format(message, args));
    }
}
