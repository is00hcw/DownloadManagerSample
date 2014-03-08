package com.malinkang.downloadmanager;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class MainActivity extends Activity {

	private String[] downloadUrls = { //
			"http://v.yingshibao.chuanke.com/cet4/CET4_reading_video/1_zongshu/001_zongshu.mp4",
			"http://v.yingshibao.chuanke.com/cet4/CET4_reading_video/2_tixing/002_tixing.mp4",
			"http://v.yingshibao.chuanke.com/cet4/CET4_reading_video/3_gejiqiao/003_xijie.mp4",
			"http://v.yingshibao.chuanke.com/cet4/CET4_reading_video/3_gejiqiao/021_ciyi.mp4",
			"http://v.yingshibao.chuanke.com/cet4/CET4_reading_video/3_gejiqiao/026_tuili.mp4",
			"http://v.yingshibao.chuanke.com/cet4/CET4_reading_video/3_gejiqiao/029_zhuzhi.mp4", };
	private ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		ArrayList<Mp4> mp4s = new ArrayList<Mp4>();
		for (int i = 0; i < downloadUrls.length; i++) {
			mp4s.add(new Mp4(downloadUrls[i], i + ""));
		}
		list = (ListView) findViewById(R.id.list);
		MyAdapter adapter = new MyAdapter(this, mp4s);
		list.setAdapter(adapter);

	}

}
