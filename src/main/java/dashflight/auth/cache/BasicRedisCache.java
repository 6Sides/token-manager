package dashflight.auth.cache;

import dashflight.auth.TokenManagerConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Handles interfacing with Redis cache. All methods in this class
 * interface with database 0 by default.
 */
public class BasicRedisCache {

    private static String host;

    private static int port;

    static {
        TokenManagerConfiguration config = TokenManagerConfiguration.getInstance();

        host = config.getREDIS_HOST();
        port = config.getREDIS_PORT();
    }

    /**
     * Redis connection pool
     */
    private static JedisPool pool;

    protected Jedis client;

    public BasicRedisCache() {
        if (pool == null) {
            pool = new JedisPool(host, port);
        }

        this.client = pool.getResource();
    }

    /**
     * Sets a key, value pair in the cache
     */
    public boolean set(String key, String value) {
        return this.client.set(key, value).equals("OK");
    }

    /**
     * Sets a key, value pair in the cache with a specified expiry length (in seconds).
     */
    public boolean setWithExpiry(String key, int seconds, String value) {
        return this.client.setex(key, seconds, value).equals("OK");
    }

    /**
     * Checks if a key exists in the cache
     */
    public boolean has(String key) {
        return this.client.exists(key);
    }

    /**
     * Query for a value with a key
     */
    public String get(String key) {
        return this.client.get(key);
    }

    /**
     * Attempts to delete a key from the cache.
     * `this.client.del` returns number of keys removed.
     */
    public boolean del(String key) {
        return this.client.del(key) > 0;
    }

}
