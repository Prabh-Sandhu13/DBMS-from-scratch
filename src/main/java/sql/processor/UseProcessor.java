package sql.processor;

import logging.events.CrashListener;
import logging.events.DatabaseListener;
import logging.events.QueryListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import sql.InternalQuery;
import sql.parser.UseParser;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UseProcessor implements IProcessor {
    static final Logger logger = LogManager.getLogger(UseProcessor.class.getName());
    static final QueryListener queryListener = new QueryListener();
    String BASE_PATH = "src/main/java/dataFiles/";
    String DB_PATH = "src/main/java/dataFiles/databases.json";
    static UseProcessor instance = null;

    private String username = null;
    private String database = null;

    public static UseProcessor instance(){
        if(instance == null){
            instance = new UseProcessor();
        }
        return instance;
    }

    public String getDatabase() {
        return database;
    }

    @Override
    public boolean process(InternalQuery query, String username, String database) {
        this.username = username;
        this.database = database;


        String newDatabase = (String) query.get("database");
        boolean dbExists = false;
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(DB_PATH)) {
            Object obj = parser.parse(reader);
            JSONArray dblist = (JSONArray) obj;
            for(Object db : dblist){
                JSONObject row = (JSONObject) db;
                if(row.get("name").equals(newDatabase)){
                    if(!row.get("username").equals(username)){
                        System.out.println("Database doesn't belongs to you.");
                        return false;
                    }
                    dbExists = true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if(!dbExists){
            System.out.println("Database doesn't exist.");
            return false;
        }

        if(this.database == null){
            logger.info("Selecting database '"+ newDatabase+"'");
        }else{
            logger.info("Changing database from '"+ this.database +"' to '"+ newDatabase+"'" );
        }
        this.database = newDatabase;
        String origPath = new File("").getAbsolutePath();
        Path path = Paths.get ("src/main/java/dataFiles/" + database);
        //Path path = Path.of(origPath+"/"+BASE_PATH + this.database);
        if (Files.exists(path)) {
            queryListener.recordEvent();
            return true;
        }else{
            System.out.println("Database not found.");
            return false;
        }
    }
}
