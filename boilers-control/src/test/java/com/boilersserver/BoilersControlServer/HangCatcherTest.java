package com.boilersserver.BoilersControlServer;

import com.boilersserver.BoilersControlServer.services.BoilersDataService;
import com.boilersserver.BoilersControlServer.services.TelegramService;
import com.boilersserver.BoilersControlServer.utils.HangCatcher;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@ExtendWith(MockitoExtension.class)
class HangCatcherTest {

    @Mock
    private TelegramService mockTelegramService;
    @Mock
    private BoilersDataService mockBoilersDataService;
    @Mock
    private RedisTemplate<String, String> mockRedisTemplate;
    @Mock
    private ZSetOperations<String, String> mockZSetOperations;

    @InjectMocks
    private HangCatcher hangCatcher;

    @BeforeEach
    void setUp() {
        //   when(mockRedisTemplate.opsForZSet()).thenReturn(mockZSetOperations);
        //   // Настраиваем мок, чтобы он возвращал список из 14 котлов
        //   List<Boiler> mockBoilersList = new ArrayList<>();
        //   for (int i = 0; i < 14; i++) {
            //       Boiler mockBoiler = mock(Boiler.class);
            //       when(mockBoiler.getId()).thenReturn(Integer.valueOf(String.valueOf(i)));
            //       when(mockBoiler.getTPod()).thenReturn("testValue" + i);
            //       when(mockBoiler.getIsOk()).thenReturn(1);
            //       mockBoilersList.add(mockBoiler);
            //   }
        //   when(mockBoilersDataService.getBoilers()).thenReturn(mockBoilersList);
    }

    @SneakyThrows
    @Test
    void testCompareAndNotify() throws TelegramApiException {
        // // Создаем моковый объект Boiler и настраиваем его поведение
        // Boiler mockBoiler = mock(Boiler.class);
        // when(mockBoiler.getId()).thenReturn(Integer.valueOf("1"));
        // when(mockBoiler.getTPod()).thenReturn("testValue");
        // when(mockBoiler.getIsOk()).thenReturn(1);
        // List<Boiler> mockBoilersList = new ArrayList<>();
        // mockBoilersList.add(mockBoiler);
        // when(mockBoilersDataService.getBoilers()).thenReturn(mockBoilersList);

        // // Вызов тестируемого метода
        // hangCatcher.compareAndNotify();

        // // Проверка, что метод setLastUpdated был вызван для mockBoiler
        // verify(mockBoiler).setLastUpdated(anyLong());

        // // Проверка, что данные были добавлены в Redis
        // verify(mockZSetOperations).add(anyString(), anyString(), anyLong());

        // // Проверка, что уведомление было отправлено через Telegram
        // verify(mockTelegramService, never()).sendAttention(anyInt(), anyString());
    }
}
