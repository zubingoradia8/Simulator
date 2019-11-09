package Data;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

public class GetDataCSV implements Data {

    private static String symbol;

    public GetDataCSV(String ticker) throws IOException {
        symbol = "symbol=" + ticker + "&";

        URL url = new URL(basicURL + symbol + interval + API + "&datatype=csv");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        System.out.println(basicURL + symbol + interval + API + "&datatype=csv");

        BufferedInputStream input = null;
        try {
            input = new BufferedInputStream(conn.getInputStream());
        } catch (UnknownHostException ex) {
            System.out.println("No Internet");
        }
        FileOutputStream out = new FileOutputStream("Data.csv");
        int c;

        while((c = input.read()) != -1) {
            out.write(c);
        }
    }

    public static String getTicker() {
        return symbol;
    }

}
