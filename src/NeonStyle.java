import java.awt.*;


public class NeonStyle implements BoardStyle {

    private static final Color BOARD_COLOR = new Color(15, 15, 30);
    private static final Color PIT_COLOR = new Color(20, 20, 50);
    private static final Color PIT_BORDER = new Color(0, 200, 255);
    private static final Color MANCALA_COLOR = new Color(10, 10, 40);
    private static final Color STONE_COLOR = new Color(255, 80, 160);
    private static final Color STONE_ACCENT = new Color(255, 180, 220);
    private static final Color LABEL_COLOR = new Color(0, 255, 200);
    private static final Color HOVER_COLOR = new Color(30, 30, 80);
    private static final Color ACTIVE_BORDER = new Color(255, 220, 0);
    private static final Color MANCALA_BORDER = new Color(160, 0, 255);

    @Override public String getStyleName() {
        return "Neon Arcade";
    }
    @Override public Color getBoardColor(){
        return BOARD_COLOR;
    }
    @Override public Color getPitColor(){
        return PIT_COLOR;
    }
    @Override public Color getPitBorderColor(){
        return PIT_BORDER;
    }
    @Override public Color getMancalaColor(){
        return MANCALA_COLOR;
    }
    @Override public Color getStoneColor(){
        return STONE_COLOR;
    }
    @Override public Color getStoneAccentColor(){
        return STONE_ACCENT;
    }
    @Override public Color getLabelColor(){
        return LABEL_COLOR;
    }
    @Override public Font getLabelFont(){
        return new Font("Monospaced", Font.BOLD, 12);
    }
    @Override public Font getMancalaFont(){
        return new Font("Monospaced", Font.BOLD, 20);
    }

    @Override
    public void drawPit(Graphics2D g2, int x, int y, int w, int h, int stones, String label, boolean isCurrentPlayerPit, boolean isHovered) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int pad = 6;
        Color fill = isHovered && isCurrentPlayerPit ? HOVER_COLOR : PIT_COLOR;
        g2.setColor(fill);
        g2.fillRoundRect(x + pad, y + pad, w - pad * 2, h - pad * 2, 16, 16);

        if (isCurrentPlayerPit) {
            g2.setColor(ACTIVE_BORDER);
            g2.setStroke(new BasicStroke(2.5f));
        } else {
            g2.setColor(PIT_BORDER);
            g2.setStroke(new BasicStroke(1.5f));
        }
        g2.drawRoundRect(x + pad, y + pad, w - pad * 2, h - pad * 2, 16, 16);
        g2.setStroke(new BasicStroke(1f));

        drawNeonStones(g2, x, y, w, h, stones);

        g2.setFont(getLabelFont());
        g2.setColor(LABEL_COLOR);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(label, x + (w - fm.stringWidth(label)) / 2, y + h + 14);
    }

    @Override
    public void drawMancala(Graphics2D g2, int x, int y, int w, int h, int stones, String label) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int pad = 5;
        g2.setColor(MANCALA_COLOR);
        g2.fillRoundRect(x + pad, y + pad, w - pad * 2, h - pad * 2, 20, 20);


        g2.setColor(MANCALA_BORDER);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawRoundRect(x + pad, y + pad, w - pad * 2, h - pad * 2, 20, 20);
        g2.setColor(new Color(160, 0, 255, 60));
        g2.setStroke(new BasicStroke(6f));
        g2.drawRoundRect(x + pad, y + pad, w - pad * 2, h - pad * 2, 20, 20);
        g2.setStroke(new BasicStroke(1f));

        g2.setFont(new Font("Monospaced", Font.BOLD, 12));
        g2.setColor(LABEL_COLOR);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("MANCALA", x + (w - fm.stringWidth("MANCALA")) / 2, y + 20);
        g2.drawString(label, x + (w - fm.stringWidth(label)) / 2, y + 35);

        g2.setFont(getMancalaFont());
        g2.setColor(new Color(255, 220, 0));
        fm = g2.getFontMetrics();
        String count = String.valueOf(stones);
        g2.drawString(count, x + (w - fm.stringWidth(count)) / 2, y + h / 2 + 8);

        drawNeonStonesInMancala(g2, x, y, w, h, stones);
    }
    private void drawNeonStones(Graphics2D g2, int x, int y, int w, int h, int stones) {
        if (stones == 0) return;
        int cx = x + w / 2;
        int cy = y + h / 2;
        int r = 7;
        int[][] offsets = ClassicStyle.getStonePitOffsets(stones);
        for (int[] off : offsets) {
            int sx = cx + off[0];
            int sy = cy + off[1];
            g2.setColor(new Color(255, 80, 160, 60));
            g2.fillOval(sx - r - 2, sy - r - 2, (r + 2) * 2, (r + 2) * 2);

            g2.setColor(STONE_ACCENT);
            g2.fillOval(sx - r + 1, sy - r + 1, r * 2 - 2, r * 2 - 2);
            g2.setColor(STONE_COLOR);
            g2.fillOval(sx - r + 2, sy - r + 2, r * 2 - 4, r * 2 - 4);
        }
    }

    private void drawNeonStonesInMancala(Graphics2D g2, int x, int y, int w, int h, int count) {
        if (count == 0) return;
        int r = 5;
        int display = Math.min(count, 12);
        int cols = 3;
        int rows = (display + cols - 1) / cols;
        int startX = x + w / 2 - (cols * (r * 2 + 4)) / 2 + r;
        int startY = y + h / 2 + 14;
        int drawn = 0;
        for (int row = 0; row < rows && drawn < display; row++) {
            for (int col = 0; col < cols && drawn < display; col++) {
                int sx = startX + col * (r * 2 + 4);
                int sy = startY + row * (r * 2 + 4);
                g2.setColor(new Color(255, 80, 160, 50));
                g2.fillOval(sx - r - 1, sy - r - 1, (r + 1) * 2, (r + 1) * 2);
                g2.setColor(STONE_ACCENT);
                g2.fillOval(sx - r + 1, sy - r + 1, r * 2 - 2, r * 2 - 2);
                g2.setColor(STONE_COLOR);
                g2.fillOval(sx - r + 2, sy - r + 2, r * 2 - 4, r * 2 - 4);
                drawn++;
            }
        }
    }
}
