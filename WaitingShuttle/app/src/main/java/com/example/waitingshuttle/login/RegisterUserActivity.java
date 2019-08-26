package com.example.waitingshuttle.login;

//import android.support.annotation.IdRes;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.waitingshuttle.R;

import org.json.JSONObject;

public class RegisterUserActivity extends AppCompatActivity {


    private ArrayAdapter adapter;
    //private Spinner spinner;
    private String userID;
    private String userPassword;
    private String userGender;
    private String userPhone;
    private String userEmail;
    private AlertDialog dialog;
    private boolean validate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        //스피너 객체 선언 및 리소스를 가져오는 부분
        //spinner = (Spinner)findViewById(R.id.majorSpinner);
        //adapter = ArrayAdapter.createFromResource(this, R.array.major, android.R.layout.simple_dropdown_item_1line);
        // spinner.setAdapter(adapter);

        final EditText idText = (EditText) findViewById(R.id.idText);
        final EditText passwordText = (EditText) findViewById(R.id.passwordText);
        final EditText emailText = (EditText) findViewById(R.id.emailText);
        final EditText phoneText = (EditText) findViewById(R.id.phoneText);
        final EditText nameText = (EditText) findViewById(R.id.nameText);
        RadioGroup genderGroup = (RadioGroup) findViewById(R.id.genderGroup);
        int genderGroupID = genderGroup.getCheckedRadioButtonId();
        userGender = ((RadioButton) findViewById(genderGroupID)).getText().toString();//초기화 값을 지정해줌


        final EditText passwordText2 = (EditText) findViewById(R.id.passwordText2);


        //phoneText 전화번호 자동 하이픈 추가
        phoneText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        //라디오버튼이 눌리면 값을 바꿔주는 부분
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                RadioButton genderButton = (RadioButton) findViewById(i);
                userGender = genderButton.getText().toString();
            }
        });



        //회원가입시 아이디가 사용가능한지 검증하는 부분
        final Button validateButton = (Button)findViewById(R.id.validateButton);
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = idText.getText().toString();
                if(validate){
                    return;//검증 완료
                }
                //ID 값을 입력하지 않았다면
                if(userID.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterUserActivity.this);
                    dialog = builder.setMessage("ID is empty")
                            .setPositiveButton("OK", null)
                            .create();
                    dialog.show();
                    return;
                }


                //검증시작
                Response.Listener<String> responseListener = new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        try{
                            Toast.makeText(RegisterUserActivity.this, response, Toast.LENGTH_LONG).show();

                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){//사용할 수 있는 아이디라면
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterUserActivity.this);
                                dialog = builder.setMessage("you can use ID")
                                        .setPositiveButton("OK", null)
                                        .create();
                                dialog.show();
                                idText.setEnabled(false);//아이디값을 바꿀 수 없도록 함
                                validate = true;//검증완료
                                idText.setBackgroundColor(getResources().getColor(R.color.colorGray));
                                validateButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
                            }else{//사용할 수 없는 아이디라면
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterUserActivity.this);
                                dialog = builder.setMessage("alreay used ID")
                                        .setNegativeButton("OK", null)
                                        .create();
                                dialog.show();
                            }

                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                };//Response.Listener 완료

                //Volley 라이브러리를 이용해서 실제 서버와 통신을 구현하는 부분
                ValidateRequest validateRequest = new ValidateRequest(userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterUserActivity.this);
                queue.add(validateRequest);
            }
        });


        //회원 가입 버튼이 눌렸을때
        Button registerButton = (Button)findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = idText.getText().toString();
                String userPassword = passwordText.getText().toString();
                String userPhone = phoneText.getText().toString();
                String userEmail = emailText.getText().toString();
                String userPassword2 = passwordText2.getText().toString();
                String userName=nameText.getText().toString();
                CheckBox checkBox=(CheckBox) findViewById(R.id.checkBox);
                CheckBox checkBox2=(CheckBox) findViewById(R.id.checkBox2);
                //ID 중복체크를 했는지 확인함
                if(!validate){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterUserActivity.this);
                    dialog = builder.setMessage("First Check ID plz")
                            .setNegativeButton("OK", null)
                            .create();
                    dialog.show();
                    return;
                }

                //한칸이라도 빠뜨렸을 경우
                if(userID.equals("")||userPassword.equals("")||userPhone.equals("")||userEmail.equals("")||userGender.equals("")||userName.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterUserActivity.this);
                    dialog = builder.setMessage("Empty text exist")
                            .setNegativeButton("OK", null)
                            .create();
                    dialog.show();
                    return;
                }


                //TEST 패스워드 검사


                if(!userPassword.equals(userPassword2)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterUserActivity.this);
                    dialog = builder.setMessage("패스워드와 패스워드 확인란의 입력이 같지 않습니다")
                            .setNegativeButton("OK", null)
                            .create();
                    dialog.show();
                    return;

                }
                //TEST 패스워드 검사

                //개인정보 동의 체크 검사
                if(checkBox.isChecked() == false){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterUserActivity.this);
                    dialog = builder.setMessage("개인정보 약관에 대한 동의가 필요합니다")
                            .setNegativeButton("OK", null)
                            .create();
                    dialog.show();
                    return;

                }

                //서비스 이용 약관 체크 검사
                if(checkBox2.isChecked() == false){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterUserActivity.this);
                    dialog = builder.setMessage("서비스 이용 약관에 대한 동의가 필요합니다")
                            .setNegativeButton("OK", null)
                            .create();
                    dialog.show();
                    return;

                }


                //개인정보 동의 체크 검사

                //회원가입 시작
                Response.Listener<String> responseListener = new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){//사용할 수 있는 아이디라면
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterUserActivity.this);
                                dialog = builder.setMessage("Register Your ID")
                                        .setPositiveButton("OK", null)
                                        .create();
                                dialog.show();
                                finish();//액티비티를 종료시킴(회원등록 창을 닫음)
                            }else{//사용할 수 없는 아이디라면
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterUserActivity.this);
                                dialog = builder.setMessage("Register fail")
                                        .setNegativeButton("OK", null)
                                        .create();
                                dialog.show();
                            }

                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                };//Response.Listener 완료

                //Volley 라이브러리를 이용해서 실제 서버와 통신을 구현하는 부분
                RegisterRequest registerRequest = new RegisterRequest(userID, userPassword, userGender, userPhone, userEmail, userName, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterUserActivity.this);
                queue.add(registerRequest);


            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }
}


//출처: //http://gakari.tistory.com/entry/안드로이드-4-수강신청앱-회원-가입-기능-구현?category=414830 [가카리의 공부방]

//출처: http://gakari.tistory.com/entry/안드로이드-2-수강신청앱-회원가입-화면-만들기?category=414830 [가카리의 공부방]