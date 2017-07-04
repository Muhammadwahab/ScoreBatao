package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;

import pojo.localdata;
import utility.utilityConstant;

/**
 * Created by abdull on 4/10/17.
 */

public class helper extends SQLiteOpenHelper {

    public static final String dataBaseName = "Famous";
    public static final String table = "People";
    public static final int Version = 1;
    public static final String UNIQUE_ID = BaseColumns._ID;
    public static final String MATCHID = "MATCHID";
    public static final String STATUS = "STATUS";
    public static final String MATCH_STATUS = "MATCHSTATUS";

    public static final String NAME = "NAME";
    public static final String UPDATE = "MATCHUPDATE";
    public static final String NUMBER = "NUMBER";
    public static final String REQUEST = "REQUEST";
    public static final String EMAIL = "EMAIL";

    // table Query

    String PeopleTable = "CREATE TABLE " + table + "("
            + UNIQUE_ID + " INTEGER PRIMARY KEY,"
            + MATCHID + " TEXT,"
            + NUMBER + " TEXT,"
            + REQUEST + " TEXT,"
            + EMAIL + " TEXT,"
            + MATCH_STATUS + " TEXT,"
            + NAME + " TEXT,"
            + UPDATE + " TEXT,"
            + STATUS + " TEXT"
            + ")";
    Context context;

    public helper(Context context) {
        super(context, dataBaseName, null, Version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PeopleTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public long insertData(ArrayList numbers) {
        SQLiteDatabase database = this.getWritableDatabase();
        long id = 0;

        for (int i = 0; i < numbers.size(); i++) {
            localdata localdata = (pojo.localdata) numbers.get(i);

            ContentValues contentValues = new ContentValues();
            contentValues.put(MATCHID, localdata.getMatchID());
            contentValues.put(NUMBER, localdata.getPhonenumber());
            contentValues.put(REQUEST, localdata.getRequest());
            contentValues.put(EMAIL, localdata.getEmail());
            contentValues.put(STATUS, localdata.getStatus());
            contentValues.put(MATCH_STATUS, localdata.getMatchStatus());
            contentValues.put(UPDATE, localdata.getUpdate());
            contentValues.put(NAME, localdata.getName());
            id = database.insert(table, null, contentValues);
        }
        utilityConstant.showToast(context, "primary id " + id);
        database.close();
        return id;
    }

    public void deleteAll() {
        SQLiteDatabase database = this.getReadableDatabase();
        database.delete(table, null, null);
        database.close();
    }

    public ArrayList showRecord() {
        ArrayList arrayList = new ArrayList();
        String Select = "Select *from " + table;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(Select, null);
        while (cursor.moveToNext()) {
            localdata user = new localdata();
            // set data
            user.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL)));
            user.setMatchID(cursor.getString(cursor.getColumnIndex(MATCHID)));
            user.setPhonenumber(cursor.getString(cursor.getColumnIndex(NUMBER)));
            user.setRequest(cursor.getString(cursor.getColumnIndex(REQUEST)));
            user.setStatus(cursor.getString(cursor.getColumnIndex(STATUS)));
            user.setUpdate(cursor.getString(cursor.getColumnIndex(UPDATE)));
            user.setName(cursor.getString(cursor.getColumnIndex(NAME)));
            user.setMatchStatus(cursor.getString(cursor.getColumnIndex(MATCH_STATUS)));

            arrayList.add(user);
        }
        database.close();
        return arrayList;
    }

    public ArrayList getSpecificRecord(String RequestCheck) {
        String interval = "INTERVAL";
        ArrayList arrayList = new ArrayList();
        String Select = "Select *from " + table + " where " + UPDATE + "='" + interval + "' and " + STATUS + "='" + utilityConstant.ON + "' and " + REQUEST + "='" + RequestCheck + "'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(Select, null);
        // String where = UPDATE+"=?";
        // String[] args = {"INTERVAL"};
        //   Cursor cursor= database.query(table,null,where,args,null,null,null);
        while (cursor.moveToNext()) {
            localdata user = new localdata();
            // set data
            user.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL)));
            user.setMatchID(cursor.getString(cursor.getColumnIndex(MATCHID)));
            user.setPhonenumber(cursor.getString(cursor.getColumnIndex(NUMBER)));
            user.setRequest(cursor.getString(cursor.getColumnIndex(REQUEST)));
            user.setStatus(cursor.getString(cursor.getColumnIndex(STATUS)));
            user.setUpdate(cursor.getString(cursor.getColumnIndex(UPDATE)));
            user.setName(cursor.getString(cursor.getColumnIndex(NAME)));
            user.setMatchStatus(cursor.getString(cursor.getColumnIndex(MATCH_STATUS)));

            arrayList.add(user);
        }
        database.close();
        return arrayList;
    }
    public ArrayList getAllEvent() {
        String event = "EVENT";
        ArrayList arrayList = new ArrayList();
        String Select = "Select *from " + table + " where " + UPDATE + "='" + event + "' and " + STATUS + "='" + utilityConstant.ON + "'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(Select, null);
        // String where = UPDATE+"=?";
        // String[] args = {"INTERVAL"};
        //   Cursor cursor= database.query(table,null,where,args,null,null,null);
        while (cursor.moveToNext()) {
            localdata user = new localdata();
            // set data
            user.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL)));
            user.setMatchID(cursor.getString(cursor.getColumnIndex(MATCHID)));
            user.setPhonenumber(cursor.getString(cursor.getColumnIndex(NUMBER)));
            user.setRequest(cursor.getString(cursor.getColumnIndex(REQUEST)));
            user.setStatus(cursor.getString(cursor.getColumnIndex(STATUS)));
            user.setUpdate(cursor.getString(cursor.getColumnIndex(UPDATE)));
            user.setName(cursor.getString(cursor.getColumnIndex(NAME)));
            user.setMatchStatus(cursor.getString(cursor.getColumnIndex(MATCH_STATUS)));

            arrayList.add(user);
        }
        database.close();
        return arrayList;
    }

    public ArrayList getTimerCounts() {
        String interval = "INTERVAL";
        ArrayList arrayList = new ArrayList();
        String Select = "Select distinct " + REQUEST + " from " + table + " where " + UPDATE + "='" + interval + "' and " + STATUS + "='" + utilityConstant.ON + "'" + " order by cast(" + REQUEST + " as INTEGER)";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(Select, null);
        //    String where = UPDATE+"=?";
        // String[] args = {"INTERVAL"};
        //   Cursor cursor= database.query(table,null,where,args,null,null,null);
        while (cursor.moveToNext()) {
            localdata user = new localdata();
            user.setRequest(cursor.getString(cursor.getColumnIndex(REQUEST)));
            arrayList.add(user);
        }
        database.close();
        return arrayList;
    }
}


