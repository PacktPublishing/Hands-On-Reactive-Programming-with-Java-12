package streams.reactive.chapter_4;


import org.junit.Test;
import streams.reactive.chapter_2.StockData;
import streams.reactive.chapter_2.StockEndSubscriber;
import streams.reactive.chapter_2.StocksPublisher;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class SubscriptionObjectColdPublisherTest {

  @Test
  public void givenPublisher_whenSubscribe_thenShouldConsumeAllElements() throws InterruptedException {
    //given
    StocksPublisher publisher = new StocksPublisher();
    StockEndSubscriber subscriber = new StockEndSubscriber(3);
    List<StockData> items = List.of(new StockData("APP", 123.4F), new StockData("GOO", 123.4F));
    List<StockData> expectedResult = List.of(new StockData("APP", 123.4F), new StockData("GOO", 123.4F));

    //when
    publisher.subscribe(subscriber);
    items.forEach(publisher::submit);
    publisher.close();

    //then
    await().atMost(1000, TimeUnit.MILLISECONDS).until(
        () -> assertThat(subscriber.consumedElements).containsExactlyElementsOf(expectedResult)
    );
  }

  @Test
  public void givenPublisher_whenSubscribe_thenShouldConsumeOneElement() throws InterruptedException {
    //given
    StocksPublisher publisher = new StocksPublisher();
    StockEndSubscriber subscriber = new StockEndSubscriber(1);
    List<StockData> items = List.of(new StockData("APP", 123.4F), new StockData("GOO", 123.4F));
    List<StockData> expectedResult = List.of(new StockData("APP", 123.4F));

    //when
    publisher.subscribe(subscriber);
    items.forEach(publisher::submit);
    publisher.close();

    //then
    await().atMost(1000, TimeUnit.MILLISECONDS).until(
        () -> assertThat(subscriber.consumedElements).containsExactlyElementsOf(expectedResult)
    );
  }
}
