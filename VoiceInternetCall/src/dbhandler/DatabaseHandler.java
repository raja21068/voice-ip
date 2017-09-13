package dbhandler;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
public class DatabaseHandler extends SQLiteOpenHelper {
 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "tracking";
 
    // Contacts table name
    private static final String TABLE_CONTACTS = "contacts";
 
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PH_NO = "phone_number";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_STATUS = "status";
    private static final String KEY_MODE = "mode";
    private static final String KEY_PASSWORD = "password";
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public DatabaseHandler() {
        super(null, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," 
        		+ KEY_NAME + " TEXT,"
                + KEY_PH_NO + " TEXT," 
        		+ KEY_EMAIL + " TEXT," 
                +  KEY_STATUS + " TEXT," 
                +  KEY_MODE + " TEXT," 
        		+ KEY_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
 
        // Create tables again
        onCreate(db);
    }
 
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
 
   // Adding new contact
 
    public  void addContact(String name, String number, String password)throws Exception{
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        
        if(!(name == null || name.equals(""))){
        	values.put(KEY_NAME, name); // Contact Name
        }
            if(!(number == null || number.equals(""))){
        values.put(KEY_PH_NO, number); // Contact Phone
        }
            if(!(password == null || password.equals(""))){
        values.put(KEY_PASSWORD, password); // Contact status
        }
//            if(!(email == null || email.equals(""))){
//        values.put(KEY_EMAIL, email); // Contact Email
//        }
  

        values.put(KEY_ID, 1);
        values.put(KEY_STATUS, "start");
        values.put(KEY_MODE, "sms");
        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
    }
 
    // Getting All Contacts
    public List<ContactBean> getAllContacts() throws Exception {
        List<ContactBean> contactList = new ArrayList<ContactBean>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	ContactBean contact = new ContactBean();
                contact.setID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))));
                contact.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
                contact.setPhoneNumber(cursor.getString(cursor.getColumnIndex(KEY_PH_NO)));
                contact.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
                contact.setStatus(cursor.getString(cursor.getColumnIndex(KEY_STATUS)));
                contact.setMode(cursor.getString(cursor.getColumnIndex(KEY_MODE)));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
 
        // return contact list
        return contactList;
    }
 
    // Updating single contact
    public int updateContact(ContactBean contact) throws Exception{
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
//        System.out.println("bean: "+contact.email+" " +contact.password+" "+ contact.phone_number+" "+contact.status);
        
        
        if(!(contact.getPassword() == null || contact.getPassword().equals(""))){
        	values.put(KEY_PASSWORD, contact.getPassword()); // Contact Name
        }
        if(!( contact.getPhoneNumber() == null || contact.getPhoneNumber().equals(""))){
        values.put(KEY_PH_NO, contact.getPhoneNumber()); // Contact Phone
        }
        if(!(contact.getEmail() == null || contact.getEmail().equals(""))){
        values.put(KEY_EMAIL, contact.getEmail()); // Contact Email
        }
        if(!(contact.getStatus() == null || contact.getStatus().equals(""))){
        values.put(KEY_STATUS, contact.getStatus()); // Contact status
        }     
        if(!(contact.getMode() == null || contact.getMode().equals(""))){
        values.put(KEY_MODE, contact.getMode()); // Contact status
        }     

            
            // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
    }
    
     
    
 
    // Deleting single contact
    public void deleteContact(ContactBean contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
        db.close();
    }
 
 
    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count=cursor.getCount();
        
        cursor.close();
        
         return count;
        //return cursor.getCount();
    }
    
    // Getting contacts password
    public int getPassCheck(String Pass) {
        String countQuery = "SELECT  * FROM  contacts WHERE name= '"+Pass+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count=cursor.getCount();
        cursor.close();
 
        return count;
        //return cursor.getCount();
    }
    
    public ContactBean getContactBean(){
    	String query = "SELECT * FROM "+TABLE_CONTACTS+" WHERE " + KEY_ID + " = 1";
    	SQLiteDatabase db = this.getReadableDatabase();
    	Cursor cursor = db.rawQuery(query, null);
    	ContactBean bean = null;
        if (cursor.moveToFirst()) {
        	bean = new ContactBean();
        	bean.setID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))));
        	bean.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        	bean.setPhoneNumber(cursor.getString(cursor.getColumnIndex(KEY_PH_NO)));
        	bean.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
        	bean.setStatus(cursor.getString(cursor.getColumnIndex(KEY_STATUS)));
        	bean.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
        }
    	return bean;
    }
 
}