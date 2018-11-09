package com.xanxus.emaildemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button sendButton, add_attachment;
    private TextView file_dir;
    private EditText et_email_title, et_email_content;
    private final int FILE_SELECT_CODE = 0;
    private final int FILECHOOSER_RESULTCODE = 1;
    private String sendEmail = "***********@163.com";//发送方邮件
    private String sendEmaiPassword = "*********";//发送方邮箱密码(或授权码)
    private String receiveEmail = "*******@qq.com";//接收方邮件
    private String file_path = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendButton = (Button) findViewById(R.id.send_btn);
        add_attachment = (Button) findViewById(R.id.add_attachment);
        file_dir = (TextView) findViewById(R.id.file_dir);
        et_email_title = (EditText) this.findViewById(R.id.et_email_title);
        et_email_content = (EditText) this.findViewById(R.id.et_email_content);


        sendButton.setOnClickListener(this);
        add_attachment.setOnClickListener(this);
    }

    public void onClick(View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
            case R.id.send_btn:
                // TODO Auto-generated method stub
                // ��������Ҫע�⣺1.�Ҳ���ֻ��QQ�����е�ͨ��163���䲻�С�2.�����QQ����
                // ������-�˻�����û�п���POP3/SMTP�����������������뼴�ɣ������������������Ͳ����ˣ�Ҫ������Ȩ�룬��
                // "fyxvinfyaajrhcbi"��
                // ��Ȼ�ᱨ��javax.mail.AuthenticationFailedException
                SenderRunnable senderRunnable = new SenderRunnable(sendEmail, sendEmaiPassword);
                senderRunnable.setMail(et_email_title.getText().toString(), et_email_content.getText().toString(),
                        receiveEmail, file_path);
                // ���һ������Ϊnull���������͸�����Ҳ���Է��͸�������"/mnt/sdcard/test.txt"
                new Thread(senderRunnable).start();
                break;
            case R.id.add_attachment:
                showFileChooser();
                break;
        }
    }

    /**
     * �����ļ�ѡ�������ѡ���ļ�
     **/
    private void showFileChooser() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        // ��֪��Ϊʲô������ֻ����ת��ͼƬ��i.setType("*/*");��������ȷ��ȡ�ļ�·��
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"),
                FILECHOOSER_RESULTCODE);
    }

    /**
     * ���ݷ���ѡ����ļ����������ϴ�����
     **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode == Activity.RESULT_OK) {
            // Get the Uri of the selected file
            Uri uri = data.getData();
            Log.d("gaolei", "uri---------------" + uri);
//			String[] pojo = { MediaStore.Images.Media.DATA };
//			CursorLoader cursorLoader = new CursorLoader(this, uri, pojo, null,
//					null, null);
//			Cursor cursor = cursorLoader.loadInBackground();
//			if (cursor != null) {
//				cursor.moveToFirst();
            file_path = getPathByUri4kitkat(this, uri);
            Log.d("gaolei", "file_path---------------" + file_path);
            file_dir.setText(file_path);
//			}

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // �첽�����ʼ�
    class SenderRunnable implements Runnable {

        private String user;
        private String password;
        private String subject;
        private String body;
        private String receiver;
        private MailSender sender;
        private String attachment;

        public SenderRunnable(String user, String password) {
            this.user = user;
            this.password = password;
            sender = new MailSender(user, password);
            String mailhost = user.substring(user.lastIndexOf("@") + 1,
                    user.lastIndexOf("."));
            if (!mailhost.equals("gmail")) {
                mailhost = "smtp." + mailhost + ".com";
                Log.i("hello", mailhost);
                sender.setMailhost(mailhost);
            }
        }

        public void setMail(String subject, String body, String receiver,
                            String attachment) {
            this.subject = subject;
            this.body = body;
            this.receiver = receiver;
            this.attachment = attachment;
        }

        public void run() {
            // TODO Auto-generated method stub
            try {
                sender.sendMail(subject, body, user, receiver, attachment);
                ToastUtils.show(MainActivity.this, "您的反馈与建议，我们已经收到");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                if (e.getMessage() != null)
                    ToastUtils.show(MainActivity.this, e.getMessage().toString());
                e.printStackTrace();
            }
        }
    }

    // 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使了。targetSdkVersion 22；如果targetSdkVersion>=23怎需要动态获取WRITE_EXTERNAL_STORAGE权限；如果targetSdkVersion>=24 则可能需要用到FileProvider
    @SuppressLint("NewApi")
    public static String getPathByUri4kitkat(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {// ExternalStorageProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {// DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {// MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore
            // (and
            // general)
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
