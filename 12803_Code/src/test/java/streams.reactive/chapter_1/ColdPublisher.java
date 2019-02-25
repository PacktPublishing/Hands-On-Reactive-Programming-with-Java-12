package streams.reactive.chapter_1;

import org.junit.Test;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static com.jayway.awaitility.Awaitility.await;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class ColdPublisher {

  @Test
  public void shouldUsePublisherToSendEvents() {
    //given
    AtomicInteger atomicInteger = new AtomicInteger();
    SubmissionPublisher<Integer> hotPublisher = new SubmissionPublisher<>();
    Flow.Subscriber<Integer> subscriber = new Flow.Subscriber<>() {
      @Override
      public void onSubscribe(Flow.Subscription subscription) {
        System.out.println("onSubscribe: " + subscription);
        subscription.request(1); //cold publisher can request as many events as needs right now.
        // Producer is not unbounded.
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

    Stream<Integer> infiniteProducer = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9);

    //when
    hotPublisher.subscribe(subscriber);//chain subscriber with publisher
    infiniteProducer.forEach(hotPublisher::submit);
    //subscriber will be flooded with data, can say: "stop, don't send more!"
    hotPublisher.close();

    //then will fail
    await().atMost(1000, TimeUnit.MILLISECONDS).until(
        () -> assertThat(atomicInteger.get()).isEqualTo(1)
    );
  }
}
