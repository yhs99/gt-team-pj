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
		String message = user.getName() +"님" + "예약 번호  " +reserve.getReserveId() +System.lineSeparator() 
						+ store.getStoreName() + " " + reserve.getReserveTime() + System.lineSeparator() +
						"예약 ";
		
		switch (reserve.getStatusCodeId()) {
		case 1:
			message += "대기입니다";
			break;
		case 2:
			message += "승인입니다";
			break;
		case 3:
			message += "취소입니다";
			break;
		case 4:
			message += "완료입니다";
			break;
		}
		
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
			mime.setText(message);
			Transport.send(mime);
		}
		
	}


	private void getAccount() throws FileNotFoundException, IOException {
		Properties account = new Properties();
		account.load(new FileReader("D:\\lecture\\gt-team-pj\\backend\\TeamGoott\\src\\main\\resources\\config.properties"));
		this.userName = (String) account.get("username");
		this.password = (String) account.get("password");
	}
}
