package dailydashboard.Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.json.simple.JSONObject;

import dailydashboard.Database.SQL;

public class Expense {
    private Optional<Integer> id;
    private long amount;
    private String category;
    private Optional<String> description;
    private Optional<Timestamp> createdAt;
    private Optional<Timestamp> modifiedAt;

    private Expense(int id, long amount, String category, Optional<String> description, 
        Timestamp createdAt, Timestamp modifiedAt) {
        this.id = Optional.of(id);
        this.amount = amount;
        this.category = category;
        this.createdAt = Optional.of(createdAt);
        this.description = description;
        this.modifiedAt = Optional.of(modifiedAt);
    }

    public Expense(long amount, String category, Optional<String> description) throws Exception {
        if (category.length() > 255) {
            throw new Exception("category cannot exceed a length of 255!");
        }

        if (description.isPresent() && description.get().length() > 255) {
            throw new Exception("description cannot a length of 255!");
        }
         this.amount = amount;
         this.category = category;
         this.description = description;

         this.createdAt = Optional.empty();
         this.modifiedAt = Optional.empty();
         this.id = Optional.empty();
    }

    public static void init() throws SQLException {
        Statement statement = SQL.getConnection().createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS expenses(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "amount BIGINT NOT NULL," +
            "category VARCHAR(255) NOT NULL," +
            "description VARCHAR(255)," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
        ")");
    }

    public static int deleteById(int id) throws SQLException {
        Connection conn = SQL.getConnection();
        PreparedStatement query = conn.prepareStatement("DELETE FROM expenses WHERE id = ?");
        query.setInt(1, id);

        int res = query.executeUpdate();
        return res;
    }

    public static HashMap<String, Long> getPerCategory(Timestamp start, Timestamp end) throws SQLException {
        Connection conn = SQL.getConnection();
        PreparedStatement query = conn.prepareStatement(
            "SELECT category, SUM(amount) AS amount FROM expenses " +
            "WHERE DATE(created_at) BETWEEN DATE(?) AND DATE(?) " +
            "GROUP BY category" 
        );
        query.setString(1, start.toString());
        query.setString(2, end.toString());

        ResultSet rs = query.executeQuery();
        HashMap<String, Long> result = new HashMap<String, Long>();
        while (rs.next()) {
            String category = rs.getString("category");
            long amount = rs.getLong("amount");
            result.put(category, amount);
        }
        
        return result;
    }

    public static ArrayList<Expense> getExpenses(int page, int size) throws Exception {
        int offset = page * size;
        
        Connection conn = SQL.getConnection();
        PreparedStatement expensesQuery = conn.prepareStatement(
            "SELECT id, amount, category, description, created_at, modified_at " +
            "FROM expenses " + 
            "ORDER BY created_at DESC " +
            "LIMIT ? OFFSET ?");
        expensesQuery.setInt(1, size);
        expensesQuery.setInt(2, offset);
        ResultSet rs = expensesQuery.executeQuery();
        
        ArrayList<Expense> paginatedExpenses = new ArrayList<Expense>();
        while (rs.next()) {
            int id = rs.getInt("id");
            long amount = rs.getLong("amount");
            String category = rs.getString("category");
            String description = rs.getString("description");
            Timestamp createdAt = rs.getTimestamp("created_at");
            Timestamp modifiedAt = rs.getTimestamp("modified_at");
 
            Expense exp = new Expense(
                id, amount, category, 
                description == null ? Optional.empty() : Optional.of(description), 
                createdAt,  modifiedAt
            );
            paginatedExpenses.add(exp);
        }

        return paginatedExpenses;
    }

    public void insert() throws Exception {
        if (id.isPresent()) {
            throw new Exception("Already inserted in the database");
        }

        Connection conn = SQL.getConnection();
        PreparedStatement expensesQuery = conn.prepareStatement(
            "INSERT INTO " +
            "expenses(amount,category,description) " +
            "VALUES(?,?,?) RETURNING *;");
        expensesQuery.setLong(1, this.amount);
        expensesQuery.setString(2, this.category);
        expensesQuery.setString(3, this.description.isPresent() ? this.description.get() : null);
        
        ResultSet rs = expensesQuery.executeQuery();

        if (rs.next()) {
            this.id = Optional.of(rs.getInt("id"));
            this.amount = rs.getLong("amount");
            this.category = rs.getString("category");

            String description = rs.getString("description");
            if (description != null) {
                this.description = Optional.of(description);
            }

            this.createdAt = Optional.of(rs.getTimestamp("created_at"));
            this.modifiedAt = Optional.of(rs.getTimestamp("modified_at"));
        }
    }

    @SuppressWarnings("unchecked")
    public JSONObject asJSON() {
        JSONObject obj = new JSONObject();
        if (this.id.isPresent()) {
            obj.put("id", this.id);
        }

        obj.put("amount", this.amount);
        obj.put("category", this.category);

        if (this.description.isPresent()) {
            obj.put("description", this.description.get());
        }

        if (this.createdAt.isPresent()) {
            obj.put("created_at", this.createdAt.get());
        }

        if (this.modifiedAt.isPresent()) {
            obj.put("modified_at", this.createdAt.get());
        }
        return obj;
    }

}
