/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gamestudio24.martianrun.actors;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.gamestudio24.martianrun.utils.AssetsManager;
import com.gamestudio24.martianrun.utils.Constants;

/**
 *
 * @author m
 */
public class Gholi extends Human {

    public Gholi(int index) {
        super(index);
    }

    @Override
    public void init() {
        TextureAtlas textureAtlas = new TextureAtlas("gholi.atlas");
        constantTexture = textureAtlas.findRegion("gholi1");
        idleAnimation = AssetsManager.createAnimation(textureAtlas, new String[]{"gholi1", "gholi2", "gholi3", "gholi4"});
        humanHeight = Constants.APP_HEIGHT / 3;
        humanY = Constants.APP_HEIGHT / 5;
    }

}
