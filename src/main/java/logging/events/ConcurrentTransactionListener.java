package logging.events;

import java.util.logging.Logger;

public class ConcurrentTransactionListener implements IEventListener {
    Logger logger = Logger.getLogger(ConcurrentTransactionListener.class.getName());

    @Override
    public void recordEvent() {
        logger.info("Concurrent transaction performed");
    }
}
