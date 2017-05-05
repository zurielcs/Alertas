package com.mlm.bitcoin.response;

import java.util.List;

import com.mlm.bitcoin.Callback;
import com.mlm.bitcoin.dto.CurrencyDTO;

public class HistoryResponse extends Callback {
	private List<CurrencyDTO> history;

	public List<CurrencyDTO> getHistory() {
		return history;
	}

	public void setHistory(List<CurrencyDTO> history) {
		this.history = history;
	}
}
