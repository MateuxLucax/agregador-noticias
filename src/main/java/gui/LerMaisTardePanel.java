package gui;

import models.Noticia;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;

public class LerMaisTardePanel extends JPanel
{
    private ArrayList<Noticia> noticiasSalvas;

    public LerMaisTardePanel(ArrayList<Noticia> noticiasSalvas)
    {
        this.noticiasSalvas = noticiasSalvas;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(gerarPainelCadastroNoticia(this));

        for (Noticia noticia : noticiasSalvas)
            add(gerarNoticiaPanel(this, noticia));
    }

    private JPanel gerarPainelCadastroNoticia(JPanel pai)
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
            if (!noticiasSalvas.contains(n)) {
                noticiasSalvas.add(n);
            }
            pai.add( gerarNoticiaPanel(pai, n) );
            pai.revalidate();
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

    private NoticiaPanel gerarNoticiaPanel(JPanel painel, Noticia noticia)
    {
        JButton btn = new JButton("Remover");
        NoticiaPanel np = new NoticiaPanel(noticia, btn);
        btn.addActionListener(e ->  {
            noticiasSalvas.remove(noticia);
            painel.remove(np);
            painel.revalidate();
        });
        return np;
    }

}
