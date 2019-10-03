package com.example.file_manager;

import java.util.ArrayList;

public class FilePN {
    //存储文件名称
    private ArrayList<String> names = null;
    //存储文件路径
    private ArrayList<String> paths = null;
	public FilePN(ArrayList<String> na, ArrayList<String> pa){
		names = na;
		paths = pa;
	}
	public ArrayList<String> GetNames(){
		return this.names;
	}
	public ArrayList<String> GetPaths(){
		return this.paths;
	}

}
