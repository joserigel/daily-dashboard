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

    /**
     * Init SQL Database
     * This initializes all Models and tables in SQLite
     * @throws SQLException
     */
    public static void init() throws SQLException  {
        Expense.init();
        Schedule.init();
    }

    /**
     * Get Connection of the database
     * @return SQLite Connection of the App
     * @throws SQLException if an SQL Exception occurs
     */
    public static Connection getConnection() throws SQLException  {
        return DriverManager.getConnection(SQL.url);
    }
}
