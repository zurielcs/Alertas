package com.mlm.bitcoin.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.repackaged.com.google.gson.Gson;
import com.mlm.bitcoin.beans.BitsoPayload;
import com.mlm.bitcoin.commons.Constantes;
import com.mlm.bitcoin.commons.Utils;
import com.mlm.bitcoin.dao.BitcoinDao;
import com.mlm.bitcoin.dto.DeviceDTO;
import com.mlm.bitcoin.dto.NotificationDTO;
import com.mlm.bitcoin.push.beans.PushCallback;
import com.mlm.bitcoin.push.beans.PushNotification;
import com.mlm.bitcoin.push.beans.PushPayload;

public class BitcoinService {
	private static Map<Integer, Date> lastPush = new HashMap<>();
	
	public static void main(String[] args){
		String token = "123123";
		String pushMsg = "Mensaje de push";
		String content = "{ \"notification\" : { \"title\" : \"Bitcoin Ticker\", \"sound\" : \"default\", \"body\" : \""
				+ pushMsg  + "\" }, \"data\" : { \"title\" : \"Bitcoin Ticker\", \"body\" : \"" + pushMsg
				+ "\" }, \"to\":\"" + token + "\" }";
		System.out.println(content);
		
		PushNotification push = new PushNotification();
		push.setTo(token);
		PushPayload payload = new PushPayload();
		payload.setTitle("Bitcoin Ticker");
		payload.setSound("default");
		payload.setBody(pushMsg);
		push.setData(payload);
		push.setNotification(payload);
		System.out.println(new Gson().toJson(push));
		
		String res = "{\"multicast_id\": 5297329502807218617,\"success\": 1,\"failure\": 0,\"canonical_ids\": 0,\"results\": [{\"message_id\": \"0:1511748909002896%79350c6979350c69\"}]}";
		PushCallback callback = new Gson().fromJson(res, PushCallback.class);
		System.out.println(new Gson().toJson(callback));
	}

