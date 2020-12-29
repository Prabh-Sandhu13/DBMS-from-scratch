package sql.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sql.InternalQuery;

public class UseParser implements IParser {
    static final Logger logger = LogManager.getLogger(UseParser.class.getName());
    static UseParser instance = null;

    public static UseParser instance(){
        if(instance == null){
            instance = new UseParser();
        }
        return instance;
    }

    @Override
    public InternalQuery parse(String query) {
        query = query.replaceAll(";", "");
        String[] sqlWords = query.split(" ");

        logger.info("Parsing Query:"+query);
        String action = sqlWords[0];
        String database = sqlWords[1];

        logger.info("Converting SQL query to internal query form");
        InternalQuery internalQuery = new InternalQuery();
        internalQuery.set("action",action);
        internalQuery.set("database",database);

        return internalQuery;
    }
}
