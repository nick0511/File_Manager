package com.example.file_manager;
 
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
import java.io.File;
import java.util.ArrayList;
 

public class MyAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Bitmap directory, file,music,image,mp4,pdf,ppt,xls,doc;
    //�洢�ļ�����
    private ArrayList<String> names = null;
    //�洢�ļ�·��
    private ArrayList<String> paths = null;
 
    //������ʼ��
    public MyAdapter(Context context, ArrayList<String> na, ArrayList<String> pa) {
        names = na;
        paths = pa;
        directory = BitmapFactory.decodeResource(context.getResources(), R.drawable.folder);
        file = BitmapFactory.decodeResource(context.getResources(), R.drawable.file);
        music = BitmapFactory.decodeResource(context.getResources(), R.drawable.music);
        image = BitmapFactory.decodeResource(context.getResources(), R.drawable.image);
        mp4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.mp4);
        pdf = BitmapFactory.decodeResource(context.getResources(), R.drawable.pdf);
        xls = BitmapFactory.decodeResource(context.getResources(), R.drawable.xls);
        doc = BitmapFactory.decodeResource(context.getResources(), R.drawable.doc);
        //��СͼƬ
        directory = small(directory, 0.16f);
        file = small(file, 0.1f);
        inflater = LayoutInflater.from(context);
    }
 
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return names.size();
    }
 
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return names.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.file, null);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.textView);
            holder.image = (ImageView) convertView.findViewById(R.id.imageView);
            holder.text1 = (TextView) convertView.findViewById(R.id.txv1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        File f = new File(paths.get(position).toString());
        if (names.get(position).equals("@1")) {
            holder.text.setText("/");
            holder.text1.setText(" ");
            holder.image.setImageBitmap(directory);
        } else if (names.get(position).equals("@2")) {
            holder.text.setText("..");			//��������
            holder.text1.setText(" ");
            holder.image.setImageBitmap(directory);	//������ʽ
        } else {
        	holder.text1.setText((f.length()/1024/1024)+"Mb");
            holder.text.setText(f.getName());
            if (f.isDirectory()) {
                holder.image.setImageBitmap(directory);
            } else if (f.isFile()) {

            	 String name = f.getName();
                 //�ļ���չ��
                 String end = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase();
                 Log.e("END", end);
                 if (end.equals("m4a") || end.equals("mp3")|| end.equals("wav")) {
                	 holder.image.setImageBitmap(music);
                 } else if (end.equals("mp4") || end.equals("3gp")) {
                	 holder.image.setImageBitmap(mp4);
                 } else if (end.equals("jpg") || end.equals("png") || end.equals("jpeg") || end.equals("bmp") || end.equals("gif")) {
                	 holder.image.setImageBitmap(image);
                 }else if (end.equals("doc") ) {
                	 holder.image.setImageBitmap(doc);
                 }else if (end.equals("pdf") ) {
                	 holder.image.setImageBitmap(pdf);
                 }else if (end.equals("xls")) {
                	 holder.image.setImageBitmap(xls);
                 }    
                 else {
                     //����޷�ֱ�Ӵ򿪣������б����û�ѡ��
                	 holder.image.setImageBitmap(file);
                 }
//                holder.image.setImageBitmap(file);
            } else {
                System.out.println(f.getName());
            }
        }
        return convertView;
    }
 
    private class ViewHolder {
        public TextView text1;
		private TextView text;
        private ImageView image;
    }
 
    private Bitmap small(Bitmap map, float num) {
        Matrix matrix = new Matrix();
        matrix.postScale(num, num);
        return Bitmap.createBitmap(map, 0, 0, map.getWidth(), map.getHeight(), matrix, true);
    }
}