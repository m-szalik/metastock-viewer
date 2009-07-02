/* MasterFileRecord.java
 * -----------------------------------------------------------------------------
 * 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * -----------------------------------------------------------------------------
 * JFB	1.0		17-Jul-2003		Created today.
 */

package com.mac.verec.datafeed.metastock ;

import java.util.Date;

import com.mac.verec.models.NumberDate;

/**
 * Where all <i>my</i> intelligence shows ;-) Not that parsing this format
 * was so extremely difficult, no. Just a useless pain inflicted on the
 * new comer. A barrier to entry of sorts!
 * @see Reader
 * @see Parser
 */

public final class MasterFileRecord {
	private static final String	FPREFIX	= "F" ;
	private static final String	FSUFFIX_DAT	= ".DAT" ;
	private static final String	FSUFFIX_MWD	= ".MWD" ;
	
	// only package visible
	int		fileNum ;
	char	fileType ;
	int		recordLength ;
	int		recordCount ;
	String	issueName ;
	boolean	v28 ;
	NumberDate	firstDate ;
	NumberDate	lastDate ;
	char	timeFrame ;
	int		idaTime ;
	String	symbol ;
	boolean	autorun ;

	/**
	 * All the parsing, reading & decoding is done in the constructor.
	 */
	public
	MasterFileRecord(
		byte[] 	data
	,	int		recNum) {

		int	recordBase	= (recNum+1) * MasterFileDescriptor.MASTER_RECORD_LENGTH ;
		
		fileNum		= Parser.readByte	(data, recordBase + MasterFileDescriptor.FILE_NUM);
		if (fileNum < 0) fileNum = Byte.MAX_VALUE - fileNum;
		fileType	= (char) Parser.readShort	(data, recordBase + MasterFileDescriptor.FILE_TYPE_0) ;
		recordLength= Parser.readByte	(data, recordBase + MasterFileDescriptor.RECORD_LENGTH) ;
		recordCount	= Parser.readByte	(data, recordBase + MasterFileDescriptor.RECORD_COUNT) ;
		
		issueName	= new String(data, recordBase + MasterFileDescriptor.ISSUE_NAME_0, MasterFileDescriptor.ISSUE_NAME_LEN).trim() ;
		v28			= Parser.readByte	(data, recordBase + MasterFileDescriptor.CT_V2_8_FLAG) == (byte) 'Y' ;

		firstDate	= Parser.readDate	(data, recordBase + MasterFileDescriptor.FIRST_DATE_0) ;
		lastDate	= Parser.readDate	(data, recordBase + MasterFileDescriptor.LAST_DATE_0) ;

		timeFrame	= Parser.readChar	(data, recordBase + MasterFileDescriptor.TIME_FRAME) ;
		idaTime		= Parser.readShort	(data, recordBase + MasterFileDescriptor.IDA_TIME_0) ;

		symbol		= new String(data, recordBase + MasterFileDescriptor.SYMBOL_0, MasterFileDescriptor.SYMBOL_LEN).trim() ;
		autorun		= Parser.readByte	(data, recordBase + MasterFileDescriptor.CT_V2_8_FLAG) == (byte) '*' ;
		if (fileNum == 0) throw new RuntimeException("Invalid Recored::" + recNum + "  " + this);
	}

	public String
	toString() {
		StringBuffer	sb = new StringBuffer() ;
		
		sb.append("fileNum:      " + Integer.toString(fileNum) + "\n") ;
		char[] ft = {fileType} ;
		sb.append("fileType:     " + new String(ft) + "\n") ;
		sb.append("recordLength: " + Integer.toString(recordLength) + "\n") ;
		sb.append("recordCount:  " + Integer.toString(recordCount) + "\n") ;
		sb.append("issueName:    " + issueName + "\n") ;
		sb.append("v28:          " + (v28 ? "true" : "false") + "\n") ;
		sb.append("firstDate:    " + (firstDate == null ? "??/??/????" : firstDate.toString()) + "\n") ;
		sb.append("lastDate:     " + (lastDate == null ? "??/??/????" : lastDate.toString()) + "\n") ;
		char[] tf = {timeFrame} ;
		sb.append("timeFrame:    " + new String(tf) + "\n") ;
		sb.append("idaTime:      " + Integer.toString(idaTime) + "\n") ;
		sb.append("symbol:       " + symbol + "\n") ;
		sb.append("autorun:      " + (autorun ? "true" : "false") + "\n") ;

		return sb.toString() ;
	}
	
	/**
	 * Used to get the target <code>F*.dat</code>.
	 */
	public String getFileName() {
		return FPREFIX + String.valueOf(fileNum) + (fileNum>256 ? FSUFFIX_MWD : FSUFFIX_DAT) ;
	}

	public int getFileNum() {
		return fileNum;
	}

	public char getFileType() {
		return fileType;
	}

	public int getRecordLength() {
		return recordLength;
	}

	public int getRecordCount() {
		return recordCount;
	}

	public String getIssueName() {
		return issueName;
	}

	public boolean isV28() {
		return v28;
	}

	public Date getFirstDate() {
		return firstDate;
	}

	public Date getLastDate() {
		return lastDate;
	}

	public char getTimeFrame() {
		return timeFrame;
	}

	public int getIdaTime() {
		return idaTime;
	}

	public String getSymbol() {
		return symbol;
	}

	public boolean isAutorun() {
		return autorun;
	}
	
}

