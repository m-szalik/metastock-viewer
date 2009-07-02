package com.gruszecm.mstock.tests;

import com.mac.verec.models.Instrument;

public abstract class AbstractMstockTest {
	
	public String getName() {
		return getClass().getSimpleName();
	}
	
	public abstract TestResult test(Instrument instrument);
	
	public boolean isImportant() {
		return true;
	}
}
