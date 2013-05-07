package com.demonzym.smartbox.data;

import com.demonzym.framework.util.Util;

public class UserInfo {
	
	public static String uid = "";
	public static String username = "";
	public static String pwd = "";
	
	public static boolean isOnline(){
		return !(Util.isStringEmpty(username) && Util.isStringEmpty(pwd));
	}

	public static void clear() {
		uid = "";
		username = "";
		pwd = "";
	}
}
