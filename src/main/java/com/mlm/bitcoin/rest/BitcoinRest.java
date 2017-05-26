package com.mlm.bitcoin.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.DefaultValue;
import com.google.api.server.spi.config.Named;
import com.mlm.bitcoin.beans.Callback;
import com.mlm.bitcoin.beans.HistoryResponse;
import com.mlm.bitcoin.beans.RequestRegisterDevice;
import com.mlm.bitcoin.commons.DataSource;
import com.mlm.bitcoin.dao.BitcoinDao;
import com.mlm.bitcoin.dto.CurrencyDTO;

/**
 * 
 */
@Api(name = "bitcoin", version = "v1")
public class BitcoinRest {

	final String createTableSql = "CREATE TABLE IF NOT EXISTS devices ( id INT NOT NULL "
			+ "AUTO_INCREMENT, token VARCHAR(100) NOT NULL, platform VARCHAR(20) NOT NULL, timestamp DATETIME NOT NULL, "
			+ "PRIMARY KEY (id) )";
	final String insertDeviceSql = "INSERT INTO devices (token, platform, timestamp) VALUES (?, ?, NOW())";

	@ApiMethod(name = "insertDevice", httpMethod = "post", path = "push")
	public Callback insertDevice(RequestRegisterDevice request) {
		Callback response = new Callback();
		StringBuilder responseBuilder = new StringBuilder();
		try {
			Connection conn = DataSource.getInstance().getConnection();
			// conn.createStatement().executeUpdate(createTableSql);
			PreparedStatement statementCreateVisit = conn.prepareStatement(insertDeviceSql);
			statementCreateVisit.setString(1, request.getToken());
			statementCreateVisit.setString(2, request.getPlatform());
			statementCreateVisit.executeUpdate();
			conn.close();
		} catch (Exception e) {
			responseBuilder.append("ERROR " + e.getMessage() + "\n");
		}
		if (responseBuilder.length() == 0) {
			responseBuilder
					.append("Dispositivo " + request.getToken() + " registrado, plataforma " + request.getPlatform());
			response.setSuccess(true);
		}

		response.setMessage(responseBuilder.toString());
		return response;
	}

	@ApiMethod(name = "history", httpMethod = ApiMethod.HttpMethod.GET, path = "history")
	public HistoryResponse getHistory(@Named("days") @DefaultValue("0") int days, @Named("hours") @DefaultValue("0") int hours) {
		List<CurrencyDTO> list = BitcoinDao.selectHistory(days, hours);
		HistoryResponse response = new HistoryResponse();
		if (list != null && !list.isEmpty()) {
			response.setSuccess(true);
			response.setHistory(list);
		} else {
			response.setSuccess(false);
		}
		return response;
	}
}
