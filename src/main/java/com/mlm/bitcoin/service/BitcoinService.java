package com.mlm.bitcoin.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mlm.bitcoin.beans.BitsoPayload;
import com.mlm.bitcoin.commons.Constantes;
import com.mlm.bitcoin.commons.Utils;
import com.mlm.bitcoin.dao.BitcoinDao;
import com.mlm.bitcoin.dto.DeviceDTO;

public class BitcoinService {
	private static Map<Integer, Date> lastPush = new HashMap<>();

	public static boolean sendPush(BitsoPayload bitcoin, BitsoPayload ether, BitsoPayload ripple, StringBuilder response) {
		List<String> lstPush = new ArrayList<String>();
		Float hsr;
		Date now = new Date();

		if (isNextPush(1)) {
			hsr = BitcoinDao.selectBestValue(Constantes.TYPE_SELECT_MIN, Constantes.CURRENCY_BTC, bitcoin.getAskFloat());
			response.append("<br>Min BTC hrs " + hsr);
			if (hsr > 9) {
				lastPush.put(1, now);
				lstPush.add("Compra Bitcoin a " + bitcoin.getAsk() + ", el valor mas bajo en " + Math.round(hsr) + " horas");
			}

		}

		if (isNextPush(2)) {
			hsr = BitcoinDao.selectBestValue(Constantes.TYPE_SELECT_MAX, Constantes.CURRENCY_BTC, bitcoin.getBidFloat());
			response.append("<br>Max BTC hrs " + hsr);
			if (hsr > 9) {
				lastPush.put(2, now);
				lstPush.add("Vende Bitcoin a " + bitcoin.getBid() + ", el valor mas alto en " + Math.round(hsr) + " horas");
			}
		}

		if (isNextPush(3)) {
			hsr = BitcoinDao.selectBestValue(Constantes.TYPE_SELECT_MIN, Constantes.CURRENCY_ETH, ether.getAskFloat());
			response.append("<br>Min ETH hrs " + hsr);
			if (hsr > 9) {
				lastPush.put(3, now);
				lstPush.add("Compra Ether a " + ether.getAsk() + ", el valor mas bajo en " + Math.round(hsr) + " horas");
			}
		}

		if (isNextPush(4)) {
			hsr = BitcoinDao.selectBestValue(Constantes.TYPE_SELECT_MAX, Constantes.CURRENCY_ETH, ether.getBidFloat());
			response.append("<br>Max ETH hrs " + hsr);
			if (hsr > 9) {
				lastPush.put(4, now);
				lstPush.add("Vende Ether a " + ether.getBid() + ", el valor mas alto en " + Math.round(hsr) + " horas");
			}
		}
		
		if (isNextPush(5)) {
			hsr = BitcoinDao.selectBestValue(Constantes.TYPE_SELECT_MIN, Constantes.CURRENCY_RIP, ripple.getAskFloat());
			response.append("<br>Min RIP hrs " + hsr);
			if (hsr > 9) {
				lastPush.put(5, now);
				lstPush.add("Compra Ripple a " + ripple.getAsk() + ", el valor mas bajo en " + Math.round(hsr) + " horas");
			}
		}

		if (isNextPush(6)) {
			hsr = BitcoinDao.selectBestValue(Constantes.TYPE_SELECT_MAX, Constantes.CURRENCY_RIP, ripple.getBidFloat());
			response.append("<br>Max RIP hrs " + hsr);
			if (hsr > 9) {
				lastPush.put(6, now);
				lstPush.add("Vende Ripple a " + ripple.getBid() + ", el valor mas alto en " + Math.round(hsr) + " horas");
			}
		}

		for (String pushMsg : lstPush) {
			BitcoinDao.insertBitacora("PUSH", pushMsg);
			List<DeviceDTO> lst = BitcoinDao.selectDevices();
			for (DeviceDTO device : lst) {
				if (device.getPlatform().toUpperCase().contains("IOS")) {
					String content = "{ \"notification\" : { \"title\" : \"Bitcoin Ticker\", \"sound\" : \"default\", \"body\" : \""
							+ pushMsg + "\" }, \"data\" : { \"title\" : \"Bitcoin Ticker\", \"body\" : \"" + pushMsg
							+ "\" }, \"to\":\"" + device.getToken() + "\" }";
					response.append("<br>IOS " + Utils.sendPushApple(content));
				} else {
					String content = "{ \"data\" : { \"title\" : \"Bitcoin Ticker\", \"body\" : \"" + pushMsg
							+ "\" }, \"to\":\"" + device.getToken() + "\" }";
					response.append("<br>ANDROID " + Utils.sendPushAndroid(content));
				}
			}
		}

//		String pushMsg = "Vende Ether a " + ether + ", el mas caro en " + Math.round(1) + " horas";
//		String token = "eMNB-r-A3ow:APA91bFr63JDcZ0n9SzMhD-LC2okvpeuF4qBoV2JvgXfameuIQAOoM1FWKCXL-jMmZbvTfdG2fH6o2qEJkUdtWjNdqh02pQmq0iAgayQkwJDxMKwAiBVIAzn9B1c0lBRIR5lX7D7jkTK";
//		String content = "{ \"notification\" : { \"title\" : \"Bitcoin Ticker\", \"sound\" : \"default\", \"body\" : \""
//				+ pushMsg + "\" }, \"data\" : { \"title\" : \"Bitcoin Ticker\", \"body\" : \"" + pushMsg
//				+ "\" }, \"to\":\"" + token  + "\" }";
//		response.append("<br>ANDROID " + Utils.sendPushAndroid(content));

		return true;
	}

	public static boolean isNextPush(int type) {
		Date last = lastPush.get(type);
		if (last != null) {
			Date now = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(last);
			cal.add(Calendar.MINUTE, 15);
			Date newTime = new Date(cal.getTimeInMillis());
			if (newTime.after(now))
				return false;
		}
		return true;
	}

}
