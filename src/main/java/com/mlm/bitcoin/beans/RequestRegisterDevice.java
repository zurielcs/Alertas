package com.mlm.bitcoin.beans;

public class RequestRegisterDevice {
	private String token;
	private String platform;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
}
