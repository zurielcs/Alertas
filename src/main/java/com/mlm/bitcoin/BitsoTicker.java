package com.mlm.bitcoin;

import java.util.List;

public class BitsoTicker {
	private boolean success;
	private List<BitsoPayload> payload;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<BitsoPayload> getPayload() {
		return payload;
	}

	public void setPayload(List<BitsoPayload> payload) {
		this.payload = payload;
	}
}
