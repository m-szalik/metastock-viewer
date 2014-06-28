package org.jsoftware.mstock.tests;

import org.jsoftware.mstock.models.Instrument;
import org.jsoftware.mstock.models.Quote;

public class QuotMinMaxTest extends AbstractMstockTest {

    @Override
    public TestResult test(Instrument instrument) {
        TestResult tr = new TestResult(this);
        Quote q;
        for (int i = 0; i < instrument.quotes.length; i++) {
            q = instrument.quotes[i];
            if (q.high < 0 && q.low < 0) continue;
            if (q.high < q.low) {
                tr.addMessage(i + ":" + q.high + "<" + q.low);
            }
        }
        return tr;
    }

}
