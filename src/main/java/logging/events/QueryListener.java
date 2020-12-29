package logging.events;

import java.util.logging.Logger;

public class QueryListener implements IEventListener {
    Logger logger = Logger.getLogger(QueryListener.class.getName());

    @Override
    public void recordEvent() {
        logger.info("Query executed");
    }
}
