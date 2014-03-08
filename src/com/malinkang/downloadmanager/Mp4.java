package com.malinkang.downloadmanager;

class Mp4 {
	private String downloadUrl;
	private String name;

	public Mp4(String downloadUrl, String name) {
		this.downloadUrl = downloadUrl;
		this.name = name;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}