package com.mac.verec.models ;

/**
 * A financial instrument with daily OHLC in raw and
 * extended flavors, together with name and symbol. A place
 * holder is also provided to relate points to currency amounts,
 * that is, some instruments are measured in points (the indexes
 * such as the DOW or the FTSE) while some other are measured in
 * Dollar amounts (most securities).
 * <p>The field <code>offDays</code> is an array of <code>int</code>s
 * that contains -1 if on that precise date, the market for that
 * instrument was closed, or the index of that date otherwise.
 * <p>The field <code>quotes</code> contains the extended <code>Quote</code>
 * data, that is, including business days on which the market for that
 * instrument was closed.
 * <p>The field <code>rawQuotes</code> contains only those dates on
 * which the instrument was actually traded.
 * <p>The reason why we have two set of quotes is because comparisons
 * between instruments wouldn't be possible if the dates didn't match
 * on one hand, and because on the other hand, on a given instrument, the
 * non traded days are mostly irrelevant.
 * @see Reader
 * @see Quote
 */
 
import com.mac.verec.datafeed.metastock.MasterFileRecord;

public class Instrument {
	private Integer validQuotas = null;
	/**
	 * The default value for the stake. <b>The actual value must
	 * be specified when generating trades, otherwise the portfolio
	 * <i>will</i> contain a bogus equity balance</b>.
	 */
	public static final float	DEFAULT_STAKE	=	1.0f ;

	/** The name of the instrument, e.g: "DOW JONES INDU A-". */
	public	String			name ;

	/** The Reuters assigned symbol of the instrument, e.g: ".DJI". */
	public	String			symbol ;

	/** The <i>only traded days included</i> quote data. */
	public	Quote[]			rawQuotes ;

	/** The <i>all business days included</i> quote data. */
	public	Quote[]			quotes ;

	public Quote[] getQuotes() {
		return quotes;
	}
	
	
	public MasterFileRecord getMasterFileRecord() {
		return masterFileRecord;
	}
	
	/**
	 * The stake corresponding to that instrument <i>It may depend on
	 * the actual broker used. For example, with a spread-betting
	 * account, the broker imposes the stake. This may not be the case
	 * with a more plain vanillia broker -- assuming such a thing exists!
	 * ;-) -- </i>
	 */
	public	float			stakePerPoint ;

	public	boolean			inUse ;

	private MasterFileRecord masterFileRecord;
	private int norec = -1;
	
	
	public void setNorec(int norec) {
		this.norec = norec;
	}
	
	/**
	 * Default constructor that does nothing of significance. Fields
	 * <b>must be</b> initialized to non default values at some point.
	 */
	public	Instrument() {
		this(null, null, null, DEFAULT_STAKE) ;
	}

	/**
	 * Default constructor that assigns the rawQuotes, name and symbol
	 * values. The other remaining fields <b>must be</b> initialized
	 * to non default values at some point.
	 */
	public Instrument(MasterFileRecord r,	Quote[]	rawQuotes) {
		this(r, rawQuotes, null, DEFAULT_STAKE) ;
	}

	/**
	 * Main constructor where every single field must be specified.
	 */
	private Instrument(MasterFileRecord r,	Quote[]	rawQuotes,	Quote[]	quotes,	float stakePerPoint) {
		if (r != null) {
			this.name = r.getIssueName();
			this.symbol = r.getSymbol();
		}
		this.rawQuotes = rawQuotes ;
		this.quotes = quotes ;
		this.stakePerPoint = stakePerPoint ;
		this.masterFileRecord = r;
	}

	public int getValidQuotas() {
		if (validQuotas == null) {
			// calculate valid quotas
			if (quotes != null) {
				int qc = 0;
				for(Quote q : quotes) if (q.isValid()) qc++;
				validQuotas = qc;
			} 
		}
		return validQuotas == null ? -1 : validQuotas;
	}


	public int getNumberOfRecords() {
		return norec;
	}
}