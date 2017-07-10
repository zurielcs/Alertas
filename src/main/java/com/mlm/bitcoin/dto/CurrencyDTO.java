package com.mlm.bitcoin.dto;

public class CurrencyDTO {
	private Float last;
	private Float bid;
	private Float ask;
	private String book;
	private String created_at;

	public Float getLast() {
		return last;
	}

	public void setLast(Float last) {
		this.last = last;
	}

	public Float getBid() {
		return bid;
	}

	public void setBid(Float bid) {
		this.bid = bid;
	}

	public Float getAsk() {
		return ask;
	}

	public void setAsk(Float ask) {
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
