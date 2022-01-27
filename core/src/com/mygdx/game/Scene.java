package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.LinkedList;
import java.util.ListIterator;

public class Scene implements Screen {

    //scene
    private Camera camera;
    private Viewport viewport;
    //graphics
    private SpriteBatch batch;
    private TextureAtlas textureAtlas;

    private TextureRegion[] background;

    private TextureRegion playerShipTextureRegion, playerShieldTextureRegion,
            enemyShipTextureRegion, enemyShieldTextureRegion,
            playerLaserTextureRegion, enemyLaserTextureRegion;


    //timing
//    private int backgroundsOffset;
    private float[] backgroundsOffset = {0,0,0,0};
    private float backgroundsMaxScrollingSpeed;

    //world parameters
    private final int WORLD_WIDTH = 72;
    private final int WORLD_HEIGHT = 128;
    private final float TOUCH_MOVEMENT_THRESHOLD = 0.5f;

    //game objects
    private Ship playerShip;
    private Ship enemyShip;
    private LinkedList<Laser> playerLaserList;
    private LinkedList<Laser> enemyLaserList;

    Scene() {

        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        //set up the texture atlas
        textureAtlas = new TextureAtlas("images.atlas");

        background = new TextureRegion[4];

        background[0] = textureAtlas.findRegion("Starscape00");
        background[1] = textureAtlas.findRegion("Starscape01");
        background[2] = textureAtlas.findRegion("Starscape02");
        background[3] = textureAtlas.findRegion("Starscape03");

        backgroundsMaxScrollingSpeed = (float) (WORLD_HEIGHT) / 4;

        //initialize texture regions
        playerShipTextureRegion = textureAtlas.findRegion("playerShip1_red");
        enemyShipTextureRegion = textureAtlas.findRegion("enemyBlack4");
        playerShieldTextureRegion = textureAtlas.findRegion("shield2");
        enemyShieldTextureRegion = textureAtlas.findRegion("shield1");
        enemyShieldTextureRegion.flip(false,true);

        playerLaserTextureRegion = textureAtlas.findRegion("laserBlue03");
        enemyLaserTextureRegion = textureAtlas.findRegion("laserRed03");


        //set up game objects
        playerShip = new PlayerShip(WORLD_WIDTH/2,WORLD_HEIGHT/4,10,10,36,3, 0.4f, 4, 45, 0.5f,  playerShipTextureRegion, playerShieldTextureRegion, playerLaserTextureRegion);
        enemyShip = new EnemyShip(WORLD_WIDTH/2,WORLD_HEIGHT*3/4,10,10,2,1, 0.3f, 5, 50, 0.8f,  enemyShipTextureRegion, enemyShieldTextureRegion, enemyLaserTextureRegion);


        batch = new SpriteBatch();

        playerLaserList = new LinkedList<>();
        enemyLaserList = new LinkedList<>();

    }

    @Override
    public void render(float deltaTime) {
        batch.begin();

        detectInput(deltaTime);

        playerShip.update(deltaTime);
        enemyShip.update(deltaTime);

        //scrolling background
        renderBackground(deltaTime);

        //enemy ships
        enemyShip.draw(batch);

        //player ships
        playerShip.draw(batch);

        //lasers
        renderLasers(deltaTime);

        //detect collisions between lasers and ships
        detectCollisions();

        //explosion
        renderExplosions(deltaTime);

        batch.end();
    }

