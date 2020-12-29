package sql.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sql.InternalQuery;

public class ListParser implements IParser {
    static final Logger logger = LogManager.getLogger(ListParser.class.getName());
    static ListParser instance = null;

    public static ListParser instance(){
        if(instance == null){
            instance = new ListParser();
        }
        return instance;
    }

    @Override
    public InternalQuery parse(String query) {
        query = query.replaceAll(";", "");
        String[] sqlWords = query.split(" ");

        logger.info("Parsing Query:"+query);
        String action = sqlWords[0];
        String subject = sqlWords[1];

        logger.info("Converting SQL query to internal query form");
        InternalQuery internalQuery = new InternalQuery();
        internalQuery.set("action",action);
        internalQuery.set("subject",subject);

        return internalQuery;
    }
}
