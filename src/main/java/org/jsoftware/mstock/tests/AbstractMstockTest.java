package org.jsoftware.mstock.tests;

import org.jsoftware.mstock.models.Instrument;

public abstract class AbstractMstockTest {

    public String getName() {
        return getClass().getSimpleName();
    }

    public abstract TestResult test(Instrument instrument);

    public boolean isImportant() {
        return true;
    }
}
