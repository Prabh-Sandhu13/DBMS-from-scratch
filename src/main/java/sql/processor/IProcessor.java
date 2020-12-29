package sql.processor;

import sql.InternalQuery;

public interface IProcessor {
    boolean process(InternalQuery query, String username, String database);
}
