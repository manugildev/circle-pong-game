package com.madtriangle.circlepong;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;
import com.madtriangle.circlepong.util.IabHelper;
import com.madtriangle.circlepong.util.IabResult;
import com.madtriangle.circlepong.util.Inventory;
import com.madtriangle.circlepong.util.Purchase;
import com.pushbots.push.Pushbots;
import com.tapjoy.Tapjoy;

import circlepong.ActionResolver;
import circlepong.CirclePongGame;
import configuration.Configuration;
import helpers.AssetLoader;


public class AndroidLauncher extends AndroidApplication implements
        ActionResolver, GameHelper.GameHelperListener {

    // Google Plus Button RequestCode
    private static final int PLUS_ONE_REQUEST_CODE = 0;

    private static final String AD_UNIT_ID_BANNER = Configuration.AD_UNIT_ID_BANNER;
    private static final String AD_UNIT_ID_INTERSTITIAL = Configuration.AD_UNIT_ID_INTERSTITIAL;
    private final static int REQUEST_CODE_UNUSED = 9002;
    private static String GOOGLE_PLAY_URL = "https://play.google.com/store/apps/details?id=";
    protected AdView adView;
    protected View gameView;
    AdView admobView;
    private InterstitialAd interstitialAd;
    private GameHelper _gameHelper;

    //ANALYTICS
    private GoogleAnalytics analytics;
    private Tracker tracker;

    //IAP
    private static final String TAG = "IAP";
    boolean mIsPremium = false;
    static final String SKU_PREMIUM = Configuration.PRODUCT_ID;
    static final int RC_REQUEST = 10001;
    IabHelper mHelper;
    private boolean removeAdsVersion = false;
    SharedPreferences prefs;

    //RATE
    FiveStarsDialog fiveStarsDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //IAP
        prefs = getSharedPreferences(Configuration.gameName, Context.MODE_PRIVATE);
        //loadIAPstuff();

        //SDKs
        //TAPJOY

        //Tapjoy.setDebugEnabled(true); //Do not set this for apps released to a store!
        //APPSLFYER
        Tapjoy.connect(getApplicationContext(), Configuration.TAPJOY_ID);
        Pushbots.sharedInstance().init(this);
        Pushbots.sharedInstance().debug(true);

        AppsFlyerLib.setAppsFlyerKey("XtLTdscrzyBhwCrMNxDbEC");
        AppsFlyerLib.sendTracking(getApplicationContext());
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker("UA-61248564-2");
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
        tracker.setScreenName("Android Launcher");

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useAccelerometer = false;
        cfg.useCompass = false;
        cfg.useImmersiveMode = true;

        // Do the stuff that initialize() would do for you
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        GOOGLE_PLAY_URL = "https://play.google.com/store/apps/details?id=" + getPackageName();

        FrameLayout layout = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);

        admobView = createAdView();

        View gameView = createGameView(cfg);
        layout.addView(gameView);
        layout.addView(admobView);
        _gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        //_gameHelper.enableDebugLog(false);

        GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener() {
            @Override
            public void onSignInSucceeded() {
            }

            @Override
            public void onSignInFailed() {
            }
        };


        _gameHelper.setup(gameHelperListener);


        setContentView(layout);
        startAdvertising(admobView);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(AD_UNIT_ID_INTERSTITIAL);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdClosed() {
            }
        });
        showOrLoadInterstital();

    }

    private void loadIAPstuff() {
        String base64EncodedPublicKey = Configuration.ENCODED_PUBLIC_KEY;// CONSTRUCT_YOUR_KEY_AND_PLACE_IT_HERE
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(AndroidLauncher.this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set
        // this to false).
        //mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Toast.makeText(AndroidLauncher.this,
                            "Problem setting up in-app billing: " + result,
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null)
                    return;

                // IAP is fully set up. Now, let's get an inventory of stuff we
                // own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
    }

    private AdView createAdView() {
        adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(AD_UNIT_ID_BANNER);
        //adView.setId(1); // this is an arbitrary id, allows for relative
        // positioning in createGameView()
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        params.gravity = Gravity.BOTTOM;

        adView.setLayoutParams(params);
        adView.setVisibility(View.VISIBLE);
        adView.setBackgroundColor(Color.TRANSPARENT);
        return adView;
    }

    private View createGameView(AndroidApplicationConfiguration cfg) {
        gameView = initializeForView(new CirclePongGame(this), cfg);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        gameView.setLayoutParams(params);
        return gameView;
    }

    private void startAdvertising(AdView adView) {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null)
            adView.resume();

    }

    @Override
    public void onPause() {
        if (adView != null)
            adView.pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (adView != null)
            adView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        _gameHelper.onStart(this);
        Tapjoy.onActivityStart(this);
    }

    @Override
    protected void onStop() {
        Tapjoy.onActivityStop(this);
        super.onStop();
        _gameHelper.onStop();
    }

    @Override
    public void showOrLoadInterstital() {
        try {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();

                    } else {
                        AdRequest interstitialRequest = new AdRequest.Builder()
                                .build();
                        interstitialAd.loadAd(interstitialRequest);

                    }
                }
            });
        } catch (Exception e) {
        }
    }

    @Override
    public void signIn() {
        try {
            runOnUiThread(new Runnable() {
                // @Override
                public void run() {
                    _gameHelper.beginUserInitiatedSignIn();
                }
            });
        } catch (Exception e) {
            Gdx.app.log("MainActivity", "Log in failed: " + e.getMessage()
                    + ".");
        }
    }

    @Override
    public void signOut() {
        try {
            runOnUiThread(new Runnable() {
                // @Override
                public void run() {
                    _gameHelper.signOut();
                }
            });
        } catch (Exception e) {
            Gdx.app.log("MainActivity", "Log out failed: " + e.getMessage()
                    + ".");
        }
    }

    @Override
    public void rateGame() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_URL)));
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("click")
                .setLabel("rate")
                .build());
    }

    @Override
    public boolean isSignedIn() {
        return _gameHelper.isSignedIn();
    }

    @Override
    public void onSignInFailed() {
    }

    @Override
    public void onSignInSucceeded() {
    }

    @Override
    public void submitScore(long score) {
        if (isSignedIn() == true) {
            Games.Leaderboards.submitScore(_gameHelper.getApiClient(),
                    Configuration.LEADERBOARD_HIGHSCORE, score);
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("PLAYSERVICES")
                    .setAction("submit")
                    .setLabel("score")
                    .build());
        } else {
            // signIn();
        }
    }

    @Override
    public void submitGamesPlayed(long score) {
        if (isSignedIn() == true) {
            Games.Leaderboards.submitScore(_gameHelper.getApiClient(),
                    Configuration.LEADERBOARD_GAMESPLAYED, score);
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("PLAYSERVICES")
                    .setAction("submit")
                    .setLabel("gamesplayed")
                    .build());
        } else {
            // signIn();
        }
    }

    @Override
    public void showScores() {
        if (isSignedIn() == true) {
            startActivityForResult(
                    Games.Leaderboards.getAllLeaderboardsIntent(_gameHelper
                            .getApiClient()),
                    REQUEST_CODE_UNUSED);
            // Games.Leaderboards.getLeaderboardIntent( _gameHelper.getApiClient(),
            // C.LEADERBOARD_ID),REQUEST_CODE_UNUSED)
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("UX")
                    .setAction("click")
                    .setLabel("leaderboards")
                    .build());
        } else {
            signIn();
        }
    }

    @Override
    public void showAchievement() {
        if (isSignedIn() == true) {
            startActivityForResult(
                    Games.Achievements.getAchievementsIntent(_gameHelper
                            .getApiClient()),
                    REQUEST_CODE_UNUSED);
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("UX")
                    .setAction("click")
                    .setLabel("achievements")
                    .build());
        } else {
            signIn();
        }
    }

    @Override
    public boolean shareGame(String msg) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg + GOOGLE_PLAY_URL);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share..."));
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("click")
                .setLabel("share")
                .build());
        return true;

    }

    @Override
    public void unlockAchievementGPGS(String string) {
        if (isSignedIn()) {
            Games.Achievements.unlock(_gameHelper.getApiClient(), string);
        }
    }

    @Override
    public void viewAd(boolean view) {
        if (view) {
            try {
                runOnUiThread(new Runnable() {
                    public void run() {
                        admobView.setVisibility(View.VISIBLE);
                    }
                });
            } catch (Exception e) {
            }

        } else {
            try {
                runOnUiThread(new Runnable() {
                    public void run() {
                        admobView.setVisibility(View.INVISIBLE);
                    }
                });
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void startButtonClicked() {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("click")
                .setLabel("play")
                .build());
    }

    @Override
    public void dollarButton() {

    }

    @Override
    public void iapClick() {
        //String payload = returnDeveloperPayload();
        if (false/*mHelper.mSetupDone*/) {
            //mHelper.launchPurchaseFlow(AndroidLauncher.this, SKU_PREMIUM, RC_REQUEST, mPurchaseFinishedListener,payload);
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("UX")
                    .setAction("click")
                    .setLabel("iap")
                    .build());
        } else {
            //Toast.makeText(AndroidLauncher.this, "Setup is not done yet.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void rateDialog() {
        runOnUiThread(new Runnable() {
            public void run() {
                fiveStarsDialog = new FiveStarsDialog(AndroidLauncher.this, "madtriangl3@gmail.com", tracker);
                fiveStarsDialog.setTitle("Rate this game")
                        .setRateText("How much do you love our game?")
                        .setForceMode(false).showAfter(15);
            }
        });
    }

    //IAP STUFF
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(final IabResult result,
                                             final Inventory inventory) {
            //Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) {
                return;
            }

            // Is it a failure?
            if (result.isFailure()) {
                Toast.makeText(AndroidLauncher.this,
                        "This has just failed." + result,
                        Toast.LENGTH_LONG).show();
                return;
            }

            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
            removeAdsVersion = premiumPurchase != null
                    && verifyDeveloperPayload(premiumPurchase);
            saveData();
            Log.d(TAG, "User is " + (removeAdsVersion ? "PREMIUM" : "NOT PREMIUM"));
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
            _gameHelper.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }


    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
    }

    String returnDeveloperPayload() {
        String payload = "CirclePongPayload";
        return payload;
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                return;
            }

            Log.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals(SKU_PREMIUM)) {
                // bought the premium upgrade!
                Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
                alert("Thank you for upgrading to premium!");
                Tapjoy.trackPurchase(SKU_PREMIUM, null);
                mIsPremium = true;
                removeAdsVersion = true;
                saveData();
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("IAP")
                        .setAction("Completed")
                        .setLabel("removeads")
                        .build());
            }
        }
    };


    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d(TAG, "Consumption successful. Provisioning.");
                saveData();
            } else {
                complain("Error while consuming: " + result);
            }
            Log.d(TAG, "End consumption flow.");
        }
    };

    void complain(String message) {
        Log.e(TAG, "**** CirclePong Error: " + message);
        //alert("Error: " + message);
    }

    void alert(String message) {
        //Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Showing alert dialog: " + message);
    }

    void saveData() {
        SharedPreferences.Editor spe = getPreferences(MODE_PRIVATE).edit();
        spe.putBoolean("ads", removeAdsVersion);
        spe.commit();
        AssetLoader.setAds(removeAdsVersion);
        Log.d(TAG, "Saved data: tank = " + String.valueOf(removeAdsVersion));
    }

    void loadData() {
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        removeAdsVersion = sp.getBoolean("ads", false);
        removeAdsVersion = AssetLoader.getAds();
        Log.d(TAG, "Loaded data: tank = " + String.valueOf(removeAdsVersion));
    }


}
