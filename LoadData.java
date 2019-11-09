package Data;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

public class LoadData {

    public static HashMap<Date, ArrayList<Double>> getData(String ticker) throws Throwable {
        new GetDataCSV(ticker);

        FileReader in = new FileReader("Data.csv");
        BufferedReader read = new BufferedReader(in);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        HashMap<Date, ArrayList<Double>> data = new HashMap<>();

        String res = "";
        int c;

        try {
            while ((c = read.read()) != -1)
                if(!Character.isAlphabetic((char)c))
                    res += (char) c;
        } catch (EOFException ex) {
            // do nothing
        }

        String[] arr = res.split("[,\n\r]");
        ArrayList<String> list = new ArrayList<>(Arrays.asList(arr));
        list.removeAll(Arrays.asList(""));

        if(list.toString().startsWith("{"))
            return null;

        ArrayList<Double> temp = null;
        Date d;

        for(int i = 0, j = 0; i < list.size(); i++, j = ++j % 5) {
            if (j == 0) {
                d = formatter.parse(list.get(i));
                temp = new ArrayList<>(5);
                i++;
                if (i != 0) {
                    temp.add(Double.parseDouble(list.get(i)));
                    data.put(d, temp);
                }
            } else {
                temp.add(Double.parseDouble(list.get(i)));
            }
        }
        System.out.println(data);

        return data;
    }

}
