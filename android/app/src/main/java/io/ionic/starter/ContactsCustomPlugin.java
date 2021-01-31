package io.ionic.starter;

import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;

import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;


import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@NativePlugin
public class ContactsCustomPlugin extends Plugin {
    public  static final String mBackupFileName = "8to10Backup.vcf";
    public static final String mAppDirectoryPath = Environment.getExternalStorageDirectory() + File.separator + "8to10Backup";
    public static final String TAG = "CODE_TEST";

    private final HashMap<String, List<String>> mContacts = new HashMap<String, List<String>>();


    @PluginMethod
    public void restorationPointExists(PluginCall call) {
        JSObject ret = new JSObject();

        ret.put("exists", restorationPointHasBeenCreated());

        call.success(ret);
    }


    @PluginMethod
    public void deleteUserContacts(PluginCall call) {

        JSObject ret = new JSObject();

        ret.put("deleted", deleteUserContacts(this.getContext().getContentResolver()));

        call.success(ret);
    }


    @PluginMethod
    public void openRestorationPointFile(PluginCall call) {
        JSObject ret = new JSObject();
        Log.d(TAG, "openRestorationPointFile: " + this.getActivity().getApplicationContext().getPackageName() + ".provider");
        boolean opened = openVcfFile();

        ret.put("opened", opened);

        call.success(ret);
    }

    @PluginMethod
    public void updateUserContacts(PluginCall call) {
        this.loadContacts(this.getContext().getContentResolver());
        JSObject ret = new JSObject();

        for (Map.Entry<String, List<String>> mapEntry : mContacts.entrySet()){

            String contactId = mapEntry.getKey();
            for(String phoneNumber : mapEntry.getValue()) {
                boolean updated = updatePhoneNumberFromContacts(this.getContext().getContentResolver(), phoneNumber,  convertToTenFormat(phoneNumber));
                if(!updated) {
                    ret.put("updated", updated);
                    break;
                }

            }
        }
        ret.put("updated", true);

        call.success(ret);
    }


    @PluginMethod
    public void undoUpdateUserContacts(PluginCall call) {
        this.loadContacts(this.getContext().getContentResolver());

        JSObject ret = new JSObject();

        for (Map.Entry<String, List<String>> mapEntry : mContacts.entrySet()){
            for(String phoneNumber : mapEntry.getValue()) {
                boolean restored = updatePhoneNumberFromContacts(this.getContext().getContentResolver(), phoneNumber,  convertToEightFormat(phoneNumber));
                if(!restored) {
                    ret.put("restored", restored);
                    break;
                }
            }
        }
        ret.put("restored", true);

        call.success(ret);
    }

    @PluginMethod
    public void saveUserContactsAsVcf(PluginCall call) {

        boolean saved = createRestorationPoint(this.getContext().getContentResolver());
        JSObject ret = new JSObject();
        ret.put("saved", saved);
        call.success(ret);
    }



    @PluginMethod
    public void countPhoneNumbers(PluginCall call) {
        Cursor cursor = this.getContext().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        JSObject ret = new JSObject();
        if (cursor != null) {
            ret.put("count", cursor.getCount());
            cursor.close();
        } else {
            ret.put("count", 0);
        }

        call.success(ret);
    }

    private String updatePhoneNumber(String eightFormat) {
        if(eightFormat.length() != 8)
            return eightFormat;

        boolean isLandLine = Integer.parseInt(eightFormat.substring(0, 1)) >= 2 && Integer.parseInt(eightFormat.substring(0, 1)) <= 3;

        if(isLandLine) {
            final int differentiators =  Integer.parseInt(eightFormat.substring(2, 3));

            if(differentiators == 8)
                return "21" + eightFormat;
            else if(differentiators == 0)
                return "25" + eightFormat;
            else
                return "27" + eightFormat;
        } else {
            final int differentiators =  Integer.parseInt(eightFormat.substring(1, 2));

            if(differentiators >= 0 && differentiators <= 3)
                return "01" + eightFormat;
            else if(differentiators >= 4 && differentiators <= 6)
                return "05" + eightFormat;
            else {
                return "07" + eightFormat;
            }
        }
    }

    public String convertToTenFormat(String oldPhoneNumber) {
        final String INTERNATIONALIZATION_00225 =  "00225";
        final String INTERNATIONALIZATION_PLUS_225 =  "+225";

        oldPhoneNumber = oldPhoneNumber.replaceAll("\\s+","");

        if(oldPhoneNumber.length() == 13 && oldPhoneNumber.startsWith(INTERNATIONALIZATION_00225))
            return INTERNATIONALIZATION_00225 + this.updatePhoneNumber(oldPhoneNumber.substring(5));
        else if(oldPhoneNumber.length() == 12 && oldPhoneNumber.startsWith(INTERNATIONALIZATION_PLUS_225))
            return INTERNATIONALIZATION_PLUS_225 + this.updatePhoneNumber(oldPhoneNumber.substring(4));
        else if(oldPhoneNumber.length() == 8)
            return updatePhoneNumber(oldPhoneNumber);
        return oldPhoneNumber;
    }





