package dailydashboard.Controllers;

import java.util.ArrayList;
import java.util.Optional;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import dailydashboard.Models.Expense;
import dailydashboard.Models.MensaLocation;

@RestController
public class ExpenseController {


    @SuppressWarnings("unchecked")
    @GetMapping(path = "/expenses")
    public @ResponseBody JSONAware expenses(@RequestParam("page") int page, @RequestParam("size") int size) throws Exception {
            ArrayList<Expense> expenses = Expense.getExpenses(page, size);
            JSONArray array = new JSONArray();
            
            for (Expense expense: expenses) {
                array.add(expense.asJSON());
            }

            return array;
    }
}
