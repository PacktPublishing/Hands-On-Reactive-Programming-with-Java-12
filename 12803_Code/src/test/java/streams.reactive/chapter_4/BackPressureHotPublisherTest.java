package streams.reactive.chapter_4;


import org.junit.Test;
import streams.reactive.chapter_2.StockData;
import streams.reactive.chapter_2.StockEndSubscriber;
import streams.reactive.chapter_2.StocksPublisher;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class BackPressureHotPublisherTest {

  @Test
  public void whenNotUsingBackPressureSubscriberWillBeFloodedWithData() throws InterruptedException {
    //given
    OverProducingPublisher publisher = new OverProducingPublisher();
    StockEndSubscriber subscriber = StockEndSubscriber.createUnbounded();

    //when
    publisher.subscribe(subscriber);
    new Thread(() -> {
      publisher.start();
      publisher.close();
    }).start();

    //then
    await().atMost(1000, TimeUnit.MILLISECONDS).until(
        () -> assertThat(subscriber.consumedElements.size()).isGreaterThan(100)
        //if not gt 100 then subscriber will be flooded with data
    );
  }


  @Test
  public void shouldApplyBackPressureOnOverProducingPublisher() throws InterruptedException {
    //given
    OverProducingPublisher publisher = new OverProducingPublisher();
    StockEndSubscriber subscriber = new StockEndSubscriber(3);

    //when
    publisher.subscribe(subscriber);
    new Thread(() -> {
      publisher.start();
      publisher.close();
    }).start();

    //then
    await().atMost(1000, TimeUnit.MILLISECONDS).until(
        () -> assertThat(subscriber.consumedElements.size()).isEqualTo(3)
    );
  }

}
