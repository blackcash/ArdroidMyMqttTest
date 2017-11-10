package com.example.mymqtttest;

import java.util.ArrayList;
import java.util.List;

import com.example.mymqtttest.MqttSetting.Mqttmsg;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private ListView listView;
	private List<String> list_Sub;
	private EditText etPublicTopic, etPublicMsg, etSubscription;
	private Button btnClear, btnConnect, btnPublication, btnSubscription;
	private TextView tvResult, tvStatus;
	private MqttSetting mqttSetting;
	private ItemAdapter adapter;

	private Mqttmsg mmsg = new Mqttmsg() {

		@Override
		public void messageArrived(String topicName, String message) {
			final String topicName1 = topicName;
			final String message1 = message;
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					tvResult.setText(tvResult.getText().toString() + topicName1
							+ "====" + message1+"\n");
				}
			});
		}

		@Override
		public void deliveryComplete() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					etPublicMsg.setText("");
				}
			});

		}

		@Override
		public void connectionOK() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					getStatusText();					
					for(String topic : list_Sub){
					    mqttSetting.setSubscribe(topic, 1);
					}
				}
			});

		}

		@Override
		public void connectionLost() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					getStatusText();
				}
			});
		}
	};

	public void getStatusText() {
		if (mqttSetting.getConStatus()) {
			tvStatus.setText("連線");
			btnConnect.setText("斷線");
		} else {
			tvStatus.setText("斷線");
			btnConnect.setText("連線");
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		list_Sub = new ArrayList<String>();
		init();
	}

	private void init() {
		mqttSetting = new MqttSetting(mmsg);
		findViews();
		setListeners();
		getStatusText();
	}

	private void findViews() {
		listView = (ListView) findViewById(R.id.listView1);
		tvStatus = (TextView) findViewById(R.id.tvStatus);
		tvResult = (TextView) findViewById(R.id.tvResult);
		etPublicMsg = (EditText) findViewById(R.id.etPublicMsg);
		etPublicTopic = (EditText) findViewById(R.id.etPublicTopic);
		etSubscription = (EditText) findViewById(R.id.etSubscription);
		btnClear = (Button) findViewById(R.id.btnClear);
		btnConnect = (Button) findViewById(R.id.btnConnect);
		btnPublication = (Button) findViewById(R.id.btnPublication);
		btnSubscription = (Button) findViewById(R.id.btnSubscription);
	}

	private void setListeners() {
		adapter = new ItemAdapter(this, list_Sub);
		//adapter = new ArrayAdapter<String>(this, R.layout.item, list_Sub);
		listView.setAdapter(adapter);
		btnClear.setOnClickListener(this);
		btnConnect.setOnClickListener(this);
		btnPublication.setOnClickListener(this);
		btnSubscription.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnClear:
			tvResult.setText("");
			break;
		case R.id.btnConnect:
			if(!mqttSetting.getConStatus())
				tvStatus.setText("連線中...");
			mqttSetting.connect(!mqttSetting.getConStatus());			
			break;
		case R.id.btnPublication: {
			String pubtopic = etPublicTopic.getText().toString();
			String data = etPublicMsg.getText().toString();
			mqttSetting.setPublish(pubtopic, data, 2);
		}
			break;
		case R.id.btnSubscription: {
			String subtopic = etSubscription.getText().toString();
			mqttSetting.setSubscribe(subtopic, 1);
			list_Sub.add(subtopic);
			adapter.setListData(list_Sub);
			listView.invalidateViews();
		}
			break;

		default:
			break;
		}
	}
}
