package com.example.speechreco;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

public class SpeechRecognitionActivit extends Activity implements RecognitionListener {
    public static final String CALL_COMMAND = "call";
    public static final String MESSAGE_COMMAND = "message";
    public static final String REMINDER_COMMAND = "reminder";

    private static final int REQUEST_CODE = 1234;

    ArrayList<String> matches_text;

    String currentCommand = null;
    String subCommandName = null;
    String subCommandText = null;

    private TextView returnedText;
    private ToggleButton toggleButton;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
	private Object context;

   

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speech);


        returnedText = (TextView) findViewById(R.id.textView1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);

        startSpeak("Namaskara, Welcome to drishti,  please speak.");


    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @TargetApi(Build.VERSION_CODES.FROYO) @Override
    public void onDestroy() {
        super.onDestroy();

        if (speech != null) {
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }

    }


    public void startSpeak(String message) {
        TexttoSpeech tts = new TexttoSpeech(this, message, new TexttoSpeech.onSpeakComplete() {
            @Override
            public void onComplete() {
                Utils.showToast(getApplicationContext(), "speak completed.");
                startRecognition();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.FROYO) @SuppressLint("NewApi") public void startRecognition() {

        progressBar.setVisibility(View.VISIBLE);
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //Vibrate for 200 milliseconds
       v1.vibrate(200);
        speech.startListening(recognizerIntent);

    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net != null && net.isAvailable() && net.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {


            matches_text = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            for (String s : matches_text) {
                Utils.print("Reading:  " + s);
                returnedText.setText(returnedText.getText().toString() + "\n" + s);
                if (currentCommand == null) {

                    if (s.toLowerCase().contains(CALL_COMMAND)) {
                        currentCommand = CALL_COMMAND;
                        Utils.showToast(this, "Please say contact name");
                        startSpeak("Please say contact name");
                    } else if (s.toLowerCase().contains(MESSAGE_COMMAND)) {
                        currentCommand = MESSAGE_COMMAND;
                        Utils.showToast(this, "Please say contact name.");
                        startSpeak("Please say contact name");
                    } else if (s.toLowerCase().contains(REMINDER_COMMAND)) {
                        currentCommand = REMINDER_COMMAND;
                        Utils.showToast(this, "Please say event name.");
                        startSpeak("Please say event name");
                    } else {
                        startRecognition();
                        currentCommand = null;
                    }

                    break;
                } else if (currentCommand.equalsIgnoreCase(CALL_COMMAND)) {

                    makeCall(s);
                    break;
                } else if (currentCommand.equalsIgnoreCase(MESSAGE_COMMAND)) {
                    sendMessage(s);
                    break;
                } else if (currentCommand.equalsIgnoreCase(REMINDER_COMMAND)) {
                    setReminder(s);
                    break;
                }

            }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setReminder(String eventName) {

        Calendar cal = Calendar.getInstance();

        Calendar calNow = Calendar.getInstance();

        Calendar calSet = (Calendar) calNow.clone();

        Time time = new Time(System.currentTimeMillis());

        int hour = time.getHours();
        int min = time.getMinutes() + 1;

        calSet.set(Calendar.HOUR_OF_DAY, hour);
        calSet.set(Calendar.MINUTE, min);
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);

        Toast.makeText(getApplicationContext(), "alarm time is " + hour + " min:  " + min, 6000).show();

        setAlarm(calSet, eventName);

    }


    @Override
    public void onPause() {
        super.onPause();

    }

    int count = 0;

    private void setAlarm(Calendar targetCal, String name) {


        count++;

        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        intent.putExtra("text", name);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), count, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

    }


    @TargetApi(Build.VERSION_CODES.DONUT) @SuppressLint("NewApi") public void sendMessage(String name) {
        Utils.print("send messagexxxx: " + name);
        if (subCommandName != null) {
            Utils.print("sending message....");
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(subCommandName, null, name, null, null);
            subCommandName = null;
            finish();
        } else {

            String phone = null;
            if (isMultipleMessageFound) {
                phone = CommandActions.fetchContacts(this, name, false);
            } else {
                phone = CommandActions.fetchContacts(this, name, true);
            }


            if (phone != null) {

                if (CommandActions.count > 1) {
                    isMultipleMessageFound = true;
                    Utils.showToast(this, "Multiple phone nos found.  " + CommandActions.repeatedname);
                    startSpeak("Multiple phone nos found.  " + CommandActions.repeatedname);

                } else if (CommandActions.count == 1) {
                    isMultipleMessageFound = false;
                    Utils.showToast(this, "Got number... Say message body...");
                    startSpeak("Please say message body..");
                    subCommandName = phone;
                }

            } else {
//                startRecognition();
                Utils.showToast(this, "Phone no not found. Try again, make sure your are in silent place.");
                startSpeak("Phone no not found. Try again, make sure your are in silent place.");

            }
        }
    }

    boolean isMultipleFound, isMultipleMessageFound;

    public void makeCall(String name) {
        Utils.print("Searching to call: " + name);
        String phone = null;
        if (isMultipleFound) {
            phone = CommandActions.fetchContacts(this, name, false);
        } else {
            phone = CommandActions.fetchContacts(this, name, true);
        }


        if (phone != null) {
            Utils.showToast(this, "count: " + CommandActions.count);
            if (CommandActions.count > 1) {
                isMultipleFound = true;
                Utils.showToast(this, "Multiple phone nos found.  " + CommandActions.repeatedname + CommandActions.count);
                startSpeak("Multiple phone nos found.  " + CommandActions.repeatedname);

            } else if (CommandActions.count == 1) {
                isMultipleFound = false;
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                Utils.showToast(this, "Got number... calling...");
                startActivity(intent);
                Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                //Vibrate for 1000 milliseconds
                v1.vibrate(1000);
                finish();
            }


        } else {
            Utils.showToast(this, "Phone no not found. Try again, make sure your are in silent place.");
            startSpeak("Phone no not found. Try again, make sure your are in silent place.");

        }
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);
        toggleButton.setChecked(false);
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        returnedText.setText(errorMessage);
        toggleButton.setChecked(false);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");

        matches_text = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        for (String s : matches_text) {
            Utils.print("Reading:  " + s);
            returnedText.setText(returnedText.getText().toString() + "\n" + s);
            if (currentCommand == null) {

                if (s.toLowerCase().contains(CALL_COMMAND)) {
                    currentCommand = CALL_COMMAND;
                    Utils.showToast(this, "Please say contact name");
                    startSpeak("Please say contact name");
                } else if (s.toLowerCase().contains(MESSAGE_COMMAND)) {
                    currentCommand = MESSAGE_COMMAND;
                    Utils.showToast(this, "Please say contact name.");
                    startSpeak("Please say contact name");
                } else if (s.toLowerCase().contains(REMINDER_COMMAND)) {
                    currentCommand = REMINDER_COMMAND;
                    Utils.showToast(this, "Please say event name.");
                    startSpeak("Please say event name");
                } else {
                    startRecognition();
                    currentCommand = null;
                }

                break;
            } else if (currentCommand.equalsIgnoreCase(CALL_COMMAND)) {

                makeCall(s);
                break;
            } else if (currentCommand.equalsIgnoreCase(MESSAGE_COMMAND)) {
                sendMessage(s);
                break;
            } else if (currentCommand.equalsIgnoreCase(REMINDER_COMMAND)) {
                setReminder(s);
                break;
            }

        }
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }
}