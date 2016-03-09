package gurbirkalsi.changelog;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {

    //Database Version
    private static final int DATABASE_VERSION = 1;
    //Database Name
    private static final String DATABASE_NAME = "AppDB";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //SQL statement to create app table
        String CREATE_APP_TABLE = "CREATE TABLE apps(" +
                "version_code INTEGER, " +
                "version_name TEXT, " +
                "last_update_time LONG, " +
                "package_name TEXT, " +
                "application_name TEXT, " +
                "changelog_text TEXT);";
        db.execSQL(CREATE_APP_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop older apps table if existed
        db.execSQL("DROP TABLE IF EXISTS apps");

        //Create fresh apps table
        this.onCreate(db);
    }


    /*
    CRUD Operations (Add, Get, Update, Delete)
     */

    //Books table name
    private static final String TABLE_NAME = "apps";

    //Books table columns names
    private static final String KEY_VERSION_CODE = "version_code";
    private static final String KEY_VERSION_NAME = "version_name";
    private static final String KEY_LAST_UPDATE_TIME = "last_update_time";
    private static final String KEY_PACKAGE_NAME = "package_name";
    private static final String KEY_APPLICATION_NAME = "application_name";
    private static final String KEY_CHANGELOG_TEXT = "changelog_text";

    private static final String[] COLUMNS = {KEY_VERSION_CODE, KEY_VERSION_NAME, KEY_LAST_UPDATE_TIME, KEY_PACKAGE_NAME, KEY_APPLICATION_NAME, KEY_CHANGELOG_TEXT};

    public void addApp(App app) {
        Log.d("addApp", app.toString());
        //Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        //Create contentValue to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_VERSION_CODE, app.getVersionNumber());
        values.put(KEY_VERSION_NAME, app.getVersionName());
        values.put(KEY_LAST_UPDATE_TIME, app.getLastUdpateTime());
        values.put(KEY_PACKAGE_NAME, app.getPackageName());
        values.put(KEY_APPLICATION_NAME, app.getApplicationName());
        values.put(KEY_CHANGELOG_TEXT, app.getChangelogText());

        //Insert the database
        db.insert(TABLE_NAME, null, values);

        //Close the database
        db.close();

    }

    public App getApp(String packageName) {

        //Get reference to readable database
        SQLiteDatabase db = this.getReadableDatabase();

        //Build query
        Cursor cursor = db.query(TABLE_NAME, COLUMNS, " packageName = ?", new String[] { packageName }, null, null, null);

        //If we got results get the first one
        if (cursor != null) {
            cursor.moveToFirst();
        }

        //Build App object
        App app = new App();
        app.setVersionNumber(cursor.getInt(0));
        app.setVersionName(cursor.getString(1));
        app.setLastUdpateTime(cursor.getString(2));
        app.setPackageName(cursor.getString(3));
        app.setApplicationName(cursor.getString(4));
        app.setChangelogText(cursor.getString(5));

        Log.d("getApp(" + packageName + ")", app.toString());

        //Return app
        return app;

    }

    // Get All Books
    public List<App> getAllApps() {
        List<App> books = new LinkedList<App>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_NAME;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        App app = null;
        if (cursor.moveToFirst()) {
            do {
                app = new App();
                app.setVersionNumber(cursor.getInt(0));
                app.setVersionName(cursor.getString(1));
                app.setLastUdpateTime(cursor.getString(2));
                app.setPackageName(cursor.getString(3));
                app.setApplicationName(cursor.getString(4));
                app.setChangelogText(cursor.getString(5));

                // Add book to books
                books.add(app);
            } while (cursor.moveToNext());
        }

        Log.d("getAllBooks()", books.toString());

        // return books
        return books;
    }

    //Update single app
    public int updateApp(App app) {

        //Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        //Create contentValue to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_VERSION_CODE, app.getVersionNumber());
        values.put(KEY_VERSION_NAME, app.getVersionName());
        values.put(KEY_LAST_UPDATE_TIME, app.getLastUdpateTime());
        values.put(KEY_PACKAGE_NAME, app.getPackageName());
        values.put(KEY_APPLICATION_NAME, app.getApplicationName());
        values.put(KEY_CHANGELOG_TEXT, app.getChangelogText());

        //Updating row
        int i = db.update(TABLE_NAME, values, KEY_PACKAGE_NAME+" = ?",new String[] { app.getPackageName() });

        //Close database
        db.close();

        return i;

    }

    //Deleting single book
    public void deleteApp(App app) {

        //Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        //Delete
        db.delete(TABLE_NAME, KEY_PACKAGE_NAME+" = ?",new String[] {app.getPackageName()});

        //Close database
        db.close();

        Log.d("deleteBook", app.toString());

    }

}
