package com.example.legistify;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {
	ArrayList<Contact> data;
	LayoutInflater inf;
	Context context;

	public MyAdapter(Context context, ArrayList<Contact> contacts) {
		this.data = contacts;
		this.context = context;
		inf = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	class ViewHolder {
		TextView tv_name;
		TextView tv_phone;

		public ViewHolder(TextView tv_name, TextView tv_phone) {
			// TODO Auto-generated constructor stub
			this.tv_name = tv_name;
			this.tv_phone = tv_phone;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder holder;

		if (convertView == null) {
			convertView = inf.inflate(R.layout.contact_entry, null);
			TextView tv_name = (TextView) convertView
					.findViewById(R.id.ll_name).findViewById(R.id.tv_name);
			TextView tv_phone = (TextView) convertView.findViewById(
					R.id.ll_phone).findViewById(R.id.tv_phone);

			holder = new ViewHolder(tv_name, tv_phone);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();

		}

		Contact c = (Contact) getItem(position);
		holder.tv_name.setText(c.getName());
		holder.tv_phone.setText(c.getPhone());
		return convertView;
	}

}