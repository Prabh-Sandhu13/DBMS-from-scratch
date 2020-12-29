package sql.processor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import logging.events.CrashListener;
import logging.events.DatabaseListener;
import logging.events.QueryListener;
import sql.InternalQuery;

import java.io.*;
import java.sql.Timestamp;
import java.util.Arrays;

public class CreateProcessor implements IProcessor {
	static final Logger logger = LogManager.getLogger(CreateProcessor.class.getName());
    static final CrashListener crashListener = new CrashListener();
    static final DatabaseListener databaseListener = new DatabaseListener();
    static final QueryListener queryListener = new QueryListener();
    
    String BASE_PATH = "src/main/java/dataFiles/";
    String DB_PATH = "src/main/java/dataFiles/databases.json";
    static CreateProcessor instance = null;

    private boolean databaseExists = false;
    private String username = null;
    private String database = null;

    public static CreateProcessor instance(){
        if(instance == null){
            instance = new CreateProcessor();
        }
        return instance;
    }

    
    public boolean processCreateQuery(InternalQuery internalQuery, String query, String username, String database) {
        this.username = username;
        this.database = database;
        logger.info("Checking if database exists!");
        if(internalQuery.get("type").equals("database")){
            return createDB(internalQuery);
        }else{
            return createTable(internalQuery,query, username, database);
        }
    }

    private boolean createDB(InternalQuery internalQuery) {
        String name = (String) internalQuery.get("name");
        String path = BASE_PATH + name;
        File file = new File(path);
        System.out.println(path);
        boolean bool = file.mkdir();
        if(bool){
            System.out.println("DB created successfully");
            logger.info("DB "+name+" created successfully!");
            databaseListener.recordEvent();
            parseDBFile(name);
        }else{
            System.out.println("Sorry couldn’t create DB");
            crashListener.recordEvent();
        }
        return true;
    }

    private boolean createTable(InternalQuery internalQuery, String query, String username, String database) {
    	query = query.replaceAll(";", "");
        query = query.replaceAll(",", " ");
        query = query.replaceAll("[^a-zA-Z ]", "");
        String[] sqlWords = query.split(" ");
        int primaryIndex = sqlWords.length-1;
        int foreignIndex = 0;
        
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        JSONObject colObj = new JSONObject();
        JSONObject meta = new JSONObject();
        JSONObject indexes = new JSONObject();
        JSONArray  data = new JSONArray();
        
        meta.put("rows", 0);
        meta.put("createdAt", timestamp.toString());
        
        if(query.toLowerCase().contains("primary key")) {
        	for(int i = 0; i< sqlWords.length; i++) {
        		if(sqlWords[i].equalsIgnoreCase("primary")) {
        			primaryIndex = i;
        			break;
        		}
        	}
        	String[] primaryKeys = Arrays.copyOfRange(sqlWords, primaryIndex, primaryIndex+3);
        	JSONObject primaryJson = new JSONObject();
        	primaryJson.put("type","primary");
        	indexes.put(primaryKeys[2],primaryJson);
        }
        if(query.toLowerCase().contains("foreign key")) {
        	for(int i = 0; i< sqlWords.length; i++) {
        		if(sqlWords[i].equalsIgnoreCase("foreign")) {
        			foreignIndex = i;
        			break;
        		}
        	}
        	String[] foreignKeys = Arrays.copyOfRange(sqlWords, foreignIndex, sqlWords.length);
        	JSONObject foreignJson = new JSONObject();
        	foreignJson.put("type","foreign");
        	foreignJson.put("refTable",foreignKeys[4]);
        	foreignJson.put("refColumn",foreignKeys[5]);
        	indexes.put(foreignKeys[2],foreignJson);
        }
        sqlWords = Arrays.copyOfRange(sqlWords, 0, primaryIndex);
        System.out.println(sqlWords);
        
        for(int i = 3; i< sqlWords.length; i+=2) {
        	colObj.put(sqlWords[i], sqlWords[i+1]);
        }
        logger.info("Adding indexes to table!");
        logger.info("Adding columns to table!");
        String tableName = (String) internalQuery.get("name");
        JSONObject tableObj = new JSONObject();
        
        tableObj.put("columns",colObj);
        tableObj.put("meta",meta);
        tableObj.put("indexes",indexes);
        tableObj.put("data",data);
        
        
        
		try (FileWriter file = new FileWriter(BASE_PATH + database +"/"+tableName+".json")) {
		    file.write(tableObj.toJSONString());
		    file.flush();
		    logger.info("Table "+tableName+" created successfully!");
		    databaseListener.recordEvent();
		} catch (IOException e) {
		    e.printStackTrace();
		    crashListener.recordEvent();
		}
        return true;
    }

    private void parseDBFile(String name) {
        databaseExists = false;
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(DB_PATH)) {
            //Read JSON file
            Object obj = parser.parse(reader);

            JSONArray dblist = (JSONArray) obj;
            System.out.println(dblist);

            dblist.forEach(db -> {
                System.out.println(db);
                if(((JSONObject) db).get("name") == name) {
                    if (((JSONObject) db).get("username") == username) {
                        databaseExists = true;
                    }
                }
            });
            if(!databaseExists) {
                JSONObject dbObj = new JSONObject();
                dbObj.put("name", name);
                dbObj.put("username", username);
                dblist.add(dbObj);
                writeDBFile(dblist);
                databaseListener.recordEvent();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            crashListener.recordEvent();
        } catch (IOException e) {
            e.printStackTrace();
            crashListener.recordEvent();
        } catch (ParseException e) {
            e.printStackTrace();
            crashListener.recordEvent();
        }
    }

    private void writeDBFile(JSONArray dblist) {
        try (FileWriter file = new FileWriter(DB_PATH)) {
            file.write(dblist.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


	@Override
	public boolean process(InternalQuery query, String username, String database) {
		// TODO Auto-generated method stub
		return false;
	}


}
