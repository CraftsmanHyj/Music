package com.hyj.music.model;

import java.io.Serializable;

public class Song implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;// 歌曲名
	private String fileName; // 文件名
	private int nameLength; // 歌曲名字长度

	public String getName() {
		return name;
	}

	public char[] getNameCharacters() {
		return name.toCharArray();
	}

	public void setName(String name) {
		this.name = name;
		this.nameLength = name.length();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getNameLength() {
		return nameLength;
	}
}
