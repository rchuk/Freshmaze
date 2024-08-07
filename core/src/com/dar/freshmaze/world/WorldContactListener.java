package com.dar.freshmaze.world;

import com.badlogic.gdx.physics.box2d.*;
import com.dar.freshmaze.entities.Bob;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        if(fixA.isSensor() == fixB.isSensor())
            return;

        final Object first = fixA.getBody().getUserData();
        final Object second = fixB.getBody().getUserData();
        if (first != null && second != null) {
            if (first instanceof Bob)
                ((Bob)first).addObjectInRadius(second);
            else if (second instanceof Bob)
                ((Bob)second).addObjectInRadius(first);
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        if(fixA.isSensor() == fixB.isSensor())
            return;

        final Object first = fixA.getBody().getUserData();
        final Object second = fixB.getBody().getUserData();
        if (first != null && second != null) {
            if (first instanceof Bob)
                ((Bob)first).removeObjectInRadius(second);
            else if (second instanceof Bob)
                ((Bob)second).removeObjectInRadius(first);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}