package sql.processor;

import sql.InternalQuery;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DropTableProcessor implements IProcessor {
    String BASE_PATH = "src/main/java/dataFiles/";
    static DropTableProcessor instance = null;

    private String username = null;
    private String database = null;

    public static DropTableProcessor instance() {
        if (instance == null) {
            instance = new DropTableProcessor ();
        }
        return instance;
    }

    @Override
    public boolean process(InternalQuery query, String username, String database) {
        this.username = username;
        this.database = database;

        String subject = query.getOption().trim();
        Path path = Paths.get (BASE_PATH + database + "/" + subject + ".json");
        try (FileReader reader = new FileReader (path.toString ())) {
            File tablePath = new File (path.toString ());
            //Not working
            tablePath.delete();
            boolean ifDelete = tablePath.delete ();
            return true;
        } catch (FileNotFoundException e) {
            System.out.println ("Table not found !!");
        } catch (IOException e) {
            e.printStackTrace ();
        }
        return false;
    }
}
