package com.web.util;

import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;

public class TransportInherit implements TransportListener  {

	public void messageDelivered(TransportEvent e) {
		System.out.println("OK");	
	}

	public void messageNotDelivered(TransportEvent e) {
		System.out.println("NG");
	}

	public void messagePartiallyDelivered(TransportEvent e) {
		// TODO Auto-generated method stub
		
	}

}
