package com.esg.user;




import org.json.JSONObject;

import com.esg.user.R;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PayActivity extends Activity {
	
	private IWXAPI api;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay);
		
		api = WXAPIFactory.createWXAPI(this, "wxbf2d3471574cbb22");

		Button appayBtn = (Button) findViewById(R.id.appay_btn);
		appayBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Button payBtn = (Button) findViewById(R.id.appay_btn);
				payBtn.setEnabled(false);
				Toast.makeText(PayActivity.this, "获取订单中...", Toast.LENGTH_SHORT).show();
		        try{
					byte[] buf = Util.httpGet(Constants.url);
					if (buf != null && buf.length > 0) {
						String content = new String(buf);
						Log.e("get server pay params:",content);
						Toast.makeText(PayActivity.this, content, Toast.LENGTH_LONG).show();
//						JSONObject json = new JSONObject(jsonArray.getString(content));
			        	JSONObject json = new JSONObject(content); 
						if(null != json && !json.has("retcode") ){
							PayReq req = new PayReq();
							req.appId = Constants.APP_ID;
//							req.appId			= json.getString("appid");
							req.partnerId		= json.getString("partnerid");
							req.prepayId		= json.getString("prepayid");
							req.nonceStr		= json.getString("noncestr");
							req.timeStamp		= json.getString("timestamp");
							req.packageValue	= json.getString("package");
							req.sign			= json.getString("sign");
//							req.extData			= "app data"; // optional
//							PayReq request = new PayReq();
//							request.appId = "wxd930ea5d5a258f4f";
//							request.partnerId = "1900000109";
//							request.prepayId= "1101000000140415649af9fc314aa427";
//							request.packageValue = "Sign=WXPay";
//							request.nonceStr= "1101000000140429eb40476f8896f4c9";
//							request.timeStamp= "1398746574";
//							request.sign= "7FFECB600D7157C5AA49810D2D8F28BC2811827B";
//							api.sendReq(request);
							Toast.makeText(PayActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
							// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
							api.sendReq(req);
						}else{
				        	Log.d("PAY_GET", "返回错误"+json.getString("retmsg"));
				        	Toast.makeText(PayActivity.this, "返回错误"+json.getString("retmsg"), Toast.LENGTH_SHORT).show();
						}
					}else{
			        	Log.d("PAY_GET", "服务器请求错误");
			        	Toast.makeText(PayActivity.this, "服务器请求错误", Toast.LENGTH_SHORT).show();
			        }
		        }catch(Exception e){
		        	Log.e("PAY_GET", "异常："+e.getMessage());
		        	Toast.makeText(PayActivity.this, "异常："+e.getMessage(), Toast.LENGTH_SHORT).show();
		        }
		        payBtn.setEnabled(true);
			}
		});		
		Button checkPayBtn = (Button) findViewById(R.id.check_pay_btn);
		checkPayBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
				Toast.makeText(PayActivity.this, String.valueOf(isPaySupported), Toast.LENGTH_SHORT).show();
			}
		});
	}
	
}
