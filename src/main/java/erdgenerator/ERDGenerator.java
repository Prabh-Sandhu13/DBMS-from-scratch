package erdgenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ERDGenerator {
	String BASE_PATH = "src/main/java/dataFiles/";
	String ERD_PATH = "src/main/java/erd/"; 
	String childtable="";
	String parentTable="";
	String refColumn ="";
	public void generateERD(String username, String database) {
		String[] pathnames;
		File db = new File(BASE_PATH+database);
        pathnames = db.list();
        JSONParser parser = new JSONParser();
        JSONArray tablelist = new JSONArray();
        for (String pathname : pathnames) {
        	try (FileReader reader = new FileReader(BASE_PATH+database+"/"+pathname)) {
                //Read JSON file
                JSONObject tableObj = (JSONObject) parser.parse(reader);
                tableObj.put("name",pathname.replaceAll(".json", ""));
                tablelist.add(tableObj);
        	 } catch (FileNotFoundException e) {
                 e.printStackTrace();
             } catch (IOException e) {
                 e.printStackTrace();
             } catch (ParseException e) {
                 e.printStackTrace();
             }
        }
        JSONArray erdtables = new JSONArray();
        JSONArray foreignkeys = new JSONArray();
        tablelist.forEach(table -> {
        	JSONObject tableName = new JSONObject();
        	JSONObject tableDetails = new JSONObject();
        	JSONArray relationships = new JSONArray();
        	JSONObject indexes = (JSONObject) ((JSONObject) table).get("indexes");
        	String primaryKey = "";
        	if (!(indexes.toString().equals("{}"))) {

        		for(Iterator iterator = indexes.keySet().iterator(); iterator.hasNext();) {
        			String key = (String) iterator.next();
        		    if (((JSONObject) indexes).get(key) instanceof JSONObject) {
        		    	JSONObject index = (JSONObject) ((JSONObject) indexes).get(key);
        		          if (index.get("type").equals("primary")) {
        		        	  primaryKey = key;
        		          }
        		          if (index.get("type").equals("foreign")) {
        		        	  JSONObject foreignKey = new JSONObject();
        		        	  foreignKey.put(((JSONObject) table).get("name"),index);
        		        	  tableDetails.put("foreignKey", index);
        		        	  foreignkeys.add(foreignKey);
        		          }
        		    }
        		}
        	}
        	tableDetails.put("columns",((JSONObject) table).get("columns"));
        	tableDetails.put("relationships",relationships);
        	tableDetails.put("primaryKey", primaryKey);
        	tableName.put(((JSONObject) table).get("name"),tableDetails);
        	erdtables.add(tableName);
        });
        
        foreignkeys.forEach(foreignIndex -> {
        	JSONObject relationship = new JSONObject();
        	parentTable = "";
        	childtable="";
        	refColumn ="";
        	for(Iterator iterator = ((JSONObject) foreignIndex).keySet().iterator(); iterator.hasNext();) {
        		childtable = (String) iterator.next();
        		JSONObject indexDetails = (JSONObject) ((JSONObject) foreignIndex).get(childtable);
        		parentTable = (String) ((JSONObject) indexDetails).get("refTable");
        		refColumn = (String) ((JSONObject) indexDetails).get("refColumn");
        	}
        	erdtables.forEach(tableObj -> {
        		for(Iterator iterator = ((JSONObject) tableObj).keySet().iterator(); iterator.hasNext();) {
	        		String	key = (String) iterator.next();
	        		JSONObject table = (JSONObject) ((JSONObject) tableObj).get(key);
	        		JSONArray relationships = new JSONArray();
	        		relationships=(JSONArray) ((JSONObject) table).get("relationships");

        			if(key.equalsIgnoreCase(parentTable)) {
        				JSONObject parentRelation = new JSONObject();
        				JSONObject parentRelationDetails = new JSONObject();
        				parentRelationDetails.put("type","childTable");
        				parentRelationDetails.put("refKey",refColumn);
        				parentRelation.put(childtable,parentRelationDetails);
        				relationships.add(parentRelation);
        			}
        			if(key.equalsIgnoreCase(childtable)) {
        				JSONObject childRelation = new JSONObject();
        				JSONObject childRelationDetails = new JSONObject();
        				childRelationDetails.put("type","parentTable");
        				childRelationDetails.put("refKey",refColumn);
        				childRelation.put(parentTable,childRelationDetails);
        				relationships.add(childRelation);
        			}
        			((JSONObject) table).put("relationships",relationships);
        			((JSONObject) tableObj).put(key,table);
        		}
        		
        		
        	});
        });
        writeERDFile(erdtables,database);
	}
	
	private void writeERDFile(JSONArray erdtables, String database) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String prettyERDString = gson.toJson(erdtables);
        try (FileWriter file = new FileWriter(ERD_PATH+database+"_ERD.json")) {
            file.write(prettyERDString);
            file.flush();
            System.out.println("ERD generated successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to Generate ERD!");
        }
    }

}
