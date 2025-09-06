package me.magicbazaar.config;

public class MagicBazaarConfig {

    public long BAZAAR_API_REFRESH_INTERVAL = 8000;
    public boolean OFFER_UPDATE_NOTIFY = true;

    public synchronized MagicBazaarConfig sync() {
        return this;
    }
}
