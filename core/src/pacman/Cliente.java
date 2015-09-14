package pacman;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import static pacman.App.tiledMap;

public class Cliente {

    int porta;
    Socket socket;
    PrintStream ps;
    BufferedReader br;
    protected byte estadoJogo;
    protected byte nivel;
    protected int pontos;
    protected int tempo;
    protected String caminhoTiledMap;
    protected String caminhoSprites;
    protected byte indiceParedes;
    protected byte indicePontosDecisao;
    protected byte indiceDoces;
    protected Pacman pacMan;
    protected Fruta fruta;
    protected Ghost ghosts[] = new Ghost[4];
    protected int docesRestantes;
    protected int straitLineDistance[] = new int[4];

    public Cliente() {
        this.porta = 50001;
        try {
            socket = new Socket("127.0.0.1", porta);
            ps = new PrintStream(socket.getOutputStream());
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            System.err.println("Erro ao realizar a conexão com o servidor local pela porta " + porta);
        }
    }

    public String enviaInformacao(String s) {
        String r = "";
        try {
            ps.print(s);
            r = br.readLine();
        } catch (Exception e) {
            System.err.println("Erro ao enviar informacão!");
        } finally {
            return r;
        }
    }

    public void fechaConexao() {
        try {
            String n = this.enviaInformacao("/q");
            System.out.println(n);
            socket.close();
        } catch (Exception e) {
            System.err.println("Erro ao fechar conexão");
        }
    }
    /*
     public static void main(String[] args) {
     Cliente c = new Cliente();
     String n;
     for (int i = 0; i < 3; i++) {
     n = "n"+i;
     n = c.enviaInformacao(n);
     System.out.println(n);
     }
     c.fechaConexao();
     }
     */

    private void initPacMan(String[] dados) throws NumberFormatException {
        pacMan = new Pacman(Float.parseFloat(dados[5]), //velocidade
                Integer.parseInt(dados[6]), //vidas
                Integer.parseInt(dados[7]), //vivo
                Integer.parseInt(dados[8]), //direcao atual
                Integer.parseInt(dados[9]), //direcao pretendida
                Float.parseFloat(dados[10]), //coord. x
                Float.parseFloat(dados[11]),//coord. y
                geraSpritesPacMan(App.sprites, App.NUM_FRAMES_PACMAN));
    }

    private TextureRegion[] geraSpritesPacMan(Texture sprites, int numFrames) {
        TextureRegion frames[] = new TextureRegion[numFrames];
        for (int i = 0; i < numFrames; i++) {
            frames[i] = new TextureRegion(sprites, i * 24, 0, 24, 24);
        }
        return frames;
    }

    private void initGhosts(String[] dados) {
        //indice inicia em 17
        for (int i = 0; i < 4; i++) {
            int deslocamento = i * 8;
            ghosts[i] = new Ghost(Integer.parseInt(dados[17 + deslocamento]), //estado
                    Integer.parseInt(dados[18 + deslocamento]), //durecai
                    Float.parseFloat(dados[19 + deslocamento]), //velocidade
                    Integer.parseInt(dados[20 + deslocamento]),//tempolivre
                    Integer.parseInt(dados[21 + deslocamento]),//tempoinvuln.
                    Integer.parseInt(dados[22 + deslocamento]),//seguepac
                    Integer.parseInt(dados[23 + deslocamento]),//x
                    Integer.parseInt(dados[24 + deslocamento]),//y
                    geraSpritesGhost(i, App.sprites),
                    i);//sprites
        }

    }

    private void initFruta(String[] dados) {
        //indice de dados inicia em 12
        fruta = new Fruta(Integer.parseInt(dados[12]),
                Float.parseFloat(dados[13]),
                Integer.parseInt(dados[14]),
                Integer.parseInt(dados[15]),
                new TextureRegion(App.sprites, 46 * 24, 0, 24, 24));
    }

    private TextureRegion[] geraSpritesGhost(int i, Texture sprites) {
        int offset = 20 + (5 * i); //deslocamento para iniciar os frames dos ghosts em estado normal
        TextureRegion frames[] = new TextureRegion[11];
        for (int j = 0; j < 5; j++) { //5 = frames do ghost no estado normal
            frames[j] = new TextureRegion(sprites, (offset++ * 24), 0, 24, 24);
        }
        int offsetComum = 40;
        for (int j = 5; j < 11; j++) {
            frames[j] = new TextureRegion(sprites, (offsetComum++ * 24), 0, 24, 24);
        }
        return frames;
    }

    private String enviaOperacao(int op, String envio) {
        String resposta = "";
        String operacao;
        try {
            ps.print(envio);
            resposta = br.readLine();
            //System.out.println(resposta);
        } catch (Exception e) {
            System.err.println("Erro ao enviar/receber operacao " + op + "!");
        } finally {
            //operacao = resposta.substring(0, 3);
        }
        return resposta;
    }

