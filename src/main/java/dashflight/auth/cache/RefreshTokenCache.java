package dashflight.auth.cache;

/**
 * Handles tracking valid refresh tokens. Uses redis database 0.
 */
public class RefreshTokenCache extends BasicRedisCache {

    public RefreshTokenCache() {
        super();
        this.client.select(0);
    }
}
