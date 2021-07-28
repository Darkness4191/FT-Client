package de.dragon.FTClient.frame;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ButtonUI extends BasicButtonUI implements java.io.Serializable, MouseListener, KeyListener {

    private final static ButtonUI m_buttonUI = new ButtonUI();

    protected Color m_backgroundNormal = UIManager.getColor("TextField.background");

    protected Color m_backgroundActive = m_backgroundNormal.getRGB() != new Color(255, 255, 255).getRGB() ? m_backgroundNormal.brighter() : new Color(205, 234, 246, 255);

    protected Color m_backgroundPressed = m_backgroundActive.darker();


    public static ComponentUI createUI(JComponent c) {
        return m_buttonUI;
    }

    public void installUI(JComponent c) {
        super.installUI(c);

        c.addMouseListener(this);
        c.addKeyListener(this);
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        c.removeMouseListener(this);
        c.removeKeyListener(this);
    }

    public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        Dimension d = b.getSize();

        g.setFont(new Font("Tahoma", System.getProperty("os.name").toLowerCase().contains("windows") ? Font.BOLD : Font.PLAIN, 11));
        FontMetrics fm = g.getFontMetrics();

        g.setColor(b.getForeground());
        String caption = b.getText();
        int x = (d.width - fm.stringWidth(caption)) / 2;
        int y = (d.height + fm.getAscent()) / 2;
        g.drawString(caption, x, y);

    }

    public Dimension getPreferredSize(JComponent c) {
        Dimension d = super.getPreferredSize(c);
        return d;
    }

    public void mouseClicked(MouseEvent e) { }

    public void mousePressed(MouseEvent e) {
        JComponent c = (JComponent) e.getComponent();
        c.setBackground(m_backgroundPressed);
    }

    public void mouseReleased(MouseEvent e) {
        JComponent c = (JComponent) e.getComponent();
        c.setBackground(m_backgroundNormal);
    }

    public void mouseEntered(MouseEvent e) {
        JComponent c = (JComponent) e.getComponent();
        c.setBackground(m_backgroundActive);
        c.repaint();
    }

    public void mouseExited(MouseEvent e) {
        JComponent c = (JComponent) e.getComponent();
        c.setBackground(m_backgroundNormal);
        c.repaint();
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
            JComponent c = (JComponent) e.getComponent();
            c.setBackground(m_backgroundPressed);
        }
    }

    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
            JComponent c = (JComponent) e.getComponent();
            c.setBackground(m_backgroundNormal);
        }
    }
}
