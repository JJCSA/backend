package com.jjcsa.service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.jjcsa.exception.invalidEmailException;

@Service
public class EmailSenderService {
	
	 @Autowired
	    private JavaMailSender mailSender;

	    public void sendSimpleEmail(List toEmail, String sender,
	                                String body,
	                                String subject) throws invalidEmailException {
	        SimpleMailMessage message = new SimpleMailMessage();
	        String to[]=new String[toEmail.size()];
	        toEmail.toArray(to);
	        String regex = "^(.+)@(.+)$";
	        Pattern pattern = Pattern.compile(regex);
	        Matcher senderMatcher = pattern.matcher(sender);
	        if(!senderMatcher.matches())
	        {
	        	throw new invalidEmailException("Invalid Sender Email "+sender);
	        }
	        
	        for(String email:to)
	        {
		       Matcher receiverMatcher=pattern.matcher(email);
		       if(!receiverMatcher.matches())
		       {
		    	   throw new invalidEmailException("Invalid recipent name "+email);
		       }

	        }
	        message.setFrom(sender);
	        
	        message.setTo(to);
	        message.setText(body);
	        message.setSubject(subject);

	        mailSender.send(message);
	        System.out.println("Mail Send...");
	    }

}
