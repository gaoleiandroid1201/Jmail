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
				//在这里需要注意：1.我测试只有QQ邮箱行得通，163邮箱不行。2.如果在QQ邮箱 （设置-账户）中没有开启POP3/SMTP服务，则正常输入密码即可，如果开启了输入密码就不行了，要输入授权码，如 "fyxvinfyaajrhcbi"，
				//不然会报错：javax.mail.AuthenticationFailedException
				SenderRunnable senderRunnable = new SenderRunnable(
						email_addr_edit.getText().toString(),email_pwd_edit.getText().toString());
				senderRunnable.setMail("发送邮件的标题",
						"发送件的内容。。。。。。。。。。。。。。。。。。",accept_email_addr_edit.getText().toString(),null);
				//最后一个参数为null表明不发送附件，也可以发送附件，如"/mnt/sdcard/test.txt"
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
