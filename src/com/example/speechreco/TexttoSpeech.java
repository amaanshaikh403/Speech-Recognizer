package com.example.speechreco;


import java.util.Locale;

import android.content.Context;
import android.os.Handler;
import android.speech.tts.TextToSpeech;





public class TexttoSpeech {

    Context mContext;

    TextToSpeech mTts;

    boolean isSpeaking;

    boolean isSpeakInit ;

    onSpeakComplete speakComplete;

    public TexttoSpeech(Context mContext, String message, onSpeakComplete speakComplete){
        this.speakComplete = speakComplete;
        this.mContext = mContext;
        init(message);
    }

    public void init(final String message){


        mTts = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if(status != TextToSpeech.ERROR) {

                    mTts.setLanguage(Locale.US);
                    mTts.speak(message, TextToSpeech.QUEUE_FLUSH, null);
                    handler.postDelayed(runnable, 0);

                }
            }
        });

    }


    Handler handler = new Handler();


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            isSpeaking = mTts.isSpeaking();
            if(isSpeaking){
                isSpeakInit = true;
            }else{
                handler.postDelayed(this, 1000);
            }

            if(isSpeakInit){
               if(!isSpeaking){
                   isSpeakInit = false;
                   close();
                   speakComplete.onComplete();

               }else{
                   handler.postDelayed(this, 1000);
               }
            }else{
                handler.postDelayed(this, 1000);
            }

        }
    };

    public void close(){
        Utils.print("complete");
        if(mTts!=null){
            mTts.shutdown();
            mTts.stop();
            handler.removeCallbacks(runnable);
        }

    }

    public interface onSpeakComplete{
        public void onComplete();
    }
}
