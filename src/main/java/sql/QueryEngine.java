package sql;

import sql.parser.*;
import sql.processor.*;


public class QueryEngine {
    private String database = null;

    public void run(String query, String username) {
        InternalQuery internalQuery = null;
        String action = query.replaceAll (" .*", "");
        action = action.toLowerCase ();
        boolean success = false;
        switch (action) {
            case "use":
                internalQuery = UseParser.instance ().parse (query);
                UseProcessor useProcessor = UseProcessor.instance ();
                useProcessor.process (internalQuery, username, database);
                this.database = useProcessor.getDatabase ();
                break;
            case "list":
                internalQuery = ListParser.instance ().parse (query);
                ListProcessor.instance ().process (internalQuery, username, database);
                break;
            case "create":
                internalQuery = CreateParser.instance ().parse (query);
                if (((String) internalQuery.get ("type")).equalsIgnoreCase ("database")) {
                    CreateProcessor.instance ().processCreateQuery (internalQuery, query, username, database);
                } else {
                    if (checkDbSelected ()) {
                        CreateProcessor.instance ().processCreateQuery (internalQuery, query, username, database);
                    }
                }
                break;
            case "insert":
                if (checkDbSelected ()) {
                    internalQuery = InsertParser.instance ().parse (query);
                    InsertProcessor.instance ().process (internalQuery, username, database);
                }
                break;
            case "select":
                if (checkDbSelected ()) {
                    internalQuery = SelectParser.instance ().parse (query);
                    SelectProcessor.instance ().process (internalQuery, username, database);
                }
                break;
            case "update":
                if (checkDbSelected ()) {
                    internalQuery = UpdateParser.instance ().parse (query);
                    if (internalQuery != null) {
                        UpdateProcessor.instance ().process (internalQuery, username, database);
                    }
                }
                break;
            case "delete":
                if (checkDbSelected ()) {
                    internalQuery = DeleteParser.instance ().parse (query);
                    if (internalQuery != null) {
                        DeleteProcessor.instance ().process (internalQuery, username, database);
                    }
                }
                break;
            case "drop":
                internalQuery = GeneralParser.instance ().parse (query);
                if (internalQuery.getSubject ().equalsIgnoreCase ("database")) {
                    DropDatabaseProcessor.instance ().process (internalQuery, username, internalQuery.getOption ());
                } else {
                    if (checkDbSelected ()) {
                        DropTableProcessor.instance ().process (internalQuery, username, database);
                    }
                }
                break;
            default:
                System.out.println ("invalid query!");
        }
    }

    private boolean checkDbSelected() {
        if (database == null) {
            System.out.println ("Please select a Database.");
            return false;
        } else {
            return true;
        }
    }
}
