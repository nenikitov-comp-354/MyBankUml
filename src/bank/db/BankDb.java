package bank.db;

import java.sql.*;
import java.util.*;

public class BankDb {
    private Connection connection;

    public BankDb(String host, Optional<Integer> port, String database, String user, Optional<String> password)
            throws SQLException {
        String url = "jdbc:postgresql://" + host + port.map((p) -> p.toString()).orElse("") + "/" + database;

        Properties props = new Properties();
        props.setProperty("user", user);
        if (password.isPresent()) {
            props.setProperty("password", password.get());
        }

        this.connection = DriverManager.getConnection(url, props);
    }
}
