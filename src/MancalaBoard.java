import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class MancalaBoard extends JPanel implements ChangeListener {
    private MancalaModel model;
    private BoardStyle style;

    private static final int MANCALA_W = 95;
    private static final int MANCALA_H = 210;
    private static final int PIT_W = 92;
    private static final int PIT_H = 92;
    private static final int COL_GAP = 6;
    private static final int H_MARGIN = 18;
    private static final int V_MARGIN = 28;
    private static final int ROW_GAP = 10;

    private Rectangle[] pitRects = new Rectangle[14];
    private int hoveredPit = -1;

    public MancalaBoard(MancalaModel model, BoardStyle style) {
        this.model = model;
        this.style = style;
        model.addChangeListener(this);

        int pw = H_MARGIN * 2 + MANCALA_W * 2 + COL_GAP * 2 + (PIT_W + COL_GAP) * 6;
        int ph = V_MARGIN * 2 + PIT_H * 2 + ROW_GAP + 22; // +22 for labels
        setPreferredSize(new Dimension(pw, ph));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { handleClick(e.getX(), e.getY()); }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                int prev = hoveredPit;
                hoveredPit = pitAt(e.getX(), e.getY());
                if (hoveredPit != prev) repaint();
            }
        });
    }

    public void refreshStyle(BoardStyle s) {
        this.style = s; repaint();
    }

    @Override public void stateChanged(ChangeEvent e) {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(style.getBoardColor());
        g2.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 44, 44);
        g2.setColor(style.getPitBorderColor());
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 44, 44);
        g2.setStroke(new BasicStroke(1f));

        computeLayout();
        int[] board = model.getBoard();
        int curPlayer = model.getCurrentPlayer();
        boolean started = model.isGameStarted();
        boolean over = model.isGameOver();

        Rectangle mb = pitRects[MancalaModel.MANCALA_B];
        style.drawMancala(g2, mb.x, mb.y, mb.width, mb.height, board[MancalaModel.MANCALA_B], "B");
        Rectangle ma = pitRects[MancalaModel.MANCALA_A];
        style.drawMancala(g2, ma.x, ma.y, ma.width, ma.height, board[MancalaModel.MANCALA_A], "A");

        for (int col = 0; col < 6; col++) {
            int aIdx = col;
            int bIdx = 12 - col;
            Rectangle ra = pitRects[aIdx];
            boolean aIsActive = (started && !over && curPlayer == 0);
            boolean aHovered  = (hoveredPit == aIdx);
            style.drawPit(g2, ra.x, ra.y, ra.width, ra.height, board[aIdx], "A" + (aIdx + 1), aIsActive, aHovered);
            Rectangle rb = pitRects[bIdx];
            boolean bIsActive = (started && !over && curPlayer == 1);
            boolean bHovered  = (hoveredPit == bIdx);
            int bLabel    = 6 - col;
            style.drawPit(g2, rb.x, rb.y, rb.width, rb.height, board[bIdx], "B" + bLabel, bIsActive, bHovered);
        }

        if (started && !over) {
            String turn = (curPlayer == 0) ? "▶ Player A's Turn" : "▶ Player B's Turn";
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            g2.setColor(style.getLabelColor());
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(turn, (getWidth() - fm.stringWidth(turn)) / 2, 18);
        }
        g2.dispose();
    }

    private void computeLayout() {
        int totalW = getWidth();
        int totalH = getHeight();

        int rowsH = PIT_H * 2 + ROW_GAP;
        int topY  = (totalH - rowsH - 22) / 2 + 10;
        int botY  = topY + PIT_H + ROW_GAP;
        int mancalaY = topY + (rowsH - MANCALA_H) / 2;

        pitRects[MancalaModel.MANCALA_B] = new Rectangle(H_MARGIN, mancalaY, MANCALA_W, MANCALA_H);
        pitRects[MancalaModel.MANCALA_A] = new Rectangle(totalW - H_MARGIN - MANCALA_W, mancalaY, MANCALA_W, MANCALA_H);

        int pitsStartX = H_MARGIN + MANCALA_W + COL_GAP;
        int pitsEndX = totalW - H_MARGIN - MANCALA_W - COL_GAP;
        int pitsW = pitsEndX - pitsStartX;
        int colW = pitsW / 6;

        for (int col = 0; col < 6; col++) {
            int x = pitsStartX + col * colW + (colW - PIT_W) / 2;
            int aIdx = col;
            int bIdx = 12 - col;
            pitRects[aIdx] = new Rectangle(x, botY, PIT_W, PIT_H);
            pitRects[bIdx] = new Rectangle(x, topY, PIT_W, PIT_H);
        }
    }
    private void handleClick(int mx, int my) {
        if (!model.isGameStarted() || model.isGameOver()) return;
        int pit = pitAt(mx, my);
        if (pit < 0 || pit == MancalaModel.MANCALA_A || pit == MancalaModel.MANCALA_B) return;
        boolean ok = model.makeMove(pit);
        if (!ok) {
        }
    }

    private int pitAt(int mx, int my) {
        computeLayout();
        for (int i = 0; i < 14; i++)
            if (pitRects[i] != null && pitRects[i].contains(mx, my)) return i;
        return -1;
    }
}
