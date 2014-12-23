package com.greenyetilab.race;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.greenyetilab.utils.FileUtils;
import com.greenyetilab.utils.RefreshHelper;
import com.greenyetilab.utils.UiBuilder;
import com.greenyetilab.utils.anchor.AnchorGroup;

/**
 * The config screen
 */
public class ConfigScreen extends com.greenyetilab.utils.StageScreen {
    private final RaceGame mGame;

    public ConfigScreen(RaceGame game) {
        mGame = game;
        setupUi();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new ConfigScreen(mGame));
            }
        };
    }

    public static class GameInputHandlerSelector extends HorizontalGroup {
        private final Label mLabel;
        private Array<GameInputHandler> mHandlers;
        private int mIndex = 0;

        public GameInputHandlerSelector(Skin skin) {
            space(20);
            mHandlers = GameInputHandlers.getAvailableHandlers();
            addButton(" { ", skin, new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    setIndex(mIndex - 1);
                }
            });

            mLabel = new Label("", skin);
            mLabel.setWidth(150);
            addActor(mLabel);

            addButton(" } ", skin, new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    setIndex(mIndex + 1);
                }
            });

            String inputHandlerName = RaceGame.getPreferences().getString("input", "");
            setIndex(findHandler(inputHandlerName));
        }

        public int findHandler(String name) {
            for (int i = 0; i < mHandlers.size; ++i) {
                if (mHandlers.get(i).toString().equals(name)) {
                    return i;
                }
            }
            return 0;
        }

        private void addButton(String text, Skin skin, ClickListener listener) {
            TextButton button = new TextButton(text, skin);
            button.setWidth(60);
            button.addListener(listener);
            addActor(button);
        }

        private void setIndex(int value) {
            mIndex = value;
            if (mIndex < 0) {
                mIndex = mHandlers.size - 1;
            } else if (mIndex >= mHandlers.size) {
                mIndex = 0;
            }
            String name = mHandlers.get(mIndex).toString();
            mLabel.setText(name);
            Preferences prefs = RaceGame.getPreferences();
            prefs.putString("input", name);
            prefs.flush();
        }
    }

    private void setupUi() {
        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().skin);
        builder.registerActorFactory("GameInputHandlerSelector", new UiBuilder.ActorFactory() {
            @Override
            public Actor createActor(XmlReader.Element element) {
                return new GameInputHandlerSelector(mGame.getAssets().skin);
            }
        });

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/config.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);
        builder.getActor("debugButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGame.pushScreen(new DebugScreen(mGame));
            }
        });
        builder.getActor("backButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGame.popScreen();
            }
        });
    }
}