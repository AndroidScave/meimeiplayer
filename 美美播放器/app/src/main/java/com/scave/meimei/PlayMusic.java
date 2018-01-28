package com.scave.meimei;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;

public class PlayMusic
{
	public static MediaPlayer mediaPlayer = new MediaPlayer();
	public static Timer mTimer=new Timer();
	public static TimerTask mTimerTask;
	public static long position;
	public static long duration;
	private static Handler handleProgress;

	public static void startPlay(Context mContext,String path, final SeekBar seek,
	                             final TextView t1,final TextView t2){
		try{
			
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener(){
					@Override
					public void onBufferingUpdate(MediaPlayer p1, int p2){
						seek.setSecondaryProgress(p2);
					}
				});
			mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
					@Override
					public void onPrepared(MediaPlayer p1){
						if(PlayMusic.mediaPlayer.isPlaying()==true){
							p1.stop();
							p1.start();
						}else{
							p1.start();
						}
					}
				});
			
			mediaPlayer.reset();
			mediaPlayer.setDataSource(path);
			mediaPlayer.prepareAsync();
		}catch (Exception e){}

		mTimerTask = new TimerTask() {
			@Override
			public void run(){
				if (mediaPlayer == null)
					return;
				if (mediaPlayer.isPlaying() && seek.isPressed() == false){
					handleProgress.sendEmptyMessage(0);
				}
			}
		};
		mTimer.schedule(mTimerTask, 0,1000);
		handleProgress = new Handler() {
			public void handleMessage(Message msg){
				position = mediaPlayer.getCurrentPosition();
				duration = mediaPlayer.getDuration();
				if (duration > 0){
					long pos = seek.getMax() * position / duration;
					seek.setProgress((int) pos);
					t1.setText(formatTime((int)position));
					t2.setText(formatTime((int)duration));
				}
			};
		};

	}
	public static String formatTime(int time) { 
		if (time / 1000 % 60 < 10) { 
			return time / 1000 / 60 + ":0" + time / 1000 % 60; 
		} else { 
			return time / 1000 / 60 + ":" + time / 1000 % 60; 
		} 

	} 
	
	public static void last(){
		
	}
	
	public static void next(){
		
	}
	
	public static void play(){
		mediaPlayer.start();
	}
	
	public static void pause(){
		mediaPlayer.pause();
	}
	
	public static void stop(){
		if (mediaPlayer != null){ 
			mediaPlayer.stop();
		} 
	}
}

