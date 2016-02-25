package com.jardenconsulting.spanishapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerItemAdapter extends ArrayAdapter<DrawerItem> {

	Context mContext;
	int layoutResourceId;
	DrawerItem data[] = null;

	public DrawerItemAdapter(Context mContext, int layoutResourceId,
			DrawerItem[] data) {
		super(mContext, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.mContext = mContext;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View listItem = convertView;
		if (listItem == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			listItem = inflater.inflate(layoutResourceId, parent, false);
	
			ImageView imageViewIcon = (ImageView) listItem
					.findViewById(R.id.imageViewIcon);
			TextView textViewName = (TextView) listItem
					.findViewById(R.id.textViewName);
	
			DrawerItem folder = data[position];
	
			imageViewIcon.setImageDrawable(folder.icon);
			textViewName.setText(folder.name);
		}

		return listItem;
	}

}