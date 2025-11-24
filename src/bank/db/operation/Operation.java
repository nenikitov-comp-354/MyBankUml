package bank.db.operation;

import java.sql.Connection;
import java.sql.SQLException;

import bank.db.*;

public interface Operation {
    public void process(Connection connection, BankDb bankDb) throws SQLException;
}
