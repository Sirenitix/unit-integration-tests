package com.github;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/*
    Необходимо замокать nameService и протестировать userService.getUserName
    TODO
        Использовать аннотацию запуска проекта с использованием SpringJUnit4ClassRunner
        Использовать аннотацию SpringBootTest вместе с MocksApplication.class
        Добавить ссылка на UserService
        Добавить ссылку на NameService
        Написать функцию для тестирования userService которая мокает nameService и возвращает "Mock user name"
*/
public class UserServiceUnitTest {
    @Autowired
    private UserService userService;

    @Autowired
    private NameService nameService;

    @Test
    public void whenUserIdIsProvided_thenRetrievedNameIsCorrect() {
        Mockito.when(nameService.getUserName("SomeId")).thenReturn("Mock user name");
        String testName = userService.getUserName("SomeId");
        Assert.assertEquals("Mock user name", testName);
    }
}