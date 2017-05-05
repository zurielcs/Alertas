package com.mlm.bitcoin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.repackaged.com.google.gson.Gson;
import com.mlm.bitcoin.dao.BitcoinDao;
import com.mlm.bitcoin.service.BitcoinService;

@SuppressWarnings("serial")
public class GetBTC extends HttpServlet {

	private static final Logger log = Logger.getLogger(GetBTC.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.info("Cron Job has been executed");
		StringBuilder response = new StringBuilder();
		try {
			String json = Utils.sendGet("https://api.bitso.com/v3/ticker/");
			BitsoTicker obj = new Gson().fromJson(json, BitsoTicker.class);
			List<BitsoPayload> lst = obj.getPayload();
			BitsoPayload bitcoin = null;
			BitsoPayload ether = null;
			for (BitsoPayload bitsoPayload : lst) {
				if (bitsoPayload.getBook().toUpperCase().contains("BTC"))
					bitcoin = bitsoPayload;
				else
					ether = bitsoPayload;
			}
			response.append("<br>bitcoin " + bitcoin.getLast());
			response.append("<br>ether " + ether.getLast());

			BitcoinService.sendPush(Float.parseFloat(bitcoin.getLast()), Float.parseFloat(ether.getLast()), response);

			BitcoinDao.insertBitcoin(bitcoin);
			BitcoinDao.insertBitcoin(ether);
			BitcoinDao.insertBitacora("RESPONSE GETBTC", response.toString().replaceAll("<br>", " - "));
		} catch (Exception e) {
			response.append("ERROR " + e.toString());
		}

		if (response.length() == 0)
			response.append("Exito");
		PrintWriter out = resp.getWriter();
		out.println(response);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	public static void main(String args[]) {
//		String token = "e2kCxVkKRsI:APA91bFvO9l0WyuRCR1mAbXc_k-Kc3MAXu_otmFkLGmVjTsxQ81xcX9h1q1Cv4YeXwauhURy_-R6KatJSBjvsHVcbDE1WK7jVVf1jJA5wzOLKROXacMwh5w69raoJPANQOIM4vyHsbmW";
//		String pushMsg = "COMPRA BITCOIN";
//		String content = "{ \"notification\" : { \"title\" : \"Bitcoin Ticker\", \"body\" : \"" + pushMsg
//				+ "\" }, \"to\":\"" + token + "\" }";
//		System.out.println(Utils.sendPushApple(content));
		BitcoinDao.selectDevices();
	}

}
