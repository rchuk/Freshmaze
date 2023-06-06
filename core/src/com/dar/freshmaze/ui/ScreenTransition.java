package com.dar.freshmaze.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ScreenTransition extends Actor {
    private static final Texture texture = new Texture(Gdx.files.internal("rectangle.png"));
    private static final ShaderProgram shader = createShader();

    private Color color = Color.BLACK;
    private final float speed;
    private float time = 0.0f;

    public ScreenTransition(float speed) {
        this.speed = speed;
    }

    public void setColor(Color newColor) {
        color = newColor;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        time += delta * speed;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        final ShaderProgram oldShader = batch.getShader();
        batch.setShader(shader);

        batch.getShader().setUniformf("time", time);
        batch.setColor(color);
        batch.draw(texture, 0.0f, 0.0f, getStage().getWidth(), getStage().getHeight());
        batch.flush();

        batch.setShader(oldShader);
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
                + "uniform float alpha_threshold;\n" //
                + "uniform float time;\n" //
                + "uniform float duration;\n" //
                + "void main()\n" //
                + "{\n" //
                + "  vec2 uv = v_texCoords;\n" //
                + "  uv = (uv - vec2(0.5)) * 2.0;\n" //
                + "  \n" //
                + "  vec4 tex = texture2D(u_texture, uv);\n" //
                + "  tex.a = step(cos(time), uv.x * uv.y);\n" //
                + "  \n" //
                + "  if(tex.a < alpha_threshold)\n" //
                + "      discard;\n"//
                + "\n" //
                + "  gl_FragColor = v_color * tex;\n"
                + "}";

        final ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled())
            throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());

        shader.bind();
        shader.setUniformf("alpha_threshold", 0.5f);

        return shader;
    }
}
