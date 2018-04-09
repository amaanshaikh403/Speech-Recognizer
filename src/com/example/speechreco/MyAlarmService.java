package com.example.speechreco ;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

public class MyAlarmService extends Service implements TextToSpeech.OnInitListener, OnUtteranceCompletedListener {
	
	
    private TextToSpeech mTts;
    private String spokenText= "";
   
	 
	
    @Override
    public void onCreate() {
    	// TODO Auto-generated method stub
    	Toast.makeText(this, "MyAlarmService.onCreate()", Toast.LENGTH_LONG).show();
 
    	  
    
    	Log.d("this is drishti","drishti alaram");
 	
    }

    @Override   
    public IBinder onBind(Intent intent) {
    	// TODO Auto-generated method stub
    	Toast.makeText(this, "MyAlarmService.onBind()", Toast.LENGTH_LONG).show();
 
    	Log.d("this is drishti","drishti alaram");
    	return null;
    }
 
    @TargetApi(Build.VERSION_CODES.DONUT) @SuppressLint("NewApi") @Override
    public void onDestroy() {
	
    	// TODO Auto-generated method stub
	
    	if (mTts != null) {
    		mTts.stop();
    		mTts.shutdown();
    	}
	         
    	super.onDestroy();
    	Toast.makeText(this, "MyAlarmService.onDestroy()", Toast.LENGTH_LONG).show();
    }

    @SuppressLint("NewApi") @Override
    public void onStart(Intent intent, int startId) {
	
    	// TODO Auto-generated method stub
    	super.onStart(intent, startId);
    	
    	//Notification(intent.getStringExtra("text"));
    	spokenText = "";
    	spokenText += intent.getStringExtra("text")+"";
 
    	
    	mTts = new TextToSpeech(this, this);
    	Toast.makeText(this, "MyAlarmService.onStart()"+spokenText, Toast.LENGTH_LONG).show();
    	
   
    }

    @Override
    public boolean onUnbind(Intent intent) {
    	// TODO Auto-generated method stub
    	Toast.makeText(this, "MyAlarmService.onUnbind()", Toast.LENGTH_LONG).show();
    	return super.onUnbind(intent);
    }
   
    @Override
    public void onUtteranceCompleted(String utteranceId) {
	// TODO Auto-generated method stub
    	Utils.showToast(this, "Completed text to speech.");
    }
    
    @TargetApi(Build.VERSION_CODES.DONUT) @SuppressLint({ "NewApi", "InlinedApi" }) @Override
    public void onInit(int status) {
	// TODO Auto-generated method stub
	   
	if (status == TextToSpeech.SUCCESS) {
		   
        int result = mTts.setLanguage(Locale.US);
        if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
        	spokenText += spokenText+""+spokenText+"";
            mTts.speak(spokenText, TextToSpeech.QUEUE_FLUSH, null);
        }   
    }   
	             
}



}