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
import sql.parser.SelectParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

public class SelectProcessor implements IProcessor {
    static final Logger logger = LogManager.getLogger(SelectProcessor.class.getName());
    static final CrashListener crashListener = new CrashListener();
    static final QueryListener queryListener = new QueryListener();
    String BASE_PATH = "src/main/java/dataFiles/";
    String DB_PATH = "src/main/java/dataFiles/databases.json";
    static SelectProcessor instance = null;

    private String username = null;
    private String database = null;

    public static SelectProcessor instance(){
        if(instance == null){
            instance = new SelectProcessor();
        }
        return instance;
    }

    @Override
    public boolean process(InternalQuery query, String username, String database) {
        this.username = username;
        this.database = database;

        String table = (String) query.get("table");
        String[] columns = (String[]) query.get("columns");
        String conditions = (String) query.get("conditions");

        String path = BASE_PATH+database+"/"+table+".json";
        JSONObject jsonObject = readFile(path);
        JSONArray rows = (JSONArray) jsonObject.get("data");

        logger.info("Identifying requested columns");
        if(columns.length ==1 && columns[0].equals("*")) {
            JSONObject tblColumns = (JSONObject) jsonObject.get("columns");
            Set<String> keys = tblColumns.keySet();
            columns = new String[keys.size()];
            int index = 0;
            for (String str : keys){
                columns[index++] = str;
            }
        }

        logger.info("Identifying requested conditions");
        if(conditions.length() > 0){
            rows = filterRows(rows,conditions);
        }

        for (String column: columns) {
            System.out.print(column+" | ");
        }
        System.out.println();

        for(int i=0;i<rows.size();i++){
            JSONObject row = (JSONObject) rows.get(i);
            for (String column: columns) {
                column = column.replace(" ","");
                System.out.print(row.get(column)+" | ");
            }
            System.out.println();
        }
        System.out.println();

        queryListener.recordEvent();
        return true;
    }

    private JSONObject readFile(String path){
        JSONParser parser = new JSONParser();
        JSONObject obj = null;
        try(FileReader reader = new FileReader(path)){
            obj = (JSONObject) parser.parse(reader);
        } catch (ParseException e) {
            e.printStackTrace();
            crashListener.recordEvent();
        } catch (IOException e) {
            e.printStackTrace();
            crashListener.recordEvent();
        }
        return obj;
    }

    private JSONArray filterRows(JSONArray rows, String conditions){
        JSONArray filteredRows = new JSONArray();
        for(int i=0;i<rows.size();i++){
            JSONObject row = (JSONObject) rows.get(i);
            String[] andConditions = conditions.split(" and ");
            int conditionsSatisfied =0;

            for(int j=0; j<andConditions.length; j++){
                String[] orConditions = andConditions[j].split(" or ");
                boolean satisfied = false;
                for(int k=0; k<orConditions.length; k++){
                    String condition = orConditions[k];
                    if(condition.contains("!=")){
                        String[] conditionParts = condition.split("!=");
                        String column = conditionParts[0];
                        String value = conditionParts[1].replace("'","").replace("\"","");
                        if(!row.get(column).equals(value)){
                            satisfied = true;
                        }
                    }else if(condition.contains("=")){
                        String[] conditionParts = condition.split("=");
                        String column = conditionParts[0];
                        String value = conditionParts[1].replace("'","").replace("\"","");
                        if(row.get(column).equals(value)){
                            satisfied = true;
                        }
                    }else if(condition.contains(" like ")){
                        String[] conditionParts = condition.split(" like ");
                        String column = conditionParts[0];
                        String regex = conditionParts[1].replace("'","").replace("\"","");;
                        String value = (String) row.get(column);
                        if(value.matches(regex)){
                            satisfied = true;
                        }
                    }
                }
                if(satisfied){
                    conditionsSatisfied++;
                }
            }
            if(conditionsSatisfied == andConditions.length){
                filteredRows.add(row);
            }
        }
        return filteredRows;
    }
}
