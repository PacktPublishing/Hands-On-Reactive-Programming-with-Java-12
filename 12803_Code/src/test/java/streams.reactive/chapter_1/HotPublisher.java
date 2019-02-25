package streams.reactive.chapter_1;

import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.jayway.awaitility.Awaitility.await;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class HotPublisher {

  @Ignore(value = "it is unbounded producer")
  @Test
  public void shouldUsePublisherToSendEvents() {
    //given
    AtomicInteger atomicInteger = new AtomicInteger();
    SubmissionPublisher<Integer> hotPublisher = new SubmissionPublisher<>();
    Flow.Subscriber<Integer> subscriber = new Flow.Subscriber<>() {
      @Override
      public void onSubscribe(Flow.Subscription subscription) {
        System.out.println("onSubscribe: " + subscription);
        subscription.request(Integer.MAX_VALUE);
      }

      @Override
      public void onNext(Integer item) {
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

    Stream<Integer> infiniteProducer = Stream.iterate(0, i -> i + 2);

    //when
    hotPublisher.subscribe(subscriber);//chain subscriber with publisher
    infiniteProducer.forEach(hotPublisher::submit);
    //subscriber will be flooded with data, can say: "stop, don't send more!"
    hotPublisher.close();

    //then will fail
    await().atMost(10_000, TimeUnit.MILLISECONDS).until(
        () -> assertThat(atomicInteger.get()).isEqualTo(1000)
    );
  }
}
