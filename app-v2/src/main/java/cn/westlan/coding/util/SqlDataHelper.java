package cn.westlan.coding.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cn.westlan.coding.core.bean.Identifier;
import cn.westlan.coding.core.bean.PrintContent;
import cn.westlan.coding.core.bean.PrintTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@SuppressLint("Range")
public class SqlDataHelper extends SQLiteOpenHelper
{
	private static final String DB_TABLENAME = "print_content";

	private static final String createSql = "create table if not exists print_content ( _id INTEGER PRIMARY KEY AUTOINCREMENT,name varchar(128) NOT NULL,content BLOB)";
	private static final String insertSql = "insert into print_content (name,content) values (?, ?)";
	private static final String updateSql = "update print_content set content = ? where name = ?";
//	private static final String saveOrUpdateSql = "insert into print_content (_id, name,content) values (?, ?, ?) on DUPLICATE KEY update name = ?, content = ?";
	private static SqlDataHelper dbHelper = null;

	private final Identifier identifier;

	public SqlDataHelper(Context paramContext, Identifier identifier)
	{
		super(paramContext, String.format(Locale.getDefault(), "content2_%s.db", identifier.getName()), null, 1);
		this.identifier = identifier;
	}

	public static SqlDataHelper getInstance(Context paramContext, Identifier identifier)
	{
		if (dbHelper == null){
			dbHelper = new SqlDataHelper(paramContext, identifier);
		}
		return dbHelper;
	}

	public List<PrintTemplate> getTemplates(){
		LinkedList<PrintTemplate> templates = new LinkedList<>();
		SQLiteDatabase sqLiteDatabase  = getReadableDatabase();
		try{
			Cursor cursor = getReadableDatabase().rawQuery("select * from print_content", null);
			if (cursor != null){
				while (true)
				{
					if (!cursor.moveToNext())
					{
						cursor.close();
						return templates;
					}
					int id = cursor.getInt(cursor.getColumnIndex("_id"));
					String name = cursor.getString(cursor.getColumnIndex("name"));
					byte[] arrayOfByte = cursor.getBlob(cursor.getColumnIndex("content"));
					if (arrayOfByte == null)
						continue;
					try {
						PrintContent printContent = new PrintContent();
						printContent.readFrom(new ByteArrayInputStream(arrayOfByte));
						templates.add(new PrintTemplate(id, name, this.identifier, printContent));
					}  catch (IOException e) {
						Log.e(getClass().getSimpleName(), "getContentMap exception", e);
					}
				}
			}
		}finally {
			sqLiteDatabase.close();
		}
		return templates;
	}

	public PrintTemplate getTemplate(Integer id){
		SQLiteDatabase sqLiteDatabase  = getReadableDatabase();
		try {
			Cursor cursor = sqLiteDatabase.rawQuery("select * from print_content where _id = ?", new String[]{id.toString()});
			if (cursor != null){
				while (true)
				{
					if (!cursor.moveToNext())
					{
						cursor.close();
						return null;
					}
					String name = cursor.getString(cursor.getColumnIndex("name"));
					byte[] arrayOfByte = cursor.getBlob(cursor.getColumnIndex("content"));
					if (arrayOfByte == null)
						break;
					try {
						PrintContent printContent = new PrintContent();
						printContent.readFrom(new ByteArrayInputStream(arrayOfByte));
						return new PrintTemplate(id, name, identifier, printContent);
					}  catch (IOException e) {
						Log.e(getClass().getSimpleName(), "getConsole exception", e);
					}
				}
			}
		}finally {
			sqLiteDatabase.close();
		}
		return null;
	}

	public boolean addContent(String name, PrintContent printContent){
		SQLiteDatabase sqLiteDatabase = getWritableDatabase();
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			printContent.writeTo(byteArrayOutputStream);
			sqLiteDatabase.execSQL(insertSql, new Object[]{name, byteArrayOutputStream.toByteArray()});
			return true;
		} catch (IOException e) {
			Log.e(getClass().getSimpleName(), "addContent exception", e);
		} catch (SQLException e){
			Log.e(getClass().getSimpleName(), "addContent exception", e);
		}finally {
			sqLiteDatabase.close();
		}
		return false;
	}

	public boolean delete(Integer id){
		SQLiteDatabase sqLiteDatabase = getWritableDatabase();
		try {
			sqLiteDatabase.execSQL("delete from print_content where _id = ?", new Object[]{id});
			return true;
		} catch (SQLException e){
			Log.e(getClass().getSimpleName(), "addContent exception", e);
		}finally {
			sqLiteDatabase.close();
		}
		return false;
	}

//	public boolean UpdateContent(String name, PrintContent printContent){
//		SQLiteDatabase sqLiteDatabase = getWritableDatabase();
//		try {
//			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//			printContent.writeTo(byteArrayOutputStream);
//			sqLiteDatabase.execSQL(updateSql, new Object[]{byteArrayOutputStream.toByteArray(), name});
//			return true;
//		} catch (IOException e) {
//			Log.e(getClass().getSimpleName(), "updateContent exception", e);
//		} catch (SQLException e){
//			Log.e(getClass().getSimpleName(), "updateContent exception", e);
//		}finally {
//			sqLiteDatabase.close();
//		}
//		return false;
//	}

//	public boolean saveOrUpdateContent(Integer id, Console console){
//		SQLiteDatabase sqLiteDatabase = getWritableDatabase();
//		try {
//			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//			console.writeTo(byteArrayOutputStream);
//			byte[] bytes = byteArrayOutputStream.toByteArray();
//			sqLiteDatabase.execSQL(saveOrUpdateSql, new Object[]{id,  "", bytes, "", bytes});
//			return true;
//		} catch (IOException e) {
//			Log.e(getClass().getSimpleName(), "saveOrUpdateContent exception", e);
//		} catch (SQLException e){
//			Log.e(getClass().getSimpleName(), "saveOrUpdateContent exception", e);
//		}finally {
//			sqLiteDatabase.close();
//		}
//		return false;
//	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(createSql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "drop table if exists " + DB_TABLENAME;
		db.execSQL(sql);
		onCreate(db);
	}
}
