package com.gruszecm.mstock.tests;

import java.util.Date;

import com.mac.verec.models.Instrument;
import com.mac.verec.models.Quote;

public class DateFirstTest extends AbstractDateTest {
	
	public DateFirstTest(boolean skipInvalid) {
		super(skipInvalid);
	}

	@Override
	protected Date getDate(Date currentDate, Quote q) {
		if (currentDate == null) return q.date;
		return currentDate.after(q.date) ? q.date : currentDate;
	}

	@Override
	protected Date getDate(Instrument instr) {
		return instr.getMasterFileRecord().getFirstDate();
	}


	

}
