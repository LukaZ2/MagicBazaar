package me.magicbazaar.bazaar;

import lombok.*;

import java.text.NumberFormat;

@Getter
@RequiredArgsConstructor
public class LocalBazaarEntry {
    public enum Type {
        BUY, SELL
    }

    private final String itemId;
    private final String itemName;
    private final int amount;
    private final Type type;
    private final double price;

    private int outbidAmount = 0;

    public void setOutbidAmount(int outbidAmount) {
        this.outbidAmount = outbidAmount;
        totalAmountBeforeTopString = String.format("%,d", totalAmountBeforeTop());
    }

    private int matchedAmount = 0;

    public void setMatchedAmount(int matchedAmount) {
        this.matchedAmount = matchedAmount;
        totalAmountBeforeTopString = String.format("%,d", totalAmountBeforeTop());
    }

    private double topPrice = 0;

    public void setTopPrice(double topPrice) {
        this.topPrice = topPrice;
        topPriceString = NumberFormat.getCurrencyInstance().format(topPrice).substring(1);
    }

    private String totalAmountBeforeTopString;
    private String topPriceString;

    public int totalAmountBeforeTop() {
        return getOutbidAmount() + getMatchedAmount();
    }

    public String getRoundedPercentageFromTop() {
        double raw = (topPrice-price)/price;
        double rounded = (double) Math.round(raw*100) / 100;
        return (type == Type.BUY ? "+" : "") + String.valueOf(rounded*100) + "%";
    }

    public boolean isSimilar(LocalBazaarEntry other) {
        return other.getItemId().equals(this.getItemId())
                && other.getPrice() == this.getPrice()
                && other.getType() == this.getType();
    }

}
