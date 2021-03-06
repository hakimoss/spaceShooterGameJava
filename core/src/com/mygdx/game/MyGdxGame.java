package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Random;

public class MyGdxGame extends Game {

	Scene scene;

	public static Random random = new Random();

	@Override
	public void create() {
		scene = new Scene();
		setScreen(scene);
	}

	@Override
	public void dispose() {
		scene.dispose();
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		scene.resize(width, height);
	}
}
