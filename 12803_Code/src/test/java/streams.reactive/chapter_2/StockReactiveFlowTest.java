package streams.reactive.chapter_2;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class StockReactiveFlowTest {
  @Test
  public void givenPublisher_whenSubscribeToIt_thenShouldConsumeAllElements() throws InterruptedException {
    //given
    StocksPublisher stocksPublisher = new StocksPublisher();
    StockEndSubscriber subscriber =
        new StockEndSubscriber(
            2,
            true);
    stocksPublisher.subscribe(subscriber);
    List<StockData> items = List
        .of(new StockData("APP", 123.4F),
            new StockData("GOO", 123.4F));

    //when
    assertThat(stocksPublisher.getNumberOfSubscribers()).isEqualTo(1);
    items.forEach(stocksPublisher::submit);
    stocksPublisher.close();

    //then

    await().atMost(1000, TimeUnit.MILLISECONDS).until(
        () -> assertThat(subscriber.consumedElements)
            .containsExactlyElementsOf(items)
    );
  }

}
