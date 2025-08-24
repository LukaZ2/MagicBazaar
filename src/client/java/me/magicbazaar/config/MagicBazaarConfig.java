package me.magicbazaar.config;

public class MagicBazaarConfig {

    public long BAZAAR_API_REFRESH_INTERVAL = 5000;

    public synchronized MagicBazaarConfig sync() {
        return this;
    }
}
