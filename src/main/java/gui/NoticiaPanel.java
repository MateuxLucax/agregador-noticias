package gui;

import models.Jornal;
import models.Noticia;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class NoticiaPanel extends JPanel
{
    private final Noticia noticia;
    private final Jornal jornal;

    public NoticiaPanel(Noticia noticia, Jornal jornal)
    {
        this.noticia = noticia;
        this.jornal  = jornal;
        init();
    }

    private JLabel makeLink(JLabel label, String url)
    {
        label.setForeground(Color.BLUE);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                    label.setForeground(Color.MAGENTA.darker().darker());
                } catch (IOException | URISyntaxException e) {
                    System.out.println("Link do jornal inválido");
                    // TODO mostrar alguma mensagem de erro em modal
                }
            }
        });
        return label;
    }

    private String abreviarString(String s, int n)
    {
        if (s.length() < n)
            return s;
        else
            return s.substring(0, n-3) + "...";
    }


    public void init()
    {
        setBorder(new EtchedBorder());

        JPanel tituloPanel = new JPanel();
        tituloPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        String tituloUnderline = String.format(
                "<html><u>%s</u></html>",
                abreviarString(noticia.getTitulo(), 120)
        );
        JLabel tituloLabel = new JLabel(tituloUnderline);
            makeLink(tituloLabel, noticia.getUrl());
            /* Font fnt = tituloLabel.getFont();
            Font newFnt = new Font(fnt.getFontName(), Font.BOLD, fnt.getSize() + 2);
            tituloLabel.setFont(newFnt); */
        tituloPanel.add(tituloLabel);

        JPanel meioPanel = new JPanel();
        meioPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // TODO mostrar a data em formato tipo "20 de Março de 2012"
        JLabel lbData = new JLabel(noticia.getData().toString());
            lbData.setForeground(Color.GRAY);
        meioPanel.add(lbData);

        String jornalUnderline = String.format("<html><u>%s</u></html>", jornal.getNome());
            JLabel jornalLabel = new JLabel(jornalUnderline);
            makeLink(jornalLabel, jornal.getUrl());
        meioPanel.add(jornalLabel);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(tituloPanel);
        add(meioPanel);

        JTextArea resumo = new JTextArea(noticia.getResumo());
            resumo.setOpaque(false);
            resumo.setLineWrap(true);
            resumo.setEditable(false);
            resumo.setBorder(new EmptyBorder(0, 10, 10, 10));
        add(resumo);
    }

}
