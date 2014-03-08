package com.malinkang.downloadmanager;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

class MyAdapter extends BaseAdapter {
	private String DOWNLOAD_FOLDER_NAME = "malinkang";

	private class ViewHolder {
		private TextView downloadSize;
		private TextView downloadPrecent;
		private ProgressBar progress;
		private Button download;
	}

	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<Mp4> mMp4s;
	private DownloadManager downloadManager;

	public MyAdapter(Context context, ArrayList<Mp4> mp4s) {
		super();
		mMp4s = mp4s;
		mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		downloadManager = (DownloadManager) mContext
				.getSystemService(mContext.DOWNLOAD_SERVICE);

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mMp4s.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mMp4s.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = null;
		ViewHolder holder = null;
		if (convertView != null) {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		} else {
			view = mInflater.inflate(R.layout.item, null, false);
			holder = new ViewHolder();
			holder.downloadSize = (TextView) view
					.findViewById(R.id.download_size);
			holder.downloadPrecent = (TextView) view
					.findViewById(R.id.download_precent);
			holder.progress = (ProgressBar) view.findViewById(R.id.pb);
			holder.download = (Button) view.findViewById(R.id.download);
			view.setTag(holder);
		}
		holder.downloadSize.setVisibility(View.GONE);
		holder.downloadPrecent.setVisibility(View.GONE);
		holder.progress.setVisibility(View.GONE);
		final Handler handler = new MyHandler(holder);
		final String url = mMp4s.get(position).getDownloadUrl();

		holder.download.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				long downloadId = PreferenceManager
						.getDefaultSharedPreferences(mContext).getLong(url, -1);
				if (((Button) v).getText().equals("下载")) {
					download(url, handler, downloadId);
				} else {
					downloadManager.remove(downloadId);
				}
			}
		});

		return view;
	}

	// 观察
	class DownloadChangeObserver extends ContentObserver {
		private Handler handler;
		private long downloadId;

		public DownloadChangeObserver(Handler handler, long downloadId) {
			super(handler);
			this.handler = handler;
			this.downloadId = downloadId;
		}

		@Override
		public void onChange(boolean selfChange) {
			updateView(handler, downloadId);
		}

	}

	private class MyHandler extends Handler {
		private ViewHolder holder;

		public MyHandler(ViewHolder holder) {
			// TODO Auto-generated constructor stub
			this.holder = holder;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case 0:
				int status = (Integer) msg.obj;
				if (isDownloading(status)) {
					//
					holder.progress.setVisibility(View.VISIBLE);
					holder.progress.setMax(0);
					holder.progress.setProgress(0);
					holder.download.setText("暂停");
					holder.downloadSize.setVisibility(View.VISIBLE);
					holder.downloadPrecent.setVisibility(View.VISIBLE);
					if (msg.arg2 < 0) {
						holder.progress.setIndeterminate(true);
						holder.downloadSize.setText("0M/0M");
					} else {
						holder.progress.setIndeterminate(false);
						holder.progress.setMax(msg.arg2);
						holder.progress.setProgress(msg.arg1);
						holder.downloadPrecent.setText(getPercent(msg.arg1,
								msg.arg2));
						//
						holder.downloadSize.setText(getSize(msg.arg1) + "/"
								+ getSize(msg.arg2));
					}
				} else {
					// 停止下载
					holder.progress.setVisibility(View.GONE);
					holder.progress.setMax(0);
					holder.progress.setProgress(0);
					holder.download.setText("下载");
					holder.downloadSize.setVisibility(View.GONE);
					holder.downloadPrecent.setVisibility(View.GONE);
					if (status == DownloadManager.STATUS_FAILED) {

					} else if (status == DownloadManager.STATUS_SUCCESSFUL) {

					} else {

					}
				}
				break;
			}
		}
	}

	static final DecimalFormat DOUBLE_DECIMAL_FORMAT = new DecimalFormat("0.##");
	public static final int MB_2_BYTE = 1024 * 1024;
	public static final int KB_2_BYTE = 1024;

	/**
	 * @param size
	 * @return
	 */
	public static CharSequence getSize(long size) {
		if (size <= 0) {
			return "0M";
		}

		if (size >= MB_2_BYTE) {
			return new StringBuilder(16).append(
					DOUBLE_DECIMAL_FORMAT.format((double) size / MB_2_BYTE))
					.append("M");
		} else if (size >= KB_2_BYTE) {
			return new StringBuilder(16).append(
					DOUBLE_DECIMAL_FORMAT.format((double) size / KB_2_BYTE))
					.append("K");
		} else {
			return size + "B";
		}
	}

	public static String getPercent(long progress, long max) {
		int rate = 0;
		if (progress <= 0 || max <= 0) {
			rate = 0;
		} else if (progress > max) {
			rate = 100;
		} else {
			rate = (int) ((double) progress / max * 100);
		}
		return new StringBuilder(16).append(rate).append("%").toString();
	}

	// 正在下载
	public static boolean isDownloading(int downloadManagerStatus) {
		return downloadManagerStatus == DownloadManager.STATUS_RUNNING
				|| downloadManagerStatus == DownloadManager.STATUS_PAUSED
				|| downloadManagerStatus == DownloadManager.STATUS_PENDING;
	}

	public void updateView(Handler handler, long downloadId) {
		// 获取状态和字节
		int[] bytesAndStatus = getBytesAndStatus(downloadId);
		//
		handler.sendMessage(handler.obtainMessage(0, bytesAndStatus[0],
				bytesAndStatus[1], bytesAndStatus[2]));
	}

	public int[] getBytesAndStatus(long downloadId) {
		int[] bytesAndStatus = new int[] { -1, -1, 0 };
		DownloadManager.Query query = new DownloadManager.Query()
				.setFilterById(downloadId);
		Cursor c = null;
		try {
			c = downloadManager.query(query);
			if (c != null && c.moveToFirst()) {
				// 当前下载的字节
				bytesAndStatus[0] = c
						.getInt(c
								.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
				// 总字节数
				bytesAndStatus[1] = c
						.getInt(c
								.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
				// 状态
				bytesAndStatus[2] = c.getInt(c
						.getColumnIndex(DownloadManager.COLUMN_STATUS));
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return bytesAndStatus;
	}

	public void download(String url, Handler handler, long downloadId) {
		File folder = new File(DOWNLOAD_FOLDER_NAME);
		if (!folder.exists() || !folder.isDirectory()) {
			folder.mkdirs();
		}
		DownloadManager.Request request = new DownloadManager.Request(
				Uri.parse(url));
		request.setDestinationInExternalPublicDir(DOWNLOAD_FOLDER_NAME,
				url.substring(url.lastIndexOf("/") + 1));
		request.setVisibleInDownloadsUi(false);
		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
		downloadId = downloadManager.enqueue(request);
		/** save download id to preferences **/
		Editor edit = PreferenceManager.getDefaultSharedPreferences(mContext)
				.edit();
		edit.putLong(url, downloadId).commit();
		// 注册
		mContext.getContentResolver().registerContentObserver(
				Uri.parse("content://downloads/my_downloads"), true,
				new DownloadChangeObserver(handler, downloadId));
		updateView(handler, downloadId);
	}
}