package com.example.register.activity;

import static android.widget.Toast.*;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.register.R;
import com.example.register.RetrofitAPI;
import com.example.register.domain.AnswerDTO;
import com.example.register.domain.AnswerReceivedDTO;
import com.example.register.domain.BoardDTO;
import com.example.register.domain.BoardReceivedDTO;
import com.example.register.domain.Member;
import com.example.register.fragment.Board;
import com.example.register.listview.ListViewAdapter;
import com.example.register.listview.ListViewItem;
import com.example.register.recyclerview.MainAdapter;
import com.example.register.recyclerview.MainData;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BoardDetailActivity extends AppCompatActivity {
    private String TAG_HOME = "home_fragment";
    private String TAG_MYWRITE = "mywrite_fragment";
    private String TAG_REPORT = "report_fragment";
    private String TAG_MYREPORT = "myreport_fragment";
    private TextView txtMemberId, txtTitle, txtContent, txtPrice, txtHashtag1, txtHashtag2, txtRequirement, txtAnswer;
    private ImageButton btnBack, btnMenu, btnSend;
    private String createDate, modifyDate;
    private int boardId;
    private EditText con;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<ListViewItem> listarr;
    private ListViewAdapter listViewAdapter;
    private ListView listView1;
    private final String MYIP = "http://192.168.2.28";
    private final String FRIP = "http://192.168.3.134";
    private final String RESTIP = "http://172.16.153.145";
    private final String BASEURL = FRIP+":9090/board/";
    private RetrofitAPI retrofitAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_detail);


        init();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);

        listarr.clear();
        getClickBoard(boardId);
        getAnswer(boardId);


        txtAnswer.setText("?????? "+ listarr.size() +"???");

        // ???????????? ?????? ????????????
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoardDetailActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0); //????????? ??????????????? ?????????
            }
        });

        // ?????? ??????
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);

                getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        if (menuItem.getItemId() == R.id.btnEdit) {
                            // ?????? ??????
                            BoardDTO boardDTO = new BoardDTO(boardId,txtTitle.getText().toString(), txtContent.getText().toString(), "#"+txtHashtag1.getText().toString()+"#"+txtHashtag2.getText().toString()
                                    , Integer.parseInt(txtPrice.getText().toString()), createDate, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")), txtRequirement.getText().toString(), Member.getInstance().getStudentNum());
                            Intent intent = new Intent(BoardDetailActivity.this, BoardUpdateActivity.class);
                            intent.putExtra("boardId", String.valueOf(boardId));
                            intent.putExtra("boardDTO", boardDTO);
                            startActivity(intent);
                            overridePendingTransition(0, 0); //????????? ??????????????? ?????????

                        } else if (menuItem.getItemId() == R.id.btnDelete) {
                            // ?????? ??????
                            AlertDialog.Builder builder = new AlertDialog.Builder(BoardDetailActivity.this);
                            builder.setTitle("?????????").setMessage("?????? ?????????????????????????");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    deleteBoard();
                                    Intent intent = new Intent(BoardDetailActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0); //????????? ??????????????? ?????????
                                }
                            });

                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    makeText(getApplicationContext(), "Cancel Click", LENGTH_SHORT).show();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();

                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (con.length() == 0) {
                    Toast.makeText(getApplicationContext(), "????????? ??????????????????.", LENGTH_SHORT).show();
                } else {
                    createAnswer();
                    Intent intent = getIntent();
                    finish(); //?????? ???????????? ?????? ??????
                    overridePendingTransition(0, 0); //????????? ??????????????? ?????????
                    startActivity(intent); //?????? ???????????? ????????? ??????
                    overridePendingTransition(0, 0); //????????? ??????????????? ?????????
                }
            }
        });

    }

    private void init(){
        txtMemberId = (TextView) findViewById(R.id.txtMemberId);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtContent = (TextView) findViewById(R.id.txtContent);
        txtPrice = (TextView) findViewById(R.id.txtPrice);
        txtHashtag1 = (TextView) findViewById(R.id.txtHashtag1);
        txtHashtag2 = (TextView) findViewById(R.id.txtHashtag2);
        txtRequirement = (TextView) findViewById(R.id.txtRequirement);
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        Intent boardIdIntent = getIntent();
        boardId = Integer.parseInt(boardIdIntent.getStringExtra("boardId"));
        btnMenu = (ImageButton) findViewById(R.id.btnMenu);
        con = (EditText) findViewById(R.id.con);
        btnSend = (ImageButton) findViewById(R.id.send);
        listarr = new ArrayList<>();
        listView1 = (ListView) findViewById(R.id.listView1);
        txtAnswer = (TextView) findViewById(R.id.txtAnswer);
    }

    // ????????? ???????????????
    private void getClickBoard(int boardId) {
        Call<BoardReceivedDTO> call = retrofitAPI.getClickBoard(boardId);

        call.enqueue(new Callback<BoardReceivedDTO>() {
            @Override
            public void onResponse(Call<BoardReceivedDTO> call, Response<BoardReceivedDTO> response) {
                Log.e("???????????????", "??????!!!!!!!!!!!!!");
                if (!response.isSuccessful()) {
                    Log.e("Response", "??????!!!!!!!!");
                    return;
                }

                BoardReceivedDTO board = response.body();

                if(Member.getInstance().getStudentNum().equals(board.getMemberId().getStudentNum())){
                    btnMenu.setVisibility(View.VISIBLE);
                }

                txtMemberId.setText(board.getMemberId().getNickname());
                txtTitle.setText(board.getTitle());
                txtContent.setText(board.getContent());
                String[] hashtag = board.getHashtag().split("#");
                String hashtag1 = hashtag[1];
                String hashtag2 = hashtag[2];
                txtHashtag1.setText(hashtag1);
                txtHashtag2.setText(hashtag2);
                txtPrice.setText(String.valueOf(board.getPrice()));
                txtRequirement.setText(board.getRequirement());
                createDate = board.getCreateDate();
                modifyDate = board.getModifyDate();

            }
            @Override
            public void onFailure(Call<BoardReceivedDTO> call, Throwable t) {
                Log.e("Response", "??????!!!!!!!!");
            }
        });
    }

    // ?????????
    private void deleteBoard(){
        Call<Void> call = retrofitAPI.deleteBoard(boardId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.e("???????????????", "??????!!!!!!!!!!!!!");
                if (!response.isSuccessful()) {
                    Log.e("Response", "??????!!!!!!!!");
                    return;
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Response", "??????!!!!!!!!");
            }
        });
    }

    // ?????? ??????
    private void createAnswer() {

        AnswerDTO answerDTO = new AnswerDTO(con.getText().toString(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"))
                , Member.getInstance().getStudentNum(), boardId);

        Call<AnswerDTO> call = retrofitAPI.createAnswer(answerDTO);

        call.enqueue(new Callback<AnswerDTO>() {
            @Override
            public void onResponse(Call<AnswerDTO> call, Response<AnswerDTO> response) {
                Log.e("????????????", "??????????????????!!");
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "??????", Toast.LENGTH_SHORT);
                    return;
                }

            }

            @Override
            public void onFailure(Call<AnswerDTO> call, Throwable t) {
                Log.e("?????????", "?????????????????????");
                t.printStackTrace();
            }
        });

    }

    // ?????? ????????????
    private void getAnswer(int boardId) {
        Call<List<AnswerReceivedDTO>> call = retrofitAPI.getAnswer(boardId);

        call.enqueue(new Callback<List<AnswerReceivedDTO>>() {
            @Override
            public void onResponse(Call<List<AnswerReceivedDTO>> call, Response<List<AnswerReceivedDTO>> response) {
                if (!response.isSuccessful()) {
                    Log.e("Response", "??????!!!!!!!!@");
                    return;
                }
                Log.e("Response", "??????!!!!!!!!");
                List<AnswerReceivedDTO> answer = response.body();
                for(AnswerReceivedDTO post : answer) {
                    listarr.add(new ListViewItem(post.getMemberId().getNickname(), post.getCreateDate(), post.getContent(), post.getId()));
                }
                listViewAdapter = new ListViewAdapter(listarr);
                listView1.setAdapter(listViewAdapter);

            }
            @Override
            public void onFailure(Call<List<AnswerReceivedDTO>> call, Throwable t) {
                Log.e("Response", "??????!!!!!!!!");
            }
        });
    }

}
