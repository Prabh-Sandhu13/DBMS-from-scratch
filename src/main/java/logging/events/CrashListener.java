package logging.events;

import java.util.logging.Logger;

public class CrashListener implements IEventListener {
    Logger logger = Logger.getLogger(CrashListener.class.getName());

    @Override
    public void recordEvent() {
        logger.info("System crashed");
    }
}
