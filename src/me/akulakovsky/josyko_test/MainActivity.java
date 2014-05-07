package me.akulakovsky.josyko_test;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.akulakovsky.josyko_test.models.Message;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener, OnItemClickListener {

//	private static final String BASE_URL = "http://agent22.bugs3.com";
	
	/*
	 * Views
	 */
	private Button btn_start_stop;
	private EditText etemail;
	private Spinner sptimer;
	private TextView tvdisptimer;
	private Button btn_get_send_message;
	
	private CountDownTimer count;
//	private JSONObject jsonObject;
	private String address_email = null;
	private String time = null;
	private boolean start = false;
//	HashMap<String, String> mapKeys = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		setContentView(R.layout.main);

		etemail = (EditText) findViewById(R.id.etemail);
		tvdisptimer = (TextView) findViewById(R.id.tvdisptimer);
		
		sptimer = (Spinner) findViewById(R.id.sptimer);
		sptimer.setOnItemClickListener(this);
		
		btn_get_send_message = (Button) findViewById(R.id.btn_get_send_message);
		btn_get_send_message.setOnClickListener(this);

		btn_start_stop = (Button) findViewById(R.id.btn_start_stop);
		btn_start_stop.setOnClickListener(this);
		
	}
	
	
	@Override
	public void onClick(View view) {
//		boolean isValidated = false;
//		if (eMailValidation(etemail.getText().toString())) {
//			if (sptimer.getSelectedItem().toString().equals("None"))
//
//				Toast.makeText(this, "Please Select A time", Toast.LENGTH_LONG).show();
//			else {
//				isValidated = true;
//				
//				address_email = etemail.getText().toString().trim();
//				time = sptimer.getSelectedItem().toString().trim();
//			}
//		}

//		if (!isValidated)
//			return;

		switch (view.getId()) {
		case R.id.btn_get_send_message:
			
			break;

		case R.id.btn_start_stop:
			
			
			
			// HERESY START
			if (btn_start_stop.getText().toString().equals("Start")) {
				start = true;
				
				Integer time = Integer.valueOf(sptimer.getSelectedItem().toString());
				time = time * 60;
				
				count = new CountDownTimer(time * 1000, 1000) {

					public void onTick(long millisUntilFinished) {
						tvdisptimer.setText("Second Remaining: " + millisUntilFinished / 1000);
						Button stsop = (Button) findViewById(R.id.btn_start_stop);
						stsop.setText("Stop");
					}

					public void onFinish() {
						tvdisptimer.setText("done!");
						sptimer.setEnabled(true);
						
						Button stsop = (Button) findViewById(R.id.btn_start_stop);
						stsop.setText("Start");
						
						stsop.performClick();
						
					}
				}.start();
				
			} else {
				start = false;
				
				count.cancel();
				Button stop = (Button) findViewById(R.id.btn_start_stop);
				stop.setText("Start");
				etemail.setText("");
				sptimer.setSelection(0);
				tvdisptimer.setText("");
			}
			break;
		}
		
		sendMessages();
		// HERESY END
	}

//	/**
//	 * Checks if internet connection available, then gets all message available
//	 * in the device and send it to server
//	 */
//	private void sendMessages() {
//		if (AppUtils.isInternetAvailable(this)) {
//			jsonObject = Message.generateRequest(this);
//
//			if (jsonObject != null) {
//				Log.i(TAG, "REQUEST: " + jsonObject.toString());
//				
//				mapKeys = new HashMap<String, String>();
//				mapKeys.put("messages", jsonObject.toString());
//				
//				mapKeys.put("address_email", address_email);
//				mapKeys.put("start", String.valueOf(start));
//				mapKeys.put("time", time);
//				
//				new SendMessagesTask(mapKeys).execute();
//			} else {
//				Toast.makeText(this, R.string.no_messages, Toast.LENGTH_LONG).show();
//			}
//
//		} else {
//			Toast.makeText(this, R.string.no_connection, Toast.LENGTH_LONG).show();
//		}
//	}