    public void opEstabeleceConexao() {
        String envio;
        String resposta = "";
        envio = "001\n\0";//operacao
        resposta = enviaOperacao(1, envio);
        String dados[] = resposta.substring(3).split("#");

        estadoJogo = Byte.parseByte(dados[0]);
        nivel = Byte.parseByte(dados[1]);
        pontos = Integer.parseInt(dados[2]);
        tempo = Integer.parseInt(dados[3]);
        caminhoSprites = dados[4];
        caminhoTiledMap = dados[5];
    }

    void opCarregaNivel(int nivel) {
        String envio = "002" + nivel + "\n\0";
        String resposta = enviaOperacao(2, envio);
        String dados[] = resposta.substring(3).split("#");
        this.nivel = Byte.parseByte(dados[0]);

        caminhoTiledMap = dados[1];
        indiceParedes = Byte.parseByte(dados[2]);
        indicePontosDecisao = Byte.parseByte(dados[3]);
        indiceDoces = Byte.parseByte(dados[4]);
        App.tiledMap = new TmxMapLoader().load(caminhoTiledMap);
        App.tiledMapRenderer = new OrthogonalTiledMapRenderer(App.tiledMap);
        App.paredes = tiledMap.getLayers().get(indiceParedes).getObjects();
        App.pontosDecisao = tiledMap.getLayers().get(indicePontosDecisao).getObjects();
        App.docesTiled = (TiledMapTileLayer) tiledMap.getLayers().get(indiceDoces);
        App.doces = App.realizaMapeamentoDoces(App.docesTiled, App.geraPosicoesDocesGrandes());
        App.sprites = new Texture(Gdx.files.internal(caminhoSprites));
        initPacMan(dados);
        initFruta(dados);
        docesRestantes = Integer.parseInt(dados[16]);
        initGhosts(dados);
        estadoJogo = Byte.parseByte(dados[dados.length - 1]);
    }

    void opIniciaMovimentacao(int teclaPressionada) {
        String envio = "003" + teclaPressionada + "\n\0";
        String resposta = enviaOperacao(3, envio);
        String dados[] = resposta.substring(3).split("#");
        estadoJogo = Byte.parseByte(dados[0]);
        pacMan.direcaoAtual = Integer.parseInt(dados[1]);
    }

    void opAnimaFruta() {
        String envio = "004\n\0";
        String resposta = enviaOperacao(4, envio);
        //    System.out.println("r "+resposta);
        String dados[] = resposta.substring(3).split("#");
        fruta.visivel = Integer.parseInt(dados[0]) == 1;
        fruta.animate();
    }

    void opComeuFruta() {
        String envio = "005\n\0";
        String resposta = enviaOperacao(5, envio);
        String dados[] = resposta.substring(3).split("#");
        pontos = Integer.parseInt(dados[0]);
        fruta.visivel = Integer.parseInt(dados[1]) == 1;
    }

    void opPacNovaDirecaoPretendida(int teclaPressionada) {
        String envio = "006" + teclaPressionada + "\n\0";
        String resposta = enviaOperacao(6, envio);
        String dados[] = resposta.substring(3).split("#");
        pacMan.direcaoPretendida = Integer.parseInt(dados[0]);
    }

    void opPacManAnda(float x, float y, boolean direcaoPretendidaPossivel) {
        int dir = (direcaoPretendidaPossivel ? 1 : 0);
        String envio = "007#" + x + "#" + y + "#" + dir + "\n\0";
        String resposta = enviaOperacao(7, envio);
        String dados[] = resposta.substring(3).split("#");
        pacMan.direcaoAtual = Integer.parseInt(dados[0]);
        pacMan.personagem.setX(Float.parseFloat(dados[1]));
        pacMan.personagem.setY(Float.parseFloat(dados[2]));
    }

    void opPacManColidiuParede() {
        String envio = "008\n\0";
        String resposta = enviaOperacao(8, envio);
        String dados[] = resposta.substring(3).split("#");
        pacMan.direcaoAtual = Integer.parseInt(dados[0]);
    }

    void opAtualizaDirecaoGhost(int id, int melhorDirecao) {
        String envio = "009" + id + "" + melhorDirecao + "\n\0";
        String resposta = enviaOperacao(9, envio);
        String dados[] = resposta.substring(3).split("#");
        ghosts[id].direcao = Integer.parseInt(dados[0]);
    }

    void opStraitLineDistance(int id, int x, int y) {
        String envio = "010#" + id + "#" + x + "#" + y + "\n\0";
        String resposta = enviaOperacao(10, envio);
        String dados[] = resposta.substring(3).split("#");
        straitLineDistance[0] = Integer.parseInt(dados[0]);
        straitLineDistance[1] = Integer.parseInt(dados[1]);
        straitLineDistance[2] = Integer.parseInt(dados[2]);
        straitLineDistance[3] = Integer.parseInt(dados[3]);
    }

