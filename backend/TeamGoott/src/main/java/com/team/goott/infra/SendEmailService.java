package com.team.goott.infra;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.springframework.core.io.ClassPathResource;

import com.team.goott.owner.domain.StoreVO;
import com.team.goott.user.domain.ReserveDTO;
import com.team.goott.user.domain.UserDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SendEmailService {
	
	private String userName;
	private String password;
	

	public void sendMail(ReserveDTO reserve, UserDTO user, StoreVO store) throws FileNotFoundException, IOException, AddressException, MessagingException {
		String subject = "smartreserve.com에서 예약 상태 변경 안내";
		String message = "<html><head></head><body style ='margin: 0; padding : 0; background-color : #f9f9f9;'>"
				+ "<div style='width: 100%; max-width: 600px;  margin: 20px auto; background: #fff; border: 1px solid #ddd; border-radius: 5px;  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); padding: 20px; '>"
				+ "<header>"
				+ "<h2>SmartReserve</h2>"
				+ "</header>"
				+ "<main><h3>"		
				+ user.getName() +"님</h3>"+
				"<h3>예약 식당 :  " + store.getStoreName() + "</h3>"
				+ "<h3> 예약 번호  "  + "</h3>"
				+ "<h1>" + reserve.getReserveId() + "</h1>"
				+ "<h3> 예약 날짜 :" + reserve.getReserveTime() + "</h3>" 
				+ "<h3>예약 ";
		
		switch (reserve.getStatusCodeId()) {
		case 1:
			message += "대기";
			break;
		case 2:
			message += "승인";
			break;
		case 3:
			message += "취소";
			break;
		case 4:
			message += "완료";
			break;
		}
		message += "(으)로 변경되었음을 알립니다. </h3></main></div>";
		message += "</body></html>";
		
		Properties props = new Properties();
		
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.ssl.protocols", "TLSv1.2");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.ssl.enable", "true");
		
		getAccount();
		
		Session mailSession = Session.getInstance(props, new Authenticator() {
			@Override
			 protected PasswordAuthentication getPasswordAuthentication() {
				 return new PasswordAuthentication(userName, password);
			 }
		});
		
		log.info("{}", mailSession.toString());
		
		if(mailSession != null) {
			MimeMessage mime = new MimeMessage(mailSession);
			
			mime.setFrom(new InternetAddress("hyunmyoung100@gmail.com"));
			mime.addRecipient(RecipientType.TO, new InternetAddress(user.getEmail()));
			
			mime.setSubject(subject);
			mime.setText(message,"utf-8", "html");
			Transport.send(mime);
		}
		
	}


	private void getAccount() throws FileNotFoundException, IOException {
		Properties account = new Properties();
		ClassPathResource resource = new ClassPathResource("connection.properties");
		account.load(new FileReader(resource.getFile()));
		this.userName = (String) account.get("username");
		this.password = (String) account.get("password");
	}
}
