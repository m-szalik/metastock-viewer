package org.jsoftware.mstock.metastock;

import org.jsoftware.mstock.browser.ErrorRecord;
import org.jsoftware.mstock.models.Instrument;
import org.jsoftware.mstock.models.NumberDate;
import org.jsoftware.mstock.models.Quote;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Provides access to EQUIIS/Metastock data file format. Creates a list of
 * <code>Instrument</code>s. Each <code>Instrument</code> contains two arrays of
 * <code>Quote</code>s.
 * <p/>
 * Subclasses need to override both <code>createQuoteArray</code> and
 * <code>createQuote</code> if they want to return a subclass of
 * <code>Quote</code> instead. Additionnaly, subclasses may want to override
 * <code>postProcess</code> if they want some processing specific to their
 * <code>Quote</code> subclass. In this case they should call
 * <code>super.postProcess</code> in order to keep the <code>Quote</code> arrays
 * consistent.
 * <p/>
 * In addition, subclasses may want to override <code>createTradingDays</code>
 * if they want to provide data outside all business days (but I doubt that any
 * such case would be useful...)
 * <p/>
 * Users of the <code>Reader</code> class need only create the instance and then
 * use <code>getInstruments</code> to retreive both raw and processed
 * <code>Quote</code> data.
 *
 * @see org.jsoftware.mstock.models.Instrument
 * @see Quote
 * @see Parser
 */
@SuppressWarnings({"unchecked"})
public class Reader {
    private static final String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    /**
     * The value that indicates a <i>non traded</i> day. Only useful when using
     * <code>Instrument.quote</code> as <code>Instrument.rawQuotes</code> only
     * contains traded days.
     */
    public static final float MARKER = -1.0f;

    /**
     * The path to the EQUIIS/Reuters quotes in Metastsock file format.
     */
    private static final String MASTER = "MASTER";

    private static final int SIZE_OF_FLOAT = 4;

    /**
     * The array of all <code>Instrument</code>s provided by Reuters.
     */
    private final Instrument[] instruments;

    protected final String path;

    private final List<ErrorRecord> errorRecords;

    /**
     * Only of interest to subclasses. Needed to provide the right kind of
     * <code>Quote</code> array.
     */
    protected Quote[] createQuoteArray(int quoteCount) {
        return new Quote[quoteCount];
    }

    /**
     * Only of interest to subclasses. Needed to provide the right kind of
     * <code>Quote</code>s.
     */
    protected Quote createQuote(int no, NumberDate date, float open, float high, float low, float close, float interest, float volume) {

        return new Quote(no, date, open, high, low, close, interest, volume);
    }

//	/**
//	 * Needed to provide the right kind of <code>Quote</code>s.
//	 * 
//	 * @see com.mac.verec.trading.turtle.TurtleQuote
//	 * @see com.mac.verec.trading.turtle.TurtleReader
//	 */
//	private Quote createQuote(int no, Quote q) {
//		return createQuote(no, q.date, q.open, q.high, q.low, q.close, q.interest, q.volume);
//	}

    /**
     * You must specify a path relative to the <code>quotes</code>
     * <i>directory</i> itself as this is where we're going to locate the
     * <code>MASTER</code> file that contains the layout and semantics of the
     * whole shebang.
     */
    public Reader(String path, ProgressMonitor procssMonitor, List<ErrorRecord> errorRecords) {
        this(path, false, procssMonitor, errorRecords);
    }

    /**
     * You must specify a path relative to the <code>quotes</code>
     * <i>directory</i> itself as this is where we're going to locate the
     * <code>MASTER</code> file that contains the layout and semantics of the
     * whole shebang.
     */
    public Reader(String path, boolean dump, ProgressMonitor progressMonitor, List<ErrorRecord> errorRecords) {
        this.errorRecords = errorRecords;
        if (progressMonitor != null) {
            progressMonitor.setProgress(0);
            progressMonitor.setNote("Reading MASTER file...");
        }
        this.path = path;
        byte[] data = Parser.readFrom(new File(path + MASTER));

        int instrumentCount = (data.length / MasterFileDescriptor.MASTER_RECORD_LENGTH) - 1;

		/*
         * minus one because we skip the first record in the MASTER file, as it
		 * is only a header, not a instrument descriptor.
		 */

        if (progressMonitor != null) {
            progressMonitor.setMaximum(instrumentCount + 3);
        }
        instruments = new Instrument[instrumentCount];
        for (int i = 0; i < instrumentCount; ++i) {
            MasterFileRecord r = new MasterFileRecord(data, i);
            if (progressMonitor != null) {
                progressMonitor.setNote("Postprocessing " + r.getSymbol() + "...");
                progressMonitor.setProgress(i);
            }
            Quote[] quotes;
            AtomicInteger norec = new AtomicInteger(-1);
            try {
                quotes = importQuotes(r, norec, path);
            } catch (Exception e) {
                e.printStackTrace();
                quotes = new Quote[0];
            }
            instruments[i] = new Instrument(r, quotes);
            instruments[i].setNorec(norec.get());
            onRecordRead(r);
            if (dump) {
                exportTab(instruments[i], r.fileNum, path);
            }
        }
        if (progressMonitor != null) {
            progressMonitor.setNote("Postprocessing....");
        }
        postProcess();
        if (progressMonitor != null) {
            progressMonitor.setProgress(progressMonitor.getMaximum());
        }
    }

