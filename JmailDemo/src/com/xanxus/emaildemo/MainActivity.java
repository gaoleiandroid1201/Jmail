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
	//������ַ��Ŀǰ��ʱֻ�����ͼƬ
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
			// ��������Ҫע�⣺1.�Ҳ���ֻ��QQ�����е�ͨ��163���䲻�С�2.�����QQ����
			// ������-�˻�����û�п���POP3/SMTP�����������������뼴�ɣ������������������Ͳ����ˣ�Ҫ������Ȩ�룬��
			// "fyxvinfyaajrhcbi"��
			// ��Ȼ�ᱨ��javax.mail.AuthenticationFailedException
			SenderRunnable senderRunnable = new SenderRunnable(email_addr_edit
					.getText().toString(), email_pwd_edit.getText().toString());
			senderRunnable.setMail("�����ʼ��ı���", "���ͼ������ݡ�����������������������������������",
					accept_email_addr_edit.getText().toString(), file_path);
			// ���һ������Ϊnull���������͸�����Ҳ���Է��͸�������"/mnt/sdcard/test.txt"
			new Thread(senderRunnable).start();
			break;
		case R.id.add_attachment:
			showFileChooser();
			break;
		}
	}

	/** �����ļ�ѡ�������ѡ���ļ� **/
	private void showFileChooser() {
		 Intent i = new Intent(Intent.ACTION_GET_CONTENT);
         i.addCategory(Intent.CATEGORY_OPENABLE);
         i.setType("image/*");
         startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
	}

	/** ���ݷ���ѡ����ļ����������ϴ����� **/
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
