import enums.Regiao;
import models.Estatisticas;
import models.Jornal;
import models.Noticia;
import parsers.BBCParser;
import parsers.FSPParser;
import parsers.G1Parser;
import gui.NoticiaPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Application {

    private ArrayList<Jornal>     jornais;
    private EstatisticasPorRegiao estatisticas;
    private DadosUsuario          dadosUsuario;
    private ArrayList<Noticia>    noticiasSalvas;

    // painelLerMaisTarde deve estar disponível globalmente
    // para poder ser atualizado, no painel de notícias
    // quando uma notícia é salva para ler mais tarde.
    // Isto é, para que possa ser atualizado, o painel deve estar
    // disponível no escopo de gerarPainelNoticias().
    private JPanel painelLerMaisTarde;
    private JFrame frame;

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
        jornais.add(new Jornal("G1", "https://g1.globo.com/", new G1Parser()));
        // jornais.add(new Jornal("Folha de São Paulo", "https://www.folha.uol.com.br/",   new FSPParser()));
        jornais.add(new Jornal("BBC", "https://www.bbc.com/portuguese/", new BBCParser()));
    }

    private void criarFrame()
    {
        frame = new JFrame("Agregador de notícias sobre COVID-19");
        JPanel contentPane = (JPanel) frame.getContentPane();
        contentPane.setLayout(new BorderLayout(6, 6));
        contentPane.setBorder(new EmptyBorder(12, 12, 12, 12));

        JTabbedPane tabs = gerarPainelTabs();
        frame.add(tabs);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                salvarDadosUsuario();
                System.exit(0);
            }
        });
    }


    //
    // Criação das tabs
    //

    // função helper para gerarPainelTabs
    private void addTabComScrollPane(JTabbedPane tabs, String titulo, JComponent comp)
    {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(comp);
        tabs.add(titulo, scrollPane);
    }

    private JTabbedPane gerarPainelTabs()
    {
        JTabbedPane tabs = new JTabbedPane();

        addTabComScrollPane(tabs, "Estatísticas", gerarTabelaEstatisticas());
        painelLerMaisTarde = gerarPainelLerMaisTarde();
        addTabComScrollPane(tabs, "Ler mais tarde", painelLerMaisTarde);
        // painelLerMaisTarde deve ser gerado ANTES do painel das notícias porque
        // o painel das notícias atualiza o painelLerMaisTarde quando o usuário
        // clica em ler uma notícia mais tarde
        addTabComScrollPane(tabs, "Notícias", gerarPainelNoticias());
        addTabComScrollPane(tabs, "Jornais seguidos", gerarPainelJornaisSeguidos());

        return tabs;
    }

    private JPanel gerarPainelNoticias()
    {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));


        for (Jornal j : jornais)
        {
            if (!j.isSeguido())
                continue;

            for (Noticia n : j.getNoticias())
            {
                // Ao adicionar um jornal para ler mais tarde,
                // precisamos atualizar o painelLerMaisTarde.
                // Mas não basta adicionar o NoticiaPanel np no painel de ler mais tarde,
                // pois o botão também deve trocar (de ter botão "Remover").
                // Para tanto, criamos um outro NoticiaPanel com esse botão,
                // e adicionamos ele no painelLerMaisTarde.

                JButton btn = new JButton("Ler mais tarde");
                JButton btnRemover = new JButton("Remover");

                NoticiaPanel np = new NoticiaPanel(n, j, btn);
                NoticiaPanel npLerMaisTarde = new NoticiaPanel(n, j, btnRemover);

                btn.addActionListener(e -> {
                    lerMaisTarde(n);
                    painelLerMaisTarde.add(npLerMaisTarde);
                    painelLerMaisTarde.repaint();
                });
                btnRemover.addActionListener(e -> {
                    removerLerMaisTarde(n);
                    painelLerMaisTarde.remove(npLerMaisTarde);
                    painelLerMaisTarde.repaint();
                });

                painel.add(np);
            }
        }

        return painel;
    }

    private JPanel gerarPainelLerMaisTarde()
    {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        for (Noticia n : noticiasSalvas)
        {
            JButton btn = new JButton("Remover");
            NoticiaPanel np = new NoticiaPanel(n, btn);
            btn.addActionListener(e ->  {
                removerLerMaisTarde(n);
                painel.remove(np);
                painel.repaint();
            });
            painel.add(np);
        }

        return painel;
    }

    private JTable gerarTabelaEstatisticas()
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
            dados[i][j  ] = df.format(e.getSegundaDose());
        }

        JTable tabela = new JTable(dados, colunas);
        // Para usuário não poder editar a coluna (https://stackoverflow.com/a/36356371)
        tabela.setDefaultEditor(Object.class, null);
        return tabela;
    }

    private JPanel gerarPainelJornaisSeguidos()
    {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        int n = jornais.size();
        JCheckBox[] cbs = new JCheckBox[n];
        for (int i = 0; i < n; i++)
        {
            Jornal j = jornais.get(i);
            cbs[i] = new JCheckBox(j.getNome(), j.isSeguido());
            painel.add(cbs[i]);
        }

        JButton btSalvar = new JButton("Salvar preferências");
        btSalvar.addActionListener(e -> {
            for (int i = 0; i < n; i++)
                jornais.get(i).setSeguido(cbs[i].isSelected());
        });
        painel.add(btSalvar);

        return painel;
    }

    //
    // Carregar e salvar dados do usuário
    //

    private void carregaDadosUsuario()
    {
        try {
            noticiasSalvas = dadosUsuario.loadNoticias();
            dadosUsuario.loadJornaisSeguidos(jornais);
        } catch (IOException e) {
            erroArquivos();
        }
    }

    private void salvarDadosUsuario()
    {
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

/* private JTable gerarTabelaNoticias(Jornal jornal)
{
    String[] colunas = {"Título", "Data de publicação", "Resumo", "URL"};
    String[][] dados = new String[jornal.getNoticias().size()][colunas.length];

    DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("pt", "br"));

    for (int i = 0; i < jornal.getNoticias().size(); i++)
    {
        int j = 0;
        dados[i][j++] = jornal.getNoticias().get(i).getTitulo();
        dados[i][j++] = dateFormat.format(jornal.getNoticias().get(i).getData());
        dados[i][j++] = jornal.getNoticias().get(i).getResumo();
        dados[i][j++] = jornal.getNoticias().get(i).getUrl();
    }

    JTable tabela = new JTable(dados, colunas);
    // Para usuário não poder editar a coluna (https://stackoverflow.com/a/36356371)
    tabela.setDefaultEditor(Object.class, null);
    return tabela;
}*/
