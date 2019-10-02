package com.example.file_manager;
 
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
 
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
 
public class MainActivity extends ListActivity {
    private static final String ROOT_PATH = "/sdcard";
    private static  String CURRENT_PATH = "/sdcard";
    
    
    //�洢�ļ�����
    private ArrayList<String> mFileName = null;
    //�洢�ļ�·��
    private ArrayList<String> mFilePath = null;
    
    
    //����������xml�ļ���ʾdialog
    private View view;
 
    private EditText editText;
 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //��ʾ�ļ��б�
        showFileDir(ROOT_PATH);
    }
 
    /**
     * ɨ����ʾ�ļ��б�
     * @param path
     */
    private void showFileDir(String path) {
        mFileName = new ArrayList<String>();
        mFilePath = new ArrayList<String>();
        File file = new File(path);
 
        File[] files = file.listFiles();
        //�����ǰĿ¼���Ǹ�Ŀ¼
        if (!ROOT_PATH.equals(path)) {
            mFileName.add("@1");
            mFilePath.add(ROOT_PATH);
            mFileName.add("@2");
            mFilePath.add(file.getParent());
        }
        //��������ļ�
        for (File f : files) {
            mFileName.add(f.getName());
            mFilePath.add(f.getPath());
            
        }
        //���Բ���
        for(String f : mFilePath){
        	Log.e("name",f);
//            Log.e("PATH",f.getPath());
        }
        this.setListAdapter(new MyAdapter(this, mFileName, mFilePath));
    }
 
    /**
     * ����¼�
     * @param l
     * @param v
     * @param position
     * @param id
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String path = mFilePath.get(position);
        File file = new File(path);
        // �ļ����ڲ��ɶ�
        if (file.exists() && file.canRead()) {
            if (file.isDirectory()) {
                //��ʾ��Ŀ¼���ļ�
                showFileDir(path);
                CURRENT_PATH=path;
            } else {
                //�����ļ�
                fileHandle(file);
            }
        }
        //û��Ȩ��
        else {
            Resources res = getResources();
            new AlertDialog.Builder(this).setTitle("��ʾ")
//                    .setMessage(res.getString(R.string.no_permission))
            		.setMessage("û��Ȩ�޴�")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "������������..", Toast.LENGTH_SHORT).show();
                        }
                    }).show();
        }
//        super.onListItemClick(l, v, position, id);
    }
 
    //���ļ�������ɾ��
    private void fileHandle(final File file) {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // ���ļ�
                if (which == 0) {
                    openFile(file);
                }
                //�޸��ļ���
                else if (which == 1) {
                    LayoutInflater factory = LayoutInflater.from(MainActivity.this);
                    view = factory.inflate(R.layout.rename_dialog, null);
                    editText = (EditText) view.findViewById(R.id.editText);
                    editText.setText(file.getName());
 
                    DialogInterface.OnClickListener listener2 = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            String modifyName = editText.getText().toString();
                            final String fpath = file.getParentFile().getPath();
                            final File newFile = new File(fpath + "/" + modifyName);
                            if (newFile.exists()) {
                                //�ų�û���޸����
                                if (!modifyName.equals(file.getName())) {
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("ע��!")
                                            .setMessage("�ļ����Ѵ��ڣ��Ƿ񸲸ǣ�")
                                            .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (file.renameTo(newFile)) {
                                                        showFileDir(fpath);
                                                        displayToast("�������ɹ���");
                                                    } else {
                                                        displayToast("������ʧ�ܣ�");
                                                    }
                                                }
                                            })
                                            .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
 
                                                }
                                            })
                                            .show();
                                }
                            } else {
                                if (file.renameTo(newFile)) {
                                    showFileDir(fpath);
                                    displayToast("�������ɹ���");
                                } else {
                                    displayToast("������ʧ�ܣ�");
                                }
                            }
                        }
                    };
                    AlertDialog renameDialog = new AlertDialog.Builder(MainActivity.this).create();
                    renameDialog.setView(view);
                    renameDialog.setButton("ȷ��", listener2);
                    renameDialog.setButton2("ȡ��", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                        }
                    });
                    renameDialog.show();
                }
                //ɾ���ļ�
                else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("ע��!")
                            .setMessage("ȷ��Ҫɾ�����ļ���")
                            .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (file.delete()) {
                                        //�����ļ��б�
                                        showFileDir(file.getParent());
                                        displayToast("ɾ���ɹ���");
                                    } else {
                                        displayToast("ɾ��ʧ�ܣ�");
                                    }
                                }
                            })
                            .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                }
            }
        };
        //ѡ���ļ�ʱ��������ɾ�ò���ѡ��Ի���
        String[] menu = {"���ļ�", "������", "ɾ���ļ�"};
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("��ѡ��Ҫ���еĲ���!")
                .setItems(menu, listener)
                .setPositiveButton("ȡ��", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }
 
    //���ļ�
    private void openFile(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        String type = getMIMEType(file);
        intent.setDataAndType(Uri.fromFile(file), type);
        startActivity(intent);
    }
 
    //��ȡ�ļ�mimetype
    private String getMIMEType(File file) {
        String type = "";
        String name = file.getName();
        //�ļ���չ��
        String end = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase();
        if (end.equals("m4a") || end.equals("mp3") || end.equals("wav")) {
            type = "audio";
        } else if (end.equals("mp4") || end.equals("3gp")) {
            type = "video";
        } else if (end.equals("jpg") || end.equals("png") || end.equals("jpeg") || end.equals("bmp") || end.equals("gif")) {
            type = "image";
        } else {
            //����޷�ֱ�Ӵ򿪣������б����û�ѡ��
            type = "*";
        }
        type += "/*";
        return type;
    }
 
    private void displayToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
 
    public boolean onCreateOptionsMenu(Menu menu) {
        //super.getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.add(Menu.NONE, Menu.FIRST + 1, 7, "�½��ļ���")
                .setIcon(android.R.drawable.ic_menu_add);
        menu.add(Menu.NONE, Menu.FIRST + 2, 8, "�½��ļ�")
        		.setIcon(android.R.drawable.ic_menu_add);
        menu.add(Menu.NONE, Menu.FIRST + 3, 9, "��ʾĿǰ·��")
			.setIcon(android.R.drawable.ic_menu_add);

        return true;
    }
 //���ڲ�����Ĵ����ļ�
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	 
        switch (item.getItemId()) {
            case Menu.FIRST + 1:{
                final EditText editText = new EditText(MainActivity.this);
                new AlertDialog.Builder(MainActivity.this).setTitle("�������ļ�������").setView(editText).setPositiveButton("ȷ��",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                String mFileName = editText.getText().toString();
                                String IMAGES_PATH = CURRENT_PATH + "/" + mFileName + "/";       //��ȡĿ¼
//                                String IMAGES_PATH = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + mFileName + "/";
                                if(CURRENT_PATH.matches("/storage/emulated/0/.*")||CURRENT_PATH.matches("/sdcard.*"))
                                	//ֻ�����洢��Ŀ¼������������ƥ���ж�
                                {		
                                    
                                	
                                	int isok=createMkdir(IMAGES_PATH);
          
                                if(isok==1){
                                	Toast.makeText(getApplicationContext(), "�Ѿ����Ŀ¼", Toast.LENGTH_LONG).show();
                               		showFileDir(IMAGES_PATH);
                                }
                               	else
                                	Toast.makeText(getApplicationContext(), "����ʧ��", Toast.LENGTH_LONG).show();
                            }
                            else{
                            	Toast.makeText(getApplicationContext(), "��Ȩ�ޣ�ֻ����/storage/emulated/0��/sdcard�´���", Toast.LENGTH_LONG).show();//�޶�
                            }
                                
                            }
                        }).setNegativeButton("ȡ��", null).show();
               
            }
                break;
            case Menu.FIRST + 2:{
                final EditText editText = new EditText(MainActivity.this);
                new AlertDialog.Builder(MainActivity.this).setTitle("�������ļ�����").setView(editText).setPositiveButton("ȷ��",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                String mFileName = editText.getText().toString();
                                String IMAGES_PATH = CURRENT_PATH + "/" + mFileName + "/";       //��ȡĿ¼
//                                String IMAGES_PATH = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + mFileName + "/";
                              //ֻ�����洢��Ŀ¼������������ƥ���ж�
                                if(CURRENT_PATH.matches("/storage/emulated/0/.*")||CURRENT_PATH.matches("/sdcard.*")){
	                                int isok =1 ;
									try {
										isok = createFile(IMAGES_PATH);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
	                                
	                                if(isok==1){
	                                	Toast.makeText(getApplicationContext(), "�����ɹ���Ŀ¼��ˢ��", Toast.LENGTH_LONG).show();
	                               		showFileDir(CURRENT_PATH);
	                                }
	                               	else{
	                                	Toast.makeText(getApplicationContext(), "����ʧ��", Toast.LENGTH_LONG).show();
	                                	Toast.makeText(getApplicationContext(), "�뿼��Ȩ�޻����ļ��Ѵ���", Toast.LENGTH_LONG).show();
	                               	}
                                }
                                else{
                                	Toast.makeText(getApplicationContext(), "��Ȩ�ޣ�ֻ����/storage/emulated/0��/sdcard�´���", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).setNegativeButton("ȡ��", null).show();
                
               
            }
                break;
            case Menu.FIRST + 3:
            	Toast.makeText(getApplicationContext(),CURRENT_PATH,Toast.LENGTH_LONG).show();
        }
        return false;
    }
    public static int createFile(String Path) throws IOException {
        File file = new File(Path);
        if (!file.exists()) {
        	try{
        		file.createNewFile();
        	}
        	catch(Exception e)
        	{
        		return 0;
        	}

        }
        else{
        	return 0;
        }
        return 1;

    }
    
    public static int createMkdir(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists()) {
        	try{
        		folder.mkdir();
        	}
        	catch(Exception e)
        	{
        		return 0;
        	}
         
        }
        else{
        	return 0;
        }
        return 1;

    }
 
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                finish();
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
        }
    }
}