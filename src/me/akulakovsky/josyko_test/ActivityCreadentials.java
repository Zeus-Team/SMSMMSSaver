package me.akulakovsky.josyko_test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
			
			if(TextUtils.isEmpty(password)){
				Toast.makeText(this, "Please enter valid password.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			SharedPreferences sp = getSharedPreferences("SMSSpy", Context.MODE_PRIVATE);
			
			sp.edit().putString("email", email).commit();
			sp.edit().putString("password", password).commit();
			
			this.finish();
			
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
