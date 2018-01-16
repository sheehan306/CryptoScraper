package com.esheejo.crypto.movement;

import com.esheejo.common.HttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MovementScraper {

    private static final String DIR = "/Users/deemoshea/Documents/data/";
    private static Map<String,Integer> todaysMap = new HashMap<String, Integer>();
    private static Map<String,Integer> yMap = new HashMap<String, Integer>();

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static LocalDate localDate = LocalDate.now();

    private static StringBuilder moveString = new StringBuilder();

    public void runMovementCheck(){

        try {
            System.out.println("Send Http GET request");
            this.sendGet();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String ydoc = parseYesterdaysFile();
        parseInfo(ydoc, yMap);

        getMarketMovement();

        writeToFile(moveString.toString(), dtf.format(localDate) + "-moveSummary" );
    }

    private static void getMarketMovement() {
        for (Map.Entry<String, Integer> entry : todaysMap.entrySet()) {
            if (yMap.containsKey(entry.getKey())){
                Integer movement = yMap.get(entry.getKey())-entry.getValue();
                System.out.println("Coin " + entry.getKey() + " movement " + movement);
                moveString.append(entry.getKey()+"|"+ movement+"\n");


            } else {
                System.out.println("Doesn't exist " + entry.getKey());
            }
        }
    }

    private static String parseYesterdaysFile() {


        String date = dtf.format(localDate.minusDays(1));

        StringBuilder ysb = new StringBuilder();

        try {

            File f = new File(DIR+date+".txt");

            BufferedReader b = new BufferedReader(new FileReader(f));

            String readLine = "";

            System.out.println("Reading file using Buffered Reader");

            while ((readLine = b.readLine()) != null) {
                ysb.append(readLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ysb.toString();


    }

    // HTTP GET request
    private void sendGet() throws Exception {
        HttpClient client = new HttpClient();
        String coinMarketResponse = client.sendGetRequest("https://coinmarketcap.com/all/views/all/");
        //System.out.println(coinMarketResponse);
        String date = dtf.format(localDate);
        writeToFile(coinMarketResponse, date);

        writeToFile(parseInfo(coinMarketResponse,todaysMap),date+"-summary");

    }

    private static String parseInfo(String stringToParse, Map<String,Integer> map){

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

    public static void writeToFile(String contentToWriteToFile, String file) {

        String fileName = file+ ".txt";
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {

            fw = new FileWriter(DIR+fileName);
            bw = new BufferedWriter(fw);
            bw.write(contentToWriteToFile);

            System.out.println("Done");

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
    }


    // HTTP POST request

}
