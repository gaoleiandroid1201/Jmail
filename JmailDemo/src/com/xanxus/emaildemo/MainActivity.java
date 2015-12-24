package com.xanxus.emaildemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	private Button sendButton = null;
	private EditText email_addr_edit,email_pwd_edit,accept_email_addr_edit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sendButton = (Button) this.findViewById(R.id.send_btn);
		email_addr_edit = (EditText) this.findViewById(R.id.email_addr_edit);
		email_pwd_edit = (EditText) this.findViewById(R.id.email_pwd_edit);
		accept_email_addr_edit = (EditText) this.findViewById(R.id.accept_email_addr_edit);
		
		
		sendButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				//��������Ҫע�⣺1.�Ҳ���ֻ��QQ�����е�ͨ��163���䲻�С�2.�����QQ���� ������-�˻�����û�п���POP3/SMTP�����������������뼴�ɣ������������������Ͳ����ˣ�Ҫ������Ȩ�룬�� "fyxvinfyaajrhcbi"��
				//��Ȼ�ᱨ��javax.mail.AuthenticationFailedException
				SenderRunnable senderRunnable = new SenderRunnable(
						email_addr_edit.getText().toString(),email_pwd_edit.getText().toString());
				senderRunnable.setMail("�����ʼ��ı���",
						"���ͼ������ݡ�����������������������������������",accept_email_addr_edit.getText().toString(),null);
				//���һ������Ϊnull���������͸�����Ҳ���Է��͸�������"/mnt/sdcard/test.txt"
				new Thread(senderRunnable).start();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
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
		String mailhost=user.substring(user.lastIndexOf("@")+1, user.lastIndexOf("."));
		if(!mailhost.equals("gmail")){
			mailhost="smtp."+mailhost+".com";
			Log.i("hello", mailhost);
			sender.setMailhost(mailhost);
		}
	}

	public void setMail(String subject, String body, String receiver,String attachment) {
		this.subject = subject;
		this.body = body;
		this.receiver = receiver;
		this.attachment=attachment;
	}

	public void run() {
		// TODO Auto-generated method stub
		try {
			sender.sendMail(subject, body, user, receiver,attachment);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
