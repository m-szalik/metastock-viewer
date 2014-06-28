package org.jsoftware.mstock.metastock;

import junit.framework.Assert;
import org.junit.Test;


public class ParserTest {

    @Test
    public void testReadInt() {
        int values[] = {50000, 1, 10, 0, -6};
        for (int i : values) {
            byte[] buf = writeIntToBytes(i);
            int r = Parser.readInt(buf, 0);
            Assert.assertEquals(i, r);
        }
    }


    private static byte[] writeIntToBytes(int i) {
        byte[] ss = new byte[4];
        ss[0] = (byte) (i & 0x00ff);
        ss[1] = (byte) (i >> 8 & 0x00ff);
        ss[2] = (byte) (i >> 16 & 0x00ff);
        ss[3] = (byte) (i >> 24 & 0x00ff);
        return ss;
    }

}
