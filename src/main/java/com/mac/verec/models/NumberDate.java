package com.mac.verec.models;

import com.mac.verec.datafeed.metastock.Parser;

import java.util.Date;

public class NumberDate extends Date{
	private static final long serialVersionUID = -3568736716658591206L;
	private Number nValue;
	private byte[] bytes;
	
	public NumberDate(Date date, float floatValue) {
		setTime(date.getTime());
		this.nValue = floatValue;
	}
	
	public NumberDate(float floatValue) {
		this(Parser.readDate((int)floatValue), floatValue);
	}
	
	public NumberDate(Date d) {
		super(d.getTime());
		nValue = null;
	}
	
	public Number getNumberValue() {
		return nValue;
	}
	
	public byte[] getBytes() {
		return bytes;
	}
	
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	
}
