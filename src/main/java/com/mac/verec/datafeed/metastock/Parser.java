/* Parser.java
 * -----------------------------------------------------------------------------
 * 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * -----------------------------------------------------------------------------
 * JFB	1.0		17-Jul-2003		Created today.
 */

package com.mac.verec.datafeed.metastock ;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Calendar;

import com.mac.verec.models.NumberDate;

/**
 * The actual code that deals with Metastock idiosyncratic format. Provision
 * for reading:
 * <ul><li>an antire <code>F*.dat</code> file,
 * <li>individual <code>byte</code>s or <code>short</code>s,
 * <li>the infamous Microsoft <code>float</code> format, and
 * <li>dates expressed as 20030815.0f for the 15-Aug-2003.
 * </ul>
 */
public final class Parser {

	/**
	 * Default constructor made private as only static methods are provided.
	 */
	private
	Parser() {
	}

	/**
	 * Transforms a dynosaur era inherited float format into something
	 * that can be actually <i>used</i>!
	 * <p><pre>
	 * IEEE 4 byte float
	 *         31 30    23 22                        0
	 *         .-------------------------------------.
	 *         |s| 8 bits |msb   23 bit mantissa  lsb|
	 *         `-------------------------------------'
	 *          |      |                `----------------  mantissa
	 *          |      `--------------------------------  biased exponent (7fh)
	 *          `-------------------------------------  sign bit
	 * 
	 * Microsoft 4 byte float
	 *         31     24 23 22                       0
	 *         .-------------------------------------.
	 *         | 8 bits |s|msb  23 bit mantissa   lsb|
	 *         `-------------------------------------'
	 *              |    |              `----------------  mantissa
	 *              |    `----------------------------  sign bit
	 *              `------------------------------  biased exponent (81h) 
	 * 	
	 * b0        b1        b2        b3		
	 * Microsoft Basic LE
	 * mmmm|mmmm mmmm|mmmm smmm|mmmm eeee|eeee
	 * BE
	 * eeee|eeee smmm|mmmm mmmm|mmmm mmmm|mmmm
	 * 
	 * IEEE LE
	 * mmmm|mmmm mmmm|mmmm emmm|mmmm seee|eeee
	 * BE
	 * seee|eeee emmm|mmmm mmmm|mmmm mmmm|mmmm
	 * </pre></code>
	 * <p>This method is <code>package</code> visible only so as to protect the innocent!
	 * <br>See, four sins have been commited:
	 * <ol><li>Metastock was written in Basic!
	 * <li>Metastock used a proprietary format for its float representation
	 * <li>That format is a Microsoft format, and
	 * <li>it is little endian (just to add insult to injury)
	 * </ol>
	 * <br>I don't <i>wonder</i>, I know that in 2103, assuming the stock market still
	 * exists (which doesn't seem so far fetched a proposition after all) we will still
	 * be relying on this stupid format unless someone with better education overtakes
	 * Metastock in an unprecendented bold move... I know: I'm dreaming...
	 */
	public static float
	readMicrosoftBASICfloat(byte[] buffer, int offset) {

		int 	b0 = (int) (buffer[offset++] & 0x0ff) ;
		int 	b1 = (int) (buffer[offset++] & 0x0ff) ;
		int 	b2 = (int) (buffer[offset++] & 0x0ff) ;
		int 	b3 = (int) (buffer[offset] & 0x0ff) ;

		// Now, this is big endian:

		int msf = b3 << 24 | b2 << 16 | b1 << 8 | b0 ;

		int val = (msf & 0x007fffff) ;
		int exp = ((msf >> 24) & 0x0ff) - 2 ;
		int sig = (msf >> 16) & 0x080 ;
				
		val |= exp << 23 ;
		val |= sig << 24 ;

		return Float.intBitsToFloat(val) ;
	}

	/**
	 * Reads a date in Metastock format.
	 * @return a <code>java.util.Date</code> object formated according to the contents
	 * of the buffer at that offset.
	 */
	static NumberDate readDate(byte[] buffer, int offset) {
		float f = readMicrosoftBASICfloat(buffer, offset);
		NumberDate d = new NumberDate(f);
		byte[] buf = Arrays.copyOfRange(buffer, offset, offset + 4);
		d.setBytes(buf);
		return d;
	}

	/**
	 * Reads a date in Metastock format.
	 * @return a <code>java.util.Date</code> object formated according to the value
	 * of the given float argument interpreted as in 20030815.0f for the
	 * 15-Aug-2003 (Not that this date has any significance: it's just
	 * today's date! ;-)
	 * <p>Also note that this stupid format was not even Y2K compliant, hence
	 * the addition of 1900 to the year to compensate ... and create a Y2.1K problem ;-(
	 */
	public static NumberDate readDate(int integer) {
		int		date = integer;
		int		day = date % 100 ;
				date /= 100 ;
		int		month = date % 100 ;
				date /= 100 ;
		int		year = 1900 + date ;

		Calendar c = Calendar.getInstance() ;
		c.clear() ;
		c.set(year, month-1, day) ;
		return new NumberDate(c.getTime(), integer) ;
	}

	/**
	 * Returns the <code>byte</code> at that offset in the buffer.
	 */
	static byte
	readByte(byte[] buffer, int offset) {
		return buffer[offset] ;
	}

	/**
	 * Returns the <code>char</code> at that offset in the buffer.
	 */
	static char
	readChar(byte[] buffer, int offset) {
		return (char) buffer[offset] ;
	}

	/**
	 * Returns the <code>short</code> at that offset in the buffer, correctly
	 * handling that stupid little endian format. Microsoft and Intel: I hate
	 * you both! ;-)
	 */
	static short
	readShort(byte[] buffer, int offset) {
		int b1 = buffer[offset++] ;
		int b2 = buffer[offset] ;
		
		return (short) ((b2 << 8) | b1) ;
	}

	/**
	 * Completely reads the given <code>File</code> into memory and
	 * returns a <code>byte</code> array corresponding to its contents.
	 */
	static byte[] readFrom(File f) {
		byte[]				data = null ;
		FileInputStream 	fis = null ;
		try {
			int length = (int) f.length() ;
			data = new byte[length] ;
			fis = new FileInputStream(f) ;
			fis.read(data) ;
		} catch (Exception e) {
			e.printStackTrace() ;
		} finally {
			try {
				if (fis != null) {
					fis.close() ;
				}
			} catch (Exception e) {
				e.printStackTrace() ;
			}
		}

		return data ;
	}

	public static int readInt(byte buf[], int offset) {
		return ((buf[offset + 0] & 0xFF) << 0) +
	       ((buf[offset + 1] & 0xFF) << 8) +
	       ((buf[offset + 2] & 0xFF) << 16) +
	       ((buf[offset + 3]) << 24);
	}

	public static NumberDate readDateInt(byte[] data, int offset) {
		int di = readInt(data, offset);
		Calendar cal = Calendar.getInstance();
		cal.clear();
		int v = di % 10000;
		di = (di - v) / 10000;
		di += 1900;
		cal.set(Calendar.YEAR, di);
		di = (v - v % 100) / 100;
		cal.set(Calendar.MONTH, di-1);
		di = v % 100;
		cal.set(Calendar.DAY_OF_MONTH, di);
		NumberDate nd = new NumberDate(cal.getTime(), di);
		byte[] buf = Arrays.copyOfRange(data, offset, offset + 4);
		nd.setBytes(buf);
		return nd;
	}
}