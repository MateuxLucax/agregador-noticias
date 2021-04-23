package gui;

import models.Jornal;
import models.Noticia;
import org.jdatepicker.impl.JDatePickerImpl;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;

public class NoticiasPanel extends JPanel
{
    private ArrayList<Jornal> jornais;

    // Quando o usuário clica no "ler mais tarde" de uma notícia do NoticiasPanel,
    // essa notícia precisa ser adicionada ao array de noticias salvas
    // e um NoticiaPanel seu precisa ser adicionado ao painelLerMaisTarde.
    // Por isso esses objetos fazem parte do estado do NoticiasPanel.
    // (... parece um bom lugar para usar o Observer pattern)
    private ArrayList<Noticia> noticiasSalvas;
    private JPanel painelLerMaisTarde;

    public NoticiasPanel(ArrayList<Jornal> jornais, ArrayList<Noticia> noticiasSalvas,
                         JPanel painelLerMaisTarde)
    {
        this.jornais = jornais;
        this.noticiasSalvas = noticiasSalvas;
        this.painelLerMaisTarde = painelLerMaisTarde;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel painelNoticias = new JPanel();
        add(gerarPainelPesquisa(painelNoticias));
        painelNoticias.setLayout(new BoxLayout(painelNoticias, BoxLayout.Y_AXIS));

        preencherPainelNoticias(painelNoticias, true);

        add(painelNoticias);
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

    private void preencherPainelNoticias(JPanel painelNoticias, boolean recentes)
    {
        painelNoticias.removeAll();
        for (Jornal jornal : jornais)
            if (jornal.isSeguido())
                for (Noticia noticia: recentes ? jornal.getNoticiasRecentes() : jornal.getNoticias())
                    painelNoticias.add(gerarNoticiaPanel(noticia, jornal));
        painelNoticias.revalidate();
    }

    private void preencherPainelNoticias(JPanel painelNoticias, String titulo)
    {
        painelNoticias.removeAll();
        for (Jornal jornal : jornais)
            if (jornal.isSeguido())
                for (Noticia noticia : jornal.getNoticias(titulo))
                    painelNoticias.add(gerarNoticiaPanel(noticia, jornal));
        painelNoticias.revalidate();
    }

    private void preencherPainelNoticias(JPanel painelNoticias, Date data)
    {
        painelNoticias.removeAll();
        for (Jornal jornal : jornais)
            if (jornal.isSeguido())
                for (Noticia noticia : jornal.getNoticias(data))
                    painelNoticias.add(gerarNoticiaPanel(noticia, jornal));
        painelNoticias.revalidate();
    }

    private void preencherPainelNoticias(JPanel painelNoticias, Date dataInicial, Date dataFinal)
    {
        painelNoticias.removeAll();
        for (Jornal jornal : jornais)
            if (jornal.isSeguido())
                for (Noticia noticia : jornal.getNoticias(dataInicial, dataFinal))
                    painelNoticias.add(gerarNoticiaPanel(noticia, jornal));
        painelNoticias.revalidate();
    }

    private NoticiaPanel gerarNoticiaPanel(Noticia noticia, Jornal jornal)
    {
        // NoticiaPanel correspondente que será adicionado no painel de "ler mais tarde"
        JButton btnRemover = new JButton("Remover");
        NoticiaPanel noticiaPanelLerMaisTarde = new NoticiaPanel(noticia, jornal, btnRemover);
        btnRemover.addActionListener(e -> {
            noticiasSalvas.remove(noticia);
            painelLerMaisTarde.remove(noticiaPanelLerMaisTarde);
            painelLerMaisTarde.revalidate();
        });

        // NoticiaPanel a ser adicionado nesse painel
        JButton btnLerMaisTarde = new JButton("Ler mais tarde");
        NoticiaPanel noticiaPanel = new NoticiaPanel(noticia, jornal, btnLerMaisTarde);
        btnLerMaisTarde.addActionListener(e -> {
            if (!noticiasSalvas.contains(noticia))
                noticiasSalvas.add(noticia);
            painelLerMaisTarde.add(noticiaPanelLerMaisTarde);
            painelLerMaisTarde.revalidate();
        });
        return noticiaPanel;
    }

}
