package streams.reactive.chapter_2;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class HandlingErrorsTest {
  @Test
  public void shouldContinueProcessingInCaseOfAnErrorWhenFailSilently() throws InterruptedException {
    //given
    StocksPublisher stocksPublisher = new StocksPublisher();
    StockEndSubscriber subscriber = new StockEndSubscriber(2, true);
    stocksPublisher.subscribe(subscriber);
    List<StockData> items = List.of(
        new StockData("XXX-company", 123.4F),
        new StockData("APP", 123.4F));

    //when
    assertThat(stocksPublisher.getNumberOfSubscribers()).isEqualTo(1);
    items.forEach(stocksPublisher::submit);
    stocksPublisher.close();

    //then

    await().atMost(1000, TimeUnit.MILLISECONDS).until(
        () -> assertThat(subscriber.consumedElements).containsExactlyElementsOf(List.of(new StockData("APP", 123.4F)))
    );
  }

  @Test
  public void shouldNotProcessAnyEventIfInFailFastMode() throws InterruptedException {
    //given
    StocksPublisher stocksPublisher = new StocksPublisher();
    StockEndSubscriber subscriber = new StockEndSubscriber(2, false);
    stocksPublisher.subscribe(subscriber);
    List<StockData> items = List.of(
        new StockData("XXX-company", 123.4F),
        new StockData("APP", 123.4F));

    //when
    assertThat(stocksPublisher.getNumberOfSubscribers()).isEqualTo(1);
    items.forEach(stocksPublisher::submit);
    stocksPublisher.close();

    //then

    await().atMost(1000, TimeUnit.MILLISECONDS).until(
        () -> assertThat(subscriber.consumedElements)
            .containsExactlyElementsOf(List.of())
    );
  }

}
