package com.mlm.bitcoin.beans;

public class BitsoPayload {
	private String high;
	private String low;
	private String last;
	private String bid;
	private String ask;
	private String book;
	private String created_at;

	public String getHigh() {
		return high;
	}

	public void setHigh(String high) {
		this.high = high;
	}

	public String getLow() {
		return low;
	}

	public void setLow(String low) {
		this.low = low;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}
	
	public Float getBidFloat() {
		return Float.parseFloat(bid);
	}

	public String getAsk() {
		return ask;
	}
	
	public Float getAskFloat() {
		return Float.parseFloat(ask);
	}

	public void setAsk(String ask) {
		this.ask = ask;
	}

	public String getBook() {
		return book;
	}

	public void setBook(String book) {
		this.book = book;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

}
