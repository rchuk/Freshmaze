package com.dar.freshmaze.world;

import com.badlogic.gdx.physics.box2d.*;
import com.dar.freshmaze.entities.Bob;
import com.dar.freshmaze.entities.EnemyOld;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        // System.out.println(contact);
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        if(fixA.isSensor() == fixB.isSensor())
            return;

        final Object first = fixA.getBody().getUserData();
        final Object second = fixB.getBody().getUserData();
        if (first != null && second != null) {
            // System.out.println("A: " + first.getClass());
            if (first instanceof Bob)
                ((Bob)first).processContact(second);
            else if (second instanceof Bob)
                ((Bob)second).processContact(first);
        }
        // if (fixB.getUserData() != null)
        //    System.out.println("B: " + second.getClass());

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
}