//	private class SendMessagesTask extends AsyncTask<Void, Void, String> {
//
//		private ProgressDialog progressDialog;
//		private HashMap<String, String> mapKeys;
//
//		private SendMessagesTask(HashMap<String, String> mapKeys) {
//			this.mapKeys = mapKeys;
//			this.progressDialog = new ProgressDialog(MainActivity.this);
//		}
//
//		@Override
//		protected void onPreExecute() {
//			progressDialog.setMessage(getString(R.string.sending));
//			progressDialog.show();
//		}
//
//		@Override
//		protected String doInBackground(Void... voids) {
//			String result = null;
//
//			HttpClient httpClient = new DefaultHttpClient();
//
//			try {
//				HttpPost httpPost = new HttpPost(BASE_URL + "/josyko.php");
//				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//				nameValuePairs.add(new BasicNameValuePair("messages", mapKeys.get("messages")));
//				
//				nameValuePairs.add(new BasicNameValuePair("address_email", mapKeys.get("address_email")));
//				nameValuePairs.add(new BasicNameValuePair("start", mapKeys.get("start")));
//				nameValuePairs.add(new BasicNameValuePair("time", mapKeys.get("time")));
//				
//				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//				
//				HttpResponse httpResponse = httpClient.execute(httpPost);
//				
//				String sl = httpResponse.getStatusLine().toString();
//				
//				result = EntityUtils.toString(httpResponse.getEntity());
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//			return result;
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//			progressDialog.dismiss();
//			Log.i(TAG, "RESPONSE: " + result);
//
//			new AlertDialog.Builder(MainActivity.this)
//					.setTitle("Success!")
//					.setPositiveButton("Close", null)
//					.setMessage(
//							"Congratulation, your data has been submited.").create().show();
//
//			// if (result != null) {
//			// Toast.makeText(MainActivity.this, result,
//			// Toast.LENGTH_LONG).show();
//			// } else {
//			// Toast.makeText(MainActivity.this, R.string.failed_to_send,
//			// Toast.LENGTH_LONG).show();
//			// }
//		}
//	}

	public static final String TAG = MainActivity.class.getName();


	
	
	
	// ==============ZEUS STUB
	
	
	/*
	 * Actions 
	 */
	private static final int ACTION_CONNECTION = 9;
	private static final int ACTION_REGISTER = 10;
	private static final int ACTION_INTERRUPT = 11;
	private static final int ACTION_SEND_MESSAGES = 12;
	
	
	/*
	 * URIs
	 */
	private static final String BASE_URL = "http://josyko.test.zeuselectronics.biz/";
	private static final String URL_LOGIN = "login/";
	private static final String URL_MESSAGES = "messages/";
	
	
