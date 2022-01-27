package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

class EnemyShip extends Ship {

    public EnemyShip(float xCenter, float yCenter,
                      float width, float height,
                      float movementSpeed, int shield,
                      float laserWidth, float laserHeight,
                      float laserMovementSpeed, float timeBetweenShots,
                      TextureRegion shipTextureRegion, TextureRegion shieldTextureRegion,
                      TextureRegion laserTextureRegion) {
        super(xCenter, yCenter, width, height, movementSpeed, shield, laserWidth, laserHeight, laserMovementSpeed, timeBetweenShots, shipTextureRegion, shieldTextureRegion, laserTextureRegion);
    }

    @Override
    public Laser[] fireLasers() {
        Laser[] laser = new Laser[2];
        laser[0] = new Laser(laserMovementSpeed,boundingBox.x+boundingBox.width*0.15f,boundingBox.y-laserHeight,laserWidth,laserHeight,laserTextureRegion);
        laser[1] = new Laser(laserMovementSpeed,boundingBox.x+boundingBox.width*0.85f,boundingBox.y-laserHeight,laserWidth,laserHeight,laserTextureRegion);

        timeSinceLastShot = 0;

        return laser;
    }

    @Override
    public void draw(Batch batch) {
        batch.draw(shipTextureRegion, boundingBox.x,boundingBox.y,boundingBox.width,boundingBox.height);
        if(shield>0) {
            batch.draw(shieldTextureRegion, boundingBox.x-boundingBox.width*0.05f, boundingBox.y-boundingBox.height*0.05f, boundingBox.width+boundingBox.width*0.1f, boundingBox.height);
        }
    }
}
