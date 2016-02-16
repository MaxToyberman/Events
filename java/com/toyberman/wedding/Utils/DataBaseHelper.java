package com.toyberman.wedding.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.toyberman.wedding.Entities.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maximtoyberman on 29/11/2015.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "contactsManager";

    private static final String TABLE_EVENTS = "events";

    // Contacts Table Columns names
    private static final String EVENT_ID = "eid";
    private static final String PKID = "pkid";
    private static final String EVENT_TITLE = "title";
    private static final String EVENT_GUEST = "guest";
    private static final String GUEST_STATUS = "status";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static String EVENT_PHONE="phone";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_EVENTS + " (" +
            PKID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            EVENT_ID + TEXT_TYPE+COMMA_SEP +
            EVENT_TITLE + TEXT_TYPE + COMMA_SEP +
            EVENT_GUEST + TEXT_TYPE + COMMA_SEP +
            EVENT_PHONE + TEXT_TYPE + COMMA_SEP +
            GUEST_STATUS + TEXT_TYPE + COMMA_SEP +
            " )";
    private static DataBaseHelper mInstance;
    private final Context context;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    public static DataBaseHelper getInstance(Context ctx) {

        if (mInstance == null) {
            mInstance = new DataBaseHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);

        //create tables again
        onCreate(db);
    }


    public void addEntry(SQLiteDatabase db,String eid,String guestName,String status,String number){



        ContentValues values = new ContentValues();
        values.put(EVENT_ID, eid); // Event id
        values.put(EVENT_GUEST,guestName); // guest Name
        values.put(GUEST_STATUS,status); // guest arrival status
        values.put(EVENT_PHONE, number);
        db.insert(TABLE_EVENTS, null, values);


    }

    public void closeDB(){
        mInstance.closeDB();
    }

    public List<Contact> getContacts(String wid){


        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS +" WHERE eid="+wid;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                String name=cursor.getString(1);
                String number=cursor.getString(3);
                Contact contact = new Contact(name,number);
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    public void addContacts(List<Contact> contacts,String eid) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (Contact contact:contacts) {
            addEntry(db,eid,contact.getName(),contact.getStatus(),contact.getPhone_number());
        }
        db.close();

    }
}
