package org.jsoftware.mstock.tests;

import org.jsoftware.mstock.models.Instrument;
import org.jsoftware.mstock.models.Quote;

import java.util.Date;

public class DateLastTest extends AbstractDateTest {

    public DateLastTest(boolean skipInvalid) {
        super(skipInvalid);
    }

    @Override
    protected Date getDate(Date currentDate, Quote q) {
        if (currentDate == null) return q.date;
        return currentDate.before(q.date) ? q.date : currentDate;
    }

    @Override
    protected Date getDate(Instrument instr) {
        return instr.getMasterFileRecord().getLastDate();
    }


}
