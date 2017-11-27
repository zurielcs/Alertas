package com.mlm.bitcoin.beans;

import java.util.List;

import com.mlm.bitcoin.dto.NotificationDTO;

public class NotificationHistoryResponse extends Callback {
	private List<NotificationDTO> history;

	public List<NotificationDTO> getHistory() {
		return history;
	}

	public void setHistory(List<NotificationDTO> history) {
		this.history = history;
	}
}
