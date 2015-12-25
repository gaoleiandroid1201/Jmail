package com.xanxus.emaildemo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {

	private Button sendButton, add_attachment;
	private TextView file_dir;
	private EditText email_addr_edit, email_pwd_edit, accept_email_addr_edit;
	private final int FILE_SELECT_CODE = 0;
	private final int FILECHOOSER_RESULTCODE = 1;
	//附件地址，目前暂时只能添加图片
	private String file_path=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sendButton = (Button) findViewById(R.id.send_btn);
		add_attachment = (Button) findViewById(R.id.add_attachment);
		file_dir = (TextView) findViewById(R.id.file_dir);
		email_addr_edit = (EditText) this.findViewById(R.id.email_addr_edit);
		email_pwd_edit = (EditText) this.findViewById(R.id.email_pwd_edit);
		accept_email_addr_edit = (EditText) this
				.findViewById(R.id.accept_email_addr_edit);

		sendButton.setOnClickListener(this);
		add_attachment.setOnClickListener(this);
	}

	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.send_btn:
			// TODO Auto-generated method stub
			// 在这里需要注意：1.我测试只有QQ邮箱行得通，163邮箱不行。2.如果在QQ邮箱
			// （设置-账户）中没有开启POP3/SMTP服务，则正常输入密码即可，如果开启了输入密码就不行了，要输入授权码，如
			// "fyxvinfyaajrhcbi"，
			// 不然会报错：javax.mail.AuthenticationFailedException
			SenderRunnable senderRunnable = new SenderRunnable(email_addr_edit
					.getText().toString(), email_pwd_edit.getText().toString());
			senderRunnable.setMail("发送邮件的标题", "发送件的内容。。。。。。。。。。。。。。。。。。",
					accept_email_addr_edit.getText().toString(), file_path);
			// 最后一个参数为null表明不发送附件，也可以发送附件，如"/mnt/sdcard/test.txt"
			new Thread(senderRunnable).start();
			break;
		case R.id.add_attachment:
			showFileChooser();
			break;
		}
	}

	/** 调用文件选择软件来选择文件 **/
	private void showFileChooser() {
		 Intent i = new Intent(Intent.ACTION_GET_CONTENT);
         i.addCategory(Intent.CATEGORY_OPENABLE);
         i.setType("image/*");
         startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
	}

	/** 根据返回选择的文件，来进行上传操作 **/
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == Activity.RESULT_OK) {
			// Get the Uri of the selected file
			Uri uri = data.getData();
			Log.d("gaolei", "uri---------------" + uri);
			String[] pojo = { MediaStore.Images.Media.DATA };
			CursorLoader cursorLoader = new CursorLoader(this, uri, pojo, null,
					null, null);
			Cursor cursor = cursorLoader.loadInBackground();
			if(cursor!=null){
			cursor.moveToFirst();
			file_path = cursor.getString(cursor.getColumnIndex(pojo[0]));
			Log.d("gaolei", "file_path---------------" + file_path);
			file_dir.setText(file_path);
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	

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
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
}
