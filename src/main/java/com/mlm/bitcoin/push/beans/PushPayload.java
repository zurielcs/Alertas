package com.mlm.bitcoin.push.beans;

public class PushPayload {
	private String title;
	private String sound;
	private String body;
	private String type;
	private Float value;
	private String book;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSound() {
		return sound;
	}

	public void setSound(String sound) {
		this.sound = sound;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Float getValue() {
		return value;
	}

	public void setValue(Float value) {
		this.value = value;
	}

	public String getBook() {
		return book;
	}

	public void setBook(String book) {
		this.book = book;
	}
	
	public static PushPayload build(String title, String message, String book, String type, Float value) {
		PushPayload payload = new PushPayload();
		payload.setTitle(title);
		payload.setSound("default");
		payload.setBody(message);
		payload.setBook(book);
		payload.setType(type);
		payload.setValue(value);
		return payload;
	}

}
