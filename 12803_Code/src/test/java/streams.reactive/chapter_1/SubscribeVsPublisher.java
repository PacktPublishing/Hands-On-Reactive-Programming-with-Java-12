package streams.reactive.chapter_1;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jayway.awaitility.Awaitility.await;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class SubscribeVsPublisher {

  @Test
  public void shouldUsePublisherToSendEvents() throws InterruptedException {
    //given
    AtomicInteger atomicInteger = new AtomicInteger();
    SubmissionPublisher<String> publisher = new SubmissionPublisher<>();
    Flow.Subscriber<String> subscriber = new Flow.Subscriber<>() {
      @Override
      public void onSubscribe(Flow.Subscription subscription) {
        System.out.println("onSubscribe: " + subscription);
        subscription.request(Integer.MAX_VALUE);
      }

      @Override
      public void onNext(String item) {
        System.out.println("on next: " + item);
        atomicInteger.incrementAndGet();
      }

      @Override
      public void onError(Throwable throwable) {
        System.out.println("on error: " + throwable.getMessage());
      }

      @Override
      public void onComplete() {
        System.out.println("on complete");
      }
    };

    List<String> items = List.of("item-1", "item-2");

    //when
    publisher.subscribe(subscriber);//chain subscriber with publisher
    items.forEach(publisher::submit);
    publisher.close();

    //then
    await().atMost(20000, TimeUnit.MILLISECONDS).until(
        () -> assertThat(atomicInteger.get()).isEqualTo(2)
    );
  }
}
