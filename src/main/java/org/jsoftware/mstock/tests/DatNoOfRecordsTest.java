package org.jsoftware.mstock.tests;

import org.jsoftware.mstock.models.Instrument;

public class DatNoOfRecordsTest extends AbstractMstockTest {

    @Override
    public TestResult test(Instrument instrument) {
        int noRec = instrument.getNumberOfRecords();
        TestResult tr = new TestResult(this);
        if (instrument.getValidQuotas() != noRec - 1) {
            tr.addMessage("noRec=" + noRec + ", validQuotas=" + instrument.getValidQuotas());
        }
        return tr;
    }

}
