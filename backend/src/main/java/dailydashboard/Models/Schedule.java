package dailydashboard.Models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

import org.json.simple.JSONObject;

import dailydashboard.Database.SQL;

public class Schedule {
    private Optional<Integer> id;
    private LocalTime start;
    private LocalTime end;
    private DayOfWeek dayOfWeek;
    private String description;
    private Optional<Timestamp> createdAt;
    private Optional<Timestamp> modifiedAt;

    /**
     * Initializes the Schedule Model
     * Creates the <code>schedule</code> table in SQLite, if the table has not yet existed
     * @throws SQLException If an SQL Exception occurs
     */
    public static void init() throws SQLException {
        Statement statement = SQL.getConnection().createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS schedule(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "start CHAR(8) NOT NULL, " + 
            "end CHAR(8) NOT NULL, " + 
            "day_of_week TINYINT NOT NULL CHECK(day_of_week >= 1 AND day_of_week <= 7), " +
            "description VARCHAR(255), " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP " +
        ")");
    }

    /**
     * Get All Schedules on the Specified Day of Week
     * @param dayOfWeek Target day of Week
     * @return List of Schedules for the Week
     * @throws SQLException If an SQL Exception occurs
     */
    public static ArrayList<Schedule> getByDayOfWeek(DayOfWeek dayOfWeek) throws SQLException {
        PreparedStatement preparedStatement = SQL.getConnection().prepareStatement(
            "SELECT * FROM schedule WHERE day_of_week = ? ORDER BY start");
        preparedStatement.setByte(1, (byte)dayOfWeek.getValue());
    
        ResultSet rs = preparedStatement.executeQuery();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        ArrayList<Schedule> list = new ArrayList<Schedule>();
        while (rs.next()) {
            int id = rs.getInt("id");
            LocalTime start = LocalTime.parse(rs.getString("start"), formatter);
            LocalTime end = LocalTime.parse(rs.getString("end"), formatter);
            DayOfWeek day = DayOfWeek.of(rs.getByte("day_of_week"));
            String description = rs.getString("description");
            Timestamp createdAt = rs.getTimestamp("created_at");
            Timestamp modifiedAt = rs.getTimestamp("modified_at");
            
            list.add(new Schedule(
                id, start, end, day, description, createdAt, modifiedAt
            ));
        }
        return list;
    }

    private Schedule(
        int id, LocalTime start, LocalTime end, DayOfWeek dayOfWeek, 
        String description, Timestamp createdAt, Timestamp modifiedAt
        ) {
        this.id = Optional.of(id);
        this.start = start;
        this.end = end;
        this.dayOfWeek = dayOfWeek;
        this.description = description;
        this.createdAt = Optional.of(createdAt);
        this.modifiedAt = Optional.of(modifiedAt);
    }

    public Schedule(LocalTime start, LocalTime end, DayOfWeek dayOfWeek, String description) {
        this.id = Optional.empty();
        this.start = start;
        this.end = end;
        this.dayOfWeek = dayOfWeek;
        this.description = description;
        this.createdAt = Optional.empty();
        this.modifiedAt = Optional.empty();
    }

    /**
     * Inserts the Current Instance into SQLite as a Row in the table <code>schedule</code>
     * @throws Exception If the current instance has already been inserted as a row
     */
    public void insert() throws Exception {
        if (this.id.isPresent()) {
            throw new Exception("Row is already inserted!");
        }
        PreparedStatement statement = SQL.getConnection().prepareStatement(
            "INSERT INTO schedule(start, end, day_of_week, description) VALUES(?, ?, ?, ?) " +
            "RETURNING *;"
        );
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        statement.setString(1, this.start.format(formatter));
        statement.setString(2, this.end.format(formatter));
        statement.setByte(3, (byte)this.dayOfWeek.getValue());
        statement.setString(4, this.description);

        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            this.id = Optional.of(rs.getInt("id"));
            this.start = LocalTime.parse(rs.getString("start"), formatter);
            this.end = LocalTime.parse(rs.getString("end"), formatter);
            this.dayOfWeek = DayOfWeek.of(rs.getByte("day_of_week"));
            this.description = rs.getString("description");
            this.createdAt = Optional.of(rs.getTimestamp("created_at"));
            this.modifiedAt = Optional.of(rs.getTimestamp("modified_at"));
        } else {
            throw new Exception("Error inserting row");
        }
    }

    /**
     * Deletes Schedule Entry by ID
     * @param id ID of target schedule row
     * @return Amount of rows affected. Should be (1) if the row exists, otherwise (0).
     * @throws SQLException
     */
    public static int deleteById(int id) throws SQLException {
        PreparedStatement statement = SQL.getConnection().prepareStatement(
            "DELETE FROM schedule WHERE id = ?"
        );
        statement.setInt(1, id);
        int res = statement.executeUpdate();
        return res;
    }

    /**
     * Converts the Current <code>Schedule</code> Instance into JSON
     * @return Current Instance as JSON with all Members
     */
    @SuppressWarnings("unchecked")
    public JSONObject asJSON() {
        JSONObject obj = new JSONObject();
        
        if (this.id.isPresent()) {
            obj.put("id", this.id.get());
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        obj.put("start", this.start.format(formatter));
        obj.put("end", this.end.format(formatter));
    
        obj.put("day_of_week", this.dayOfWeek.toString());

        obj.put("description", this.description);

        if (this.createdAt.isPresent()) {
            obj.put("created_at", this.createdAt.get().toString());
        }

        if (this.modifiedAt.isPresent()) {
            obj.put("modified_at", this.modifiedAt.get().toString());
        }

        return obj;
    }
}