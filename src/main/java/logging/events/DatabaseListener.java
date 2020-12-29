package logging.events;

import java.util.logging.Logger;

public class DatabaseListener implements IEventListener {
    Logger logger = Logger.getLogger(DatabaseListener.class.getName());

    @Override
    public void recordEvent() {
        logger.info("Database changes made");
    }
}
