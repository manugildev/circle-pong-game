package configuration;

/**
 * Created by ManuGil on 18/01/15.
 */
public final class Configuration {

    //GAME NAME
    public static String gameName = "Circle Pong";

    //IAP
    public static final String PRODUCT_ID = "removeads";
    public static final String ENCODED_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlPItVJ488h5WlD+Re93z0ABzOW4eKyT7jOOZR8n+LXDX9L9CYABLAzEht8W49FdQxr6c3Xdxpy52VIN1HMriOIYw6n/k7Iay5BQJeTzR90z5cT8nVJE9NHyR5SJJxY+N/O27oiIz/5mK9uSQXQAro9Cdu7lXMGU/Glq6Sn5jXwgbbTCqwFtG9NCfHMwFPgXY5FbkMP9cZzQOsuFhi5t0ALRv96LQjI+xfX64vd2NgwUVwwPyj0cE80sF1sHFgUeRXRalBQ6iRzbQQUEdA0zLueKzZBCjQSnet0JtLaytJ6M7PVtlZvlNfAXsExgz2knRfCaxor5rAA/0vB8FvHRacQIDAQAB";

    //TAJOY
    public static final String TAPJOY_ID = "kXtDjszHS6KLWJtdak-8xQECv6yj7FIJjGuNG6MiMXZJTUZ212seijW7H9BD";

    //ADMOB IDS
    public static final String AD_UNIT_ID_BANNER = "ca-app-pub-6147578034437241/9683740610";
    public static final String AD_UNIT_ID_INTERSTITIAL = "ca-app-pub-6147578034437241/2160473811";
    public static final float AD_FREQUENCY = 0.5f; //Number between 0 and 1

    //LEADERBOARDS
    public static final String LEADERBOARD_HIGHSCORE = "CgkIiOzr5vYaEAIQAA";
    public static final String LEADERBOARD_GAMESPLAYED = "CgkIiOzr5vYaEAIQAQ";

    //ACHIEVEMENTS
    public static final String SCORE_10 = "CgkIiOzr5vYaEAIQAg";
    public static final String SCORE_25 = "CgkIiOzr5vYaEAIQAw";
    public static final String SCORE_50 = "CgkIiOzr5vYaEAIQBA";
    public static final String SCORE_75 = "CgkIiOzr5vYaEAIQBQ";
    public static final String SCORE_100 = "CgkIiOzr5vYaEAIQBg";

    public static final String GAMESPLAYED_10 = "CgkIiOzr5vYaEAIQCw";
    public static final String GAMESPLAYED_25 = "CgkIiOzr5vYaEAIQDA";
    public static final String GAMESPLAYED_50 = "CgkIiOzr5vYaEAIQDQ";
    public static final String GAMESPLAYED_75 = "CgkIiOzr5vYaEAIQDg";
    public static final String GAMESPLAYED_100 = "CgkIiOzr5vYaEAIQDw";

    //BALL HAS TAIL
    public static boolean tail = true;

    //VELOCITY ADJUSTMENT
    public static float VELOCITY_OVER_0 = 1.75f;
    public static float VELOCITY_OVER_20 = 1.85f;
    public static float VELOCITY_OVER_50 = 1.95f;
    public static float VELOCITY_OVER_75 = 2.0f;
    public static float VELOCITY_OVER_100 = 2.1f;
}
