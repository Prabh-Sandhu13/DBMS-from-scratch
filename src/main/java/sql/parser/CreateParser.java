package sql.parser;

import sql.InternalQuery;

public class CreateParser implements IParser {
    static CreateParser instance = null;

    public static CreateParser instance(){
        if(instance == null){
            instance = new CreateParser();
        }
        return instance;
    }

    @Override
    public InternalQuery parse(String query) {
        query = query.replaceAll(";", "");
        query = query.replaceAll(",", " ");
        query = query.replaceAll("[^a-zA-Z ]", "");
        String[] sqlWords = query.split(" ");

        String action = sqlWords[0];
        String type = sqlWords[1];
        String name = sqlWords[2];

        InternalQuery internalQuery = new InternalQuery();
        internalQuery.set("action", action);
        internalQuery.set("type", type);
        internalQuery.set("name", name);

        return internalQuery;
    }
}
