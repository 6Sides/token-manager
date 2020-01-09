package dashflight.auth.cache;

/**
 * Acts as a blacklist for invalidated JWTs. Uses redis database 1.
 */
public class JwtCache extends BasicRedisCache {

    public JwtCache() {
        super();
        this.client.select(1);
    }
}
