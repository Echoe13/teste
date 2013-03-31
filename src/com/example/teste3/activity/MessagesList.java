package com.example.teste3.activity;


import java.io.File;

import com.example.teste3.ApplicationManager;
import com.example.teste3.Message;
import com.example.teste3.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MessagesList extends Activity {

	private MessagesAdapter adapter;
    private StringBuffer outStringBuffer;
    private ListView conversationView;
    private EditText outEditText;
    private Button sendButton;
    private Message selectedMessage;
	private RefreshHandler redrawHandler = new RefreshHandler();

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.conversation_view);

        try {
			adapter = ApplicationManager.getInstance().getClient().getAdapter().getMessAdapter();
			ApplicationManager.getInstance().getClient().setRedrawHandler(redrawHandler);
		} catch (Exception e) {
			e.printStackTrace();
			ApplicationManager.appendLog(Log.ERROR, "Message adapter", "Impossible to get Message Adapter");
		}
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	// Initialize the array adapter for the conversation thread
        conversationView = (ListView) findViewById(R.id.in);
        conversationView.setAdapter(adapter);
        conversationView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> adapt, View view, int position, long id) {
				selectedMessage = (Message) adapter.getItem(position);
		        registerForContextMenu(conversationView);
				return false;
			}
        	
		});
        
        // Initialize the compose field with a listener for the return key
        outEditText = (EditText) findViewById(R.id.edit_text_out);
        outEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        sendButton = (Button) findViewById(R.id.button_send);
        sendButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
                // Send a message using content of the edit text widget
                sendMessage(outEditText.getText().toString());
            }
        });

        // Initialize the buffer for outgoing messages
        outStringBuffer = new StringBuffer("");
    }
    
    private void sendMessage(String message) {

        // Check that there's actually something to send
        if (message != null) {
            // Get the client and tell him to send message
        	try {
				ApplicationManager.getInstance().getClient().sendTextMessage(message, ApplicationManager.getInstance().getClient().getAdapter().getConversations().get(adapter.getId()).getContactPhoneNumber());
			} catch (Exception e) {
				e.printStackTrace();
				ApplicationManager.appendLog(Log.ERROR, "Sending message", "Impossible to send a message");
			}

            // Reset out string buffer to zero and clear the edit text field
            outStringBuffer.setLength(0);
            outEditText.setText(outStringBuffer);
        }
        adapter.notifyDataSetChanged();
    }
    
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_popup_messageslist, menu);
	}
	
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.deleteMess:
			try {
				if (ApplicationManager.getInstance().getClient().getAdapter().getConversations().get(adapter.getId()).getMessages().size() == 1) {
					ApplicationManager.getInstance().getClient().deleteMessage(ApplicationManager.getInstance().getClient().getAdapter().getConversations().get(adapter.getId()), selectedMessage);					
					startActivity(new Intent(this, ConversationsList.class));
				}
				else {
					ApplicationManager.getInstance().getClient().deleteMessage(ApplicationManager.getInstance().getClient().getAdapter().getConversations().get(adapter.getId()), selectedMessage);
				}
			} catch (Exception e) {
				ApplicationManager.appendLog(Log.ERROR, "Delete message", "Message deleting failed");
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
		return super.onContextItemSelected(item);
	}
    
    // The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener =
        new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                sendMessage(outEditText.getText().toString());
            }
            return true;
        }
    };
    
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
				startActivity(new Intent(ApplicationManager.getInstance().getApplicationContext(), ProfileTypeChooser.class));
				break;
			}
		}
    }
}
