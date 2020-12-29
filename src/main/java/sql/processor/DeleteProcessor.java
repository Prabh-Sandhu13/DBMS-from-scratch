package sql.processor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sql.InternalQuery;

import java.io.*;
import java.util.Set;

public class DeleteProcessor implements IProcessor {
    String BASE_PATH = "src/main/java/dataFiles/";
    static DeleteProcessor instance = null;

    private String username = null;
    private String database = null;

    public static DeleteProcessor instance() {
        if (instance == null) {
            instance = new DeleteProcessor ();
        }
        return instance;
    }

    @Override
    public boolean process(InternalQuery query, String username, String database) {
        this.username = username;
        this.database = database;
        int conditionFlag = 0;

        String table = query.getTableName ().trim ();

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
                if (column.equals (conditions[0])) {
                    conditionFlag = 1;
                }
            }

            if (conditionFlag == 1) {
                JSONArray data = (JSONArray) jsonObject.get ("data");
                for (int i = 0; i < data.size (); i++) {
                    JSONObject row = (JSONObject) data.get (i);
                    if (row.get (conditions[0]).toString ().equals (conditions[1].trim ())) {
                        data.remove (i);
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
            System.out.println ("Table not found !!");
        } catch (ParseException e) {
            e.printStackTrace ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
        System.out.println ("Sorry wrong condition !!");
        return false;
    }

}