    //void opGhostAnda(int id, int x, int y) {
    void opGhostAnda(int id, float x, float y) {
        String envio = "011#" + id + "#" + x + "#" + y + "\n\0";
        String resposta = enviaOperacao(11, envio);
        String dados[] = resposta.substring(3).split("#");
        ghosts[id].personagem.setX(Float.parseFloat(dados[0]));
        ghosts[id].personagem.setY(Float.parseFloat(dados[1]));
    }

    void opRequisitaEstadoGhost(int id, int estado) {
        String envio = "012" + id + estado + "\n\0";
        String resposta = enviaOperacao(12, envio);
        String dados[] = resposta.substring(3).split("#");
        ghosts[id].estado = Integer.parseInt(dados[0]);
    }

    void opRequisitaTempoFicarLivre(int id) {
        String envio = "013" + id + "\n\0";
        String resposta = enviaOperacao(13, envio);
        String dados[] = resposta.substring(3).split("#");
        ghosts[id].tempoParaFicarLivre = Integer.parseInt(dados[0]);
    }

    void opRequisitaTempoParaInvulneravel(int id) {
        String envio = "014" + id + "\n\0";
        String resposta = enviaOperacao(14, envio);
        String dados[] = resposta.substring(3).split("#");
        ghosts[id].tempoParaInvulneravel = Integer.parseInt(dados[0]);
    }

    void opColisaoFantasma(int id) {
        String envio = "015" + id + "\n\0";
        String resposta = enviaOperacao(15, envio);
        String dados[] = resposta.substring(3).split("#");
        this.pontos = Integer.parseInt(dados[0]);
        this.estadoJogo = Byte.parseByte(dados[1]);
        ghosts[id].estado = Integer.parseInt(dados[2]);
    }

    void opPacManMorto() {
        String envio = "016\n\0";
        String resposta = enviaOperacao(16, envio);
        String dados[] = resposta.substring(3).split("#");
        this.pacMan.vidas = Integer.parseInt(dados[0]);
        this.pacMan.vivo = Integer.parseInt(dados[1]) == 1;

    }

    void opReviverPacMan() {
        String envio = "017\n\0";
        String resposta = enviaOperacao(17, envio);
        String dados[] = resposta.substring(3).split("#");
        this.estadoJogo = Byte.parseByte(dados[0]);
    }

    void opIniciaPacMan() {
        String envio = "018\n\0";
        String resposta = enviaOperacao(18, envio);
        String dados[] = resposta.substring(3).split("#");
        this.pacMan.frameAtual = 0;
        this.pacMan.delay = Pacman.DELAY_MAX;
        this.pacMan.batch = new SpriteBatch();
        this.pacMan.personagem.setPosition(Integer.parseInt(dados[0]), Integer.parseInt(dados[1]));
        this.pacMan.vivo = Integer.parseInt(dados[2]) == 1;
        this.pacMan.direcaoAtual = Integer.parseInt(dados[3]);
        this.pacMan.direcaoPretendida = Integer.parseInt(dados[4]);

    }

    void opIniciaGhost() {
        String envio = "019\n\0";
        String resposta = enviaOperacao(19, envio);
        String dados[] = resposta.substring(3).split("#");
        for (int i = 0; i < 4; i++) {
            int deslocamento = i * 6;
            ghosts[i].estado = Byte.parseByte(dados[0 + deslocamento]);
            ghosts[i].direcao = Integer.parseInt(dados[1 + deslocamento]);
            ghosts[i].tempoParaFicarLivre = Integer.parseInt(dados[2 + deslocamento]);
            ghosts[i].tempoParaInvulneravel = Integer.parseInt(dados[3 + deslocamento]);
            ghosts[i].personagem.setX(Integer.parseInt(dados[4 + deslocamento]));
            ghosts[i].personagem.setY(Integer.parseInt(dados[5 + deslocamento]));
            ghosts[i].frameAtual = 0;
            ghosts[i].delay = Ghost.DELAY_MAX;

        }

    }

    void opDoce(int tipoDoce) {
        String envio = "020\n\0";
        String resposta = enviaOperacao(20, envio);
        String dados[] = resposta.substring(3).split("#");
        pontos = Integer.parseInt(dados[0]);
        docesRestantes = Integer.parseInt(dados[1]);
        estadoJogo = Byte.parseByte(dados[2]);
    }

    void opExibeAjuda() {
        String envio = "021\n\0";
        String resposta = enviaOperacao(21, envio);
        String dados[] = resposta.substring(3).split("#");
        caminhoTiledMap = dados[0];
        App.tiledMap = new TmxMapLoader().load(caminhoTiledMap);
        App.tiledMapRenderer = new OrthogonalTiledMapRenderer(App.tiledMap);
    }

    void opExibeWin() {
        String envio = "022\n\0";
        String resposta = enviaOperacao(22, envio);
        String dados[] = resposta.substring(3).split("#");
        caminhoTiledMap = dados[0];
        App.tiledMap = new TmxMapLoader().load(caminhoTiledMap);
        App.tiledMapRenderer = new OrthogonalTiledMapRenderer(App.tiledMap);
    }
}
