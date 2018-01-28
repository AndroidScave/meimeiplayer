package com.scave.meimei;

import android.widget.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Dialog;
import android.content.DialogInterface;

public class MainActivity extends Activity 
{
	private TextView titleText;
	private TextView startTime;
	private SeekBar seek;
	private TextView endTime;
	private Button songList;
	private Button last;
	private Button stop;
	private Button next;
	private Button menu;
	private MyAdapter adapter;
	private ListView mListView;
	private View view;
	private AlertDialog.Builder builder;
	private AlertDialog dialog;
	int index=-1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		view=(LinearLayout) getLayoutInflater().inflate(R.layout.dialog_list,null);	
		initView();
		//歌曲列表对话框
		builder =new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("歌曲列表");
		builder.setView(view);          
		builder.create();
		dialog = builder.create();
		
		adapter = new MyAdapter(this,getMusicData());
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
					play(p3);
					index = p3;
				}
			});
			
		songList.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1){
					dialog.show();
			}});
			
		seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
				int progress;
				@Override
				public void onProgressChanged(SeekBar p1, int p2, boolean p3)
				{
					this.progress = p2 * PlayMusic.mediaPlayer.getDuration()/ seek.getMax();
				}

				@Override
				public void onStartTrackingTouch(SeekBar p1)
				{
					// TODO: Implement this method
				}

				@Override
				public void onStopTrackingTouch(SeekBar p1)
				{
					PlayMusic.mediaPlayer.seekTo(progress);
				}
			});
		PlayMusic.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
				@Override
				public void onCompletion(MediaPlayer p1)
				{
					startTime.setText(PlayMusic.formatTime((int)PlayMusic.duration));
					index++;
					play(index);
				}
			});
			
		last.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1) {
					index--;
					if(index<0){
					index = mListView.getCount()-1;
					play(index);
					}else{
						play(index);
					}
				}
			});
		next.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1) {
					index++;
					if(index<mListView.getCount()-1){
						play(index);
					}else{
						index=0;
						play(index);
					}
				}
			});
		stop.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1) {
					if(index>=0){
					if(PlayMusic.mediaPlayer.isPlaying()==true){
						PlayMusic.pause();
						stop.setText("▲");
					}else{
						PlayMusic.play();
						stop.setText("■");
					}
					}
				}
			});
		menu.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1) {
					AlertDialog.Builder d = new AlertDialog.Builder(MainActivity.this);
					d.setItems(new String[]{"关于","退出"}, new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface p1, int p2){
								switch(p2){
									case 0:
										toast("Scave制作！无版权！");
										break;
									case 1:
										finish();
										break;
								}
							}
					});
					d.show();
				}
			});
    }
	private void initView(){
		titleText = (TextView) findViewById(R.id.titleText);
		startTime = (TextView) findViewById(R.id.startTime);
		seek = (SeekBar) findViewById(R.id.seek);
		endTime = (TextView) findViewById(R.id.endTime);
		songList = (Button) findViewById(R.id.songList);
		last = (Button) findViewById(R.id.last);
		stop = (Button) findViewById(R.id.stop);
		next = (Button) findViewById(R.id.next);
		menu = (Button) findViewById(R.id.menu);
		mListView = (ListView) view.findViewById(R.id.listListView1);
	}
	
	private List<Map<String, Object>> getMusicData(){
		List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();
		// 媒体库查询语句
		Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		      null, null, null, MediaStore.Audio.AudioColumns.IS_MUSIC); 
		if (cursor != null) { 
			while (cursor.moveToNext()) { 
				Song song = new Song(); 
				song.song = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)); 
				song.singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)); 
				song.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)); 
				song.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)); 
				song.size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)); 

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("song",song.song);
				map.put("singer",song.singer);
				map.put("path",song.path);
				map.put("duration",song.duration);
				map.put("size",song.size);

				if (song.size > 1000 * 800) { 
					// 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范） 
					if (song.song.contains("-")) { 
						String[] str = song.song.split("-"); 
						song.singer = str[0]; 
						song.song = str[1]; 
					} 
					listData.add(map); 
				} 
			} 
			// 释放资源 
			cursor.close(); 
		} 
		return listData; 
	}
	private void play(int index){
		Map<String, String> map = (HashMap<String, String>) mListView.getItemAtPosition(index);
		String path = map.get("path");
		String song = map.get("song");
		index = index-1;
		titleText.setText(song.replace(".mp3","").replace(".flac",""));
		PlayMusic.startPlay(MainActivity.this,path,seek,startTime,endTime);
		dialog.dismiss();
	}
	private void toast(CharSequence msg){
		Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
	}
}
