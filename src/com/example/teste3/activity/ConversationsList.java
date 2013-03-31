package com.example.teste3.activity;

import java.io.File;

import com.example.teste3.ApplicationManager;
import com.example.teste3.Conversation;
import com.example.teste3.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class ConversationsList extends Activity {

	private ConversationsAdapter adapter;
	private Conversation selectedConversation;
	private int conversationIndex;
	private RefreshHandler redrawHandler = new RefreshHandler();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ApplicationManager.appendLog(Log.DEBUG, "ok", "ConversationList Start");
		setContentView(R.layout.conversation_list);
		
		try {
			adapter = ApplicationManager.getInstance().getClient().getAdapter();
			ApplicationManager.getInstance().getClient().setRedrawHandler(redrawHandler);
		} catch (Exception e) {
			e.printStackTrace();
		}

		final ListView list = (ListView) findViewById(R.id.conversationList);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long index) {
				adapter.setMessAdapter(new MessagesAdapter(view.getContext(), adapter.getConversations().get(position).getMessages(), adapter.getConversations().get(position).getContactName(), position));
				Intent intent = new Intent();
				intent.setClass(view.getContext(), MessagesList.class);
				startActivity(intent);
			}
		});
		list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
				selectedConversation = (Conversation) adapter.getItem(position);
		        registerForContextMenu(list);
		        conversationIndex = position;
		        return false;
			}
		});
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_popup_conversationslist, menu);
	}

	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.delete:
			try {
				ApplicationManager.getInstance().getClient().deleteConversation(selectedConversation);
			} catch (Exception e) {
				e.printStackTrace();
				ApplicationManager.appendLog(Log.ERROR, "Conversation", "Deleting conversation failed");
			}
			break;
			
		case R.id.showConv:
			adapter.setMessAdapter(new MessagesAdapter(this, selectedConversation.getMessages(), selectedConversation.getContactName(), conversationIndex));
			Intent intent = new Intent();
			intent.setClass(this, MessagesList.class);
			startActivity(intent);
			break;

		default:
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_client, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.disconnectC:
        	startActivity(new Intent(this, ProfileTypeChooser.class));
        	try {
				ApplicationManager.getInstance().exitClient();
			} catch (Exception e) {
				e.printStackTrace();
			}
            return true;
        case R.id.showlog:
        	startActivity(new Intent(this, LogPage.class));
            return true;
        case R.id.deletelog:
    		File sdcard = Environment.getExternalStorageDirectory();
        	File file = new File(sdcard,"/RoodroidLog.log");
        	boolean deleted = file.delete();
        	if(deleted == false) {
        		ApplicationManager.appendLog("Log deleting", "log deleting failed");
        		Toast.makeText(this, "Log deleting failed", Toast.LENGTH_SHORT).show();
        	}
        	return true;
        }
        return false;
    }
    
    public class RefreshHandler extends Handler {  
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ApplicationManager.CLIENT_DISCONNECTED:
        		Toast.makeText(ApplicationManager.getInstance().getApplicationContext(), "Client connection timed out", Toast.LENGTH_LONG).show();
				finish();
				break;
			}
		}
    }
}