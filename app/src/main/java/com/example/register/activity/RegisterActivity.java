package com.example.register.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.register.R;
import com.example.register.RetrofitAPI;
import com.example.register.domain.Member;
import com.example.register.domain.MemberDTO;
import com.example.register.mail.GmailSender;

import javax.mail.MessagingException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RegisterActivity extends AppCompatActivity {

    private EditText studentNum, regPassword, editName, editNickname, editEmail, editKey;
    private Button checkStudentNum, sendEmail, certKey, reg, checkEmail, checkNickname;
    private RadioGroup radioGroup;
    private RadioButton radioMan, radioWoman;
    private ImageButton btnBack;
    private String genderResult = "", code; //code는 이메일 인증코드 담을 변수
    private Boolean completeStudentNum = false, completeEmail = false, completeCert = false, completeNickname = false;

    private final String MYIP = "http://192.168.2.28";
    private final String FRIP = "http://192.168.3.134";
    private final String RESTIP = "http://172.16.153.145";
    private final String BASEURL = FRIP+":9090/member/";
    private RetrofitAPI retrofitAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        init();

        //레트로핏 설정
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);

        //메일 설정
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());


        //ID 중복확인 이벤트
        checkStudentNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ID중복확인버튼", "click!!");
                checkStudentNum();
            }
        });

        //닉네임 중복확인 이벤트
        checkNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("닉네임중복확인버튼", "click!!");
                checkNickname();
            }
        });

        //메일 중복확인 이벤트
        checkEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("메일중복확인버튼", "click!!");
                if (sahmyookEmailCheck(editEmail.getText().toString())) {
                    checkEmail();
                } else {
                    Toast.makeText(getApplicationContext(), "삼육대 이메일 형식으로 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //라디오 버튼 성별 문자열 값 받아오는 이벤트
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.man) genderResult = radioMan.getText().toString();
                else if(checkedId == R.id.woman) genderResult = radioWoman.getText().toString();
            }
        });

        //가입하기 버튼 이벤트
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("가입하기", "click!!");
                if(completeStudentNum == false || regPassword.length() == 0 || editName.length() == 0 ||
                editNickname.length() == 0 || genderResult.length() == 0 || completeEmail == false || completeCert == false){
                    Toast.makeText(getApplicationContext(), "정보를 모두 입력해주세요", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "정보를 모두 입력받음!!", Toast.LENGTH_SHORT).show();
                    createMember();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                }
            }
        });

        //메일 보내기 버튼 이벤트
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("메일보내기", "click!!");
                try {
                    //구글메일 객체
                    GmailSender gmailSender = new GmailSender("ghkddlscks19@gmail.com", "acevvwsgsxbxjqaq");
                    code = gmailSender.getEmailCode();
                    //GMailSender.sendMail(제목, 본문내용, 받는사람);
                    gmailSender.sendMail("시간어때 이메일 인증코드입니다.", gmailSender.getEmailCode(), editEmail.getText().toString());
                    Toast.makeText(getApplicationContext(), "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
                    sendEmail.setVisibility(View.INVISIBLE);
                } catch (MessagingException e) {
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //인증하기 버튼 이벤트
        certKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //구글메일 객체
                Log.e("인증하기", "click!!");
                if(editKey.length() != 8){
                    Toast.makeText(getApplicationContext(), "인증번호 8자리를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    if (code.equals(editKey.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "인증에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                        completeCert = true;
                        editKey.setFocusable(false);
                        certKey.setVisibility(View.INVISIBLE);
                    } else {
                        Toast.makeText(getApplicationContext(), "인증에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        // 뒤로가기 클릭
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
            }
        });

    }

    private void init() {
        studentNum = (EditText) findViewById(R.id.studentNum); //학번입력창
        regPassword = (EditText) findViewById(R.id.regPassword); //비밀번호 입력창
        editName = (EditText) findViewById(R.id.editName); //이름 입력창
        editNickname = (EditText) findViewById(R.id.editNickname); //닉네임 입력창
        editEmail = (EditText) findViewById(R.id.editEmail); //이메일 입력창
        editKey = (EditText) findViewById(R.id.editKey); //이메일 인증키 입력창
        checkStudentNum = (Button) findViewById(R.id.checkStudentNum); //ID 중복확인 버튼
        checkNickname = (Button) findViewById(R.id.checkNickname); //닉네임 중복확인 버튼
        sendEmail = (Button) findViewById(R.id.sendEmail);//이메일 보내기 버튼
        certKey = (Button) findViewById(R.id.certKey);//인증번호 확인 버튼
        reg = (Button) findViewById(R.id.reg); //가입하기 버튼
        checkEmail = (Button) findViewById(R.id.checkEmail); //메일 중복확인 버튼
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup); //성별 라디오 그룹
        radioMan = (RadioButton) findViewById(R.id.man); //남성
        radioWoman = (RadioButton) findViewById(R.id.woman); //여성
        btnBack = (ImageButton) findViewById(R.id.btnBack);
    }

    //학번 중복확인 메소드
    private void checkStudentNum() {
        Call<Boolean> call = retrofitAPI.checkStudentNum(studentNum.getText().toString());
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("ID중복확인", "성공!!");
                if (!response.isSuccessful()) {
                    Toast.makeText(getBaseContext(), "실패", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (studentNum.length() != 10) {
                    Toast.makeText(getBaseContext(), "학번은 10자리입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    if(!response.body()){
                        Toast.makeText(getBaseContext(), "이미 가입한 ID입니다.", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getBaseContext(), "성공", Toast.LENGTH_SHORT).show();
                        completeStudentNum = true;
                        studentNum.setFocusable(false);
                        checkStudentNum.setVisibility(View.INVISIBLE);
                    }
                }

            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("ID중복확인", "실패");
                t.printStackTrace();
            }
        });
    }

    //닉네임 중복확인 메소드
    private void checkNickname() {
        Call<Boolean> call = retrofitAPI.checkNickname(editNickname.getText().toString());
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("닉네임중복확인", "성공!!");
                if (!response.isSuccessful()) {
                    Toast.makeText(getBaseContext(), "실패", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(editNickname.length() == 0){
                    Toast.makeText(getBaseContext(), "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    if(!response.body()){
                        Toast.makeText(getBaseContext(), "이미 가입한 닉네임 입니다.", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getBaseContext(), "성공", Toast.LENGTH_SHORT).show();
                        completeNickname = true;
                        editNickname.setFocusable(false);
                        checkNickname.setVisibility(View.INVISIBLE);
                    }
                }

            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("이메일중복확인", "실패");
                t.printStackTrace();
            }
        });
    }

    //이메일 중복확인 메소드
    private void checkEmail() {
        Call<Boolean> call = retrofitAPI.checkEmail(editEmail.getText().toString());
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("메일중복확인", "성공!!");
                if (!response.isSuccessful()) {
                    Toast.makeText(getBaseContext(), "실패", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!response.body()){
                    Toast.makeText(getBaseContext(), "이미 가입한 이메일 입니다.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getBaseContext(), "성공", Toast.LENGTH_SHORT).show();
                    completeEmail = true;
                    editEmail.setFocusable(false);
                    checkEmail.setVisibility(View.INVISIBLE);
                    sendEmail.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("이메일중복확인", "실패");
                t.printStackTrace();
            }
        });
    }

    //회원가입 메소드
    private void createMember() {
        MemberDTO memberDTO = new MemberDTO(studentNum.getText().toString(), regPassword.getText().toString(),
                editName.getText().toString(), editNickname.getText().toString(),
                genderResult, editEmail.getText().toString());

        Call<MemberDTO> call = retrofitAPI.createMember(memberDTO);

        call.enqueue(new Callback<MemberDTO>() {
            @Override
            public void onResponse(Call<MemberDTO> call, Response<MemberDTO> response) {
                Log.e("회원가입", "회원가입성공!!");
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT);
                    return;
                }
                Member.setInstance(response.body().getStudentNum(), response.body().getPassword(), response.body().getName(),
                        response.body().getNickname(), response.body().getGender(), response.body().getEmail());

            }

            @Override
            public void onFailure(Call<MemberDTO> call, Throwable t) {
                Log.e("회원가입", "회원가입실패ㅠㅠ");
               t.printStackTrace();
            }
        });

    }

    //삼육대 이메일 형식 체크
    private Boolean sahmyookEmailCheck(String email) {
        int atnum = email.indexOf("@");
        if (atnum == -1) {
            return false;
        } else {
            String sahmyookEmail = email.substring(atnum);
            if (sahmyookEmail.equals("@syuin.ac.kr")) {
                return true;
            } else {
                return false;
            }
        }
    }
}

