package com.gruszecm.mstock.tests;

import java.util.Date;

import com.mac.verec.models.Instrument;
import com.mac.verec.models.Quote;

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
