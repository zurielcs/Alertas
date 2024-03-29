package com.mlm.bitcoin.push.beans;

import java.util.List;

public class PushCallback {
	public Long getMulticast_id() {
		return multicast_id;
	}

	public void setMulticast_id(Long multicast_id) {
		this.multicast_id = multicast_id;
	}

	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	public int getFailure() {
		return failure;
	}

	public void setFailure(int failure) {
		this.failure = failure;
	}

	public int getCanonical_ids() {
		return canonical_ids;
	}

	public void setCanonical_ids(int canonical_ids) {
		this.canonical_ids = canonical_ids;
	}

	public List<PushResult> getResults() {
		return results;
	}

	public void setResults(List<PushResult> results) {
		this.results = results;
	}

	private Long multicast_id;
	private int success;
	private int failure;
	private int canonical_ids;
	private List<PushResult> results;
}