    private void detectInput(float deltaTime) {
        //keyboard input


        //strategy : determine the max distance the ship can move
        //check each key that matters and move accordingly

        float leftLimit, rightLimit, upLimit, downLimit;
        leftLimit = -playerShip.boundingBox.x;
        downLimit = -playerShip.boundingBox.y;
        rightLimit = WORLD_WIDTH - playerShip.boundingBox.x - playerShip.boundingBox.width;
        upLimit = WORLD_HEIGHT/2 - playerShip.boundingBox.y - playerShip.boundingBox.height;

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && rightLimit > 0) {
            float xChange = playerShip.movementSpeed*deltaTime;
            xChange = Math.min(xChange, rightLimit);
            playerShip.translate(xChange, 0f);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP) && upLimit > 0) {
            float yChange = playerShip.movementSpeed*deltaTime;
            yChange = Math.min(yChange, upLimit);
            playerShip.translate(0f, yChange);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && leftLimit < 0) {
            float xChange = playerShip.movementSpeed*deltaTime;
            xChange = Math.max(-xChange, leftLimit);
            playerShip.translate(xChange, 0f);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) && downLimit < 0) {
            float yChange = playerShip.movementSpeed*deltaTime;
            yChange = Math.max(-yChange, downLimit);
            playerShip.translate(0f, yChange);
        }

        //touch input (also mouse)
        if(Gdx.input.isTouched()) {
            //get the screen position of the touch
            float xTouchPixels = Gdx.input.getX();
            float yTouchPixels = Gdx.input.getY();


            //convert to world position
            Vector2 touchePoint = new Vector2(xTouchPixels, yTouchPixels);
            touchePoint = viewport.unproject(touchePoint);


            //calculate the x and y differences
            Vector2 playerShipCentre = new Vector2(playerShip.boundingBox.x + playerShip.boundingBox.width/2, playerShip.boundingBox.y + playerShip.boundingBox.height/2);
            float touchDistance = touchePoint.dst(playerShipCentre);

            if(touchDistance > TOUCH_MOVEMENT_THRESHOLD) {
                float xTouchDifference = touchePoint.x - playerShipCentre.x;
                float yTouchDifference = touchePoint.y - playerShipCentre.y;

                //scale to the maximum speed of the ship
                float xMove = xTouchDifference / touchDistance * playerShip.movementSpeed *deltaTime;
                float yMove = yTouchDifference / touchDistance * playerShip.movementSpeed *deltaTime;

                if(xMove > 0) xMove = Math.min(xMove, rightLimit);
                else xMove = Math.max(xMove, leftLimit);

                if(yMove > 0) yMove = Math.min(yMove, upLimit);
                else yMove = Math.max(yMove, downLimit);

                playerShip.translate(xMove, yMove);

            }
        }

    }

    private void detectCollisions() {
        //for each player laser, check whether it intersect an enemy ship
        ListIterator<Laser> iterator = playerLaserList.listIterator();
        while(iterator.hasNext()) {
            Laser laser = iterator.next();
            if(enemyShip.intersects(laser.boundingBox)) {
                //contact with enemy ship
                enemyShip.hit(laser);
                iterator.remove();
            }
        }
        //for each enemy laser, check whether it intersect the player ship
        iterator = enemyLaserList.listIterator();
        while(iterator.hasNext()) {
            Laser laser = iterator.next();
            if (playerShip.intersects(laser.boundingBox)) {
                //contact with player ship
                playerShip.hit(laser);
                iterator.remove();
            }
        }
    }

    private void renderExplosions(float deltaTime) {

    }

    private void renderLasers(float deltaTime) {
        //create new lasers
        //player lasers
        if(playerShip.canFireLaser()) {
            Laser[] lasers = playerShip.fireLasers();
            for (Laser laser: lasers) {
                playerLaserList.add(laser);
            }
        }
        //enemy lasers
        if(enemyShip.canFireLaser()) {
            Laser[] lasers = enemyShip.fireLasers();
            for (Laser laser: lasers) {
                enemyLaserList.add(laser);
            }
        }

        //draw lasers
        //remove old lasers
        ListIterator<Laser> iterator = playerLaserList.listIterator();
        while(iterator.hasNext()) {
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y += laser.movementSpeed*deltaTime;
            if(laser.boundingBox.y > WORLD_HEIGHT) {
                iterator.remove();
            }
        }
        iterator = enemyLaserList.listIterator();
        while(iterator.hasNext()) {
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y -= laser.movementSpeed*deltaTime;
            if(laser.boundingBox.y + laser.boundingBox.height < 0) {
                iterator.remove();
            }
        }
    }

    private void renderBackground(float deltaTime) {

        backgroundsOffset[0] += deltaTime * backgroundsMaxScrollingSpeed / 8;
        backgroundsOffset[1] += deltaTime * backgroundsMaxScrollingSpeed / 4;
        backgroundsOffset[2] += deltaTime * backgroundsMaxScrollingSpeed / 2;
        backgroundsOffset[3] += deltaTime * backgroundsMaxScrollingSpeed;

        for(int layer = 0; layer < backgroundsOffset.length; layer++) {

            if(backgroundsOffset[layer] > WORLD_HEIGHT) {
                backgroundsOffset[layer] = 0;
            }

            batch.draw(background[layer],0,-backgroundsOffset[layer], WORLD_WIDTH, WORLD_HEIGHT);
            batch.draw(background[layer],0,-backgroundsOffset[layer]+WORLD_HEIGHT, WORLD_WIDTH, WORLD_HEIGHT);

        }

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height,true);
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void show() {

    }

    @Override
    public void dispose() {

    }
}
