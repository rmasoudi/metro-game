package com.gamestudio24.martianrun;

import android.os.Bundle;
import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.gamestudio24.martianrun.utils.GameEventListener;

public class AndroidLauncher extends AndroidApplication {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(new MartianRun(new GameEventListener() {
            @Override
            public void displayAd() {
                Gdx.app.log(GameEventListener.class.getSimpleName(), "displayAd");
            }

            @Override
            public void hideAd() {
                Gdx.app.log(GameEventListener.class.getSimpleName(), "hideAd");
            }

            @Override
            public void submitScore(int score) {
                Gdx.app.log(GameEventListener.class.getSimpleName(), "submitScore");
            }

            @Override
            public void displayLeaderboard() {
                Gdx.app.log(GameEventListener.class.getSimpleName(), "displayLeaderboard");
            }

            @Override
            public void displayAchievements() {
                Gdx.app.log(GameEventListener.class.getSimpleName(), "displayAchievements");
            }

            @Override
            public void share() {
                Gdx.app.log(GameEventListener.class.getSimpleName(), "share");
            }

            @Override
            public void unlockAchievement(String id) {

            }

            @Override
            public void incrementAchievement(String id, int steps) {

            }

            @Override
            public String getGettingStartedAchievementId() {
                return null;
            }

            @Override
            public String getLikeARoverAchievementId() {
                return null;
            }

            @Override
            public String getSpiritAchievementId() {
                return null;
            }

            @Override
            public String getCuriosityAchievementId() {
                return null;
            }

            @Override
            public String get5kClubAchievementId() {
                return null;
            }

            @Override
            public String get10kClubAchievementId() {
                return null;
            }

            @Override
            public String get25kClubAchievementId() {
                return null;
            }

            @Override
            public String get50kClubAchievementId() {
                return null;
            }

            @Override
            public String get10JumpStreetAchievementId() {
                return null;
            }

            @Override
            public String get100JumpStreetAchievementId() {
                return null;
            }

            @Override
            public String get500JumpStreetAchievementId() {
                return null;
            }

        }), config);
    }
}
