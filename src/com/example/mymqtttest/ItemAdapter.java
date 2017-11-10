package com.example.mymqtttest;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ItemAdapter extends BaseAdapter{

	private List<String> list;
	private Context context;
	private LayoutInflater inflater;
	
	private class ViewHolder {
		private TextView tv;
	}
	
	public void setListData(List<String> list){
		this.list = list;
	}
	
	public ItemAdapter(Context context,List<String> list){
		this.list = list;
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.item, null);
			viewHolder = new ViewHolder();
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tv = (TextView) convertView.findViewById(R.id.textView1);
	    viewHolder.tv.setText(list.get(position));
		return convertView;
	}
}
