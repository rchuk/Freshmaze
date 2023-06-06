package com.dar.freshmaze.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Modified stage, that renders object using OpenGL depth test
 */
public class DepthSortedStage extends Stage {
    private Vector2 verticalViewBounds;

    public DepthSortedStage() {
        super();
        init();
    }
    /** Creates a stage with the specified viewport. The stage will use its own {@link Batch} which will be disposed when the stage
     * is disposed. */
    public DepthSortedStage(Viewport viewport) {
        super(viewport);
        init();
    }

    /** Creates a stage with the specified viewport and batch. This can be used to specify an existing batch or to customize which
     * batch implementation is used.
     * @param batch Will not be disposed if {@link #dispose()} is called, handle disposal yourself. */
    public DepthSortedStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
        init();
    }

    private void init() {
        getBatch().setShader(createShader());
    }

    public Vector2 getVerticalViewBounds() {
        return verticalViewBounds;
    }

    public void setVerticalViewBounds(Vector2 newVerticalViewBounds) {
        verticalViewBounds = newVerticalViewBounds;
    }

    public void shaderSetVerticalViewBounds() {
        final Vector2 bounds = getVerticalViewBounds();
        getBatch().getShader().bind();
        getBatch().getShader().setUniform2fv("bounds_vert", new float[] { bounds.x, bounds.y }, 0, 2);
    }

    @Override
    public void draw () {
        Camera camera = getViewport().getCamera();
        camera.update();

        if (!getRoot().isVisible()) return;

        Batch batch = getBatch();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthMask(true);
        Gdx.gl.glDepthFunc(GL20.GL_LESS);

        getRoot().draw(batch, 1);
        batch.end();

        // if (debug) drawDebug();
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
                + "uniform float alpha_threshold;\n"
                + "void main()\n" //
                + "{\n" //
                + "  vec4 tex = texture2D(u_texture, v_texCoords);\n" //
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
}