    /*

     ** private methods
     */

    private boolean openVcfFile() {
        if(restorationPointHasBeenCreated()) {

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            File file = new File(getVcfFileBackupPath());

            Context context = this.getContext();
            Uri uri = FileProvider.getUriForFile(context, this.getActivity().getApplicationContext().getPackageName() + ".provider", file);

            intent.setDataAndType(uri, "text/x-vcard");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


            if(intent.resolveActivity(context.getPackageManager()) != null) {
                this.getActivity().startActivity(intent);
                return true;
            }
        }
        return false;
    }

    private boolean deleteUserContacts(ContentResolver contentResolver) {
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null,  null, null);
        while (cursor.moveToNext()) {
            String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
            contentResolver.delete(uri, null, null);
        }

        cursor.close();
       return true;
    }

    private boolean updatePhoneNumberFromContacts(ContentResolver contentResolver, String oldPhoneNumber, String newPhoneNumber)
    {
        boolean hasBeenUpdated = false;
        //change selection for number
        String where = String.format(
                "%s = '%s' AND %s = ?",
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Phone.NUMBER);

        String[] args = {oldPhoneNumber};
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        operations.add(
                ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(where, args)
                        .withValue( ContactsContract.CommonDataKinds.Phone.NUMBER, newPhoneNumber)
                        .build()
        );
        try {

            contentResolver.applyBatch(ContactsContract.AUTHORITY, operations);
            hasBeenUpdated = true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return hasBeenUpdated;
    }

    private void loadContacts(ContentResolver contentResolver) {


        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);


        if ((cursor != null ? cursor.getCount() : 0) > 0) {


            while (cursor.moveToNext()) {
                String id = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));


                if (cursor.getInt(cursor.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);

                    List<String> phoneNumbers = new ArrayList<>();
                    while (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNumbers.add(phoneNumber);
                    }
                    mContacts.put(name, phoneNumbers);
                    phoneCursor.close();
                }
            }
        }

        if (cursor != null)
            cursor.close();
    }



    private boolean createRestorationPoint(ContentResolver contentResolver) {

        if(restorationPointHasBeenCreated())
            return true;

        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        boolean mRestorationPointHasBeenCreatedSuccessFully = true;

        if ((cursor != null ? cursor.getCount() : 0) > 0) {


            while (cursor.moveToNext()) {
                String lookUpKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookUpKey);

                AssetFileDescriptor fileDescriptor;

                try {
                    fileDescriptor = contentResolver.openAssetFileDescriptor(uri, "r");

                    FileInputStream fileInputStream = fileDescriptor.createInputStream();
                    byte[] buf = new byte[(int) fileDescriptor.getDeclaredLength()];
                    fileInputStream.read(buf);

                    String vCard = new String(buf);
                    FileOutputStream fileOutputStream = new FileOutputStream(getVcfFileBackupPath(), true);
                    fileOutputStream.write(vCard.getBytes());

                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    mRestorationPointHasBeenCreatedSuccessFully = false;
                }

            }
        }


        if (cursor != null)
            cursor.close();
        return mRestorationPointHasBeenCreatedSuccessFully;
    }

    private String getVcfFileBackupPath() {
        return mAppDirectoryPath + File.separator + mBackupFileName;
    }

    private boolean restorationPointHasBeenCreated() {
        return new File(getVcfFileBackupPath()).exists();
    }



    public String undoUpdatePhoneNumber(String phoneNumber) {
        return phoneNumber.substring(2);
    }


    public String convertToEightFormat(String oldPhoneNumber) {
        final String INTERNATIONALIZATION_00225 =  "00225";
        final String INTERNATIONALIZATION_PLUS_225 =  "+225";

        oldPhoneNumber = oldPhoneNumber.replaceAll("\\s+","");

        if(oldPhoneNumber.length() == 15 && oldPhoneNumber.startsWith(INTERNATIONALIZATION_00225))
            return INTERNATIONALIZATION_00225 + this.undoUpdatePhoneNumber(oldPhoneNumber.substring(5));
        else if(oldPhoneNumber.length() == 14 && oldPhoneNumber.startsWith(INTERNATIONALIZATION_PLUS_225))
            return INTERNATIONALIZATION_PLUS_225 + this.undoUpdatePhoneNumber(oldPhoneNumber.substring(4));
        else if(oldPhoneNumber.length() == 10)
            return undoUpdatePhoneNumber(oldPhoneNumber);
        return oldPhoneNumber;
    }
}
