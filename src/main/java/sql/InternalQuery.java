package sql;

import java.util.HashMap;
import java.util.Map;

public class InternalQuery {
    private Map<String, Object> objectMap = new HashMap<> ();

    private String action = "";
    private String tableName = "";
    private String subject = "";
    private String option = "";
    private String condition = "";

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        if (subject == null) {
            subject = "";
        }
        this.subject = subject;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        if (option == null) {
            option = "";
        }
        this.option = option;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        if (condition == null) {
            condition = "";
        }
        this.condition = condition;
    }

    public Object get(String key) {
        return objectMap.get (key);
    }

    public void set(String key, Object value) {
        if (value == null) {
            value = "";
        }
        objectMap.put (key, value);
    }
}
