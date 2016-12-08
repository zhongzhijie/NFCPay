package com.example.nfcpay;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
	
	private EditText userName, password;
	private Button btn_reg;
	private Button btn_login;
    private String userNameValue,passwordValue;
    SQLiteOpenHelper helper;

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    getActionBar().hide();
	    setContentView(R.layout.splashscreen);
		userName = (EditText) findViewById(R.id.et_zh);
		password = (EditText) findViewById(R.id.et_mima);
	    btn_reg = (Button) findViewById(R.id.btn_reg);
	    btn_login = (Button) findViewById(R.id.btn_login);

	    helper = new SqliteOpenHelper(this);
	    
		btn_login.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{
				userNameValue = userName.getText().toString();
			    passwordValue = password.getText().toString();
			    
			    if(userNameValue.equals("") || passwordValue.equals(""))
			    {
			    	Toast.makeText(Login.this,"请输入用户名和密码", Toast.LENGTH_SHORT).show();
			    }
			    else
			    {
			    	if(checkUser(userNameValue,passwordValue))
			    	{
			    		SharedPreferences login_info = getSharedPreferences("login_info", 0);
			    		SharedPreferences.Editor editor = login_info.edit();
			    		editor.putString("username", userNameValue);
			    		editor.putString("password", passwordValue);
			    		//Editor editor = getSharedPreferences("login_info", 0).edit();
			    		//editor.putString();
			    		editor.commit();
						Intent intent = new Intent(Login.this, MainActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						finish();
			    	}
			    	else
			    	{
			    		Toast.makeText(Login.this,"用户名或密码错误，请重新登录", Toast.LENGTH_SHORT).show();
			    	}
			    }
			}
		});
		
		btn_reg.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{
				userNameValue = userName.getText().toString();
			    passwordValue = password.getText().toString();
			    
			    if(userNameValue.equals("") || passwordValue.equals(""))
			    {
			    	Toast.makeText(Login.this,"请输入用户名和密码", Toast.LENGTH_SHORT).show();
			    }
			    else
			    {
			    	if(regUser(userNameValue,passwordValue))
			    	{
			    		Toast.makeText(Login.this,"注册成功，请登录", Toast.LENGTH_SHORT).show();
			    	}
			    	else
			    	{
			    		Toast.makeText(Login.this,"用户名已经存在，请直接登录", Toast.LENGTH_SHORT).show();
			    	}
			    }
			}
		});
	}
	
	private boolean checkUser(String userName,String passWord)
	{
		SQLiteDatabase sdb = helper.getReadableDatabase();
		String sql = "select * from user where username=? and password=?";
		Cursor cursor = sdb.rawQuery(sql, new String[] {userName, passWord});
		if(cursor.moveToFirst()==true){
			cursor.close();
			return true;
		}
		return false;
	}
	
	private boolean regUser(String userName,String passWord)
	{
		SQLiteDatabase sdb = helper.getReadableDatabase();
		String sql = "select * from user where username=?";
		Cursor cursor = sdb.rawQuery(sql, new String[] {userName});
		if(cursor.moveToFirst()==true){
			cursor.close();
			return false;
		}
		else
		{
	        String sql1="insert into user(username,password,account) values(?,?,?)";
	        Object obj1[]={userName,passWord,0};
	        sdb.execSQL(sql1, obj1);
	        //用来保存用户交易记录，首次注册时交易记录为存款0
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String curTime = sDateFormat.format(new Date());
	        String sql2="insert into translog(date,username,action,value) values(?,?,?,?)";
	        Object obj2[]={curTime,userName,"充值",0};
	        sdb.execSQL(sql2, obj2);
			sdb.close();
	        return true;	
		}
	}
}