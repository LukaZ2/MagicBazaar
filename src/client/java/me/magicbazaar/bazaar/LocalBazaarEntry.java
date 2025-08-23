package me.magicbazaar.bazaar;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LocalBazaarEntry {
    public enum Type {
        BUY, SELL
    }

    private String itemId;
    private int amount;
    private Type type;
    private double price;
}
