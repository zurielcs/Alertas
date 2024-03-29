package com.mlm.bitcoin.cron;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.repackaged.com.google.gson.Gson;
import com.mlm.bitcoin.beans.BitsoPayload;
import com.mlm.bitcoin.beans.BitsoTicker;
import com.mlm.bitcoin.commons.Utils;
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
			BitsoPayload ripple = null;
			for (BitsoPayload bitsoPayload : lst) {
				switch(bitsoPayload.getBook().toLowerCase()){
				case "btc_mxn":
					bitcoin = bitsoPayload;
					break;
				case "eth_mxn":
					ether = bitsoPayload;
					break;
				case "xrp_mxn":
					ripple = bitsoPayload;
					break;
				}
			}
			response.append("<br>bitcoin " + bitcoin.getLast());
			response.append("<br>ether " + ether.getLast());
			response.append("<br>ripple " + ether.getLast());

			BitcoinService.sendPush(bitcoin, ether, ripple, response);

			BitcoinDao.insertBitcoin(bitcoin);
			BitcoinDao.insertBitcoin(ether);
			BitcoinDao.insertBitcoin(ripple);
//			BitcoinDao.insertBitacora("RESPONSE GETBTC", response.toString().replaceAll("<br>", " - "));
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

}
