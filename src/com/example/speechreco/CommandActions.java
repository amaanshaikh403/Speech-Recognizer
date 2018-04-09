package com.example.speechreco;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.Toast;


public class CommandActions {

    static String repeatedname;

    static int count;

    public static String fetchContacts(Context mContext, String matchName, boolean isMultipleCheck) {

        count=0;
        repeatedname = "";

        String phoneNumber = null;

        String email = null;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        ContentResolver contentResolver = mContext.getContentResolver();

        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

        Utils.print("Count "+cursor.getCount());
        // Loop for every contact in the phone
        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                Utils.print("Name: " + name + " Matchname: " + matchName);
                boolean isFound = false;

                if(name !=null && name.toLowerCase().startsWith(matchName.toLowerCase())){
                    Utils.print("matched...");

                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));

                    if (hasPhoneNumber > 0) {

                        Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);

                        while (phoneCursor.moveToNext()) {
                            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                            Toast.makeText(mContext, "Found "+ phoneNumber , 60000).show();

                            if(phoneNumber != null){


                                if(isMultipleCheck){
                                    count++;
                                    repeatedname = repeatedname  + "             " + name;
                                }else{
                                    count++;
                                    isFound = true;
                                    break;
                                }


                            }
                        }
                        phoneCursor.close();

                 }

                if(isFound) {
                    break;

                }

                }


            }
            cursor.close();

        }
        return phoneNumber;
    }
}
