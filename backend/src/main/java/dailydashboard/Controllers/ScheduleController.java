package dailydashboard.Controllers;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import dailydashboard.Models.Schedule;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class ScheduleController {
    private static Logger logger = LoggerFactory.getLogger(ScheduleController.class);

    @SuppressWarnings("unchecked")
    @GetMapping(path = "/schedules")
    public @ResponseBody JSONAware getSchedules(@RequestParam("dayOfWeek") String dayOfWeek, HttpServletResponse response) throws SQLException {
        try {
            DayOfWeek parsedDayOfWeek = DayOfWeek.valueOf(dayOfWeek);
            ArrayList<Schedule> schedules = Schedule.getByDayOfWeek(parsedDayOfWeek);
            JSONArray jsonArray = new JSONArray();
            for (Schedule schedule: schedules) {
                jsonArray.add(schedule.asJSON());
            }
            return jsonArray;
        } catch(IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JSONObject obj = new JSONObject();
            obj.put("error", e.getMessage());
            return obj;
        }
    }

    @SuppressWarnings("unchecked")
    @PostMapping(path = "/schedule")
    public JSONObject postSchedule(
        @RequestBody Map<String, Object> body, 
        HttpServletResponse response
        ) throws Exception {
        if (
            !body.containsKey("day_of_week") || 
            !body.containsKey("start") || 
            !body.containsKey("end") ||
            !body.containsKey("description")
        ) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JSONObject obj = new JSONObject();
            obj.put("error", "day_of_week, start, end, and description required in body!");
            return obj;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime start = LocalTime.parse((String)body.get("start"), formatter);
        LocalTime end = LocalTime.parse((String)body.get("end"), formatter);
        DayOfWeek day = DayOfWeek.valueOf((String)body.get("day_of_week"));
        String description = (String)body.get("description");
        
        Schedule schedule = new Schedule(start, end, day, description);

        schedule.insert();

        return schedule.asJSON();
    }


    @DeleteMapping(path = "/schedule/{id}")
    @ResponseBody
    public void deleteSchedule(@PathVariable("id") int id, HttpServletResponse response) throws SQLException {
        int res = Schedule.deleteById(id);
        if (res > 0) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
