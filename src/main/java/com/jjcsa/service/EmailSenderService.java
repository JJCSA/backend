package com.jjcsa.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.google.common.collect.Lists;
import com.jjcsa.dto.EmailTemplateDto;
import com.jjcsa.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderService {

    private final AmazonSimpleEmailService emailService;
    private final SendEmailRequest sendEmailRequest;
    private final EmailTemplateService emailTemplateService;

    // TODO fetch it from yml based on the limit of recipients
    private static final int batchSize = 50;

    private int sendEmail(List<Destination> destinationList, Message message, String fromAddress){
        int failedCount = 0;
        log.info("Destination List:{}, size:{}", destinationList, destinationList.size());
        sendEmailRequest.setMessage(message);
        sendEmailRequest.setSource(fromAddress);
        for(Destination destination: destinationList) {
            sendEmailRequest.setDestination(destination);
            try{
                SendEmailResult res = emailService.sendEmail(sendEmailRequest);
                log.info("Email sent to destinations:{}, Response:{}",destination, res);
            }
            // TODO catch specific error and exception
            catch (Exception exception){
                log.info("Email sent failed for destination:{} with error:{}, stacktrace:{}", destination, exception.getMessage(), exception.getStackTrace());
                failedCount += 1;
            }
        }
        log.info("Failed Count:{}", failedCount);
        return failedCount * batchSize;
    }

    public int sendEmail(User user, String fromAddress, String emailFor,
                          List<String> toAddressList, List<String> ccAddressList, List<String> bccAddressList) {
        List<Destination> destinationList = this.resolveDestination(bccAddressList,ccAddressList,toAddressList);
        Message message = this.resolveMessage(user, emailFor);
        int failed = this.sendEmail(destinationList, message, fromAddress);
        log.info("Email Failures:{}",failed);
        return failed;
    }

    private Destination getDestination(final List<String> bccAddressList, final List<String> ccAddressList, final List<String> toAddressList) {
        Destination destination = new Destination();
        destination.setBccAddresses(bccAddressList);
        destination.setCcAddresses(ccAddressList);
        destination.setToAddresses(toAddressList);
        return destination;
    }

    /**
     * Resolve the Destination based on limit of recipients.
     * @param bccAddressList list of address that needs to be included in bcc field
     * @param ccAddressList list of address that needs to be included in cc field
     * @param toAddressList list of address that needs to be included in to field
     * @return List of Destination objects based on batchSize or limit.
     */
    private List<Destination> resolveDestination(final List<String> bccAddressList, final List<String> ccAddressList, final List<String> toAddressList) {
        log.info("To Address:{}, bccAddress:{}, ccAddress:{}",toAddressList.size(), bccAddressList.size(), ccAddressList.size());
        if(bccAddressList.size() + ccAddressList.size() + toAddressList.size() <= batchSize) {
            return Collections.singletonList(this.getDestination(bccAddressList,ccAddressList,toAddressList));
        }
        List<Destination> destinationList = new ArrayList<>();
        List<List<String>> bccAddressBatches = Lists.partition(bccAddressList, batchSize);
        for(List<String> address: bccAddressBatches) {
            destinationList.add(this.getDestination(address,Collections.EMPTY_LIST, Collections.EMPTY_LIST));
        }
        if(ccAddressList.size() + toAddressList.size() <= batchSize){
            destinationList.add(this.getDestination(Collections.EMPTY_LIST, ccAddressList, toAddressList));
            return destinationList;
        }
        List<List<String>> ccAddressBatches = Lists.partition(ccAddressList, batchSize);
        List<List<String>> toAddressBatches = Lists.partition(toAddressList, batchSize);
        for(List<String> address: toAddressBatches) {
            destinationList.add(this.getDestination(Collections.EMPTY_LIST, Collections.EMPTY_LIST, address));
        }
        for(List<String> address: ccAddressBatches) {
            destinationList.add(this.getDestination(Collections.EMPTY_LIST, address, Collections.EMPTY_LIST));
        }
        return destinationList;
    }

    /**
     * Get Content From data provided. It assumes charset for the data provided is ASCII.
     * If it is different then must have to set the charset field of content as well.
     * @param data Data to be set as Content
     * @return the content object
     */
    private Content getContent(final String data){
        Content content = new Content();
        content.setData(data);
        return content;
    }

    /**
     * Resolve the email message based on user. this method assumes the charset for data is ASCII
     * @param user message should be sent to this user
     * @param resolvingFor resolving for i.e. Registration , Approved template etc.
     * @return the Message object for AWS ses SendEmailRequest
     */
    private Message resolveMessage(User user, String resolvingFor) {
        EmailTemplateDto resolvedTemplate = emailTemplateService.resolveTemplate(user, resolvingFor);
        Body body = new Body();
        body.setHtml(this.getContent(resolvedTemplate.getBody()));
        Message message = new Message();
        message.setSubject(this.getContent(resolvedTemplate.getSubject()));
        message.setBody(body);
        return message;
    }
}
