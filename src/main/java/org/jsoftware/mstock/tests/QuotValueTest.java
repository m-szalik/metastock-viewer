package org.jsoftware.mstock.tests;

import org.jsoftware.mstock.models.Instrument;
import org.jsoftware.mstock.models.Quote;

public class QuotValueTest extends AbstractMstockTest {

    @Override
    public TestResult test(Instrument instrument) {
        TestResult tr = new TestResult(this);
        Quote q;
        for (int i = 0; i < instrument.quotes.length; i++) {
            q = instrument.quotes[i];
            if (q.close == 0) tr.addMessage(i + " close is zero");
            if (q.open == 0) tr.addMessage(i + " open is zero");
            if (q.high == 0) tr.addMessage(" high is zero");
            if (q.low == 0) tr.addMessage(" low is zero");
        }
        return tr;
    }

}
