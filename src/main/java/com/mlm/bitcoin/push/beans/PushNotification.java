package com.mlm.bitcoin.push.beans;

public class PushNotification {
	private PushPayload notification;
	private PushPayload data;
	private String to;

	public PushPayload getNotification() {
		return notification;
	}

	public void setNotification(PushPayload notification) {
		this.notification = notification;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public PushPayload getData() {
		return data;
	}

	public void setData(PushPayload data) {
		this.data = data;
	}
	
	public static PushNotification build(String title, String message, String book, String type, Float value){
		PushNotification push = new PushNotification();
		PushPayload payload = new PushPayload();
		payload.setTitle(title);
		payload.setSound("default");
		payload.setBody(message);
		payload.setBook(book);
		payload.setType(type);
		payload.setValue(value);
		push.setData(payload);
		push.setNotification(payload);
		return push;
	}
}