package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

class PlayerShip extends Ship {

    int lives;


    public PlayerShip(float xCenter, float yCenter,
                      float width, float height,
                      float movementSpeed, int shield,
                      float laserWidth, float laserHeight,
                      float laserMovementSpeed, float timeBetweenShots,
                      TextureRegion shipTextureRegion, TextureRegion shieldTextureRegion,
                      TextureRegion laserTextureRegion) {
        super(xCenter, yCenter, width, height, movementSpeed, shield, laserWidth, laserHeight, laserMovementSpeed, timeBetweenShots, shipTextureRegion, shieldTextureRegion, laserTextureRegion);
        lives = 3;
    }

    @Override
    public Laser[] fireLasers() {
        Laser[] laser = new Laser[2];
        laser[0] = new Laser(laserMovementSpeed,boundingBox.x+ boundingBox.width*0.03f,boundingBox.y+ boundingBox.height*0.42f,laserWidth,laserHeight,laserTextureRegion);
        laser[1] = new Laser(laserMovementSpeed, boundingBox.x+ boundingBox.width*0.97f,boundingBox.y+ boundingBox.height*0.42f,laserWidth,laserHeight,laserTextureRegion);

        timeSinceLastShot = 0;

        return laser;
    }
}
