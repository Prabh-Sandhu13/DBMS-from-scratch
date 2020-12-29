package SQLDump;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLDump {
    String BASE_PATH = "src/main/java/dataFiles/";
    String ERD_PATH = "src/main/java/SQLDump/";
    ArrayList<String> sqlDump = new ArrayList<> ();

    public void generateSQLDump(String username, String database) {
        String query = "";
        File databaseSelected = new File (BASE_PATH + database);
        String[] tableList = databaseSelected.list ();
        JSONParser parser = new JSONParser ();
        for (String tableName : tableList) {
            Pattern pattern = Pattern.compile (".*(?=\\.)");
            Matcher matcher = pattern.matcher (tableName);
            if (matcher.find ()) {
                String name = matcher.group (0);
                query = "create table " + name + " (";
            }
            try (FileReader reader = new FileReader (BASE_PATH + database + "/" + tableName)) {
                JSONObject tableObj = (JSONObject) parser.parse (reader);
                JSONObject tblColumns = (JSONObject) tableObj.get ("columns");
                Set<String> keys = tblColumns.keySet ();

                for (String str : keys) {
                    Object values = tblColumns.get (str);
                    query = query + str + " " + values + ", ";
                }

                JSONObject indexes = (JSONObject) tableObj.get ("indexes");
                if (!(indexes.toString ().equals ("{}"))) {

                    for (Iterator iterator = indexes.keySet ().iterator (); iterator.hasNext (); ) {
                        String key = (String) iterator.next ();
                        if (((JSONObject) indexes).get (key) instanceof JSONObject) {
                            JSONObject index = (JSONObject) ((JSONObject) indexes).get (key);
                            if (index.get ("type").equals ("primary")) {
                                query = query + "PRIMARY KEY (" + key + "), ";
                            }
                            if (index.get ("type").equals ("foreign")) {
                                Object refTable = index.get ("refTable");
                                query = query + "FOREIGN KEY (" + key + ") REFERENCES " + index.get ("refTable") + " (" + index.get ("refColumn") + "), ";
                            }
                        }
                    }
                }
                query = query.substring (0, query.lastIndexOf (",") - 0);
                query = query + ");";
                sqlDump.add (query);
            } catch (FileNotFoundException e) {
                e.printStackTrace ();
            } catch (IOException e) {
                e.printStackTrace ();
            } catch (ParseException e) {
                e.printStackTrace ();
            }
        }
        writeDump (sqlDump, database);
    }

    private void writeDump(ArrayList<String> sqlDump, String database) {
        try (FileWriter file = new FileWriter (ERD_PATH + database + "_SQLDump.sql")) {
            for (int i = 0; i < sqlDump.size (); i++) {
                file.write (sqlDump.get (i));
                file.append ("\n");
            }
            file.flush ();
            System.out.println ("SQL Dump generated successfully!");
        } catch (IOException e) {
            e.printStackTrace ();
            System.out.println ("Failed to Generate SQL Dump!");
        }
    }
}
