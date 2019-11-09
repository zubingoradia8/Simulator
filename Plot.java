package Data;

import GUI.AlertMessage;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.OHLCDataset;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Plot extends JFrame {

    private SimpleDateFormat parser;
    private final Date marketOpen;
    private final Date marketClose;

    public Plot(String title) throws Throwable {
        super(title);

        parser = new SimpleDateFormat("HH:mm");
        marketOpen = parser.parse("10:00");
        marketClose = parser.parse("16:00");

        OHLCDataset dataset = createDataset(title);
        JFreeChart chart = ChartFactory.createHighLowChart(title, "Date", "Price", dataset, false);
        XYPlot plot = (XYPlot) chart.getPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRangeIncludesZero(false);

        ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);
        setDefaultLookAndFeelDecorated(true);
    }

    private OHLCDataset createDataset(String ticker) {
        OHLCDataItem[] dataItem = null;
        try {
            HashMap<Date, ArrayList<Double>> data = LoadData.getData(ticker);
            dataItem = new OHLCDataItem[data.size()];

            Iterator<Date> dates = data.keySet().iterator();

            for(int i = 0; i < data.size(); i++) {
                Date date = dates.next();
                String str = date.toString().replaceAll("[^1-9: ]+", "").trim();
                str = str.substring(0, str.lastIndexOf(":"));
                if(str.endsWith(":"))
                    str += "00";
                Date temp = parser.parse(str.substring(str.indexOf(" ") + 1));

                if(!(temp.before(marketOpen) || temp.after(marketClose)))
                    dataItem[i] = new OHLCDataItem(date, data.get(date).get(0),
                            data.get(date).get(1), data.get(date).get(2), data.get(date).get(3),  data.get(date).get(4));
            }
        } catch (Throwable t) {
            AlertMessage.getAlert("Error. ");
        }

        return new DefaultOHLCDataset(GetDataCSV.getTicker(), dataItem);
    }

    public static void plot(String ticker)  {
        SwingUtilities.invokeLater(() -> {
            try {
                Plot plt = new Plot(ticker);
                plt.setSize(800, 400);
                plt.setLocationRelativeTo(null);
                plt.setVisible(true);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });
    }

}
