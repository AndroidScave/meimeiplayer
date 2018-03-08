package com.scave.meimei;

import android.app.Activity;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity 
{
	private PageLayout page;
	private TextView titleText;
	private TextView author;
	private TextView startTime;
	private ImageView home;
	private ImageView list;
	private SeekBar seek;
	private TextView endTime;
	private ImageView last;
	private ImageView stop;
	private ImageView next;
	private MyAdapter adapter;
	private ListView mListView;
	int index=-1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		initView();
		page.setTouchScale(dip2px(150));
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
					if(index!=-1){
					index--;
					if(index<0){
					index = mListView.getCount()-1;
					play(index);
					}else{
						play(index);
					}
				}
				}
			});
		next.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1) {
					if(index!=-1){
					index++;
					if(index<mListView.getCount()-1){
						play(index);
					}else{
						index=0;
						play(index);
					}
					}
				}
			});
		stop.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1) {
					if(index>=0){
					if(PlayMusic.mediaPlayer.isPlaying()==true){
						PlayMusic.pause();
						stop.setImageResource(R.drawable.player_play);
					}else{
						PlayMusic.play();
						stop.setImageResource(R.drawable.player_pause);
					}
					}
				}
			});
			
		home.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1) {
					page.showPage(0);
					list.setColorFilter(0xffc0c0c0);
					home.setColorFilter(0xfffffffff);
				}
			});
		list.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1) {
					page.showPage(1);
					home.setColorFilter(0xffc0c0c0);
					list.setColorFilter(0xfffffffff);
				}
			});
		page.setOnPageChangeListener(new PageLayout.OnPageChangeListener(){
				@Override
				public void onPageChange(View v, int idx){
					if(idx==0){
						list.setColorFilter(0xffc0c0c0);
						home.setColorFilter(0xfffffffff);
					}else{
						home.setColorFilter(0xffc0c0c0);
						list.setColorFilter(0xfffffffff);
					}
				}
			});
    }
	private void initView(){
		page = (PageLayout) findViewById(R.id.page);
		home = (ImageView) findViewById(R.id.home);
		list = (ImageView) findViewById(R.id.list);
		titleText = (TextView) findViewById(R.id.titleText);
		author = (TextView) findViewById(R.id.author);
		startTime = (TextView) findViewById(R.id.startTime);
		seek = (SeekBar) findViewById(R.id.seek);
		endTime = (TextView) findViewById(R.id.endTime);
		last = (ImageView) findViewById(R.id.last);
		stop = (ImageView) findViewById(R.id.play);
		next = (ImageView) findViewById(R.id.next);
		mListView = (ListView) findViewById(R.id.listListView1);
		list.setColorFilter(0xffc0c0c0);
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
		String singer = map.get("singer");
		index = index-1;
		titleText.setText(song.replace(".mp3","").replace(".flac",""));
		author.setText(singer);
		PlayMusic.startPlay(MainActivity.this,path,seek,startTime,endTime);
		stop.setImageResource(R.drawable.player_pause);
	}
	private void toast(CharSequence msg){
		Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
	}
	private int dip2px(float f) {
        return (int) ((getResources().getDisplayMetrics().density * f) + 0.5f);
    }
	
}