    protected void onRecordRead(MasterFileRecord r) {
    }

    private void exportTab(Instrument instr, int index, String path) {
        File f = new File(path + "I" + index + ".txt");
        try {
            if (!f.createNewFile()) {
                throw new IOException("Cannot create file '" + f.getAbsolutePath() + "'!");
            }
            FileOutputStream fos = new FileOutputStream(f);
            PrintWriter pw = new PrintWriter(fos);

            Quote[] quotes = instr.rawQuotes;

            pw.println(instr.name + "\t" + instr.symbol);
            pw.println("Date\tO\tH\tL\tC\tI\tV");
            for (Quote q : quotes) {
                pw.println(dateToString(q.date) + "\t" + q.open + "\t" + q.high + "\t" + q.low + "\t" + q.close + "\t" + q.interest + "\t" + q.volume);
            }
            pw.close();
        } catch (Exception e) {
            throw new IllegalDataException("Error processing instrument '" + instr + " index=" + index + ", from path='" + path + "'.", e);
        }
    }

    /**
     * The array of instrument quotes is inconsistent, in that, for example the
     * 4th of july is never present for the S&P but exists for the FTSE, while
     * the Easter Monday is not traded for the CAC but is a normal day for the
     * Dow...
     * <p/>
     * So we set up to homogenize the whole thing by standardizing on a calendar
     * where all possible business days are present, and for which we will enter
     * a special MARKER on those days and markets that are not traded.
     */
    protected void postProcess() {
        int instrumentCount = instruments.length;

        // First we create an array of Dates, with as many entries as the union
        // of all tradable days in all markets (and then some, possibly)
        Date[] wholePeriod = createTradingDays();

        // We've got to get two arrays; one densely populated (rawQuotes) and
        // another
        // sparsely populated (quotes). By definition, rawQuotes only contains
        // those dates on which trading did occur. Quotes contains everything
        // that
        // rawQuotes contains together with dummy (-1) entries for those days
        // when the market was closed. This is neat as it allows us to compare
        // instruments on a given date (if the two were trading on that day)
        // with each instrument having the same date index for the same date.

        // Now, we resize all the instrument quotes ...
        int dayCount = wholePeriod.length;
        Quote[][] newQuotes = new Quote[instrumentCount][0];
        int[] dateIndex = new int[instrumentCount];
        int[] dateLimit = new int[instrumentCount];

        for (int i = 0; i < instrumentCount; ++i) {
            if (instruments[i].rawQuotes == null || instruments[i].rawQuotes.length == 0) {
                continue;
            }
            dateIndex[i] = 0;
            dateLimit[i] = instruments[i].rawQuotes.length;
            newQuotes[i] = createQuoteArray(dayCount);
        }

        for (int p = 0; p < dayCount; ++p) {

            NumberDate d = new NumberDate(wholePeriod[p]);

            for (int i = 0; i < instrumentCount; ++i) {
                Quote[] raw = instruments[i].rawQuotes;
                if (raw == null || raw.length == 0) {
                    continue;
                }
                // look for a matching date, if found, add it to the new
                // array, otherwise add the MARKER

                int index = dateIndex[i];
                if (index < dateLimit[i]) {
                    Quote q = raw[index];
                    if (d.compareTo(q.date) == 0) {
                        // don't clone q, just alias it. This is important!
                        newQuotes[i][p] = q;
                        ++dateIndex[i];
                        continue;
                    }
                }

                newQuotes[i][p] = createQuote(p, d, MARKER, MARKER, MARKER, MARKER, MARKER, MARKER);
            }
        }

        for (int i = 0; i < instrumentCount; ++i) {
            instruments[i].quotes = newQuotes[i];
            instruments[i].stakePerPoint = Instrument.DEFAULT_STAKE;
        }
    }

