package streams.reactive.chapter_3;

import streams.reactive.chapter_2.StockData;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Function;

public class StockTransformProcessor<R> extends SubmissionPublisher<R> implements Flow.Processor<StockData, R> {

    private Function<StockData, R> function;
    private Flow.Subscription subscription;

    public StockTransformProcessor(Function<StockData, R> function) {
        super();
        this.function = function;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(StockData item) {
        submit(function.apply(item));
        subscription.request(1);
    }

    @Override
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @Override
    public void onComplete() {
        close();
    }
}  