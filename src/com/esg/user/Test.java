package com.esg.user;

import android.util.Log;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		byte[] buf = Util.httpGet(Constants.url);
		if (buf != null && buf.length > 0) {
			String content = new String(buf);
			Log.e("get server pay params:",content);

	}

}
}