    /**
     * Provided as protected because of my paranoia, but I doubt there's any use
     * in overriding this; but who knows... Computes a list of dates that is the
     * union of all traded days across all <code>Instrument</code>s, and then
     * possibly some more. In practice, it is enough to create a list of dates
     * comprising all single <code>Calendar.MONDAY</code>s to
     * <code>Calendar.FRIDAY</code>s from the earliest period of trading on any
     * <code>Instrument</code> to the latest one.
     */
    protected Date[] createTradingDays() {
        Date earliest = null;
        Date latest = null;
        Date[] wholePeriod;
        // determine the bounds
        for (Instrument instrument : instruments) {
            Quote[] raw = instrument.rawQuotes;
            if (raw == null || raw.length == 0) {
                continue;
            }
            Date first = raw[0].date;
            Date last = raw[raw.length - 1].date;

            if ((earliest == null) || (earliest.compareTo(first) > 0))
                earliest = first;

            if ((latest == null) || (latest.compareTo(last) < 0))
                latest = last;
        }

        // Now earliest is the earliest ANY instrument got quote data for, and
        // latest is the latest that any instrument got quote data for, too.

        Vector<Object> dates = new Vector<Object>();
        Calendar c = Calendar.getInstance();

        // Scan the whole calendar from earliest to latest, and add all
        // business days, irrespective of locale dependent hollidays.

        c.clear();
        c.setTime((Date) earliest.clone());

        do {

            int weekDay = c.get(Calendar.DAY_OF_WEEK);
            if ((weekDay != Calendar.SATURDAY) && (weekDay != Calendar.SUNDAY)) {
                dates.add(c.getTime().clone());
            }
            c.add(Calendar.DATE, 1);

        } while (c.getTime().compareTo(latest) <= 0);

        wholePeriod = dates.toArray(new Date[dates.size()]);
        return wholePeriod;
    }

    /**
     * The actual data acquistion from the Metastock format. Relies on the
     * <code>Parser</code> class for decoding. The main idea here is that every
     * single <code>F*.dat</code> is merely a list of <code>float</code>s
     * (albeit in a peculiar, obsolete, non standard, non IEEE format, only
     * MicroSoft could contribute to History). We rely on there being at least
     * five fields (date, open, high, low, close) and ignore everything else, as
     * this is of no relevance to price-only focused algorithms.
     */
    @SuppressWarnings("finally")
    private Quote[] importQuotes(MasterFileRecord r, AtomicInteger norec, String dataPath) {

        Quote[] quotes = null;

        try {
            File in = new File(dataPath + r.getFileName());
            if (!in.exists()) {
                errorRecords.add(ErrorRecord.warn("Data file (" + r.getFileName() + ") for " + r.getSymbol() + " is missing."));
                return new Quote[0];
            }
            byte[] data = Parser.readFrom(in);
            int length = data.length;
            int quoteCount = (length / r.recordLength) - 1;
            int quoteIndex = 0;
            float[] row = new float[Math.min(r.recordCount, 7)];

            if (row.length < 7) {
                throw new IllegalDataException("Bad data format: date and OHLC fields required for " + r);
            }

            quotes = createQuoteArray(quoteCount);

            // ignore the first row which contains zeroes.
            norec.set(Parser.readInt(data, 2));
            for (int i = r.recordLength; i < length; i += r.recordLength, ++quoteIndex) {
                for (int col = 0; col < r.recordCount; ++col) {
                    row[col] = Parser.readMicrosoftBASICfloat(data, i + (col * SIZE_OF_FLOAT));
                }
                for (int col = r.recordCount; col < row.length; ++col) {
                    row[col] = 0.0f;
                }
                byte[] dbuf = Arrays.copyOfRange(data, i, i + SIZE_OF_FLOAT);
                NumberDate nd = new NumberDate(row[0]);
                nd.setBytes(dbuf);

                Quote q = createQuote(quoteIndex, nd, row[1], row[2], row[3], row[4], row[5], row[6]);
                quotes[quoteIndex] = q;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return quotes;
    }

    /**
     * A concession to some client code. Notably the Settings that have to
     * consolidate stored preferences with actual quotes when the very
     * instruments they remembered disappeared from under their feet because the
     * user deleted some, or, more likely, because an instrument expired (eg: is
     * not uncommon for say "WHEAT FUTURE JULY" to lack significance when in
     * August ... )
     *
     * @return well, the number of <code>Instrument</code>s! Guess that!
     */
    public int getInstrumentCount() {
        return instruments.length;
    }

    /**
     * Returns an <code>Enumeration</code> that lists all the
     * <code>Instrument</code>s.
     */
    @SuppressWarnings("rawtypes")
    public Enumeration<Instrument> getInstruments() {
        return new Enumeration() {
            int index = 0;

            public boolean hasMoreElements() {
                return index < instruments.length;
            }

            public Object nextElement() {
                return instruments[index++];
            }
        };
    }

    public Instrument getInstrument(int index) {
        return instruments[index];
    }


    private static String dateToString(Date d) {
        StringBuilder sb = new StringBuilder();
        Calendar c = Calendar.getInstance();
        c.clear();
        c.setTime(d);
        sb.append(Integer.toString(c.get(Calendar.DATE)));
        sb.append("-");
        sb.append(monthNames[c.get(Calendar.MONTH)]);
        sb.append("-");
        //	sb.append(Integer.toString(c.get(Calendar.YEAR)+1900)) ;
        sb.append(Integer.toString(c.get(Calendar.YEAR)));
        return sb.toString();
    }
}