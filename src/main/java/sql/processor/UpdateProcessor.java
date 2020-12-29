package sql.processor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import logging.events.CrashListener;
import logging.events.QueryListener;
import sql.InternalQuery;

import java.io.*;
import java.util.Set;

public class UpdateProcessor implements IProcessor {
	static final Logger logger = LogManager.getLogger(UpdateProcessor.class.getName());
    static final CrashListener crashListener = new CrashListener();
    static final QueryListener queryListener = new QueryListener();
    
    String BASE_PATH = "src/main/java/dataFiles/";
    static UpdateProcessor instance = null;

    private String username = null;
    private String database = null;

    public static UpdateProcessor instance() {
        if (instance == null) {
            instance = new UpdateProcessor ();
        }
        return instance;
    }

    @Override
    public boolean process(InternalQuery query, String username, String database) {
        this.username = username;
        this.database = database;
        int columnFlag = 0;
        int conditionFlag = 0;

        logger.info("Identifying requested columns");
        String table = query.getTableName ().trim ();
        String y = query.getOption ().replaceAll ("[^a-zA-Z]", " ");
        String[] columnValue = y.split (" ");

        String x = query.getCondition ().replaceAll ("[^a-zA-Z1-9]", " ");
        String[] conditions = x.split (" ");

        String path = BASE_PATH + database + "/" + table + ".json";

        JSONParser parser = new JSONParser ();
        try (FileReader reader = new FileReader (path)) {

            Object obj = parser.parse (reader);
            JSONObject jsonObject = (JSONObject) obj;

            String[] columnInTable;
            JSONObject tblColumns = (JSONObject) jsonObject.get ("columns");
            Set<String> keys = tblColumns.keySet ();
            columnInTable = new String[keys.size ()];
            int index = 0;
            for (String str : keys) {
                columnInTable[index++] = str.toLowerCase ();
            }

            //Check if column names in the query is valid
            for (String column : columnInTable) {
                if (column.equals (columnValue[0])) {
                    columnFlag = 1;
                }
                if (column.equals (conditions[0])) {
                    conditionFlag = 1;
                }
            }
            logger.info("Identifying requested conditions");
            if (columnFlag == 1 && conditionFlag == 1) {
                JSONArray data = (JSONArray) jsonObject.get ("data");
                for (int i = 0; i < data.size (); i++) {
                    JSONObject row = (JSONObject) data.get (i);
                    if (row.get (conditions[0]).toString ().equals (conditions[1].trim ())) {
                        row.remove (columnValue[0]);
                        row.put (columnValue[0], columnValue[2]);

                        try (Writer out = new FileWriter (path)) {
                            out.write (jsonObject.toJSONString ());
                        } catch (IOException e) {
                            e.printStackTrace ();
                        }
                        System.out.println ("Update done successfully !!");
                        return true;
                    }
                }

            } else {
                System.out.println ("Invalid column name !!");
                return false;
            }
        } catch (FileNotFoundException e) {
            System.out.println ("Table not found !!!");
            crashListener.recordEvent();
        } catch (ParseException e) {
            e.printStackTrace ();
            crashListener.recordEvent();
        } catch (IOException e) {
            e.printStackTrace ();
            crashListener.recordEvent();
        }
        System.out.println ("Sorry wrong condition !!");
        return false;
    }

}
