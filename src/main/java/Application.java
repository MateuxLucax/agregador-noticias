import enums.Regiao;
import models.Estatisticas;
import models.Jornal;
import models.Noticia;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Application {

    private ArrayList<Jornal>     jornais;
    private EstatisticasPorRegiao estatisticas;
    private DadosUsuario          dadosUsuario;
    private ArrayList<Noticia>    noticiasSalvas;

    private JFrame frame;
    private JTable tabelaEstatisticas;

    public Application()
    {
        estatisticas = EstatisticasPorRegiao.getInstance();
        dadosUsuario = DadosUsuario.getInstance();
        criarJornais();
        carregaDadosUsuario();

        criarFrame();
        frame.pack();
        frame.setVisible(true);
    }

    public void lerMaisTarde(Noticia n)
    {
        if (!noticiasSalvas.contains(n))
            noticiasSalvas.add(n);
    }

    public void removerLerMaisTarde(Noticia n)
    {
        noticiasSalvas.remove(n);
    }

    private void criarJornais()
    {
        jornais = new ArrayList<>();
        // jornais.add(new Jornal("G1", "https://g1.globo.com/", new G1Parser()));
        // jornais.add(new Jornal("Folha de São Paulo", "https://www.folha.uol.com.br/",   new FSPParser()));
        // jornais.add(new Jornal("BBC", "https://www.bbc.com/portuguese/", new BBCParser()));
    }

    private void criarFrame()
    {
        frame = new JFrame("Agregador de notícias sobre COVID-19");
        JPanel contentPane = (JPanel) frame.getContentPane();
        contentPane.setLayout(new BorderLayout(6, 6));
        contentPane.setBorder(new EmptyBorder(12, 12, 12, 12));

        criarTabela();
        // ScrollPane necessário pra mostrar o cabeçalho da tabela
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(tabelaEstatisticas);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                salvarDadosUsuario();
                System.exit(0);
            }
        });
    }

    private void criarTabela()
    {
        Regiao[] regioes = Regiao.values();
        String[] colunas = {"Região", "Casos", "Óbitos", "Recuperados", "Vacinados", "Segunda dose"};
        String[][] dados = new String[regioes.length][colunas.length];
        DecimalFormat df = new DecimalFormat();
        df.setGroupingSize(3);

        for (int i = 0; i < regioes.length; i++)
        {
            int j = 0;
            Estatisticas e = estatisticas.getEstatisticas(regioes[i]);
            dados[i][j++] = regioes[i].name();
            dados[i][j++] = df.format(e.getCasos());
            dados[i][j++] = df.format(e.getObitos());
            dados[i][j++] = df.format(e.getRecuperados());
            dados[i][j++] = df.format(e.getVacinados());
            dados[i][j++] = df.format(e.getSegundaDose());
        }

        tabelaEstatisticas = new JTable(dados, colunas);
        // Para usuário não poder editar a coluna (https://stackoverflow.com/a/36356371)
        tabelaEstatisticas.setDefaultEditor(Object.class, null);
    }

    private void carregaDadosUsuario()
    {
        System.out.println("Carregando dados do usuário");
        try {
            noticiasSalvas = dadosUsuario.loadNoticias();
            dadosUsuario.loadJornaisSeguidos(jornais);
        } catch (IOException e) {
            erroArquivos();
        }
    }

    private void salvarDadosUsuario()
    {
        System.out.println("Salvando dados do usuário");
        try {
            dadosUsuario.saveJornaisSeguidos(jornais);
            dadosUsuario.saveNoticias(noticiasSalvas);
        } catch (IOException e) {
            erroArquivos();
        }
    }

    private void erroArquivos()
    {
        // TODO mostrar num modal, não no console
        // Se bem que fazendo isso pode dar problema com o System.exit(0) logo em seguida
        System.out.println("Algum(ns) arquivo(s) estão faltando.");
        System.out.println("Baixe-os em https://github.com/MateuxLucax/agregador-noticias");
        System.out.println("e coloque-os em " + dadosUsuario.getDiretorio());
    }


    public static void main(String[] args) {

        Application app = new Application();

        /* try {

            app.noticiasSalvas = dadosUsuario.loadNoticias();
            if (app.noticiasSalvas.size() > 0) {
                System.out.println("Notícias que você salvou para ler mais tarde: ");
                for (Noticia n : noticiasSalvas)
                    System.out.println(n);
            }

            dadosUsuario.loadJornaisSeguidos(jornais);
            for (Jornal jornal : jornais) {
                if (jornal.seguido()) {
                    Instant now = Instant.now();
                    Date yesterday = Date.from(now.minus(1, ChronoUnit.DAYS));
                    Date today     = Date.from(now.truncatedTo(ChronoUnit.DAYS));

                    System.out.println(jornal.toString());
                }
            }

            estatisticas.printTabela();

            dadosUsuario.saveJornaisSeguidos(jornais);
            dadosUsuario.saveNoticias(noticiasSalvas);

        } catch (IOException e) {  // FileNotFoundException extends IOException
            System.out.println("Algum(ns) arquivo(s) estão faltando.");
            System.out.println("Baixe-os em https://github.com/MateuxLucax/agregador-noticias");
            System.out.println("e coloque-os em " + dadosUsuario.getDiretorio());
        }
       */
    }
}

