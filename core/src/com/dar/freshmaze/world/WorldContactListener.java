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
        if (fixA.getUserData() != null) {
            // System.out.println("A: " + fixA.getUserData().getClass());
            if(fixA.getUserData().getClass() == Bob.class) {
                ((Bob) fixA.getUserData()).processContact((EnemyOld) fixB.getUserData());
            }
        }
        // if (fixB.getUserData() != null)
        //    System.out.println("B: " + fixB.getUserData().getClass());

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