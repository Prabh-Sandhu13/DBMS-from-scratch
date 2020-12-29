package datadictionarygenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DataDictionaryGenerator {
    String BASE_PATH = "src/main/java/dataFiles/";
    String OUTPUT_PATH = "src/main/java/datadictionary/";

    public void generate(String username, String database) {
        File db = new File(BASE_PATH+database);
        String[] tables = db.list();

        JSONArray arr = new JSONArray();
        for (int i=0; i< tables.length; i++) {
            String tablePath = BASE_PATH+database+"/"+tables[i];
            JSONObject tableObj = readFile(tablePath);
            JSONObject tableCols = (JSONObject) tableObj.get("columns");

            JSONObject entity = new JSONObject();
            entity.put("entity", tables[i].replace(".json",""));
            entity.put("items", tableCols);
            arr.add(entity);
        }
        try {
            writeFile(arr,database);
            System.out.println("Datadictionary for "+database+" created");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Datadictionary creation failed");
        }
    }

    private JSONObject readFile(String path){
        JSONParser parser = new JSONParser();
        JSONObject obj = null;
        try(FileReader reader = new FileReader(path)){
            obj = (JSONObject) parser.parse(reader);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    }

    private void writeFile(JSONArray arr, String database) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJsonString = gson.toJson(arr);
        File file = new File(OUTPUT_PATH, database+".json");
        FileWriter fw;
        if (file.exists()) {
            fw = new FileWriter(file,false);
        }
        else {
            file.createNewFile();
            fw = new FileWriter(file);
        }
        fw.write(prettyJsonString);
        fw.flush();
    }
}
