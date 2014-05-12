package me.akulakovsky.josyko_test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityCreadentials extends Activity implements OnClickListener {

	/*
	 * Views
	 */
	private EditText email, password;
	private Button btnSaveCredentials;
	
	
	/*
	 * Constants
	 */
	private final String TAG = this.getClass().getName();
	
	
	
	/*
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.act_credentials);
		
		this.email = (EditText) findViewById(R.id.email);
		this.password = (EditText) findViewById(R.id.password);
		
		this.btnSaveCredentials = (Button) findViewById(R.id.btnSaveCredentials);
		this.btnSaveCredentials.setOnClickListener(this);
		
		String[] cred = new String[2];
		getCredentials(cred);
		
		email.setText(cred[0]);
		password.setText(cred[1]);
		
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
	
	
	/*
	 * 
	 */
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btnSaveCredentials){
			
			String email = this.email.getText().toString().trim();
			String password = this.password.getText().toString().trim();
			
			if(!isEmailValid(email)){
				Toast.makeText(this, "Please enter valid Email address.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if(TextUtils.isEmpty(password) || password.length() < 6){
				Toast.makeText(this, "Please enter valid password. Minimum 6 symbols (A-Z a-z _ -)", Toast.LENGTH_SHORT).show();
				return;
			}
			
			SharedPreferences sp = getSharedPreferences("SMSSpy", Context.MODE_PRIVATE);
			
			String currentEmail = sp.getString("email", null);
			String currentPass = sp.getString("password", null);
			

			boolean justInserted = (TextUtils.isEmpty(currentEmail) && TextUtils.isEmpty(currentPass));
			
			Intent data = new Intent();

			if(!email.equals(currentEmail) || !password.equals(currentPass)){
				
				sp.edit().putString("email", email).commit();
				sp.edit().putString("password", password).commit();
				
				data.putExtra("modified", true);
				data.putExtra("just_entered", justInserted);
				
			} else {
				data.putExtra("modified", false);
			}
			
			if (getParent() == null) {
				setResult(Activity.RESULT_OK, data);
			} else {
				getParent().setResult(Activity.RESULT_OK, data);
			}
			
			finish();
			
		}
	}
	
	
	
	/**
	 * Validates email
	 * @param emailstring - string that contains email
	 * @return true if email valid, false otherwise
	 */
	public boolean isEmailValid(String emailstring) {
		if(TextUtils.isEmpty(emailstring)) return false;
		Pattern emailPattern = Pattern.compile(".+@.+\\.[a-z]+");
		Matcher emailMatcher = emailPattern.matcher(emailstring);
		return emailMatcher.matches();
	}
	
	
	
}
