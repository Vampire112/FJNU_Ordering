package com.fjnu.fjnu_ordering.Activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fjnu.fjnu_ordering.R;
import com.fjnu.fjnu_ordering.util.JellyInterpolator;
import com.fjnu.fjnu_ordering.web.WebService;

import static java.lang.Thread.sleep;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener{
    public static Activity loginactivity;
    public static  Boolean LOGINFLAG=false;
    private TextView mBtnLogin,mRegister;
     private  int role = 0;
    private String info=null;

    private ActionBar actionBar;
    private View mInputLayout;

    private float mWidth, mHeight;

    private LinearLayout mName, mPsw;
    private TextView username,pwd;
    private View progress,login_input;
    private RadioGroup mRole;

    private static Handler handler=new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginactivity=this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        actionBar=getSupportActionBar();
        actionBar.hide();
        initView();
    }

    private void initView() {
        mBtnLogin = (TextView)findViewById(R.id.main_btn_login);
        mRegister = (TextView)findViewById(R.id.register);
        mInputLayout = findViewById(R.id.input_layout);
        progress = findViewById(R.id.layout_progress);
        mName = (LinearLayout) findViewById(R.id.input_layout_name);
        mPsw = (LinearLayout) findViewById(R.id.input_layout_psw);
        mRole = (RadioGroup) findViewById(R.id.login_role_rg);
        mRole.setOnCheckedChangeListener(this);
        username=(TextView)findViewById(R.id.login_username);
        pwd=(TextView)findViewById(R.id.login_pwd);

        mBtnLogin.setOnClickListener(this);
        mRegister.setOnClickListener(this);
       Intent intent =getIntent();
       if(intent.getStringExtra("username")!=null){
           username.setText(intent.getStringExtra("username"));
       }
    }

    @Override
    public void onClick(View v) {
           switch (v.getId()){
                //register
               case R.id.register:
                   Intent intent =new Intent(LoginActivity.this,RegisterActivity.class);
                   this.startActivity(intent);

                   break;
               case R.id.main_btn_login:
                   //check net
                   mWidth = mBtnLogin.getMeasuredWidth();
                   mHeight = mBtnLogin.getMeasuredHeight();

                   mName.setVisibility(View.INVISIBLE);
                   mPsw.setVisibility(View.INVISIBLE);
                   inputAnimator(mInputLayout, mWidth, mHeight);

                   if(!checkNet()){
                       Toast toast =Toast.makeText(LoginActivity.this,"网络未连接",Toast.LENGTH_SHORT);
                       toast.setGravity(Gravity.CENTER,0,0);
                       toast.show();
                       break;
                   }
                    Log.e("login","...");


                   try {

                       Thread thread=new Thread(new MyThread());
                       thread.start();
                       sleep(2000);
                       thread.interrupt();
                       thread.join();
                       System.out.println("线程退出!");
                       info=null;
                       //alertDialog.dismiss();
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }



                   Log.e("",LOGINFLAG+"");
                   if(LOGINFLAG){

                       Intent  intent1=new Intent(LoginActivity.this,IndividualActivity.class);
                       this.startActivity(intent1);
                       break;
                   }else {

                       Toast toast =Toast.makeText(LoginActivity.this,"账号或者密码错误，请重试",Toast.LENGTH_SHORT);
                       toast.setGravity(Gravity.CENTER,0,0);
                       toast.show();

                   }
                    break;


           }




    }
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId){
        switch (checkedId){
            case R.id.login_role_rg_nm:
                role = 0;
                break;
            case R.id.login_role_rg_hall:
                role = 1;
                break;
            case R.id.login_role_rg_root:
                role =2;
                break;
        }

    }


    //CHECK NET
    private boolean checkNet(){
        ConnectivityManager connectivityManager =(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getActiveNetworkInfo()!=null){
            return  connectivityManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }





    /**
     * 输入框的动画效果
     *
     * @param view
     *            控件
     * @param w
     *            宽
     * @param h
     *            高
     */
    private void inputAnimator(final View view, float w, float h) {



        ValueAnimator animator = ValueAnimator.ofFloat(0, w);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                params.leftMargin = (int) value;
                params.rightMargin = (int) value;
                view.setLayoutParams(params);
            }
        });
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout, "scaleX", 1f, 0.5f);
        set.setDuration(1000);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(animator, animator2);
        set.start();
        set.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                progress.setVisibility(View.VISIBLE);
                progressAnimator(progress);
                mInputLayout.setVisibility(View.INVISIBLE);

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        recovery();
                    }
                }, 2000);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub

            }
        });


    }

    private void progressAnimator(final View view) {
        PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX", 0.5f, 1f);
        PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY", 0.5f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view, animator, animator2);
        animator3.setDuration(1000);
        animator3.setInterpolator(new JellyInterpolator());
        animator3.start();

    }

    /**
     * 恢复初始状态
     */
    private void recovery() {
        progress.setVisibility(View.GONE);
        mInputLayout.setVisibility(View.VISIBLE);
        mName.setVisibility(View.VISIBLE);
        mPsw.setVisibility(View.VISIBLE);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mInputLayout.getLayoutParams();
        params.leftMargin = 0;
        params.rightMargin = 0;
        mInputLayout.setLayoutParams(params);


        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout, "scaleX", 0.5f,1f );
        animator2.setDuration(500);
        animator2.setInterpolator(new AccelerateDecelerateInterpolator());
        animator2.start();
    }

        public class MyThread implements Runnable{
        @Override
            public void run(){
            LOGINFLAG=false;
            Log.e("MyThread:"," ");
               info= WebService.exceteHttpGet(username.getText().toString(),pwd.getText().toString(),String.valueOf(role).toString());
               Log.e("info:",info+"");
               if(info.contains("true")){
                   LOGINFLAG=true;

               }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(""+info,"");


                    }
                });

        }

        }

}