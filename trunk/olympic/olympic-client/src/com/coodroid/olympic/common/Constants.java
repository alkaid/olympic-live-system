package com.coodroid.olympic.common;

public class Constants {
	public static final String OlympicDB = "Olympic.db";
	public static final int Version = 1;
	public static final String PWD="www.coodroid.com";
	private static final String server="http://192.168.235.168/ocdemo/index.php?route=olympic/";
//	private static final String server="http://coodroid.com/ocdemo/index.php?route=olympic/";
	public static class url{
		private static final String api_root=server+"api/";
		private static final String user_root=server+"user/";
		public static class api{
			public static final String medal=api_root+"medal";
			public static final String match=api_root+"match";
			public static final String textlive=api_root+"textlive";
			public static final String question=api_root+"question";
			public static final String points=api_root+"points";
		}
		public static class user{
			public static final String register=user_root+"register";
			public static final String login=user_root+"login";
			public static final String modifypwd=user_root+"modifypwd";
			public static final String lottery=user_root+"lottery";
			public static final String history=user_root+"guess/history";
			public static final String guessSubmit=user_root+"guess/answer";
			public static final String userPoint=user_root+"guess/points";
		}
	}
	public static class bundleKey{
		/**正在直播的比赛的信息*/
		public static final String matchOfLive = "matchOfLive";
	}
	public static class cookie{
		public static final String sessionIdKey="PHPSESSID";
		public static final String ZDEDebuggerPresent="ZDEDebuggerPresent";
	}
}
