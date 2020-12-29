package sql.processor;

import logging.events.CrashListener;
import logging.events.DatabaseListener;
import logging.events.QueryListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sql.InternalQuery;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ListProcessor implements IProcessor {
    static final Logger logger = LogManager.getLogger(ListProcessor.class.getName());
    static final CrashListener crashListener = new CrashListener();
    static final QueryListener queryListener = new QueryListener();

    String BASE_PATH = "src/main/java/dataFiles/";
    String DB_PATH = "src/main/java/dataFiles/databases.json";
    static ListProcessor instance = null;

    private boolean databaseExists = false;
    private String username = null;
    private String database = null;

    public static ListProcessor instance(){
        if(instance == null){
            instance = new ListProcessor();
        }
        return instance;
    }

    @Override
    public boolean process(InternalQuery internalQuery, String username, String database) {
        this.username = username;
        this.database = database;

        if(internalQuery.get("subject").equals("databases")){
            return listDatabases(internalQuery);
        }else if(internalQuery.get("subject").equals("tables")){
            return listTables(internalQuery);
        }else{
            System.out.println("Undefined subject.");
            return false;
        }
    }

    private boolean listDatabases(InternalQuery internalQuery){
        logger.info("Identifying databases");
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(DB_PATH)) {
            Object obj = parser.parse(reader);
            JSONArray dblist = (JSONArray) obj;
            dblist.forEach(db -> {
                JSONObject row = (JSONObject)db;
                System.out.println(row.get("name"));
            });
            queryListener.recordEvent();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            crashListener.recordEvent();
            return false;
        } catch (ParseException e) {
            e.printStackTrace();
            crashListener.recordEvent();
            return false;
        }
    }

    private boolean listTables(InternalQuery internalQuery){
        if(database == null) {
            System.out.println("Please select a Database to list Tables.");
            return false;
        }else {
            logger.info("Identifying tables in the select database");
            File dbPath = new File(BASE_PATH + database);
            String tables[] = dbPath.list();
            for (int i = 0; i < tables.length; i++) {
                System.out.println(tables[i].replace(".json", ""));
            }
            queryListener.recordEvent();
            return true;
        }
    }
}
