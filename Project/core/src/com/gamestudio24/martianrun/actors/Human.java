/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gamestudio24.martianrun.actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.gamestudio24.martianrun.enums.GameState;
import com.gamestudio24.martianrun.utils.Constants;
import com.gamestudio24.martianrun.utils.GameManager;

/**
 *
 * @author m
 */
public abstract class Human extends Actor {

    private static final float HUMAN_WIDTH = Constants.APP_WIDTH / 8;
    private boolean moving = false;
    private ShapeRenderer shapeRenderer;
    private int humanIndex = 0;
    private float humanX;
    private float stateTime=0f;

    protected float humanHeight;
    protected TextureRegion constantTexture;
    protected Animation idleAnimation;
    protected Animation talkingAnimation;

    protected float humanY;

    public Human(int index) {
        this.humanIndex = index;
        if (index == 4) {
            humanX = Constants.APP_WIDTH - HUMAN_WIDTH - HUMAN_WIDTH / 4;
        } else {
            humanX = Constants.APP_WIDTH + humanIndex * HUMAN_WIDTH - HUMAN_WIDTH / 4;
        }
        shapeRenderer = new ShapeRenderer();
    }

    public abstract void init();

    @Override
    public void act(float delta) {

        if (GameManager.getInstance().getGameState() != GameState.RUNNING) {
            return;
        }
        if (!isMoving()) {
            return;
        }
        if (leftBoundsReached(delta)) {
            remove();
        } else {
            updateXBounds(-delta);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(constantTexture, humanX, humanY, HUMAN_WIDTH, humanHeight);

    }

    /**
     * @return the moving
     */
    public boolean isMoving() {
        return moving;
    }

    /**
     * @param moving the moving to set
     */
    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    private boolean leftBoundsReached(float delta) {
        return (humanX - (delta * Background.BACKGROUND_SPEED)) <= -HUMAN_WIDTH;
    }

    private void updateXBounds(float delta) {
        humanX += delta * Background.BACKGROUND_SPEED;
    }
}
