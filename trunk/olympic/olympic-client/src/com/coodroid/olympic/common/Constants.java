package com.coodroid.olympic.common;

public class Constants {
	public static final String OlympicDB = "Olympic.db";
	public static final int Version = 1;
//	private static final String server="http://192.168.235.101/ocdemo/index.php?route=olympic/";
	private static final String server="http://coodroid.com/ocdemo/index.php?route=olympic/";
	public static class url{
		private static final String api=server+"api/";
		private static final String user_root=server+"user/";
		public static class api{
			public static final String medal=api+"medal";
			public static final String match=api+"match";
		}
		public static class user{
			public static final String register=user_root+"register";
			public static final String login=user_root+"login";
		}
	}
}