package com.dar.freshmaze.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.TimeUtils;
import com.dar.freshmaze.level.tilemap.rooms.BattleLevelRoom;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.dar.freshmaze.util.TimeUtil;

public class HealthBonus extends Entity {
    private final static ShaderProgram shader = createShader();
    private final BattleLevelRoom room;


    public HealthBonus(World physWorld, BattleLevelRoom room, Vector2 spawnPos) {
        super(physWorld, createSprite(), createBody(physWorld), new Vector2(0.25f, 0.25f), SpriteKind.Isometric, spawnPos);

        this.room = room;
    }

    @Override
    public void draw(Batch batch, float alpha) {
        final ShaderProgram oldShader = batch.getShader();
        batch.setShader(shader);
        shader.setUniformf("time", 2.0f * TimeUtil.timef());

        super.draw(batch, alpha);

        batch.setShader(oldShader);
    }

    private static Sprite createSprite() {
        final Sprite sprite = new Sprite(new Texture(Gdx.files.internal("heart.png")));
        sprite.setSize(0.5f, 0.5f);

        return sprite;
    }

    private static Body createBody(World physWorld) {
        final CircleShape circle = new CircleShape();
        circle.setPosition(new Vector2(0.5f, 0.5f));
        circle.setRadius(0.5f);

        final BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.StaticBody;
        bd.fixedRotation = true;
        bd.gravityScale = 0;

        final FixtureDef fdef = new FixtureDef();
        fdef.shape = circle;

        final Body body = physWorld.createBody(bd);
        body.createFixture(circle, 1);
        body.createFixture(fdef);

        return body;
    }

    private static ShaderProgram createShader() {
        final String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                + "uniform mat4 u_projTrans;\n" //
                + "varying vec4 v_color;\n" //
                + "varying vec2 v_texCoords;\n" //
                + "\n" //
                + "void main()\n" //
                + "{\n" //
                + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                + "   v_color.a = v_color.a * (255.0/254.0);\n" //
                + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                + "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "}\n";
        final String fragmentShader = "#ifdef GL_ES\n" //
                + "#define LOWP lowp\n" //
                + "precision mediump float;\n" //
                + "#else\n" //
                + "#define LOWP \n" //
                + "#endif\n" //
                + "varying LOWP vec4 v_color;\n" //
                + "varying vec2 v_texCoords;\n" //
                + "uniform sampler2D u_texture;\n" //
                + "uniform float height;\n" //
                + "uniform vec2 bounds_vert;\n" //
                + "uniform float alpha_threshold;\n" //
                + "uniform float time;\n" //
                + "void main()\n" //
                + "{\n" //
                + "  vec2 uv = v_texCoords;\n" //
                + "  uv.x = (uv.x - 0.5) * 2.0;\n" //
                + "  uv.x /= sin(time);\n" //
                + "  uv.x = uv.x / 2.0 + 0.5;\n" //
                + "  \n" //
                + "  vec4 tex = texture2D(u_texture, uv);\n" //
                + "  tex.a *= step(uv.x, 1.0) * step(0.0, uv.x);\n" //
                + "  \n" //
                + "  if(tex.a < alpha_threshold)\n" //
                + "      discard;\n"//
                + "\n" //
                + "  gl_FragColor = v_color * tex;\n"
                + "  gl_FragDepth = (height - bounds_vert.x) / bounds_vert.y;\n" //
                + "}";

        final ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled())
            throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());

        shader.bind();
        shader.setUniformf("alpha_threshold", 0.5f);

        return shader;
    }
    //
}
