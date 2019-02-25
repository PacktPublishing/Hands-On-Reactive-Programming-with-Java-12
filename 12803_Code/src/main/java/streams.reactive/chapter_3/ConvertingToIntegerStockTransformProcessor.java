package streams.reactive.chapter_3;

import streams.reactive.chapter_2.StockData;

import java.util.function.Function;

public class ConvertingToIntegerStockTransformProcessor extends StockTransformProcessor<String> {
  public static ConvertingToIntegerStockTransformProcessor create() {
    return new ConvertingToIntegerStockTransformProcessor(
        StockData::getName);
  }

  private ConvertingToIntegerStockTransformProcessor(
      Function<StockData, String> function) {
    super(function);
  }

  public static void main(String[] args) {
    ConvertingToIntegerStockTransformProcessor transformer = ConvertingToIntegerStockTransformProcessor.create();

  }
}
