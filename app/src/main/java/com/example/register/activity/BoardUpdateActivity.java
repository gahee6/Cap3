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
import com.example.register.domain.Board;
import com.example.register.domain.BoardDTO;
import com.example.register.domain.BoardReceivedDTO;
import com.example.register.domain.Member;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Path;

public class BoardUpdateActivity extends AppCompatActivity {
    private String TAG_HOME = "home_fragment";
    private String TAG_MYWRITE = "mywrite_fragment";
    private String TAG_REPORT = "report_fragment";
    private String TAG_MYREPORT = "myreport_fragment";
    private BottomNavigationView bottomNavigationView;
    private final String MYIP = "http://192.168.2.28";
    private final String FRIP = "http://192.168.3.134";
    private final String RESTIP = "http://172.16.153.145";
    private final String BASEURL = FRIP+":9090/board/";
    private int boardId;
    private BoardDTO boardDTO;
    private RetrofitAPI retrofitAPI;

    private EditText editTitle, editContent, editRequirement, editHashtag1, editHashtag2, editPrice;
    private Button btnWrite;
    private ImageButton btnBack;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_update);
        init();

        // 레트로핏 설정
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);

        // 뒤로가기 버튼 클릭했을때
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoardUpdateActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
            }
        });

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("글수정버튼", "click!!!!!!!!!!!!!");
                if(editTitle.length() == 0 || editContent.length() == 0 || editHashtag1.length() == 0 || editHashtag2.length() == 0 || editPrice.length() == 0){
                    Toast.makeText(getApplicationContext(), "정보를 모두 입력해주세요", Toast.LENGTH_SHORT).show();
                }else{
                    updateBoard();
                    Intent intent = new Intent(BoardUpdateActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                }

            }
        });


    }

    private void init() {
        editTitle = (EditText) findViewById(R.id.editTitle);
        editContent = (EditText) findViewById(R.id.editContent);
        editRequirement = (EditText) findViewById(R.id.editRequirement);
        editHashtag1 = (EditText) findViewById(R.id.editHashtag1);
        editHashtag2 = (EditText) findViewById(R.id.editHashtag2);
        editPrice = (EditText) findViewById(R.id.editPrice);
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnWrite = (Button) findViewById(R.id.btnWrite);
        bottomNavigationView = findViewById(R.id.bottomNavi);

        // 글 상세보기 값 받아오기
        Intent boardInfoIntent = getIntent();
        boardId = Integer.parseInt(boardInfoIntent.getStringExtra("boardId"));
        boardDTO = (BoardDTO) boardInfoIntent.getSerializableExtra("boardDTO");


        editTitle.setText(boardDTO.getTitle());
        editContent.setText(boardDTO.getContent());
        String[] hashtag = boardDTO.getHashtag().split("#");
        String hashtag1 = hashtag[1];
        String hashtag2 = hashtag[2];
        editHashtag1.setText(hashtag1);
        editHashtag2.setText(hashtag2);
        editRequirement.setText(boardDTO.getRequirement());
        editPrice.setText(String.valueOf(boardDTO.getPrice()));
    }

    private void updateBoard() {


        boardDTO.setTitle(editTitle.getText().toString());
        boardDTO.setContent(editContent.getText().toString());
        boardDTO.setRequirement(editRequirement.getText().toString());
        boardDTO.setHashtag("#" + editHashtag1.getText().toString() + "#" + editHashtag2.getText().toString());
        boardDTO.setPrice(Integer.parseInt(editPrice.getText().toString()));
        boardDTO.setModifyDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")));


        Call<BoardDTO> call = retrofitAPI.updateBoard(boardDTO);

        call.enqueue(new Callback<BoardDTO>() {
            @Override
            public void onResponse(Call<BoardDTO> call, Response<BoardDTO> response) {
                Log.e("글수정", "글수정성공!!");
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT);
                    return;
                }
            }

            @Override
            public void onFailure(Call<BoardDTO> call, Throwable t) {
                Log.e("글수정", "글수정실패ㅠㅠ");
                t.printStackTrace();
            }
        });

    }

}
