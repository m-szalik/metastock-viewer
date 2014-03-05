package com.gruszecm.mstock.tests;

import com.mac.verec.models.Instrument;
import com.mac.verec.models.Quote;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

abstract class AbstractDateTest extends AbstractMstockTest {
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private final boolean skipInvalid;

    protected AbstractDateTest(boolean skipInvalid) {
        this.skipInvalid = skipInvalid;
    }

    @Override
    public boolean isImportant() {
        return skipInvalid;
    }


    @Override
    public TestResult test(Instrument instrument) {
        Date iDate = getDate(instrument);
        Date d = null;
        TestResult tr = new TestResult(this);
        for (Quote q : instrument.quotes) {
            if (skipInvalid && q.close < 0) continue;
            d = getDate(d, q);
        }
        if (d == null) {
            if (skipInvalid) tr.addInfo("No valid quotas.");
            else tr.addInfo("No quotas.");
        } else {
            Calendar c1 = Calendar.getInstance();
            c1.setTime(iDate);
            Calendar c2 = Calendar.getInstance();
            c2.setTime(d);
            if (!isSameDay(c1, c2)) {
                String msg = "INSTR:" + df.format(iDate) + " QUOTE:" + df.format(d);
                tr.addMessage(msg);
            }
        }
        return tr;
    }


    @Override
    public String getName() {
        StringBuilder builder = new StringBuilder();
        builder.append(super.getName());
        if (skipInvalid) {
            builder.append("(onlyValid)");
        } else {
            builder.append("(withInvalid)");
        }
        return builder.toString();
    }


    protected abstract Date getDate(Date currentDate, Quote q);

    protected abstract Date getDate(Instrument instr);


    private static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

}
