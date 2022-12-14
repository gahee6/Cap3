package com.example.register.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.register.R;
import com.example.register.RetrofitAPI;
import com.example.register.domain.BoardDTO;
import com.example.register.domain.BoardReceivedDTO;
import com.example.register.domain.Member;
import com.example.register.domain.Report;
import com.example.register.domain.ReportDTO;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReportCreateActivity extends AppCompatActivity {
    private String TAG_HOME = "home_fragment";
    private String TAG_MYWRITE = "mywrite_fragment";
    private String TAG_REPORT = "report_fragment";
    private String TAG_MYREPORT = "myreport_fragment";
    private BottomNavigationView bottomNavigationView;
    private final String MYIP = "http://192.168.2.28";
    private final String FRIP = "http://192.168.3.134";
    private final String RESTIP = "http://172.16.153.145";
    private final String BASEURL = FRIP+":9090/report/";
    private RetrofitAPI retrofitAPI;

    private EditText editTitle, editContent, editAttackerNickname;
    private Button btnWrite;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_create);

        init();

        //레트로핏 설정
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);

        Log.e("createdate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")));

        //뒤로가기 버튼 눌렀을 때
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("뒤로가기버튼", "click!!");
                Intent intent = new Intent(ReportCreateActivity.this, ReportMainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
            }
        });

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("신고글쓰기버튼", "click!!");
                if(editTitle.length() == 0 || editAttackerNickname.length() == 0 || editContent.length() == 0){
                    Toast.makeText(getApplicationContext(), "정보를 모두 입력해주세요", Toast.LENGTH_SHORT).show();
                }else{
                    createReport();
                    Intent intent = new Intent(ReportCreateActivity.this, ReportMainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                }
            }
        });
    }

    private void init(){
        editTitle = (EditText) findViewById(R.id.editTitle);
        editContent = (EditText) findViewById(R.id.editContent);
        editAttackerNickname = (EditText) findViewById(R.id.editAttackerNickname);
        btnWrite = (Button) findViewById(R.id.btnWrite);
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        bottomNavigationView = findViewById(R.id.bottomNavi);
    }

    private void createReport() {

        ReportDTO reportDTO = new ReportDTO(editTitle.getText().toString(), editAttackerNickname.getText().toString(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"))
                , LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")), editContent.getText().toString(), Member.getInstance().getStudentNum());

        Call<ReportDTO> call = retrofitAPI.createReport(reportDTO);

        call.enqueue(new Callback<ReportDTO>() {
            @Override
            public void onResponse(Call<ReportDTO> call, Response<ReportDTO> response) {
                Log.e("글쓰기", "글쓰기성공!!");
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT);
                    return;
                }

            }

            @Override
            public void onFailure(Call<ReportDTO> call, Throwable t) {
                Log.e("글쓰기", "글쓰기실패ㅠㅠ");
                t.printStackTrace();
            }
        });

    }

}
