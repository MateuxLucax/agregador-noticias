import enums.Regiao;
import gui.DatePicker;
import gui.NoticiaPanel;
import models.Estatisticas;
import models.Jornal;
import models.Noticia;
import org.jdatepicker.impl.JDatePickerImpl;
import parsers.BBCParser;
import parsers.FSPParser;
import parsers.G1Parser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

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
        carregarDadosUsuario();

        criarFrame();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
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
        jornais.add(new Jornal("Folha de São Paulo", "https://www.folha.uol.com.br/",   new FSPParser()));
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

    private JPanel gerarPainelLerMaisTarde()
    {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        painel.add(gerarPainelCadastroNoticia(painel));

        for (Noticia n : noticiasSalvas)
            painel.add( gerarNoticiaPanelDoPainelLerMaisTarde(painel, n) );

        return painel;
    }

    private JPanel gerarPainelCadastroNoticia(JPanel painel)
    {
        JPanel cadastro = new JPanel();
        cadastro.setLayout(new BoxLayout(cadastro, BoxLayout.Y_AXIS));
        JPanel linha;
        JLabel label;

        linha = new JPanel(new FlowLayout(FlowLayout.LEFT));
        label = new JLabel("Título: ");
        label.setPreferredSize(new Dimension(120, 20));
        linha.add(label);
        JTextField tfTitulo = new JTextField(30);
        linha.add(tfTitulo);
        cadastro.add(linha);

        // TODO adicionar data (dd/mm/aaaa) da notícia

        linha = new JPanel(new FlowLayout(FlowLayout.LEFT));
        label = new JLabel("URL: ");
        label.setPreferredSize(new Dimension(120, 20));
        linha.add(label);
        JTextField tfUrl = new JTextField(30);
        linha.add(tfUrl);
        cadastro.add(linha);

        linha = new JPanel(new FlowLayout(FlowLayout.LEFT));
        label = new JLabel("Resumo: ");
        label.setPreferredSize(new Dimension(120, 20));
        linha.add(label);
        JTextField tfResumo = new JTextField(30);
        linha.add(tfResumo);
        cadastro.add(linha);

        linha = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.addActionListener(e -> {
            Noticia n = new Noticia(tfTitulo.getText(), new Date(), tfUrl.getText(), tfResumo.getText());
            painel.add( gerarNoticiaPanelDoPainelLerMaisTarde(painel, n) );
            painel.revalidate();
        });
        linha.add(btnCadastrar);
        JButton btnLimpar = new JButton("Limpar");
        btnLimpar.addActionListener(e -> {
            tfTitulo.setText("");
            tfUrl.setText("");
            tfResumo.setText("");
        });
        linha.add(btnLimpar);
        cadastro.add(linha);

        return cadastro;
    }

    private NoticiaPanel gerarNoticiaPanelDoPainelLerMaisTarde(JPanel painel, Noticia noticia)
    {
        JButton btn = new JButton("Remover");
        NoticiaPanel np = new NoticiaPanel(noticia, btn);
        btn.addActionListener(e ->  {
            removerLerMaisTarde(noticia);
            painel.remove(np);
            painel.revalidate();
        });
        return np;
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
    // Criação de tabs >> painel de notícias e pesquisa
    //

    // função helper para gerarPainelTabs
    private void addTabComScrollPane(JTabbedPane tabs, String titulo, JComponent comp)
    {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(comp);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        tabs.add(titulo, scrollPane);
    }

    private JTabbedPane gerarPainelTabs()
    {
        JTabbedPane tabs = new JTabbedPane();

        var tabEstatisticas = gerarTabelaEstatisticas();
        // painelLerMaisTarde deve ser gerado ANTES do painel das notícias porque
        // o painel das notícias atualiza o painelLerMaisTarde quando o usuário
        // clica em ler uma notícia mais tarde
        painelLerMaisTarde = gerarPainelLerMaisTarde();
        var tabNoticias = gerarPainelNoticias();
        var tabJornaisSeguidos = gerarPainelJornaisSeguidos();

        addTabComScrollPane(tabs, "Estatísticas", tabEstatisticas);
        addTabComScrollPane(tabs, "Notícias", tabNoticias);
        addTabComScrollPane(tabs, "Ler mais tarde", painelLerMaisTarde);
        addTabComScrollPane(tabs, "Jornais seguidos", tabJornaisSeguidos);

        return tabs;
    }

    private JPanel gerarPainelNoticias()
    {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        JPanel painelNoticias = new JPanel();
        painel.add(gerarPainelPesquisa(painelNoticias));
        painelNoticias.setLayout(new BoxLayout(painelNoticias, BoxLayout.Y_AXIS));

        preencherPainelNoticias(painelNoticias, true);

        painel.add(painelNoticias);

        return painel;
    }

    // painelNoticias é passado para que os botões de pesquisam possam
    // atualizá-lo e mostrar as notícias resultantes da pesquisa
    private JTabbedPane gerarPainelPesquisa(JPanel painelNoticias)
    {

        JTabbedPane tabs = new JTabbedPane();

        //
        //  Mostrar noticias
        //

        JPanel painelInicial = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton btTodasNoticias = new JButton("Todas as notícias");
        JButton btNoticiasRecentes = new JButton("Notícias recentes");
        btTodasNoticias.addActionListener(e -> {
            preencherPainelNoticias(painelNoticias, false);
            painelNoticias.repaint();
        });
        btNoticiasRecentes.addActionListener(e -> {
            preencherPainelNoticias(painelNoticias, true);
            painelNoticias.repaint();
        });

        painelInicial.add(btTodasNoticias);
        painelInicial.add(btNoticiasRecentes);

        tabs.add("Painel Inicial", painelInicial);

        //
        // Pesquisa por título
        //

        JPanel painelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JLabel lbTitulo = new JLabel("Título: ");
        JTextField pesquisa = new JTextField(20);
        painelTitulo.add(lbTitulo);
        painelTitulo.add(pesquisa);

        JButton btPesquisarTitulo = new JButton("Pesquisar");
        btPesquisarTitulo.addActionListener(e -> {
            preencherPainelNoticias(painelNoticias, pesquisa.getText());
            painelNoticias.repaint();
        });

        painelTitulo.add(btPesquisarTitulo);

        tabs.add("Por título", painelTitulo);

        //
        // Pesquisa por uma data
        //

        // TODO adicionar botão "Último dia" aqui,
        // que preenche com as notícias recentes usando
        // preencherPainelNoticias(painelNoticias)

        JPanel painelData = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btPesquisarData = new JButton("Pesquisar");
        JDatePickerImpl datePicker = DatePicker.generate();

        btPesquisarData.addActionListener(e -> {
            Date selectedDate = (Date) datePicker.getModel().getValue();

            preencherPainelNoticias(painelNoticias, selectedDate);
        });

        painelData.add(datePicker);
        painelData.add(btPesquisarData);
        tabs.add("Por data", painelData);

        //
        // Pesquisa por intervalo de datas
        //

        JPanel painelDataIntervalo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JDatePickerImpl datePickerInicial = DatePicker.generate();
        JDatePickerImpl datePickerFinal = DatePicker.generate();
        JButton btPesquisarIntervalo = new JButton("Pesquisar");

        btPesquisarIntervalo.addActionListener(e -> {
            Date selectedDateInicial = (Date) datePickerInicial.getModel().getValue();
            Date selectedDateFinal = (Date) datePickerFinal.getModel().getValue();

            preencherPainelNoticias(painelNoticias, selectedDateInicial, selectedDateFinal);
        });

        painelDataIntervalo.add(datePickerInicial);
        painelDataIntervalo.add(datePickerFinal);
        painelDataIntervalo.add(btPesquisarIntervalo);

        tabs.add("Por intervalo de datas", painelDataIntervalo);

        return tabs;
    }

    private void preencherPainelNoticias(JPanel painelNoticias, boolean recentes) {
        painelNoticias.removeAll();
        for (Jornal jornal : jornais)
            if (jornal.isSeguido())
                for (Noticia noticia: recentes ? jornal.getNoticiasRecentes() : jornal.getNoticias())
                    painelNoticias.add(gerarNoticiaPanelDoPainelNoticias(noticia, jornal));
        painelNoticias.revalidate();
    }

    private void preencherPainelNoticias(JPanel painelNoticias, String titulo)
    {
        painelNoticias.removeAll();
        for (Jornal jornal : jornais)
            if (jornal.isSeguido())
                for (Noticia noticia : jornal.getNoticias(titulo))
                    painelNoticias.add(gerarNoticiaPanelDoPainelNoticias(noticia, jornal));
        painelNoticias.revalidate();
    }

    private void preencherPainelNoticias(JPanel painelNoticias, Date data)
    {
        painelNoticias.removeAll();
        for (Jornal jornal : jornais)
            if (jornal.isSeguido())
                for (Noticia noticia : jornal.getNoticias(data))
                    painelNoticias.add(gerarNoticiaPanelDoPainelNoticias(noticia, jornal));
        painelNoticias.revalidate();
    }

    private void preencherPainelNoticias(JPanel painelNoticias, Date dataInicial, Date dataFinal)
    {
        painelNoticias.removeAll();
        for (Jornal jornal : jornais)
            if (jornal.isSeguido())
                for (Noticia noticia : jornal.getNoticias(dataInicial, dataFinal))
                    painelNoticias.add(gerarNoticiaPanelDoPainelNoticias(noticia, jornal));
        painelNoticias.revalidate();
    }

    private NoticiaPanel gerarNoticiaPanelDoPainelNoticias(Noticia noticia, Jornal jornal)
    {
        // Ao adicionar um jornal para ler mais tarde,
        // precisamos atualizar o painelLerMaisTarde.
        // Mas não basta adicionar o NoticiaPanel np no painel de ler mais tarde,
        // pois o botão também deve trocar (de ter botão "Remover").
        // Para tanto, criamos um outro NoticiaPanel com esse botão,
        // e adicionamos ele no painelLerMaisTarde.

        NoticiaPanel npLerMaisTarde = gerarNoticiaPanelDoPainelLerMaisTarde(painelLerMaisTarde, noticia);

        JButton btn = new JButton("Ler mais tarde");
        NoticiaPanel np = new NoticiaPanel(noticia, jornal, btn);
        btn.addActionListener(e -> {
            lerMaisTarde(noticia);
            painelLerMaisTarde.add(npLerMaisTarde);
            painelLerMaisTarde.revalidate();
        });

        return np;
    }


    //
    // Carregar e salvar dados do usuário
    //

    private void carregarDadosUsuario()
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
