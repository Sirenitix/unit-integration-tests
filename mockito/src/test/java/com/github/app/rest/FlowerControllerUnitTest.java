package com.github.app.rest;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.app.api.Flower;
import com.github.app.rest.FlowerController;
import com.github.domain.service.FlowerService;

/*
    Необходимо протестировать MessageController используя моки
    TODO
        создайте мок для FlowerService
        создайте мок для FlowerController
        напишите метод для тестирования flowerController.isAFlower, создав мок для flowerService.isABigFlower, который возвращает true
        напишите метод для тестирования flowerController.isABigFlower, создав мок для flowerService.isABigFlower, который возвращает true
*/
@RunWith(MockitoJUnitRunner.class)
public class FlowerControllerUnitTest {

    @Mock
    private FlowerService flowerService;
    @InjectMocks
    private FlowerController flowerController;

    @Test
    public void isAFlower_withMockito_OK() {
        when(flowerService.analize(eq("nurs"))).thenReturn("Human");
        String response = flowerController.isAFlower("nurs");
        Assert.assertEquals("Human", response);
    }
    @Test
    public void isABigFlower_withMockito_OK() {
        when(flowerService.isABigFlower(eq("rose"), anyInt())).thenReturn(true);
        Flower flower = new Flower("rose", 25);
        Boolean response = flowerController.isABigFlower(flower);
        Assert.assertTrue(response);
    }
    
}