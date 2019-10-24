package com.example.a1117p.osam.host;



import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import java.util.*;
import android.widget.EditText;
import android.widget.Toast;


public class DeleteUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);
        
        findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String passwd = ((EditText)findViewById(R.id.passwd)).getText().toString();
                if(passwd.equals("")){
                    Toast.makeText(DeleteUserActivity.this,"비밀번호를 입력하세요",Toast.LENGTH_LONG).show();
                    return;
                }
                final HashMap params = new HashMap<String, String>(); 
                
                params.put("hostUserPassword",passwd);
                
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        final String html = RequestHttpURLConnection.request("https://be-light.store/api/hoster?_method=DELETE",params,true,"POST");
                        runOnUiThread(new Runnable(){
                            
                            @Override
                            public void run() {
                                
                                Toast.makeText(DeleteUserActivity.this,html,Toast.LENGTH_LONG).show();
                            }
                            
                        });
                        
                    }
                }).start();
            }
        });
    }

    

}
