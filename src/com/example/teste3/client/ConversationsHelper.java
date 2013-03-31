package com.example.teste3.client;

import com.example.teste3.ApplicationManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * ConversationsHelper
 * Extends SQLiteOpenHelper
 * 
 * Manages database creation and version management for the Conversations.
 *  
 * @author Jonathan Perichon <jonathan.perichon@gmail.com>
 * @author Lucas Gerbeaux <lucas.gerbeaux@gmail.com>
 *
 */
public class ConversationsHelper extends SQLiteOpenHelper {

	public static final String TAB_CONV = "conversations";
	public static final String COL_CONVID = "_id";
	public static final String COL_CONVCONTACTPHONENUMBER = "contact_phone_number";
	public static final String COL_CONVCONTACTNAME = "contact_name";
	
	public static final String TAB_MSG = "messages";
	public static final String COL_MSGID = "_id";
	public static final String COL_MSGIDCONV = "_id_conv";
	public static final String COL_MSGDATE = "date";
	public static final String COL_MSGCONTENT = "content";
	public static final String COL_MSGSTATUS = "status";
	public static final String COL_MSGTYPE = "type";

	private static final String DB_NAME = "messages.db";
	private static final int DB_VERSION = 11;
	private static final String MSG_CREATE = "create table "
			+ TAB_MSG + "( "
			+ COL_MSGID + " integer primary key autoincrement, "
			+ COL_MSGIDCONV + " integer not null, "
			+ COL_MSGDATE + " integer not null, "
			+ COL_MSGSTATUS + " integer not null, "
			+ COL_MSGTYPE + " integer not null, "
			+ COL_MSGCONTENT + " text not null, "
			+ "foreign key(" + COL_MSGIDCONV + ") references "+ TAB_CONV +"(" + COL_CONVID + ") on delete cascade )";
	
	private static final String CONV_CREATE = "create table "
			+ TAB_CONV + "( "
			+ COL_CONVID + " integer primary key autoincrement, "
			+ COL_CONVCONTACTPHONENUMBER + " text not null, "
			+ COL_CONVCONTACTNAME + " text not null )";

	public ConversationsHelper() {
		super(ApplicationManager.getInstance().getApplicationContext(), DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(MSG_CREATE);
		database.execSQL(CONV_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(ConversationsHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TAB_MSG);
		db.execSQL("DROP TABLE IF EXISTS " + TAB_CONV);
		onCreate(db);
	}

}
