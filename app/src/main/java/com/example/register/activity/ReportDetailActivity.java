package com.example.register.activity;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.register.R;
import com.example.register.RetrofitAPI;
import com.example.register.domain.AnswerDTO;
import com.example.register.domain.AnswerReceivedDTO;
import com.example.register.domain.BoardDTO;
import com.example.register.domain.BoardReceivedDTO;
import com.example.register.domain.Member;
import com.example.register.domain.ReportAnswerDTO;
import com.example.register.domain.ReportAnswerReceivedDTO;
import com.example.register.domain.ReportDTO;
import com.example.register.domain.ReportReceivedDTO;
import com.example.register.listview.ListViewAdapter;
import com.example.register.listview.ListViewItem;
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

public class ReportDetailActivity extends AppCompatActivity {
    private String TAG_HOME = "home_fragment";
    private String TAG_MYWRITE = "mywrite_fragment";
    private String TAG_REPORT = "report_fragment";
    private String TAG_MYREPORT = "myreport_fragment";
    private TextView txtMemberId, txtTitle, txtContent, txtAttackerNickname;
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
    private final String BASEURL = FRIP+":9090/report/";
    private RetrofitAPI retrofitAPI;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_detail);
        init();

        // ???????????? ??????
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);



        getClickReport(boardId);
        getReportAnswer(boardId);


        // ???????????? ?????? ????????????
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportDetailActivity.this, ReportMainActivity.class);
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
                            ReportDTO reportDTO = new ReportDTO(boardId, txtTitle.getText().toString(), txtAttackerNickname.getText().toString()
                                    , createDate, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")), txtContent.getText().toString(), Member.getInstance().getStudentNum());
                            Intent intent = new Intent(ReportDetailActivity.this, ReportUpdateActivity.class);
                            intent.putExtra("boardId", String.valueOf(boardId));
                            intent.putExtra("reportDTO", reportDTO);
                            startActivity(intent);
                            overridePendingTransition(0, 0); //????????? ??????????????? ?????????

                        } else if (menuItem.getItemId() == R.id.btnDelete) {
                            // ?????? ??????
                            AlertDialog.Builder builder = new AlertDialog.Builder(ReportDetailActivity.this);
                            builder.setTitle("?????????").setMessage("?????? ?????????????????????????");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    deleteReport();
                                    Intent intent = new Intent(ReportDetailActivity.this, MainActivity.class);
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
                    createReportAnswer();
                    Intent intent = getIntent();
                    finish(); //?????? ???????????? ?????? ??????
                    overridePendingTransition(0, 0); //????????? ??????????????? ?????????
                    startActivity(intent); //?????? ???????????? ????????? ??????
                    overridePendingTransition(0, 0); //????????? ??????????????? ?????????
                }
            }
        });

    }

    private void init() {
        txtMemberId = (TextView) findViewById(R.id.txtMemberId);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtContent = (TextView) findViewById(R.id.txtContent);
        txtAttackerNickname = (TextView) findViewById(R.id.txtAttackerNickname);
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnMenu = (ImageButton) findViewById(R.id.btnMenu);
        Intent boardIdIntent = getIntent();
        boardId = Integer.parseInt(boardIdIntent.getStringExtra("boardId"));
        con = (EditText) findViewById(R.id.con);
        btnSend = (ImageButton) findViewById(R.id.send);
        listarr = new ArrayList<>();
        listView1 = (ListView) findViewById(R.id.listView1);

    }

    // ????????? ???????????????
    private void getClickReport(int boardId) {
        Call<ReportReceivedDTO> call = retrofitAPI.getClickReport(boardId);

        call.enqueue(new Callback<ReportReceivedDTO>() {
            @Override
            public void onResponse(Call<ReportReceivedDTO> call, Response<ReportReceivedDTO> response) {
                Log.e("???????????????", "??????!!!!!!!!!!!!!");
                if (!response.isSuccessful()) {
                    Log.e("Response", "??????!!!!!!!!");
                    return;
                }

                ReportReceivedDTO board = response.body();

                if(Member.getInstance().getStudentNum().equals(board.getMemberId().getStudentNum())){
                    btnMenu.setVisibility(View.VISIBLE);
                }

                txtMemberId.setText(board.getMemberId().getNickname());
                txtTitle.setText(board.getTitle());
                txtAttackerNickname.setText(board.getAttackerNickname());
                txtContent.setText(board.getContent());
                createDate = board.getCreateDate();
                modifyDate = board.getModifyDate();
            }
            @Override
            public void onFailure(Call<ReportReceivedDTO> call, Throwable t) {
                Log.e("Response", "??????!!!!!!!!");
            }
        });
    }

    // ?????????
    private void deleteReport(){
        Call<Void> call = retrofitAPI.deleteReport(boardId);

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

    // ?????? ?????? ??????
    private void createReportAnswer() {

        ReportAnswerDTO reportAnswerDTO = new ReportAnswerDTO(con.getText().toString(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"))
                , Member.getInstance().getStudentNum(), boardId);

        Call<ReportAnswerDTO> call = retrofitAPI.createReportAnswer(reportAnswerDTO);

        call.enqueue(new Callback<ReportAnswerDTO>() {
            @Override
            public void onResponse(Call<ReportAnswerDTO> call, Response<ReportAnswerDTO> response) {
                Log.e("????????????", "??????????????????!!");
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "??????", Toast.LENGTH_SHORT);
                    return;
                }

            }

            @Override
            public void onFailure(Call<ReportAnswerDTO> call, Throwable t) {
                Log.e("?????????", "?????????????????????");
                t.printStackTrace();
            }
        });

    }

    // ?????? ?????? ????????????
    private void getReportAnswer(int boardId) {
        Call<List<ReportAnswerReceivedDTO>> call = retrofitAPI.getReportAnswer(boardId);

        call.enqueue(new Callback<List<ReportAnswerReceivedDTO>>() {
            @Override
            public void onResponse(Call<List<ReportAnswerReceivedDTO>> call, Response<List<ReportAnswerReceivedDTO>> response) {
                if (!response.isSuccessful()) {
                    Log.e("Response", "??????!!!!!!!!@");
                    return;
                }
                Log.e("Response", "??????!!!!!!!!");
                List<ReportAnswerReceivedDTO> answer = response.body();
                for(ReportAnswerReceivedDTO post : answer) {
                    listarr.add(new ListViewItem(post.getMemberId().getNickname(), post.getCreateDate(), post.getContent(), post.getId()));
                }
                listViewAdapter = new ListViewAdapter(listarr);
                listView1.setAdapter(listViewAdapter);

            }
            @Override
            public void onFailure(Call<List<ReportAnswerReceivedDTO>> call, Throwable t) {
                Log.e("Response", "??????!!!!!!!!");
            }
        });
    }

}
