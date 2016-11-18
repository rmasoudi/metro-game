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
package com.gamestudio24.martianrun.actors.menu;

import com.badlogic.gdx.math.Rectangle;
import com.gamestudio24.martianrun.enums.GameState;
import com.gamestudio24.martianrun.utils.Constants;
import com.gamestudio24.martianrun.utils.GameManager;

public class MoveButton extends GameButton {

    public interface MoveButtonListener {

        public void onMove();

        public void onStop();
    }

    private MoveButtonListener listener;

    public MoveButton(Rectangle bounds, MoveButtonListener listener) {
        super(bounds);
        this.listener = listener;
    }

    @Override
    protected String getRegionName() {
        return Constants.PLAY_REGION_NAME;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (GameManager.getInstance().getGameState() == GameState.OVER) {
            remove();
        }
    }

    @Override
    public void touched() {
        if (GameManager.getInstance().getGameState() == GameState.RUNNING) {
            listener.onMove();
        }
    }

    @Override
    public void unTouched() {
        if (GameManager.getInstance().getGameState() == GameState.RUNNING) {
            listener.onStop();
        }
    }
}
