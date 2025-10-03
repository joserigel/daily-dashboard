package dailydashboard.Controllers;

import java.sql.SQLException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import dailydashboard.Models.Expense;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class ExpenseController {


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
