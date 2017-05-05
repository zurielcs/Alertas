package com.mlm.bitcoin.dto;

public class DeviceDTO {
	private String platform;
	private String token;
	
	public DeviceDTO(String platform, String token) {
		this.platform = platform;
		this.token = token;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
