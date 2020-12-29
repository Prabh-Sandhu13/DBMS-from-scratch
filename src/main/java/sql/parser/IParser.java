package sql.parser;

import sql.InternalQuery;

public interface IParser {
    InternalQuery parse(String query);
}
