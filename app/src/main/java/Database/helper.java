package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import pojo.userLocal;

/**
 * Created by abdull on 4/10/17.
 */

public class helper extends SQLiteOpenHelper {

    public static final String dataBaseName="Famous";
    public static final String table="People";
    public static final int Version=1;
    public static final String UNIQUE_ID = BaseColumns._ID;
    public static final String MATCHID = "MATCHID";
    public static final String STATUS = "STATUS";
    public static final String NUMBER = "NUMBER";
    public static final String TIME = "TIME";
    public static final String EMAIL = "EMAIL";

    // table Query

    String PeopleTable = "CREATE TABLE " + table + "("
            + UNIQUE_ID + " INTEGER PRIMARY KEY,"
            + MATCHID + " TEXT,"
            + NUMBER + " TEXT,"
            + TIME + " TEXT,"
            + EMAIL + " TEXT,"
            + STATUS + " TEXT"
            + ")";
    Context context;

    public helper(Context context) {
        super(context, dataBaseName, null, Version);
        this.context=context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PeopleTable);
        Log.d("Database Table", "Database tables created");
        Toast.makeText(context, "Dataabse create "+db.toString(), Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    public long insertData(String MatchID, String Status, ArrayList numbers, String Email, String time)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        long id=0;

        for (int i = 0; i <numbers.size() ; i++) {

            ContentValues contentValues=new ContentValues();
            contentValues.put(MATCHID,MatchID);
            contentValues.put(NUMBER,numbers.get(i).toString());
            contentValues.put(TIME,time);
            contentValues.put(EMAIL,Email);
            contentValues.put(STATUS,Status);
             id=database.insert(table,null,contentValues);
        }
        Toast.makeText(context, "primary id "+id, Toast.LENGTH_SHORT).show();
        database.close();
        return id;
    }
    public void deleteAll()
    {
        SQLiteDatabase database=this.getReadableDatabase();
        database.delete(table,null,null);
        database.close();
    }
    public ArrayList showRecord()
    {
        ArrayList arrayList=new ArrayList();
        String Select="Select *from "+table;
        SQLiteDatabase database=this.getReadableDatabase();
        Cursor cursor=database.rawQuery(Select,null);
        while(cursor.moveToNext())
        {
            int matchIdLocal=cursor.getColumnIndex(MATCHID);
            int numberLocalPosition=cursor.getColumnIndex(NUMBER);
            int timeLocalPosiiton=cursor.getColumnIndex(TIME);
            int emailLocalPosition=cursor.getColumnIndex(EMAIL);
            int statusLocalPosition=cursor.getColumnIndex(STATUS);
            // create instance
            userLocal user = new userLocal();
            // set data
            user.setEmail(cursor.getString(emailLocalPosition));
            user.setMatchID(cursor.getString(matchIdLocal));
            user.setNumber(cursor.getString(numberLocalPosition));
            user.setTime(cursor.getString(timeLocalPosiiton));
            user.setStatus(cursor.getString(statusLocalPosition));

            arrayList.add(user);
        }
        database.close();
        return arrayList;
    }
//    public void updateRecord(long Position,String name,String inspiration)
//    {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(NAME, name);
//        values.put(Inspiration, inspiration);
//        String[] args = new String[]{++Position+""};
//        long id=db.update(table, values, "_id=?", args);
//        Toast.makeText(context,"id is "+id,Toast.LENGTH_LONG).show();
//        db.close();
//
//    }
//
//    public void deleteSpecificRecord(long id)
//    {
//        SQLiteDatabase database=this.getReadableDatabase();
//        String[] args = new String[]{++id+""};
//        database.delete(table,"_id=?",args);
//        database.close();
//    }

//
//    public ArrayList searchRecoid(String searchData)
//    {
//        ArrayList arrayList=new ArrayList();
//        String Select="Select *from "+table+" where "+NAME+"='"+searchData+"'"+" OR "+Inspiration+"='"+searchData+"'";
//        SQLiteDatabase database=this.getReadableDatabase();
//        Cursor cursor=database.rawQuery(Select,null);
//
//
//        while(cursor.moveToNext())
//        {
//            int namePosition=cursor.getColumnIndex(NAME);
//            int inspirationPostion=cursor.getColumnIndex(Inspiration);
//            String data="Name is "+cursor.getString(namePosition)+"\n Inspiration is "+cursor.getString(inspirationPostion);
//            arrayList.add(data);
//        }
//        database.close();
//        return arrayList;
//    }
}


