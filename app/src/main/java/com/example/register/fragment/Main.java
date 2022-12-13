package com.example.register.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.register.R;
import com.example.register.RetrofitAPI;
import com.example.register.activity.BoardCreateActivity;
import com.example.register.activity.SearchActivity;
import com.example.register.domain.BoardReceivedDTO;
import com.example.register.recyclerview.MainAdapter;
import com.example.register.recyclerview.MainData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main extends Fragment {

    ImageButton btnSearch;
    private View view;
    private ArrayList<MainData> arrayList;
    private MainAdapter mainAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    RetrofitAPI retrofitAPI;
    private FloatingActionButton add;
    private final String MYIP = "http://192.168.2.28";
    private final String FRIP = "http://192.168.3.134";
    private final String RESTIP = "http://172.16.153.145";
    private final String BASEURL = FRIP+":9090/board/";

    private void init() {

        add = view.findViewById(R.id.add);
        btnSearch = view.findViewById(R.id.btnSearch);
        //recyclerView = (RecyclerView) findViewById(R.id.rv);
        //arrayList = new ArrayList<>();
        //bottomNavigationView = findViewById(R.id.bottomNavi);
        //btnMyBoard = (Button) findViewById(R.id.btnMyBoard);
        // = (Button) findViewById(R.id.btnReportMain);
        //bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavi);
        //mainActivity = new MainActivity();

    }

    private void clickSearch(){
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //프레그먼트에서는 액티비티 호출 불가 따라서 getActivity() 사용
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

    }
    private void getBoard() {
        Call<List<BoardReceivedDTO>> call = retrofitAPI.getBoard();

        call.enqueue(new Callback<List<BoardReceivedDTO>>() {
            @Override
            public void onResponse(Call<List<BoardReceivedDTO>> call, Response<List<BoardReceivedDTO>> response) {
                if (!response.isSuccessful()) {
                    Log.e("Response", "실패!!!!!!!!@");
                    return;
                }
                Log.e("Response", "성공!!!!!!!!");
                List<BoardReceivedDTO> board = response.body();
                for(BoardReceivedDTO post : board) {
                    arrayList.add(new MainData(post.getHashtag(), post.getTitle(), post.getModifyDate(), post.getMemberId().getNickname(), post.getId()));
                }
                mainAdapter = new MainAdapter(arrayList);
                recyclerView.setAdapter(mainAdapter);
                linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);

                recyclerView.setLayoutManager(linearLayoutManager);


            }
            @Override
            public void onFailure(Call<List<BoardReceivedDTO>> call, Throwable t) {
                Log.e("Response", "실패!!!!!!!!");
            }
        });
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.main, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        //linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();

        mainAdapter = new MainAdapter(arrayList);
        recyclerView.setAdapter(mainAdapter);

        //레트로핏 설정
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);

        init();
        getBoard();

        // 글 생성 버튼
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BoardCreateActivity.class);
                startActivity(intent);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

}