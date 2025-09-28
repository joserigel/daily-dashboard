package dailydashboard.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dailydashboard.Models.Expense;

public class SQL {
    private static Logger logger = LoggerFactory.getLogger(SQL.class);
    private static final String url = "jdbc:sqlite:dashboard.db";

    private static Connection connection;


    public static void init() throws SQLException  {
        SQL.connection = DriverManager.getConnection(SQL.url);
        Expense.init();
    }

    public static Connection getConnection()  {
        return SQL.connection;
    }

    public static void closeConnection() throws SQLException {
        SQL.connection.close();
        SQL.connection = null;
    }
}
