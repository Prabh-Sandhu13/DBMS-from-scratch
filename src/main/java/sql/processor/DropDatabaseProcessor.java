package sql.processor;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import logging.events.CrashListener;
import logging.events.QueryListener;
import sql.InternalQuery;

public class DropDatabaseProcessor implements IProcessor {
	static final Logger logger = LogManager.getLogger(DropDatabaseProcessor.class.getName());
    static final CrashListener crashListener = new CrashListener();
    static final QueryListener queryListener = new QueryListener();
    
	static DropDatabaseProcessor instance = null;
	private String username = null;
    private String database = null;
    boolean databaseExists = false;
    String BASE_PATH = "src/main/java/dataFiles/";
    String DB_PATH = "src/main/java/dataFiles/databases.json";
	public static DropDatabaseProcessor instance(){
        if(instance == null){
            instance = new DropDatabaseProcessor();
        }
        return instance;
    }
	@Override
    public boolean process(InternalQuery query, String username, String database) {
        this.username = username;
        this.database = database;
        boolean databaseExistsForUser = ifDatabaseExists(username, database);
        if(databaseExistsForUser) {
            Path path = Paths.get ("src/main/java/dataFiles/" + database);
        	//Path path = Path.of(BASE_PATH + database);
        	File dbPath = new File(path.toString());
        	dbPath.delete();
        	logger.info("Database "+database+ " dropped!");
        	return true;
        }      
        logger.info("Database "+database+ " not dropped!");
        return false;
    }
	private boolean ifDatabaseExists(String username, String database) {
        databaseExists = false;
        JSONParser parser = new JSONParser();
        logger.info("Checking if database exists!");
        try (FileReader reader = new FileReader(DB_PATH)) {
            //Read JSON file
            Object obj = parser.parse(reader);

            JSONArray dblist = (JSONArray) obj;
            System.out.println(dblist);

            dblist.forEach(db -> {
                System.out.println(db);
                if(((JSONObject) db).get("name").equals(database)) {
                    if (((JSONObject) db).get("username").equals( username)) {
                        databaseExists = true;
                    }
                }
            });
           return databaseExists;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            crashListener.recordEvent();
            return false;
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

}
