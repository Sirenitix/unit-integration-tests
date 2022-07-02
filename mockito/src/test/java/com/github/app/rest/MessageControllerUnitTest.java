package com.github.app.rest;

import com.github.app.api.MessageApi;
import com.github.domain.model.Message;
import com.github.domain.service.MessageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

    @Mock
    MessageController messageController;

    @Mock
    MessageService messageService;

    @Test
    public void testSaveUser() {
        Message message = new Message();
        message.setFrom("Darkness");
        message.setText("Hi");
        message.setTo("Light");
        message.setDate(Date.from(Instant.now()));
        message.setId(UUID.randomUUID());

        MessageApi messageDTO = new MessageApi();
        messageDTO.setFrom("Darkness");
        messageDTO.setText("Hi");
        messageDTO.setTo("Light");

        when(messageService.deliverMessage(Mockito.eq(message))).thenReturn(message);
        when(messageController.createMessage(Mockito.eq(messageDTO))).thenReturn(message);
        Message result = messageController.createMessage(messageDTO);


        assertThat(result).isEqualTo(message);

    }


}