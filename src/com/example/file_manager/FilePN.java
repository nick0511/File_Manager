package com.example.file_manager;

import java.util.ArrayList;

public class FilePN {
    //�洢�ļ�����
    private ArrayList<String> names = null;
    //�洢�ļ�·��
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