	public static boolean sendPush(BitsoPayload bitcoin, BitsoPayload ether, BitsoPayload ripple, StringBuilder response) {
		List<PushPayload> lstPush = new ArrayList<PushPayload>();
		Float hsr;
		Date now = new Date();

		if (isNextPush(1)) {
			hsr = BitcoinDao.selectBestValue(Constantes.TYPE_SELECT_MIN, Constantes.CURRENCY_BTC, bitcoin.getAskFloat());
			response.append("<br>Min BTC hrs " + hsr);
			if (hsr > 9) {
				lastPush.put(1, now);
				String message = "Compra Bitcoin a " + bitcoin.getAsk() + ", el valor mas bajo en " + Math.round(hsr) + " horas";
				PushPayload push = PushPayload.build(Constantes.PUSH_TITLE, message, Constantes.CURRENCY_BTC,
						Constantes.TYPE_SELECT_MIN, bitcoin.getAskFloat());
				lstPush.add(push);
			}

		}

		if (isNextPush(2)) {
			hsr = BitcoinDao.selectBestValue(Constantes.TYPE_SELECT_MAX, Constantes.CURRENCY_BTC, bitcoin.getBidFloat());
			response.append("<br>Max BTC hrs " + hsr);
			if (hsr > 9) {
				lastPush.put(2, now);
				String message = "Vende Bitcoin a " + bitcoin.getBid() + ", el valor mas alto en " + Math.round(hsr) + " horas";
				PushPayload push = PushPayload.build(Constantes.PUSH_TITLE, message, Constantes.CURRENCY_BTC,
						Constantes.TYPE_SELECT_MAX, bitcoin.getBidFloat());
				lstPush.add(push);
			}
		}

		if (isNextPush(3)) {
			hsr = BitcoinDao.selectBestValue(Constantes.TYPE_SELECT_MIN, Constantes.CURRENCY_ETH, ether.getAskFloat());
			response.append("<br>Min ETH hrs " + hsr);
			if (hsr > 9) {
				lastPush.put(3, now);
				String message = "Compra Ether a " + ether.getAsk() + ", el valor mas bajo en " + Math.round(hsr) + " horas";
				PushPayload push = PushPayload.build(Constantes.PUSH_TITLE, message, Constantes.CURRENCY_ETH,
						Constantes.TYPE_SELECT_MIN, ether.getAskFloat());
				lstPush.add(push);
			}
		}

		if (isNextPush(4)) {
			hsr = BitcoinDao.selectBestValue(Constantes.TYPE_SELECT_MAX, Constantes.CURRENCY_ETH, ether.getBidFloat());
			response.append("<br>Max ETH hrs " + hsr);
			if (hsr > 9) {
				lastPush.put(4, now);
				String message = "Vende Ether a " + ether.getBid() + ", el valor mas alto en " + Math.round(hsr) + " horas";
				PushPayload push = PushPayload.build(Constantes.PUSH_TITLE, message, Constantes.CURRENCY_ETH,
						Constantes.TYPE_SELECT_MAX, ether.getBidFloat());
				lstPush.add(push);
			}
		}
		
		if (isNextPush(5)) {
			hsr = BitcoinDao.selectBestValue(Constantes.TYPE_SELECT_MIN, Constantes.CURRENCY_RIP, ripple.getAskFloat());
			response.append("<br>Min RIP hrs " + hsr);
			if (hsr > 9) {
				lastPush.put(5, now);
				String message = "Compra Ripple a " + ripple.getAsk() + ", el valor mas bajo en " + Math.round(hsr) + " horas";
				PushPayload push = PushPayload.build(Constantes.PUSH_TITLE, message, Constantes.CURRENCY_RIP,
						Constantes.TYPE_SELECT_MIN, ripple.getAskFloat());
				lstPush.add(push);
			}
		}

		if (isNextPush(6)) {
			hsr = BitcoinDao.selectBestValue(Constantes.TYPE_SELECT_MAX, Constantes.CURRENCY_RIP, ripple.getBidFloat());
			response.append("<br>Max RIP hrs " + hsr);
			if (hsr > 9) {
				lastPush.put(6, now);
				String message = "Vende Ripple a " + ripple.getBid() + ", el valor mas alto en " + Math.round(hsr) + " horas";
				PushPayload push = PushPayload.build(Constantes.PUSH_TITLE, message, Constantes.CURRENCY_RIP,
						Constantes.TYPE_SELECT_MAX, ripple.getBidFloat());
				lstPush.add(push);
			}
		}
		Gson gson = new Gson();
		for (PushPayload payload : lstPush) {
			NotificationDTO notificationDTO =  new NotificationDTO();
			notificationDTO.setBook(payload.getBook());
			notificationDTO.setType(payload.getType());
			notificationDTO.setMessage(payload.getBody());
			notificationDTO.setValue(0F);
			BitcoinDao.insertNotification(notificationDTO);
			List<DeviceDTO> lst = BitcoinDao.selectDevices();
			for (DeviceDTO device : lst) {
				PushNotification push = new PushNotification();
				push.setTo(device.getToken());
				String pushResponse;
				if (device.getPlatform().toUpperCase().contains("IOS")) {
					push.setData(payload);
					push.setNotification(payload);
					String content = gson.toJson(push);
					pushResponse = Utils.sendPushApple(content);
					response.append("<br>IOS " + pushResponse);
				} else {
					push.setData(payload);
					String content = gson.toJson(push);
					pushResponse = Utils.sendPushAndroid(content);
					response.append("<br>ANDROID " + pushResponse);
				}
				PushCallback callback = new Gson().fromJson(pushResponse, PushCallback.class);
				if(callback.getFailure() > 0){
					List<String> tokenList = new ArrayList<>();
					tokenList.add(push.getTo());
					BitcoinDao.disableDevices(tokenList);
				}
			}
		}

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
