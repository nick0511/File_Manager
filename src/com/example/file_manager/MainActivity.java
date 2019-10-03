package com.example.file_manager;
 
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;
 
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
 
public class MainActivity extends ListActivity {
    private static final String ROOT_PATH = "/sdcard";
    private static  String CURRENT_PATH = "/sdcard";
    private int returnstatus = 1;
    private long firstPressedTime;
    //存储文件名称
    private ArrayList<String> mFileName = null;
    //存储文件路径
    private ArrayList<String> mFilePath = null;
    
//    private String last_cmd = "su";
    private long clickTime=0;
    private int searchon=0;

    
    //重命名布局xml文件显示dialog
    private View view;
 
    private EditText editText;
 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //显示文件列表
        showFileDir(ROOT_PATH);
        
        //添加长按选项
        this.getListView().setOnItemLongClickListener(new OnItemLongClickListener(){
        	@Override
        	public boolean onItemLongClick(AdapterView<?> arg0,View arg1,int position,long arg3){
        		String path = mFilePath.get(position);
                File file = new File(path);
                fileHandle(file);
//        		Toast.makeText(getApplicationContext(), file.getName(), Toast.LENGTH_LONG).show();
        		return true;
        		
        	}
        });
    }
 
    /**
     * 扫描显示文件列表
     * @param path
     */
    String LOG_TAG="TAG";
    private void showFileDir(ArrayList<String> mFileName,ArrayList<String> mFilePath) {
//      for(String f : mFilePath){
//    	Log.e("name",f);
////        Log.e("PATH",f.getPath());
//    }
         this.mFileName=mFileName;
         this.mFilePath=mFilePath;
    	 this.setListAdapter(new MyAdapter(this, mFileName, mFilePath));
        
    }

    private void showFileDir(String path) {
        mFileName = new ArrayList<String>();
        mFilePath = new ArrayList<String>();
        ArrayList<String> FileSet = new ArrayList<String>();
        ArrayList<String> FilePathSet = new ArrayList<String>();
        File file = new File(path);
 
        File[] files = file.listFiles();
        CURRENT_PATH=path;
        //如果当前目录不是根目录
        if (!ROOT_PATH.equals(path)) {
            mFileName.add("@1");
            mFilePath.add(ROOT_PATH);
            mFileName.add("@2");
            mFilePath.add(file.getParent());
        }
      //添加所有文件
        for (File f : files) {
        	if(!f.isDirectory()){
        		FileSet.add(f.getName());
        		FilePathSet.add(f.getPath());
        	}
        	else{
        		mFileName.add(f.getName());
            	mFilePath.add(f.getPath());
        	}
//            mFileSize.add((f.length()/1024/1024)+"Mb");
        }
        for(String name : FileSet){
        	mFileName.add(name);
        }
        for(String path1 : FilePathSet){
        	mFilePath.add(path1);
        }
        //调试操作
        for(String f : mFilePath){
        	Log.e("name",f);
//            Log.e("PATH",f.getPath());
        }
        this.setListAdapter(new MyAdapter(this, mFileName, mFilePath));

    }
 
    /**
     * 点击事件
     * @param l
     * @param v
     * @param position
     * @param id
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String path = mFilePath.get(position);
        File file = new File(path);
        // 文件存在并可读
        if (file.exists() && file.canRead()) {
            if (file.isDirectory()) {
                //显示子目录及文件
                showFileDir(path);
                CURRENT_PATH=path;
            } else {
                //处理文件
                fileHandle(file);
            }
        }
        //没有权限
        else {
            Resources res = getResources();
            new AlertDialog.Builder(this).setTitle("提示")
//                    .setMessage(res.getString(R.string.no_permission))
            		.setMessage("没有权限打开")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "试试其他操作..", Toast.LENGTH_SHORT).show();
                        }
                    }).show();
        }
//        super.onListItemClick(l, v, position, id);
    }
 
    //对文件进行增删改
    private void fileHandle(final File file) {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 打开文件
                if (which == 0) {
                    openFile(file);
                }
                //修改文件名
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
                                //排除没有修改情况
                                if (!modifyName.equals(file.getName())) {
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("注意!")
                                            .setMessage("文件名已存在，是否覆盖？")
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (file.renameTo(newFile)) {
                                                        showFileDir(fpath);
                                                        displayToast("重命名成功！");
                                                    } else {
                                                        displayToast("重命名失败！");
                                                    }
                                                }
                                            })
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
 
                                                }
                                            })
                                            .show();
                                }
                            } else {
                                if (file.renameTo(newFile)) {
                                    showFileDir(fpath);
                                    displayToast("重命名成功！");
                                } else {
                                    displayToast("重命名失败！");
                                }
                            }
                        }
                    };
                    AlertDialog renameDialog = new AlertDialog.Builder(MainActivity.this).create();
                    renameDialog.setView(view);
                    renameDialog.setButton("确定", listener2);
                    renameDialog.setButton2("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                        }
                    });
                    renameDialog.show();
                }
                //删除文件

                else  {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("注意!")
                            .setMessage("确定要删除此文件吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (file.delete()) {
                                        //更新文件列表
                                        showFileDir(file.getParent());
                                        displayToast("删除成功！");
                                    } else {
                                        displayToast("删除失败！");
                                    }
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                }
            }
        };
        //选择文件时，弹出增删该操作选项对话框
        String[] menu = {"打开", "重命名", "删除"};
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("请选择要进行的操作!")
                .setItems(menu, listener)
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }
 
    //打开文件
    private void openFile(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        String type = getMIMEType(file);
        intent.setDataAndType(Uri.fromFile(file), type);
        startActivity(intent);
    }
 
    //获取文件mimetype
    private String getMIMEType(File file) {
        String type = "";
        String name = file.getName();
        //文件扩展名
        String end = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase();
        if (end.equals("m4a") || end.equals("mp3") || end.equals("wav")) {
            type = "audio";
        } else if (end.equals("mp4") || end.equals("3gp")) {
            type = "video";
        } else if (end.equals("jpg") || end.equals("png") || end.equals("jpeg") || end.equals("bmp") || end.equals("gif")) {
            type = "image";
        } else {
            //如果无法直接打开，跳出列表由用户选择
            type = "*";
        }
        type += "/*";
        return type;
    }
 
    private void displayToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
 //侧栏的菜单设置
    public boolean onCreateOptionsMenu(Menu menu) {
        //super.getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.add(Menu.NONE, Menu.FIRST + 1, 7, "新建文件夹")
                .setIcon(android.R.drawable.ic_menu_add);
        menu.add(Menu.NONE, Menu.FIRST + 2, 8, "新建文件")
        		.setIcon(android.R.drawable.ic_menu_add);
        menu.add(Menu.NONE, Menu.FIRST + 3, 9, "显示目前路径")
			.setIcon(android.R.drawable.ic_menu_add);
        menu.add(Menu.NONE, Menu.FIRST + 4, 10, "搜索文件")
		.setIcon(android.R.drawable.ic_menu_add);

        return true;
    }
 //用于侧边栏的创建文件的监听动作
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	 
        switch (item.getItemId()) {
            case Menu.FIRST + 1:{
                final EditText editText = new EditText(MainActivity.this);
                new AlertDialog.Builder(MainActivity.this).setTitle("请输入文件夹名称").setView(editText).setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                String mFileName = editText.getText().toString();
                                String IMAGES_PATH = CURRENT_PATH + "/" + mFileName + "/";       //获取目录
//                                String IMAGES_PATH = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + mFileName + "/";
                                if(CURRENT_PATH.matches("/storage/emulated/0/.*")||CURRENT_PATH.matches("/sdcard.*"))
                                	//只允许到存储器目录创建。做正则匹配判断
                                {		
                                    
                                	
                                	int isok=createMkdir(IMAGES_PATH);
          
                                if(isok==1){
                                	Toast.makeText(getApplicationContext(), "已经入该目录", Toast.LENGTH_LONG).show();
                               		showFileDir(IMAGES_PATH);
                                }
                               	else
                                	Toast.makeText(getApplicationContext(), "创建失败", Toast.LENGTH_LONG).show();
                            }
                            else{
                            	Toast.makeText(getApplicationContext(), "无权限，只能在/storage/emulated/0或/sdcard下创建", Toast.LENGTH_LONG).show();//限定
                            }
                                
                            }
                        }).setNegativeButton("取消", null).show();
               
            }
                break;
            case Menu.FIRST + 2:{
                final EditText editText = new EditText(MainActivity.this);
                new AlertDialog.Builder(MainActivity.this).setTitle("请输入文件名称").setView(editText).setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                String mFileName = editText.getText().toString();
                                String IMAGES_PATH = CURRENT_PATH + "/" + mFileName + "/";       //获取目录
//                                String IMAGES_PATH = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + mFileName + "/";
                              //只允许到存储器目录创建。做正则匹配判断
                                if(CURRENT_PATH.matches("/storage/emulated/0/.*")||CURRENT_PATH.matches("/sdcard.*")){
	                                int isok =1 ;
									try {
										isok = createFile(IMAGES_PATH);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
	                                
	                                if(isok==1){
	                                	Toast.makeText(getApplicationContext(), "创建成功，目录已刷新", Toast.LENGTH_LONG).show();
	                               		showFileDir(CURRENT_PATH);
	                                }
	                               	else{
	                                	Toast.makeText(getApplicationContext(), "创建失败", Toast.LENGTH_LONG).show();
	                                	Toast.makeText(getApplicationContext(), "请考虑权限或者文件已存在", Toast.LENGTH_LONG).show();
	                               	}
                                }
                                else{
                                	Toast.makeText(getApplicationContext(), "无权限，只能在/storage/emulated/0或/sdcard下创建", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).setNegativeButton("取消", null).show();
                
               
            }
                break;
            case Menu.FIRST + 3:
            	Toast.makeText(getApplicationContext(),CURRENT_PATH,Toast.LENGTH_LONG).show();
            break;
            case Menu.FIRST + 4:{
                final EditText editText = new EditText(MainActivity.this);

                new AlertDialog.Builder(MainActivity.this).setTitle("请输入文件名称(当前目录下)").setView(editText).setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                String KeyWord = editText.getText().toString();	//关键字
                                FilePN pn = null;
								try {
									pn = Shell_Find(CURRENT_PATH,KeyWord);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								finally{
                                showFileDir(pn.GetPaths(),pn.GetNames());
                                searchon=1;
								}
                            }
                        }).setNegativeButton("取消", null).show();
                
               
            }
            break;
        }
        return false;
    }
    
//    创建文件代码
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
    //创建目录代码，检测文件是否存在
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
    
    //文件搜索
    
    public FilePN Shell_Find(String Path,String KeyWord) throws InterruptedException{

		ArrayList<String> names = new ArrayList<String>();
	    //存储文件路径
	    ArrayList<String> paths = new ArrayList<String>();
		
		
	    String cmd="busybox find "+ Path+"/ -name \"*"+ KeyWord +"*\" ";

        DataOutputStream dos = null;
        DataInputStream dis = null;

        try {
            Process p = Runtime.getRuntime().exec("su");// 经过Root处理的android系统即有su命令
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());

            Log.i("cmd", cmd);
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
//            this.last_cmd = cmd;

            String line = null;
            while ((line = dis.readLine()) != null) {
                
                File tmp = new File(line);
                names.add(line);
            	paths.add(tmp.getParent());
            	Log.d("abpath", line);
            	Log.d("abparent", tmp.getParent());
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
		
		
		
		
		
		
		
		FilePN pn = new FilePN(names,paths); 
		return pn;
	}
    
    
 //回退键两次点击会退出
 //利用clickTime计算
        public boolean onKeyDown(int keyCode, KeyEvent event) {
        	File file = new File(CURRENT_PATH);
        		if(searchon==1&&CURRENT_PATH.equals(ROOT_PATH) ){
        			showFileDir(CURRENT_PATH);
        			searchon=0;
        			return false;
        		}

        		if(CURRENT_PATH.equals(ROOT_PATH) &&keyCode == KeyEvent.KEYCODE_BACK){
        			if (SystemClock.uptimeMillis() - clickTime <= 1500) {
        				return super.onKeyDown(keyCode, event);
        				}
        			else{
                        clickTime = SystemClock.uptimeMillis();
                        Toast.makeText(getApplicationContext(), "再次点击退出", Toast.LENGTH_SHORT).show();
                        return false;
        			}
        		}
        		else
        			showFileDir(file.getParent());
        		return false;
        		
    }
}