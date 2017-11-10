package com.example.mymqtttest;

import java.util.Properties;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttSetting {

	private String host = "broker.hivemq.com";
	private String name = MqttClient.generateClientId();;
	private String port = "1883";
	private MqttClient client;
	private MqttConnectOptions options;
	private boolean isConnecting = false;
	private boolean isOption = true;
	private Mqttmsg msg;

	public MqttSetting(Mqttmsg msg) {
		try {
			this.msg = msg;
			client = new MqttClient("tcp://" + host + ":"+port, name,
					new MemoryPersistence());
			init();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setSubscribe(String topic, int qos) {
		try {
			client.subscribe(topic, qos);
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setPublish(String topic, String data, int qos) {
		MqttMessage m = new MqttMessage();
		String msg = data;
		m.setPayload(msg.getBytes());
		m.setQos(qos);
		m.setRetained(true);
		try {
			client.publish(topic, m);
		} catch (MqttPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean getConStatus() {
		return client.isConnected();
	}

	public void connect(boolean isConnect) {
		if (isConnect) {
			if (!client.isConnected()) {
				connect();
			}
		} else {
			if (client.isConnected()) {
				disconnect();
			}
		}
	}

	private void disconnect() {
		isConnecting = false;
		try {
			client.disconnect();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	private void connect() {
		if (!isConnecting) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						isConnecting = true;
						if (isOption) {
							client.connect(options);
						} else {
							client.connect();
						}
						isConnecting = false;
						msg.connectionOK();
					} catch (Exception e) {
						isConnecting = false;
						msg.connectionLost();
					}
				}
			}).start();
		}
	}

	private void init() {
		try {
			client = new MqttClient("tcp://" + host + ":"+port, name,
					new MemoryPersistence());
			options = new MqttConnectOptions();
			options.setCleanSession(true);
			options.setConnectionTimeout(5);
			client.setCallback(new MqttCallback() {

				@Override
				public void connectionLost(Throwable cause) {
					msg.connectionLost();
					isConnecting = false;
				}

				@Override
				public void deliveryComplete(IMqttDeliveryToken token) {
					msg.deliveryComplete();
				}

				@Override
				public void messageArrived(String topicName, MqttMessage message)
						throws Exception {
					msg.messageArrived(topicName, message.toString());
				}
			});
			// connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public interface Mqttmsg {
		void connectionLost();

		void connectionOK();

		void deliveryComplete();

		void messageArrived(String topicName, String message);
	}
}
