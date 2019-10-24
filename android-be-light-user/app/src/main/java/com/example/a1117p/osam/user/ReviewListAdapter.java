package com.example.a1117p.osam.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

    public class ReviewListAdapter extends BaseAdapter {
        ArrayList<ReviewListItem> reviews = new ArrayList<>();
        ReviewListAdapter(JSONArray jsonArray){
            for(Object object:jsonArray){
                reviews.add(new ReviewListItem((JSONObject) object));
            }
        }
        @Override
        public int getCount() {
            return reviews.size();
        }

        @Override
        public Object getItem(int position) {
            return reviews.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Context context = parent.getContext();

            // "listview_item" Layout을 inflate하여 convertView 참조 획득.
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.reviewitem, parent, false);
            }

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            TextView id = convertView.findViewById(R.id.id) ;
            TextView score = convertView.findViewById(R.id.score) ;
            TextView date = convertView.findViewById(R.id.date) ;
            TextView review = convertView.findViewById(R.id.review) ;

            // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
            ReviewListItem listViewItem = reviews.get(position);

            // 아이템 내 각 위젯에 데이터 반영
            id.setText(listViewItem.getUserId());
            StringBuilder scoreStar= new StringBuilder();
            long scorel = listViewItem.getReviewScore();
            for(int i=0;i<scorel;i++)
                scoreStar.append("★");
            score.setText(scoreStar);
            date.setText(listViewItem.getReviewDate());
            review.setText(listViewItem.getReview());

            return convertView;
        }
    }


