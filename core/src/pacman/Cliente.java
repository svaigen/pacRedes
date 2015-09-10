package pacman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente {

    int porta;
    Socket socket;
    PrintStream ps;
    BufferedReader br;

    public Cliente(int porta) {
        this.porta = porta;
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
            socket.close();
        } catch (Exception e) {
            System.err.println("Erro ao fechar conexão");
        }
    }
    
    public static void main(String[] args) {
        Cliente c = new Cliente(50001);
        String n;
        for (int i = 0; i < 3; i++) {
            n = new Scanner(System.in).next();
            n = c.enviaInformacao(n);
            System.out.println(n);
        }
        c.fechaConexao();
    }
    

}
