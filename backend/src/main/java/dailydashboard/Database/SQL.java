package dailydashboard.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dailydashboard.Models.Expense;
import dailydashboard.Models.Schedule;

public class SQL {
    private static Logger logger = LoggerFactory.getLogger(SQL.class);
    private static final String url = "jdbc:sqlite:dashboard.db";

    private static Connection connection;

    /**
     * Init SQL Database
     * This initializes all Models and tables in SQLite
     * @throws SQLException
     */
    public static void init() throws SQLException  {
        SQL.connection = DriverManager.getConnection(SQL.url);
        Expense.init();
        Schedule.init();
    }

    /**
     * Get Connection of the database
     * @return SQLite Connection of the App
     */
    public static Connection getConnection()  {
        return SQL.connection;
    }

    /**
     * Closes and Commits the SQLite Connection
     * @throws SQLException
     */
    public static void closeConnection() throws SQLException {
        SQL.connection.close();
        SQL.connection = null;
    }
}
