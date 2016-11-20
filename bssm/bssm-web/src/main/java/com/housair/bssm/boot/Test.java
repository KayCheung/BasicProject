package com.housair.bssm.boot;

import java.util.Enumeration;
import java.util.Properties;

public class Test {

	public static void main(String[] args) {
		Properties p = System.getProperties();
		Enumeration enums =  p.elements();
		while(enums.hasMoreElements()) {
			Object v = enums.nextElement();
			System.out.println(v);
		}
	}
	
}
