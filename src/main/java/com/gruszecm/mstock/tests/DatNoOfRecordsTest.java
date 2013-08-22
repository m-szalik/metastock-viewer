package com.gruszecm.mstock.tests;

import com.mac.verec.models.Instrument;

public class DatNoOfRecordsTest extends AbstractMstockTest {

    @Override
    public TestResult test(Instrument instrument) {
        int norec = instrument.getNumberOfRecords();
        TestResult tr = new TestResult(this);
        if (instrument.getValidQuotas() != norec - 1) {
            tr.addMessage("norec=" + norec + ", validQuotas=" + instrument.getValidQuotas());
        }
        return tr;
    }

}
