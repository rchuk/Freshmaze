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
    private final float duration;
    private float time = 0.0f;

    private boolean fadeOut;
    private boolean isFrozen = false;

    private final TransitionCallback callback;

    public ScreenTransition(float speed, float duration, boolean fadeOut) {
        this(speed, duration, fadeOut, null);
    }

    public ScreenTransition(float speed, float duration, boolean fadeOut, TransitionCallback callback) {
        this.speed = speed;
        this.duration = duration;
        this.fadeOut = fadeOut;
        this.callback = callback;

        shader.bind();
        shader.setUniformf("fade_mult", fadeOut ? -1.0f : 1.0f);
    }

    public void setColor(Color newColor) {
        color = newColor;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void setIsFrozen(boolean newIsFrozen) {
        isFrozen = newIsFrozen;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (isFrozen)
            return;

        if (time >= duration) {
            if (fadeOut)
                remove();

            if (callback != null)
                callback.onComplete();

            return;
        }

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
                + "uniform float time;\n" //
                + "uniform float duration;\n" //
                + "uniform float fade_mult;\n" //
                + "void main()\n" //
                + "{\n" //
                + "  vec2 uv = v_texCoords;\n" //
                + "  uv = (uv - vec2(0.5)) * 2.0;\n" //
                + "  \n" //
                + "  vec4 tex = texture2D(u_texture, uv);\n" //
                + "  tex.a = step(fade_mult * cos(time), uv.x * uv.y);\n" //
                + "\n" //
                + "  gl_FragColor = v_color * tex;\n"
                + "}";

        final ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled())
            throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());

        return shader;
    }

    public interface TransitionCallback {
        void onComplete();
    }
}
