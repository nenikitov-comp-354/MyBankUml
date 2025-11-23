package bank.db.operation;

import java.sql.Connection;
import java.sql.SQLException;

public interface Operation {
    public void process(Connection connection) throws SQLException;
}
