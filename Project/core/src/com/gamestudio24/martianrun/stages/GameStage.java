/*
 * Copyright (c) 2014. William Mora
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gamestudio24.martianrun.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.gamestudio24.martianrun.actors.*;
import com.gamestudio24.martianrun.actors.menu.*;
import com.gamestudio24.martianrun.enums.Difficulty;
import com.gamestudio24.martianrun.enums.GameState;
import com.gamestudio24.martianrun.listeners.BackgroundMoveListener;
import com.gamestudio24.martianrun.utils.*;

public class GameStage extends Stage implements ContactListener {

    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;

    private World world;
    private Runner runner;

    private final float TIME_STEP = 1 / 300f;
    private float accumulator = 0f;

    private OrthographicCamera camera;

    private SoundButton soundButton;
    private MusicButton musicButton;
    private PauseButton pauseButton;
    private MoveButton moveButton;
    private MokhZaniButton mokhZaniButton;
    private JarZaniButton jarZaniButton;
    private StartButton startButton;
    private AboutButton aboutButton;
    private ShareButton shareButton;
    private AchievementsButton achievementsButton;

    private Score score;
    private float totalTimePassed;
    private boolean tutorialShown;

    private Vector3 touchPoint;
    private Background background;
    private BackgroundMoveListener backgroundMoveListener;
    private float humanProbability = .7f;

    public GameStage() {
        super(new ScalingViewport(Scaling.stretch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT,
                new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)));
        setUpCamera();
        setUpStageBase();
        setUpMainMenu();
        setUpTouchControlAreas();
        setListeners();
        AudioUtils.getInstance().init();
        onGameOver();
    }

    private void setListeners() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new SimpleDirectionGestureDetector(new SimpleDirectionGestureDetector.DirectionListener() {

            @Override
            public void onUp() {
                runner.moveUp();
            }

            @Override
            public void onRight() {
            }

            @Override
            public void onLeft() {

            }

            @Override
            public void onDown() {

            }
        }));
        multiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void setUpStageBase() {
        setUpWorld();
        setUpFixedMenu();
    }

    private void setUpAboutText() {
        Rectangle gameLabelBounds = new Rectangle(0, getCamera().viewportHeight * 5 / 8,
                getCamera().viewportWidth, getCamera().viewportHeight / 4);
        addActor(new AboutLabel(gameLabelBounds));
    }

    /**
     * These menu buttons are always displayed
     */
    private void setUpFixedMenu() {
//        setUpSound();
//        setUpMusic();
        setUpScore();
    }

    private void setUpSound() {
        Rectangle soundButtonBounds = new Rectangle(getCamera().viewportWidth / 64,
                getCamera().viewportHeight * 13 / 20, getCamera().viewportHeight / 10,
                getCamera().viewportHeight / 10);
        soundButton = new SoundButton(soundButtonBounds);
        addActor(soundButton);
    }

    private void setUpMusic() {
        Rectangle musicButtonBounds = new Rectangle(getCamera().viewportWidth / 64,
                getCamera().viewportHeight * 4 / 5, getCamera().viewportHeight / 10,
                getCamera().viewportHeight / 10);
        musicButton = new MusicButton(musicButtonBounds);
        addActor(musicButton);
    }

    private void setUpScore() {
        Rectangle scoreBounds = new Rectangle(getCamera().viewportWidth * 47 / 64,
                getCamera().viewportHeight * 57 / 64, getCamera().viewportWidth / 4,
                getCamera().viewportHeight / 8);
        score = new Score(scoreBounds);
        addActor(score);
    }

    private void setUpPause() {
        Rectangle pauseButtonBounds = new Rectangle(getCamera().viewportWidth * 15 / 16,
                getCamera().viewportHeight * 7 / 8, getCamera().viewportHeight / 15,
                getCamera().viewportHeight / 15);
        pauseButton = new PauseButton(pauseButtonBounds, new GamePauseButtonListener());
        addActor(pauseButton);
    }

    private void setUpMoveButton() {
        Rectangle moveButtonBounds = new Rectangle(getCamera().viewportWidth * 14 / 16,
                getCamera().viewportHeight * 1 / 25, getCamera().viewportHeight * 3 / 16,
                getCamera().viewportHeight / 16);
        moveButton = new MoveButton(moveButtonBounds, new GameMoveButtonListener());
        addActor(moveButton);
    }

    private void setUpMokhZaniButton() {
        Rectangle buttonBounds = new Rectangle((getCamera().viewportWidth * 14 / 16) - (getCamera().viewportHeight * 3 / 16),
                getCamera().viewportHeight * 1 / 25, getCamera().viewportHeight * 3 / 16,
                getCamera().viewportHeight / 16);
        mokhZaniButton = new MokhZaniButton(buttonBounds, new GameMokhZaniButtonListener());
        addActor(mokhZaniButton);
    }

    private void setUpJarZaniButton() {
        Rectangle buttonBounds = new Rectangle((getCamera().viewportWidth * 1 / 32),
                getCamera().viewportHeight * 1 / 25, getCamera().viewportHeight * 3 / 16,
                getCamera().viewportHeight / 16);
        jarZaniButton = new JarZaniButton(buttonBounds, new GameJarZaniButtonListener());
        addActor(jarZaniButton);
    }

    /**
     * These menu buttons are only displayed when the game is over
     */
    private void setUpMainMenu() {
        setUpTransparent();
        setUpStart();
//        setUpAbout();
//        setUpShare();
//        setUpAchievements();
    }

    private void setUpStart() {
        Rectangle startButtonBounds = new Rectangle(getCamera().viewportWidth * 3 / 8,
                getCamera().viewportHeight / 2, getCamera().viewportWidth / 4,
                getCamera().viewportHeight / 8);
        startButton = new StartButton(startButtonBounds, new GameStartButtonListener());
        addActor(startButton);
    }

    private void setUpAbout() {
        Rectangle aboutButtonBounds = new Rectangle(getCamera().viewportWidth * 23 / 25,
                getCamera().viewportHeight * 13 / 20, getCamera().viewportHeight / 10,
                getCamera().viewportHeight / 10);
        aboutButton = new AboutButton(aboutButtonBounds, new GameAboutButtonListener());
        addActor(aboutButton);
    }

    private void setUpShare() {
        Rectangle shareButtonBounds = new Rectangle(getCamera().viewportWidth / 64,
                getCamera().viewportHeight / 2, getCamera().viewportHeight / 10,
                getCamera().viewportHeight / 10);
        shareButton = new ShareButton(shareButtonBounds, new GameShareButtonListener());
        addActor(shareButton);
    }

    private void setUpAchievements() {
        Rectangle achievementsButtonBounds = new Rectangle(getCamera().viewportWidth * 23 / 25,
                getCamera().viewportHeight / 2, getCamera().viewportHeight / 10,
                getCamera().viewportHeight / 10);
        achievementsButton = new AchievementsButton(achievementsButtonBounds,
                new GameAchievementsButtonListener());
        addActor(achievementsButton);
    }

    private void setUpWorld() {
        world = WorldUtils.createWorld();
        world.setContactListener(this);
        setUpBackground();
    }

    private void setUpBackground() {
        setBackgroundListeners();
        background = new Background(backgroundMoveListener);
        addActor(background);
    }

    private void setUpCharacters() {
        setUpRunner();
        setUpPauseLabel();
    }

    private void setUpRunner() {
        if (runner != null) {
            runner.remove();
        }
        runner = new Runner(WorldUtils.createRunner(world));
        addActor(runner);
    }

    private void setUpCamera() {
        camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f);
        camera.update();
    }

    private void setUpTouchControlAreas() {
        touchPoint = new Vector3();
    }

    private void setUpPauseLabel() {
        Rectangle pauseLabelBounds = new Rectangle(0, getCamera().viewportHeight * 7 / 8,
                getCamera().viewportWidth, getCamera().viewportHeight / 4);
        addActor(new PausedLabel(pauseLabelBounds));
    }

    private void setUpTransparent() {
        Rectangle bounds = new Rectangle(getCamera().viewportWidth / 8, getCamera().viewportHeight / 8, getCamera().viewportWidth * 6 / 8,
                getCamera().viewportHeight * 6 / 8);
        addActor(new Transparent(bounds, Constants.TRANSPARENT_ASSET_ID));
    }

    private void setUpTutorial() {
        tutorialShown = true;
        if (tutorialShown) {
            return;
        }
        setUpLeftTutorial();
        setUpRightTutorial();
        tutorialShown = true;
    }

    private void setUpLeftTutorial() {
        float width = getCamera().viewportHeight / 4;
        float x = getCamera().viewportWidth / 4 - width / 2;
        Rectangle leftTutorialBounds = new Rectangle(x, getCamera().viewportHeight * 9 / 20, width,
                width);
        addActor(new Tutorial(leftTutorialBounds, Constants.TUTORIAL_LEFT_REGION_NAME,
                Constants.TUTORIAL_LEFT_TEXT));
    }

    private void setUpRightTutorial() {
        float width = getCamera().viewportHeight / 4;
        float x = getCamera().viewportWidth * 3 / 4 - width / 2;
        Rectangle rightTutorialBounds = new Rectangle(x, getCamera().viewportHeight * 9 / 20, width,
                width);
        addActor(new Tutorial(rightTutorialBounds, Constants.TUTORIAL_RIGHT_REGION_NAME,
                Constants.TUTORIAL_RIGHT_TEXT));
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (GameManager.getInstance().getGameState() == GameState.PAUSED) {
            return;
        }

        if (GameManager.getInstance().getGameState() == GameState.RUNNING) {
            totalTimePassed += delta;
            updateDifficulty();
        }

        // Fixed timestep
        accumulator += delta;

        while (accumulator >= delta) {
            world.step(TIME_STEP, 6, 2);
            accumulator -= TIME_STEP;
        }

        //TODO: Implement interpolation
    }

    private void update(Body body) {
        if (!BodyUtils.bodyInBounds(body)) {
            if (BodyUtils.bodyIsEnemy(body) && !runner.isHit()) {
//                createEnemy();
            }
            world.destroyBody(body);
        }
    }

    private void createEnemy() {
//        Enemy enemy = new Enemy(WorldUtils.createEnemy(world));
//        enemy.getUserData().setLinearVelocity(
//                GameManager.getInstance().getDifficulty().getEnemyLinearVelocity());
//        addActor(enemy);
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {

        // Need to get the actual coordinates
        translateScreenToWorldCoordinates(x, y);

        // If a menu control was touched ignore the rest
        if (menuControlTouched(touchPoint.x, touchPoint.y)) {
            return super.touchDown(x, y, pointer, button);
        }

        if (GameManager.getInstance().getGameState() != GameState.RUNNING) {
            return super.touchDown(x, y, pointer, button);
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        if (GameManager.getInstance().getGameState() != GameState.RUNNING) {
            return super.touchUp(screenX, screenY, pointer, button);
        }
        return super.touchUp(screenX, screenY, pointer, button);
    }

    private boolean menuControlTouched(float x, float y) {
        boolean touched = false;

        switch (GameManager.getInstance().getGameState()) {
            case OVER:
                touched = startButton.getBounds().contains(x, y)
                        || aboutButton.getBounds().contains(x, y);
                break;
            case RUNNING:
            case PAUSED:
                touched = pauseButton.getBounds().contains(x, y);
                break;
        }

        return touched || moveButton.getBounds().contains(x, y);
    }

    /**
     * Helper function to get the actual coordinates in my world
     *
     * @param x
     * @param y
     */
    private void translateScreenToWorldCoordinates(int x, int y) {
        getCamera().unproject(touchPoint.set(x, y, 0));
    }

    @Override
    public void beginContact(Contact contact) {

        Body a = contact.getFixtureA().getBody();
        Body b = contact.getFixtureB().getBody();

        if ((BodyUtils.bodyIsRunner(a) && BodyUtils.bodyIsEnemy(b))
                || (BodyUtils.bodyIsEnemy(a) && BodyUtils.bodyIsRunner(b))) {
//            if (runner.isHit()) {
//                return;
//            }
//            runner.hit();
//            displayAd();
//            GameManager.getInstance().submitScore(score.getScore());
//            onGameOver();
//            GameManager.getInstance().addGamePlayed();
//            GameManager.getInstance().addJumpCount(runner.getJumpCount());
        } else if ((BodyUtils.bodyIsRunner(a) && BodyUtils.bodyIsGround(b))
                || (BodyUtils.bodyIsGround(a) && BodyUtils.bodyIsRunner(b))) {
//            runner.landed();
        }

    }

    private void updateDifficulty() {

        if (GameManager.getInstance().isMaxDifficulty()) {
            return;
        }

        Difficulty currentDifficulty = GameManager.getInstance().getDifficulty();

        if (totalTimePassed > GameManager.getInstance().getDifficulty().getLevel() * 5) {

            int nextDifficulty = currentDifficulty.getLevel() + 1;
            String difficultyName = "DIFFICULTY_" + nextDifficulty;
            GameManager.getInstance().setDifficulty(Difficulty.valueOf(difficultyName));

            runner.onDifficultyChange(GameManager.getInstance().getDifficulty());
            score.setMultiplier(GameManager.getInstance().getDifficulty().getScoreMultiplier());

            displayAd();
        }

    }

    private void displayAd() {
        GameManager.getInstance().displayAd();
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    private class GamePauseButtonListener implements PauseButton.PauseButtonListener {

        @Override
        public void onPause() {
            onGamePaused();
        }

        @Override
        public void onResume() {
            onGameResumed();
        }

    }

    private class GameMoveButtonListener implements MoveButton.MoveButtonListener {

        @Override
        public void onMove() {
            background.setMoving(true);
            runner.setRunning(true);
            moveAllEnemies(true);
        }

        @Override
        public void onStop() {
            background.setMoving(false);
            runner.setRunning(false);
            moveAllEnemies(false);
        }
    }

    private class GameMokhZaniButtonListener implements MokhZaniButton.MokhZaniButtonListener {

        @Override
        public void onMove() {
        }

        @Override
        public void onStop() {
        }
    }

    private class GameJarZaniButtonListener implements JarZaniButton.JarZaniButtonListener {

        @Override
        public void onMove() {
        }

        @Override
        public void onStop() {
        }
    }

    private class GameStartButtonListener implements StartButton.StartButtonListener {

        @Override
        public void onStart() {
            clear();
            setUpStageBase();
            setUpCharacters();
            setUpPause();
            setUpMoveButton();
            setUpMokhZaniButton();
            setUpJarZaniButton();
            setUpTutorial();
            onGameResumed();
        }

    }

    private class GameLeaderboardButtonListener
            implements LeaderboardButton.LeaderboardButtonListener {

        @Override
        public void onLeaderboard() {
            GameManager.getInstance().displayLeaderboard();
        }

    }

    private class GameAboutButtonListener implements AboutButton.AboutButtonListener {

        @Override
        public void onAbout() {
            if (GameManager.getInstance().getGameState() == GameState.OVER) {
                onGameAbout();
            } else {
                clear();
                setUpStageBase();
                onGameOver();
            }
        }

    }

    private class GameShareButtonListener implements ShareButton.ShareButtonListener {

        @Override
        public void onShare() {
            GameManager.getInstance().share();
        }

    }

    private class GameAchievementsButtonListener
            implements AchievementsButton.AchievementsButtonListener {

        @Override
        public void onAchievements() {
            GameManager.getInstance().displayAchievements();
        }

    }

    private void onGamePaused() {
        GameManager.getInstance().setGameState(GameState.PAUSED);
    }

    private void onGameResumed() {
        GameManager.getInstance().setGameState(GameState.RUNNING);
    }

    private void onGameOver() {
        GameManager.getInstance().setGameState(GameState.OVER);
        GameManager.getInstance().resetDifficulty();
        totalTimePassed = 0;
        setUpMainMenu();
    }

    private void onGameAbout() {
        GameManager.getInstance().setGameState(GameState.ABOUT);
        clear();
        setUpStageBase();
        setUpAboutText();
        setUpAbout();
    }

    private void moveAllEnemies(boolean move) {
        Array<Actor> actors = getActors();
        for (Actor actor : actors) {
            if (actor instanceof Human) {
                Human human = (Human) actor;
                human.setMoving(move);
            }
        }
    }

    private void createHumans() {
        for (int i = 0; i < 5; i++) {
//            if (RandomUtils.canCrate(humanProbability)) 
            {
                Human human = new Human(i);
                addActor(human);
            }
        }
    }

    private void setBackgroundListeners() {
        createHumans();
        backgroundMoveListener = new BackgroundMoveListener() {

            @Override
            public void onReset() {
                createHumans();
            }
            @Override
            public void onMove(boolean move) {
                moveAllEnemies(move);
            }
        };
    }
}
