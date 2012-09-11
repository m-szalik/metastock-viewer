package com.gruszecm.mstock.browser;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.MovingAverage;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.XYDataset;

import com.mac.verec.models.Instrument;
import com.mac.verec.models.Quote;

public class Chart1Frame extends AbstractBrowserFrame {
	private static final long serialVersionUID = 6409974181595978626L;

	public Chart1Frame(JInternalFrame parent, JDesktopPane desktopPane, Instrument instrument) {
		super(parent, desktopPane);
		setTitle("OHLC Chart - " + instrument.name);
		TimeTableXYDataset volumeDataset = new TimeTableXYDataset();
		OHLCSeries series = new OHLCSeries(instrument.name);
		for (Quote quote : instrument.getQuotes()) {
			if (quote.isValid()) {
				RegularTimePeriod period = new Day(quote.date);
				series.add(period, quote.open, quote.high, quote.low, quote.close);
				float vol = quote.volume / 10E3f;
				if (vol >= 0) {
					volumeDataset.add(period, vol, "Volume");
				}
			}
		}
		OHLCSeriesCollection dataset1 = new OHLCSeriesCollection();
		dataset1.addSeries(series);
		final JFreeChart chart = ChartFactory.createHighLowChart("OHLC", "Time", "Value", dataset1, true);
		final DateAxis axis = (DateAxis) chart.getXYPlot().getDomainAxis();
		axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
		// add moveing average
		final XYDataset dataset2 = MovingAverage.createMovingAverage(dataset1, "-MAVG", 3 * 24 * 60 * 60 * 1000L, 0L);
		final XYPlot plot = (XYPlot) chart.getPlot();
		plot.setDataset(1, dataset2);
		plot.setRenderer(1, new StandardXYItemRenderer());
		// add volume
		NumberAxis rangeAxis3 = new NumberAxis("Volume (x 1000)");
		rangeAxis3.setUpperMargin(1.0); 
		plot.setRangeAxis(2, rangeAxis3);
		plot.setDataset(2, volumeDataset);
		plot.mapDatasetToRangeAxis(2, 2);
		XYBarRenderer renderer3 = new XYBarRenderer(0.20);
//		renderer2.setToolTipGenerator(new StandardXYToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0,000.00")));
		plot.setRenderer(2, renderer3);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
	}             

}
