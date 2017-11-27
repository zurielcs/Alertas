package com.mlm.bitcoin.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.DefaultValue;
import com.google.api.server.spi.config.Named;
import com.mlm.bitcoin.beans.Callback;
import com.mlm.bitcoin.beans.HistoryResponse;
import com.mlm.bitcoin.beans.NotificationHistoryResponse;
import com.mlm.bitcoin.beans.RequestRegisterDevice;
import com.mlm.bitcoin.commons.DbUtils;
import com.mlm.bitcoin.dao.BitcoinDao;
import com.mlm.bitcoin.dto.CurrencyDTO;
import com.mlm.bitcoin.dto.NotificationDTO;

/**
 * 
 */
@Api(name = "bitcoin", version = "v1")
public class BitcoinRest {

	final String createTableSql = "CREATE TABLE IF NOT EXISTS devices ( id INT NOT NULL "
			+ "AUTO_INCREMENT, token VARCHAR(100) NOT NULL, platform VARCHAR(20) NOT NULL, timestamp DATETIME NOT NULL, "
			+ "PRIMARY KEY (id) )";
	final String insertDeviceSql = "INSERT INTO devices (token, platform, timestamp, activo) VALUES (?, ?, NOW(), 1)";

	@ApiMethod(name = "insertDevice", httpMethod = "post", path = "push")
	public Callback insertDevice(RequestRegisterDevice request) {
		Callback response = new Callback();
		StringBuilder responseBuilder = new StringBuilder();
		try {
//			Connection conn = DataSource.getInstance().getConnection();
			Connection conn = DbUtils.getConnection();
			// conn.createStatement().executeUpdate(createTableSql);
			PreparedStatement statementCreateVisit = conn.prepareStatement(insertDeviceSql);
			statementCreateVisit.setString(1, request.getToken());
			statementCreateVisit.setString(2, request.getPlatform());
			statementCreateVisit.executeUpdate();
			statementCreateVisit.close();
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
	
	@ApiMethod(name = "notificationHistory", httpMethod = ApiMethod.HttpMethod.GET, path = "notificationHistory")
	public NotificationHistoryResponse getNotificationHistory(@Named("days") @DefaultValue("0") int days, @Named("hours") @DefaultValue("0") int hours) {
		List<NotificationDTO> list = BitcoinDao.selectNotificationHistory(days, hours);
		NotificationHistoryResponse response = new NotificationHistoryResponse();
		if (list != null && !list.isEmpty()) {
			response.setSuccess(true);
			response.setHistory(list);
		} else {
			response.setSuccess(false);
		}
		return response;
	}

	
	@ApiMethod(name = "test", httpMethod = ApiMethod.HttpMethod.GET, path = "test")
	public Callback test(){
		List<String> tokenList = new ArrayList<String>();
		tokenList.add("dljgWqTpEqw:APA91bHo3W-ENdJwzJNiwpxQOG_BSlv9eGYCYjxxHZB6GtPcejPcdezKePTrc9QG6gWDBq2H1BiNMj5H0966STadWIEqZLyRl_9l8XxEFxsU0DTq8jLTxPyj2IBfvutqf6E4wgXgHtwI");
		Callback callback = new Callback();
		callback.setMessage(BitcoinDao.disableDevices(tokenList));
		return callback;
	}
	
}
