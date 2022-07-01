package com.github.app.rest;

import com.github.app.api.MessageApi;
import com.github.domain.model.Message;
import com.github.domain.service.MessageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/*
    Необходимо протестировать MessageController используя моки
    TODO
        создайте мок для MessageService
        создайте мок для MessageController
        протестируйте создание нового сообщения
*/
@RunWith(MockitoJUnitRunner.class)
public class MessageControllerUnitTest {

    @InjectMocks
    MessageController messageController;

    @Mock
    MessageService messageService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSaveUser() {
        Message message = new Message();
        message.setFrom("Sun");
        message.setText("Hi");
        message.setTo("Moon");
        MessageApi messageApi = new MessageApi();
        messageApi.setFrom("Jupiter");
        messageApi.setText("Hi");
        messageApi.setTo("Mars");
        when(messageService.deliverMessage(eq(message))).thenReturn(message);
        Message message_1 = messageController.createMessage(messageApi);
        System.out.println(messageService.deliverMessage(message).toString());
        System.out.println(message_1 + " - message_1");
    }


}