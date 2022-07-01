package com.github;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/*
    TODO
        Создайте bean для создания мока
*/
@Profile("test")
@Configuration
public class NameServiceTestConfiguration {
   // your mock conf
   @Bean
   @Primary
   public NameService nameService() {
       return Mockito.mock(NameService.class);
   }
}