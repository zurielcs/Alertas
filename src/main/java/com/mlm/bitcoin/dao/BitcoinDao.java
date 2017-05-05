package com.mlm.bitcoin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mlm.bitcoin.BitsoPayload;
import com.mlm.bitcoin.Constantes;
import com.mlm.bitcoin.DbUtils;
import com.mlm.bitcoin.dto.CurrencyDTO;
import com.mlm.bitcoin.dto.DeviceDTO;

public class BitcoinDao {
	private final static String insertDeviceSql = "INSERT INTO bitcoin (value, currency, timestamp) VALUES (?, ?, NOW())";
	private final static String insertBitacoraSql = "INSERT INTO bitacora (tipo_evento, evento, timestamp) VALUES (?, ?, NOW())";

	// private final static String createTableSql = "CREATE TABLE IF NOT EXISTS
	// bitcoin ( id INT NOT NULL "
	// + "AUTO_INCREMENT, value FLOAT(7,2) NOT NULL DEFAULT '0.00', timestamp
	// DATETIME NOT NULL, PRIMARY KEY (id) )";

	private final static String selectDevices = "SELECT * FROM devices";
	private final static String selectMin = "SELECT time_to_sec(timediff(now(), max(timestamp)))/60/60 hrs FROM bitcoin where currency = ? and value < ?";
	private final static String selectMax = "SELECT time_to_sec(timediff(now(), max(timestamp)))/60/60 hrs FROM bitcoin where currency = ? and value > ?";

	private final static String selectLastDays = "select truncate(avg(value), 2) value, currency, DATE_FORMAT(timestamp, '%Y-%m-%d %H:00') time from bitcoin b where DATE(timestamp) >= DATE_ADD(CURDATE(), INTERVAL - ? DAY) group by currency, DATE_FORMAT(timestamp, '%Y-%m-%d %H:00') order by currency, DATE_FORMAT(timestamp, '%Y-%m-%d %H:00') desc";

	public static List<DeviceDTO> selectDevices() {
		List<DeviceDTO> list = new ArrayList<>();
		try {
			Connection conn = DbUtils.getConnection();
			PreparedStatement ps = conn.prepareCall(selectDevices);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String platform = rs.getString("platform");
				String token = rs.getString("token");
				list.add(new DeviceDTO(platform, token));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static boolean insertBitcoin(BitsoPayload bitsoPayload) {
		Connection conn = DbUtils.getConnection();
		// conn.createStatement().executeUpdate(createTableSql);
		try {
			PreparedStatement ps = conn.prepareStatement(insertDeviceSql);
			float value = Float.parseFloat(bitsoPayload.getLast());
			ps.setFloat(1, value);
			ps.setString(2, bitsoPayload.getBook());
			ps.executeUpdate();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean insertBitacora(String tipoEvento, String evento) {
		Connection conn = DbUtils.getConnection();
		// conn.createStatement().executeUpdate(createTableSql);
		try {
			PreparedStatement ps = conn.prepareStatement(insertBitacoraSql);
			ps.setString(1, tipoEvento);
			ps.setString(2, evento);
			ps.executeUpdate();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static Float selectBestValue(String type, String currency, Float currentValue) {
		Float res = null;
		String sql = null;
		switch (type) {
		case Constantes.TYPE_SELECT_MIN:
			sql = selectMin;
			break;
		case Constantes.TYPE_SELECT_MAX:
			sql = selectMax;
		}
		System.out.println("type " + type + " sql " + sql);
		try {
			Connection conn = DbUtils.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, currency);
			ps.setFloat(2, currentValue);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				res = rs.getFloat("hrs");
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public static List<CurrencyDTO> selectHistory(int days) {
		List<CurrencyDTO> res = new ArrayList<>();
		try {
			Connection conn = DbUtils.getConnection();

			PreparedStatement ps = conn.prepareStatement(selectLastDays);
			ps.setInt(1, days);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				CurrencyDTO dto = new CurrencyDTO();
				dto.setBook(rs.getString("currency"));
				dto.setValue(rs.getFloat("value"));
				dto.setCreated_at(rs.getString("time") + ":00:00+00:00");
				res.add(dto);
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
}