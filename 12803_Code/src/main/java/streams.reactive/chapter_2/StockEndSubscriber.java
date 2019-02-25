package streams.reactive.chapter_2;

import com.google.common.annotations.VisibleForTesting;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.atomic.AtomicInteger;

public class StockEndSubscriber implements Subscriber<StockData> {
  private final AtomicInteger howMuchMessagesToConsume;
  private boolean failSilently;
  private Subscription subscription;
  @VisibleForTesting
  public List<StockData> consumedElements = new LinkedList<>();

  public static StockEndSubscriber createUnbounded() {
    return new StockEndSubscriber(Integer.MAX_VALUE);
  }

  public StockEndSubscriber(Integer howMuchMessagesToConsume) {
    this(howMuchMessagesToConsume, false);
  }

  public StockEndSubscriber(Integer howMuchMessagesToConsume, boolean failSilently) {
    this.howMuchMessagesToConsume = new AtomicInteger(howMuchMessagesToConsume);
    this.failSilently = failSilently;
  }

  @Override
  public void onSubscribe(Subscription subscription) {
    this.subscription = subscription;
    subscription.request(1);
  }

  @Override
  public void onNext(StockData item) {

    int toIncrement = howMuchMessagesToConsume.decrementAndGet();
    System.out.println("Got : " + item);

    if (isForbiddenStock(item.getName())) {
      if (!failSilently) {
        throw new IllegalArgumentException("stocks data cannot contain stock: " + item);
      }
      System.out.println("fail silently: " + item);
    } else {
      consumedElements.add(item);
    }

    if (howMuchMessagesToConsume.get() > 0) {
      //what will happen if two threads enter here?
      subscription.request(1);
    }

  }

  private boolean isForbiddenStock(String name) {
    return name.contains("XXX");
  }

  @Override
  public void onError(Throwable t) {
    System.out.println("on error: " + t.getMessage());
    System.out.println("will stop processing");
  }

  @Override
  public void onComplete() {
    System.out.println("Done");
  }
}