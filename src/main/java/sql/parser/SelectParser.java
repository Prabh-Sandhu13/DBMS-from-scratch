package sql.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sql.InternalQuery;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectParser implements IParser {
    static final Logger logger = LogManager.getLogger(SelectParser.class.getName());
    static SelectParser instance = null;

    public static SelectParser instance(){
        if(instance == null){
            instance = new SelectParser();
        }
        return instance;
    }

    @Override
    public InternalQuery parse(String query) {
        query = query.replace(";", "");
        query = query+";";
        Pattern pattern = Pattern.compile("select\\s+(.*?)\\s*from\\s+(.*?)\\s*(where\\s(.*?)\\s*)?;", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(query);
        matcher.find();

        logger.info("Parsing Query:"+query);
        String[] columns = (String[]) matcher.group(1).split(",");
        String table = matcher.group(2);
        String conditions = matcher.group(4);

        logger.info("Converting SQL query to internal query form");
        InternalQuery internalQuery = new InternalQuery();
        internalQuery.set("columns",columns);
        internalQuery.set("table",table);
        internalQuery.set("conditions",conditions);

        return internalQuery;
    }
}
