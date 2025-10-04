package dailydashboard.Controllers;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

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

import dailydashboard.Models.Expense;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class ExpenseController {
    private static Logger logger = LoggerFactory.getLogger(ExpenseController.class);

    @SuppressWarnings("unchecked")
    @GetMapping(path = "/expenses")
    public @ResponseBody JSONAware getExpenses(@RequestParam("page") int page, @RequestParam("size") int size) throws Exception {
        ArrayList<Expense> expenses = Expense.getExpenses(page, size);
        JSONArray array = new JSONArray();
        
        for (Expense expense: expenses) {
            array.add(expense.asJSON());
        }

        return array;
    }

    @SuppressWarnings("unchecked")
    @GetMapping(path = "/expenses-per-category")
    public @ResponseBody JSONObject getExpensesPerCategory(
        @RequestParam("start") String start, 
        @RequestParam("end") String end,
        HttpServletResponse response) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Timestamp startParsed = new Timestamp(dateFormat.parse(start).getTime());
                Timestamp endParsed = new Timestamp(dateFormat.parse(end).getTime());
                JSONObject obj = new JSONObject(Expense.getPerCategory(startParsed, endParsed));
                return obj;
            } catch (Exception e) {
                JSONObject obj = new JSONObject();
                obj.put("error", e.getMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return obj;
            }
    }

    @SuppressWarnings("unchecked")
    @PostMapping(path = "/expense")
    public JSONObject postExpense(@RequestBody Map<String, Object> body, HttpServletResponse response) {
        if (!body.containsKey("amount") || !body.containsKey("category")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JSONObject obj = new JSONObject();
            obj.put("error", "amount and category required in body!");
            return obj;
        }

        Integer amount = (Integer)body.get("amount");
        String category = (String)body.get("category");

        String description = (String)body.get("description");
        
        try {
            Expense expense = new Expense(
                amount.intValue(), 
                category, 
                description == null ? Optional.empty() : Optional.of(description)
            );
            expense.insert();
            return expense.asJSON();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject obj = new JSONObject();
            obj.put("error", e.getMessage());
            return obj;
        }
    }

    @DeleteMapping(path = "/expense/{id}")
    @ResponseBody
    public void deleteExpense(@PathVariable("id") int id, HttpServletResponse response) throws SQLException {
        int res = Expense.deleteById(id);
        if (res > 0) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
