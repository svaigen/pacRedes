package pacman;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import sun.security.x509.X500Name;

public class PacMan extends ApplicationAdapter {

    SpriteBatch batch;
    Texture pacImg;
    TiledMap tiledMap;
    OrthographicCamera camera;
    TiledMapRenderer tiledMapRenderer;
    Sprite pac;

    @Override
    public void create() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 600, 600);
        camera.update();
        tiledMap = new TmxMapLoader().load("maps/level1.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        pacImg = new Texture(Gdx.files.internal("sprites/pacman.png"));
        pac = new Sprite(pacImg);
        pac.setPosition(w / 2 - pac.getWidth() / 2, (h / 2 - pac.getHeight() / 2) - 48);

        //Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        MapObjects objects = tiledMap.getLayers().get(2).getObjects();

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            pac.translateX(-1.0f);
            if (colidiu(objects)) {
                pac.translateX(1.0f);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            pac.translateX(1.0f);
            if (colidiu(objects)) {
                pac.translateX(-1.0f);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            pac.translateY(1.0f);
            if (colidiu(objects)) {
                pac.translateY(-1.0f);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            pac.translateY(-1.0f);
            if (colidiu(objects)) {
                pac.translateY(1.0f);
            }
        }

//        System.out.println(tiledMap.getLayers().get(1).getObjects().get(0));
        //TiledMapTileLayer collisionObjectLayer = (TiledMapTileLayer) tiledMap.getLayers().get(2);
        batch.begin();
        pac.draw(batch);
        batch.end();

    }

    @Override
    public void dispose() {
        batch.dispose();
        pacImg.dispose();
    }

    private boolean colidiu(MapObjects objects) {
        for (RectangleMapObject rectangleObject : objects.getByType(RectangleMapObject.class)) {
            Rectangle rectangle = rectangleObject.getRectangle();
            if (Intersector.overlaps(rectangle, pac.getBoundingRectangle())) {
                return true;
            }
        }
        return false;
    }
}
