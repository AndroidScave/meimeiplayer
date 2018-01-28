package com.scave.meimei;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.scave.meimei.R;
import java.util.List;
import java.util.Map;

public class MyAdapter extends BaseAdapter { 
	private Context context; 
	private List<Map<String, Object>> list;
	public MyAdapter(Context mainActivity, List<Map<String, Object>> list) { 
		this.context = mainActivity; 
		this.list = list; 
	} 

	@Override 
	public int getCount() { 
		return list.size(); 
	} 

	@Override 
	public Object getItem(int i) { 
		return list.get(i); 
	} 

	@Override 
	public long getItemId(int i) { 
		return i; 
	} 

	@Override 
	public View getView(int i, View view, ViewGroup viewGroup) { 
		ViewHolder holder = null; 
		if (view == null) { 
			holder = new ViewHolder(); 
			//引入布局 
			view = View.inflate(context, R.layout.item_music, null); 
			//实例化对象 
			holder.song = (TextView) view.findViewById(R.id.item_mymusic_song); 
			holder.singer = (TextView) view.findViewById(R.id.item_mymusic_singer); 
			holder.duration = (TextView) view.findViewById(R.id.item_mymusic_duration); 
			holder.position = (TextView) view.findViewById(R.id.item_mymusic_postion); 
			view.setTag(holder); 
		} else { 
			holder = (ViewHolder) view.getTag(); 
		} 
		//设置控件属性
		holder.song.setText(list.get(i).get("song").toString()); 
		holder.singer.setText(list.get(i).get("singer").toString()); 
		//转换时长
		Object duration = list.get(i).get("duration"); 
		String time = PlayMusic.formatTime((int)duration); 
		holder.duration.setText(time); 
		holder.position.setText(i+1+""); 

		return view; 
	} 
	class ViewHolder{ 
		TextView song;//歌曲名 
		TextView singer;//歌手 
		TextView duration;//时长 
		TextView position;//序号 

	} 

} 

