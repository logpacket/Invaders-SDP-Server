package engine;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Implements a simple logging format.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class MinimalFormatter extends Formatter {

    /** Format for the date. */
    private final DateFormat format = new SimpleDateFormat("h:mm:ss");
    /** System line separator. */
    private final String lineSeparator = System.lineSeparator();

    @Override
    public final String format(final LogRecord logRecord) {

        return "[" +
                logRecord.getLevel() + '|' +
                format.format(new Date(logRecord.getMillis())) +
                "]: " + logRecord.getMessage() + ' ' +
                lineSeparator;
    }
}
