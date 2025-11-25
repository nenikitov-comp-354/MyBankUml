package bank.db.operation;

import bank.db.*;
import java.sql.Connection;
import java.sql.SQLException;

public interface Operation {
    public void process(Connection connection, BankDb bankDb)
        throws SQLException;
}
