package com.dreadnought.traslucenttest;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    private static final String INFO = "INFORMATION";
    private static final int POINTS = 200;
    private Handler handler;
    private AlertDialog alertDialog;
    private MediaPlayer mediaPlayer;
    private TextView point;
    private int totalPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();
        totalPoint = 0;
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer = MediaPlayer.create(MainActivity.this,R.raw.only_drum);//get the sound
                mediaPlayer.setLooping(true);
                mediaPlayer.start();//execute the sound(play)
                alertDialog = new AlertDialog.Builder(MainActivity.this)//create a alertdialog customize
                        .setView(R.layout.win_screen)
                        .create();
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {//from here if the alertdialog is closed
                    //the sound will stop and the resources are release
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Log.i(INFO,"setOnDismissListener");
                        if (mediaPlayer != null){
                            mediaPlayer.release();
                        }

                        handler.removeCallbacks(pointResume);//the call to runner is stopped
                        totalPoint = 0;//the variable totalpoint is restarted
                    }
                });
                alertDialog.show();
                point = alertDialog.findViewById(R.id.textviewPoint);
                pointResume.run();
            }
        });




        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer = MediaPlayer.create(MainActivity.this,R.raw.lost_trombone);
                alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setView(R.layout.lost_screen)
                        .create();
                alertDialog.show();

                ImageView imageView = alertDialog.findViewById(R.id.imageViewLost);
                imageView.setBackgroundResource(R.drawable.lost_animation);
                final AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
                animationDrawable.start();
                handler = new Handler();
                final Runnable runnable = new Runnable() {//I created a runnable that then I pass it to postDelayed method
                    @Override
                    public void run() {
                        mediaPlayer.release();
                        alertDialog.dismiss();
                    }
                };
                handler.postDelayed(runnable,4000);

                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {//from here if the alertdialog is closed
                    //the sound will stop and the resources are release
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Log.i(INFO,"setOnDismissListener");
                        if (mediaPlayer != null){
                            mediaPlayer.release();
                        }

                        animationDrawable.stop(); // the animation will stop
                        handler.removeCallbacks(runnable);//the call to runnable is stop.
                    }
                });
                mediaPlayer.start();
            }
        });
    }

    private Runnable pointResume = new Runnable() {
        @Override
        public void run() {
            Log.i("INFORMATION","pointResume");
            point.setText(String.valueOf(totalPoint));
            handler.postDelayed(this,80);
            if (totalPoint == POINTS){
                mediaPlayer.release();
                mediaPlayer = MediaPlayer.create(MainActivity.this,R.raw.final_drum);
                mediaPlayer.start();
                handler.removeCallbacks(this);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       mediaPlayer.stop();
                       mediaPlayer.release();
                    }
                },2500);
            }
            totalPoint++;
        }
    };

}
