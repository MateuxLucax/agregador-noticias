package main;

import gui.*;
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
import java.util.ArrayList;
import java.util.Date;

public class Application
{
    private ArrayList<Jornal>  jornais;
    private ArrayList<Noticia> noticiasSalvas;
    private ArquivosUsuario    arquivosUsuario;

    private JFrame frame;

    public Application()
    {
        arquivosUsuario = ArquivosUsuario.getInstance();
        criarJornais();
        carregarDadosUsuario();

        criarFrame();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setVisible(true);
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

        EstatisticasTable.inicializar();
        JTable tabEstatisticas = EstatisticasTable.get();
        JPanel painelLerMaisTarde = new LerMaisTardePanel(noticiasSalvas);
        JPanel tabNoticias = new NoticiasPanel(jornais, noticiasSalvas, painelLerMaisTarde);
        JPanel tabJornaisSeguidos = new JornaisSeguidosPanel(jornais);

        addTabComScrollPane(tabs, "Estatísticas", tabEstatisticas);
        addTabComScrollPane(tabs, "Notícias", tabNoticias);
        addTabComScrollPane(tabs, "Ler mais tarde", painelLerMaisTarde);
        addTabComScrollPane(tabs, "Jornais seguidos", tabJornaisSeguidos);

        return tabs;
    }


    //
    // Carregar e salvar dados do usuário
    //

    private void carregarDadosUsuario()
    {
        try {
            noticiasSalvas = arquivosUsuario.loadNoticias();
            arquivosUsuario.loadJornaisSeguidos(jornais);
        } catch (IOException e) {
            erroArquivos();
        }
    }

    private void salvarDadosUsuario()
    {
        try {
            arquivosUsuario.saveJornaisSeguidos(jornais);
            arquivosUsuario.saveNoticias(noticiasSalvas);
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
        System.out.println("e coloque-os em " + arquivosUsuario.getDiretorio());
    }


    public static void main(String[] args)
    {
        Application app = new Application();
    }
}
