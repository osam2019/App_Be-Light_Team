package com.example.a1117p.osam.user;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;

public class CustomInfoWindowFragment extends Fragment {

    InfoWindowData data;
    AppCompatActivity context;
    ListView listView;
    boolean isReview = true;
    private View mWindow;

    CustomInfoWindowFragment(InfoWindowData data, AppCompatActivity context) {
        this.data = data;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mWindow = inflater.inflate(R.layout.custom_info_window, container, false);
        return mWindow;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        render(view);
        //final  LinearLayout linearLayout = context.findViewById(R.id.map_linear);
        mWindow.findViewById(R.id.recipt_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new ResvtnClickDialog(context, data, Integer.parseInt(((TextView) context.findViewById(R.id.count)).getText().toString()));


                dialog.show();

            }
        });
        listView = mWindow.findViewById(R.id.review_list);
        mWindow.findViewById(R.id.review_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (isReview) {

                    final ProgressDialog Pdialog = new ProgressDialog(context);
                    Pdialog.setMessage("리뷰를 불러오는중입니다.");

                    Pdialog.show();
                    listView.setAdapter(null);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final HashMap params = new HashMap<String, String>();

                            try {
                                final String html = RequestHttpURLConnection.request("https://be-light.store/api/reviews?hostIdx=" + data.hostIdx, params, true, "GET");
                                JSONParser jsonParser = new JSONParser();
                                JSONArray jsonArray = (JSONArray) jsonParser.parse(html);
                                final ReviewListAdapter adapter = new ReviewListAdapter(jsonArray);

                                context.runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        Pdialog.dismiss();
                                        if (adapter.getCount() == 0) {
                                            ((Button) v).setText("리뷰");
                                            Toast.makeText(context, "리뷰가 없습니다.", Toast.LENGTH_LONG).show();

                                            isReview = true;
                                            return;
                                        }
                                        listView.setAdapter(adapter);
                                        ((Button) v).setText("닫기");
                                    }

                                });
                            } catch (final Exception e) {
                                context.runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }

                                });
                            }

                        }
                    }).start();
                    isReview = false;
                } else {

                    listView.setAdapter(null);
                    ((Button) v).setText("리뷰");
                    isReview = true;
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                context.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        ViewGroup viewGroup = ((ViewGroup) view.getParent());
                        if (viewGroup != null)
                            viewGroup.setBackgroundColor(Color.TRANSPARENT);
                    }

                });

            }
        }).start();

    }

    private void render(View view) {
        ((TextView) view.findViewById(R.id.host_name)).setText(data.hostName);
        ((TextView) view.findViewById(R.id.host_num)).setText(data.hostTel);
        ((TextView) view.findViewById(R.id.host_addr)).setText(data.hostAddress);
        ((TextView) view.findViewById(R.id.term)).setText(data.term);
        ((TextView) view.findViewById(R.id.hostIntro)).setText(data.hostIntro);
        if (data.hostImage != null && !data.hostImage.equals("")) {
            new DownloadImageTask((ImageView) view.findViewById(R.id.host_img), false)
                    .execute(data.hostImage);
        }
    }
}