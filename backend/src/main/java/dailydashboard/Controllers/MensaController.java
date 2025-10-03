package dailydashboard.Controllers;

import java.io.IOException;
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

import dailydashboard.Models.MensaLocation;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class MensaController {

    private MensaLocation matchMensaLocation(Optional<String> location) {
        if (!location.isPresent()) {
            return MensaLocation.ACADEMICA;
        }

        String locationString = location.get();
        for (MensaLocation mensa: MensaLocation.values()) {
            if (locationString.equals(mensa.getLabel())) {
                return mensa;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @GetMapping(path = "/mensa-menu")
    public @ResponseBody JSONAware menu(@RequestParam("location") Optional<String> location, HttpServletResponse response) throws IOException {
        MensaLocation mensaLocation = matchMensaLocation(location);

        if (mensaLocation == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JSONObject obj = new JSONObject();
            obj.put("error", location.get() + " is not a valid mensa location!");
            return obj;
        }
        
        Document doc = Jsoup.connect(mensaLocation.getUrl()).get();
        Elements elements = doc.body().selectFirst("* div.accordion").children();
        
        JSONArray days = new JSONArray();
        for (Element element: elements.asList()) {
            JSONObject menuJson = new JSONObject();
            
            Element header = element.selectFirst("h3");
            menuJson.put("date", header.text());

            Elements menus = element.selectFirst(".menues").selectFirst("tbody").children();
            JSONArray menusJson = new JSONArray();
            for (Element menu: menus.asList()) {
                Element desc = menu.selectFirst(".expand-nutr");
                desc.children().forEach((tag) -> tag.remove());
                Element category = menu.select(".menue-item").first();
                Element price = menu.select(".menue-item").last();

                JSONObject itemJson = new JSONObject();
                itemJson.put("name", desc.text());
                itemJson.put("category", category.text());
                itemJson.put("price", price.text());

                menusJson.add(itemJson);
            }

            menuJson.put("menu", menusJson);

            days.add(menuJson);
        }
        

        return days;
    }
}
