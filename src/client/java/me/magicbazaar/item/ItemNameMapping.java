package me.magicbazaar.item;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemNameMapping {

    private final String itemId;
    private final String displayName;

    public static class NotFoundException extends Exception {
        public NotFoundException(String message) {
            super(message);
        }
    }
}
