package streams.reactive.chapter_2;

import com.google.common.base.Objects;

public class StockData {
  private final String name;
  private final Float price;

  public StockData(String name, Float price) {
    this.name = name;
    this.price = price;
  }

  public String getName() {
    return name;
  }

  public Float getPrice() {
    return price;
  }

  @Override
  public String toString() {
    return "StockData{" +
        "name='" + name + '\'' +
        ", price=" + price +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    StockData stockData = (StockData) o;
    return Objects.equal(name, stockData.name) &&
        Objects.equal(price, stockData.price);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name, price);
  }
}