//	private static final String LOG_TAG = "MainActivity";
	
	
	/**
	 * Connect to service and check if we have 
	 * record in DB
	 * @return - the device id which is held in DB. 
	 * If no device id then return null.
	 */
	private void connectToService(){
		String uri = BASE_URL + URL_LOGIN + getDeviceId();
		HttpGet request = new HttpGet(uri);
		NetTaskRequest netRequest = new NetTaskRequest(ACTION_CONNECTION, request);
		NetTask task = new NetTask();
		task.execute(netRequest);
	}
	
	
	
	/**
	 * Register device with DB. Sends
	 * @param email
	 * @param password
	 * @return
	 */
	private String register(String email, String password){
		
		return null;
	}
	
	
	
	/**
	 * Returns WiFi module MAC address. 
	 * Use it as the unique device id, as the classic device id has
	 * issues over API level differences.
	 * @return
	 */
	private String getDeviceId(){
		WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE); 
		return manager.getConnectionInfo().getMacAddress();
	}
	
	
	
	/**
	 * Interrupts sending process initiated by timer
	 * updates data in DB according to this event.
	 */
	private void interruptSending(){
		
		
		
	}
	
	
	
	
	/**
	 * 
	 */
	private void sendMessages(){
		
		JSONObject json = Message.generateRequest(this);

		if (json != null) {
//			Log.i(TAG, "REQUEST: " + json.toString());
			
//			mapKeys = new HashMap<String, String>();
//			mapKeys.put("messages", json.toString());
//			
//			mapKeys.put("address_email", address_email);
//			mapKeys.put("start", String.valueOf(start));
//			mapKeys.put("time", time);
			
////			new SendMessagesTask(mapKeys).execute();
			
			HttpPost httpPost = new HttpPost(BASE_URL + URL_MESSAGES + getDeviceId());
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("messages", json.toString()));
			nameValuePairs.add(new BasicNameValuePair("address_email", address_email));
			nameValuePairs.add(new BasicNameValuePair("start", String.valueOf(start)));
			nameValuePairs.add(new BasicNameValuePair("time", time));
			
			try {
				
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				NetTaskRequest request = new NetTaskRequest(ACTION_SEND_MESSAGES, httpPost);
				NetTask task = new NetTask();
				task.execute(request);
				
			} catch (UnsupportedEncodingException e) {
				
				e.printStackTrace();
				
			}
			
			
		} else {
			
			Toast.makeText(this, R.string.no_messages, Toast.LENGTH_LONG).show();
			
		}
		
	}
	
	
	
	
	
	/**
	 * Wrapper for network task request 
	 * @author Maksym Fedyays
	 */
	private static class NetTaskRequest{
		
		private int action;
		
		private HttpUriRequest request;
		
		public NetTaskRequest(int action, HttpUriRequest request){
			
			if(action == 0 || request == null){
				throw new IllegalArgumentException("Not valid arguments passed to NetTaskReqest constructor");
			}
			
			this.action = action;
			this.request = request;
			
		}
		
		public int getAction(){
			return this.action;
		}
		
		public HttpUriRequest getRequest(){
			return this.request;
		}
		
		
	}
	
	
	
	
	
	/**
	 *  
	 * @author Maksym Fedyay
	 */
	private class NetTask extends AsyncTask<NetTaskRequest, Void, String>{

		private NetTaskRequest request;
		
		private ProgressDialog dialog;
		
		@Override
		protected void onPreExecute() {
			this.dialog = new ProgressDialog(MainActivity.this);
			this.dialog.setCancelable(false);
			this.dialog.setTitle(R.string.sending);
			this.dialog.show();
		}
		
		@Override
		protected String doInBackground(NetTaskRequest... params) {
			
			
			
			
			String result = null;
			
			this.request = params[0];
			
			HttpUriRequest request = this.request.getRequest();
			DefaultHttpClient client = new DefaultHttpClient();
			client.setRedirectHandler(new DefaultRedirectHandler());
			
			try {
				HttpResponse response = client.execute(request);
				InputStream is = response.getEntity().getContent();
				
				Scanner scanner = new Scanner(is);
				
				result = scanner.next("\\A");
				
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return result;
			
		}
		
		
		@Override
		protected void onPostExecute(String result) {
			
			this.dialog.dismiss();
			
			if(result == null){
				// Notify about error here;
				return;
			}
			
			JSONObject json = null;
			
			String status = null;
			String errMessage = null;
			
			if(!result.equals("")){
				try {
					json = new JSONObject(result);
					errMessage = json.getString("message");
					status = json.getString("status");
					if(errMessage != null){
						Log.e(TAG, errMessage);
						return;
					}
				} catch (JSONException e) {
					e.printStackTrace();
					return;
				}
			}
			
			switch (this.request.getAction()) {
			case ACTION_CONNECTION:
				
				if(status.equals("ok")){
					// Start Action 
					
				} else if (status.equals("fail")) {
					// Start register action
					
				}
				
				break;
			case ACTION_INTERRUPT:
				
				break;
			case ACTION_REGISTER:
				
				break;
			case ACTION_SEND_MESSAGES:
				
				break;
			}
			
		}
		
		
		

		
	}


	private String item = getResources().getStringArray(R.array.digit_array)[0];
	

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		String s = (String) parent.getAdapter().getItem(position);
		
		if(s.equals(item)) return;
		
		interruptSending();
		
	}
	
	

	
	
}


