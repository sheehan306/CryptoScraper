package com.esheejo.crypto.newcoins;

import com.esheejo.common.HttpClient;
import com.esheejo.crypto.movement.MovementScraper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by deemoshea on 16/01/2018.
 */
public class NewCoinsScraper {

    private static Map<String,Integer> todaysMap = new HashMap<String, Integer>();
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static LocalDate localDate = LocalDate.now();

    public void runCoinCheck() {
        HttpClient client = new HttpClient();
        String URL = "https://coinmarketcap.com/exchanges/bittrex/";
        String response = "";
        try {
            response = client.sendGetRequest(URL);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }


        MovementScraper.writeToFile(parseInfo(response,todaysMap),dtf.format(localDate) +"-bittrex");

    }

    private String parseInfo(String stringToParse, Map<String,Integer> map){

        Document doc = Jsoup.parse(stringToParse);
        Elements table = doc.getElementsByTag("table");
        table.html().toString();
        Elements tableRows = table.get(0).getElementsByTag("tr");
        tableRows.remove(0);

        StringBuilder sb = new StringBuilder();

        for (Element row : tableRows) {
            String coinName = row.getElementsByTag("td").get(1).getElementsByTag("a").text();
            String index = row.getElementsByTag("td").get(0).text();
            String cap = row.getElementsByTag("td").get(3).text();
            String coinPrice = row.getElementsByTag("td").get(4).text();

            sb.append("|"+coinName+ "|"+index+"|"+cap+"|"+coinPrice+"|\n");
            map.put(coinName,Integer.valueOf(index));

        }
        return sb.toString();

    }

}
