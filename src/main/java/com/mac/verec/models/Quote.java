package com.mac.verec.models ;



/**
 * Encapsulates that date, open, high, low and close value for a given
 * instrument on that particular date.
 * @see Instrument
 */
public class Quote {
	public int no;
	/** The date corresponding to the OHLC fields below */
	public NumberDate		date ;

	/** The O of OHLC */
	public float	open ;

	/** The H of OHLC */
	public float	high ;

	/** The L of OHLC */
	public float	low ;

	/** The C of OHLC */
	public float	close ;

	/** The Open Interest */
	public float	interest ;
	
	/** The volume */
	public float	volume ;
	
	/** Default constructor. */
	public
	Quote(	int no,
			NumberDate	date 
		,	float	open 
		,	float	high 
		,	float	low
		,	float	close 
		,	float	interest
		,	float	volume) {

		this.no = no;
		this.date = date ;
		this.open = open ;
		this.high = high ;
		this.low = low ;
		this.close = close ;
		this.interest = interest ;
		this.volume = volume ;
	}
	
	/** Copy constructor. */
	public Quote(int no,		Quote other) {
		this.no = no;
		this.date = other.date ;
		this.open = other.open ;
		this.high = other.high ;
		this.low = other.low ;
		this.close = other.close ;
		this.interest = other.interest ;
		this.volume = other.volume ;
	}
	
	public void
	reset() {
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof Quote) {
			Quote q = (Quote) obj;
			return q.no == no && q.close==close && q.date.equals(date) && q.high==high && q.interest==interest && q.low==low && q.open==open && q.volume==volume; 
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return date == null ? 0 : date.hashCode();
	}

	/** Just so that dumps are a bit more menaingful. */
	public String toString() {
		return "Date: " + date +
				", open:" + open + 
				", high:" + high + 
				", low:" + low + 
				", close:" + close + 
				", interest:" + interest + 
				", volume:" + volume ;
	}

	public boolean isValid() {
		if (close < 0) return false;
		if (high < 0) return false;
		if (low < 0) return false;
		if (open < 0) return false;
//		if (interest < 0) return false;
//		if (volume < 0) return false;
		return true;
	}
}
