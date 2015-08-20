package pacman;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class Doce {
    static final int TIPO_PEQUENO = 1;
    static final int TIPO_GRANDE = 2;
    
    protected int x;
    protected int y;
    protected int tipo;
    protected boolean comido;
    protected TiledMapTileLayer layer;

    public Doce(int x, int y, int tipo, boolean comido, TiledMapTileLayer layer) {
        this.x = x;
        this.y = y;
        this.tipo = tipo;
        this.comido = comido;
        this.layer = layer;
    }
    
    public boolean isDoceGrande(){
        return this.tipo == TIPO_GRANDE;
    }
}
