package sql.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sql.InternalQuery;

public class GeneralParser implements IParser{
	static final Logger logger = LogManager.getLogger(GeneralParser.class.getName());
	static GeneralParser instance = null;

    public static GeneralParser instance(){
        if(instance == null){
            instance = new GeneralParser();
        }
        return instance;
    }

    @Override
    public InternalQuery parse(String query) {
        query = query.replaceAll(";", "");
        query = query.replaceAll(",", " ");
        query = query.replaceAll("[^a-zA-Z ]", "");
        String[] sqlWords = query.split(" ");

        logger.info("Parsing Query:"+query);
        String action = sqlWords[0];
        String subject = sqlWords[1];
        String option = sqlWords[2];

        logger.info("Converting SQL query to internal query form");
        InternalQuery internalQuery = new InternalQuery();
        internalQuery.setAction(action);
        internalQuery.setSubject(subject);
        internalQuery.setOption(option);

        return internalQuery;
    }

}
