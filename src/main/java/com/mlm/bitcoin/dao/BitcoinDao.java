package com.mlm.bitcoin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mlm.bitcoin.beans.BitsoPayload;
import com.mlm.bitcoin.commons.Constantes;
import com.mlm.bitcoin.commons.DbUtils;
import com.mlm.bitcoin.dto.CurrencyDTO;
import com.mlm.bitcoin.dto.DeviceDTO;
import com.mlm.bitcoin.dto.NotificationDTO;

public class BitcoinDao {
	private final static String insertDeviceSql = "INSERT INTO bitcoin (last, bid, ask, high, low, currency, timestamp) VALUES (?, ?, ?, ?, ?, ?, NOW())";
	private final static String insertNotification = "INSERT INTO notification_history (currency, type, value, message, timestamp) VALUES (?, ?, ?, ?, NOW())";
	private final static String insertBitacoraSql = "INSERT INTO bitacora (tipo_evento, evento, timestamp) VALUES (?, ?, NOW())";

	// private final static String createTableSql = "CREATE TABLE IF NOT EXISTS
	// bitcoin ( id INT NOT NULL "
	// + "AUTO_INCREMENT, value FLOAT(7,2) NOT NULL DEFAULT '0.00', timestamp
	// DATETIME NOT NULL, PRIMARY KEY (id) )";

	private final static String selectDevices = "SELECT * FROM devices";
	private final static String selectMin = "SELECT COALESCE(time_to_sec(timediff(now(), max(timestamp)))/60/60, 999) hrs FROM bitcoin where currency = ? and ask <= ?";
	private final static String selectMax = "SELECT COALESCE(time_to_sec(timediff(now(), max(timestamp)))/60/60, 999) hrs FROM bitcoin where currency = ? and bid >= ?";

	private final static String selectLastDays = "select truncate(avg(last), 2) last, currency, DATE_FORMAT(timestamp, '%Y-%m-%d %H:00') time from bitcoin b where DATE(timestamp) >= DATE_ADD(CURDATE(), INTERVAL - ? DAY) group by currency, DATE_FORMAT(timestamp, '%Y-%m-%d %H:00') order by currency, DATE_FORMAT(timestamp, '%Y-%m-%d %H:00') desc";
	private final static String selectLastHours = "select last, bid, ask, currency, DATE_FORMAT(timestamp, '%Y-%m-%d %H:%i') time from bitcoin b where timestamp >= NOW() - INTERVAL ? HOUR order by currency, DATE_FORMAT(timestamp, '%Y-%m-%d %H:%i') desc";

	private final static String selectLastNotificationDays = "select id, type, message, currency, value, DATE_FORMAT(timestamp, '%Y-%m-%d %H:%i') time from notification_history nh where timestamp >= NOW() - INTERVAL ? DAY order by DATE_FORMAT(timestamp, '%Y-%m-%d %H:%i') desc";
	private final static String selectLastNotificationHours = "select id, type, message, currency, value, DATE_FORMAT(timestamp, '%Y-%m-%d %H:%i') time from notification_history nh where timestamp >= NOW() - INTERVAL ? HOUR order by DATE_FORMAT(timestamp, '%Y-%m-%d %H:%i') desc";

	public static List<DeviceDTO> selectDevices() {
		List<DeviceDTO> list = new ArrayList<>();
		try {
//			Connection conn = DataSource.getInstance().getConnection();
			Connection conn = DbUtils.getConnection();
			PreparedStatement ps = conn.prepareCall(selectDevices);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String platform = rs.getString("platform");
				String token = rs.getString("token");
				list.add(new DeviceDTO(platform, token));
			}
			ps.close();
			rs.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static boolean insertBitcoin(BitsoPayload bitsoPayload) {
		// conn.createStatement().executeUpdate(createTableSql);
		try {
//			Connection conn = DataSource.getInstance().getConnection();
			Connection conn = DbUtils.getConnection();
			PreparedStatement ps = conn.prepareStatement(insertDeviceSql);
			float last = Float.parseFloat(bitsoPayload.getLast());
			float bid = Float.parseFloat(bitsoPayload.getBid());
			float ask = Float.parseFloat(bitsoPayload.getAsk());
			float high = Float.parseFloat(bitsoPayload.getHigh());
			float low = Float.parseFloat(bitsoPayload.getLow());
			ps.setFloat(1, last);
			ps.setFloat(2, bid);
			ps.setFloat(3, ask);
			ps.setFloat(4, high);
			ps.setFloat(5, low);
			ps.setString(6, bitsoPayload.getBook());
			ps.executeUpdate();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean insertBitacora(String tipoEvento, String evento) {
		// conn.createStatement().executeUpdate(createTableSql);
		try {
//			Connection conn = DataSource.getInstance().getConnection();
			Connection conn = DbUtils.getConnection();
			PreparedStatement ps = conn.prepareStatement(insertBitacoraSql);
			ps.setString(1, tipoEvento);
			ps.setString(2, evento);
			ps.executeUpdate();
			ps.close();
			conn.close();
		} catch (Exception e) {
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
		try {
//			Connection conn = DataSource.getInstance().getConnection();
			Connection conn = DbUtils.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, currency);
			ps.setFloat(2, currentValue);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				res = rs.getFloat("hrs");
			}
			ps.close();
			rs.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public static List<CurrencyDTO> selectHistory(int days, int hours) {
		List<CurrencyDTO> res = new ArrayList<>();
		try {
//			Connection conn = DataSource.getInstance().getConnection();
			Connection conn = DbUtils.getConnection();
			PreparedStatement ps;
			if(days == 0) {
				ps = conn.prepareStatement(selectLastHours);
				ps.setInt(1, hours);
			}
			else {
				ps = conn.prepareStatement(selectLastDays);
				ps.setInt(1, days);
			}

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				CurrencyDTO dto = new CurrencyDTO();
				dto.setBook(rs.getString("currency"));
				dto.setLast(rs.getFloat("last"));
				dto.setAsk(rs.getFloat("ask"));
				dto.setBid(rs.getFloat("bid"));
				dto.setCreated_at(rs.getString("time") + ":00:00+00:00");
				res.add(dto);
			}
			ps.close();
			rs.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public static boolean insertNotification(NotificationDTO dto) {
		try {
//			Connection conn = DataSource.getInstance().getConnection();
			Connection conn = DbUtils.getConnection();
			PreparedStatement ps = conn.prepareStatement(insertNotification);
			ps.setString(1, dto.getBook());
			ps.setString(2, dto.getType());
			ps.setFloat(3, dto.getValue());
			ps.setString(4, dto.getMessage());
			ps.executeUpdate();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static List<CurrencyDTO> selectNotificationHistory(int days, int hours) {
		List<CurrencyDTO> res = new ArrayList<>();
		try {
//			Connection conn = DataSource.getInstance().getConnection();
			Connection conn = DbUtils.getConnection();
			PreparedStatement ps;
			if(days == 0) {
				ps = conn.prepareStatement(selectLastNotificationHours);
				ps.setInt(1, hours);
			} else {
				ps = conn.prepareStatement(selectLastNotificationDays);
				ps.setInt(1, days);
			}

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				CurrencyDTO dto = new CurrencyDTO();
				dto.setBook(rs.getString("currency"));
				dto.setLast(rs.getFloat("last"));
				dto.setAsk(rs.getFloat("ask"));
				dto.setBid(rs.getFloat("bid"));
				dto.setCreated_at(rs.getString("time") + ":00:00+00:00");
				res.add(dto);
			}
			ps.close();
			rs.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
}
