import java.awt.*;

public class ClassicStyle implements BoardStyle {

    private static final Color BOARD_COLOR = new Color(101, 67, 33);
    private static final Color PIT_COLOR = new Color(210, 180, 140);
    private static final Color PIT_BORDER = new Color(70, 40, 10);
    private static final Color MANCALA_COLOR = new Color(185, 150, 100);
    private static final Color STONE_COLOR = new Color(40, 30, 20);
    private static final Color STONE_ACCENT = new Color(80, 60, 40);
    private static final Color LABEL_COLOR = new Color(255, 240, 200);
    private static final Color HOVER_COLOR = new Color(230, 200, 160);
    private static final Color ACTIVE_BORDER = new Color(255, 215, 0);

    @Override public String getStyleName() {
        return "Classic Wood";
    }
    @Override public Color getBoardColor() {
        return BOARD_COLOR;
    }
    @Override public Color getPitColor() {
        return PIT_COLOR;
    }
    @Override public Color getPitBorderColor() {
        return PIT_BORDER;
    }
    @Override public Color getMancalaColor() {
        return MANCALA_COLOR;
    }
    @Override public Color getStoneColor() {
        return STONE_COLOR;
    }
    @Override public Color getStoneAccentColor(){
        return STONE_ACCENT;
    }
    @Override public Color getLabelColor(){
        return LABEL_COLOR;
    }
    @Override public Font getLabelFont(){
        return new Font("Serif", Font.BOLD, 13);
    }
    @Override public Font getMancalaFont(){
        return new Font("Serif", Font.BOLD, 22);
    }
    @Override
    public void drawPit(Graphics2D g2, int x, int y, int w, int h, int stones, String label, boolean isCurrentPlayerPit, boolean isHovered) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color fill = isHovered && isCurrentPlayerPit ? HOVER_COLOR : PIT_COLOR;
        g2.setColor(fill);
        g2.fillOval(x + 4, y + 4, w - 8, h - 8);

        if (isCurrentPlayerPit) {
            g2.setColor(ACTIVE_BORDER);
            g2.setStroke(new BasicStroke(2.5f));
        } else {
            g2.setColor(PIT_BORDER);
            g2.setStroke(new BasicStroke(1.5f));
        }
        g2.drawOval(x + 4, y + 4, w - 8, h - 8);
        g2.setStroke(new BasicStroke(1f));
        drawStonesInPit(g2, x, y, w, h, stones);

        g2.setFont(getLabelFont());
        g2.setColor(LABEL_COLOR);
        FontMetrics fm = g2.getFontMetrics();
        int lx = x + (w - fm.stringWidth(label)) / 2;
        g2.drawString(label, lx, y + h + 14);
    }

    @Override
    public void drawMancala(Graphics2D g2, int x, int y, int w, int h, int stones, String label) {

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(MANCALA_COLOR);
        g2.fillRoundRect(x + 4, y + 4, w - 8, h - 8, 30, 30);
        g2.setColor(PIT_BORDER);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(x + 4, y + 4, w - 8, h - 8, 30, 30);
        g2.setStroke(new BasicStroke(1f));

        g2.setFont(new Font("Serif", Font.BOLD, 15));
        g2.setColor(LABEL_COLOR);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("Mancala", x + (w - fm.stringWidth("Mancala")) / 2, y + 22);
        g2.drawString(label, x + (w - fm.stringWidth(label)) / 2, y + 35);

        g2.setFont(getMancalaFont());
        g2.setColor(STONE_COLOR);
        fm = g2.getFontMetrics();
        String count = String.valueOf(stones);
        g2.drawString(count, x + (w - fm.stringWidth(count)) / 2, y + h / 2 - 3);

        drawStonesInMancala(g2, x, y, w, h, stones);
    }
    private void drawStonesInPit(Graphics2D g2, int x, int y, int w, int h, int stones) {
        if (stones == 0)
            return;
        int cx = x + w / 2;
        int cy = y + h / 2;
        int r = 7;
        int[][] offsets = getStonePitOffsets(stones);
        for (int[] off : offsets) {
            int sx = cx + off[0];
            int sy = cy + off[1];
            g2.setColor(STONE_ACCENT);
            g2.fillOval(sx - r + 1, sy - r + 1, r * 2, r * 2);
            g2.setColor(STONE_COLOR);
            g2.fillOval(sx - r, sy - r, r * 2 - 1, r * 2 - 1);
        }
    }

    private void drawStonesInMancala(Graphics2D g2, int x, int y, int w, int h, int count) {
        if (count == 0) return;
        int r = 5;
        int display = Math.min(count, 12);
        int cols = 3;
        int rows = (display + cols - 1) / cols;
        int startX = x + w / 2 - (cols * (r * 2 + 3)) / 2 + r;
        int startY = y + h / 2 + 5;
        int drawn = 0;
        for (int row = 0; row < rows && drawn < display; row++) {
            for (int col = 0; col < cols && drawn < display; col++) {
                int sx = startX + col * (r * 2 + 3);
                int sy = startY + row * (r * 2 + 3);
                g2.setColor(STONE_ACCENT);
                g2.fillOval(sx - r + 1, sy - r + 1, r * 2, r * 2);
                g2.setColor(STONE_COLOR);
                g2.fillOval(sx - r, sy - r, r * 2 - 1, r * 2 - 1);
                drawn++;
            }
        }
    }

    static int[][] getStonePitOffsets(int stones) {
        switch (stones) {
            case 1: return new int[][]{{0,0}};
            case 2: return new int[][]{{-8,0},{8,0}};
            case 3: return new int[][]{{0,-8},{-8,6},{8,6}};
            case 4: return new int[][]{{-8,-8},{8,-8},{-8,8},{8,8}};
            default: return new int[][]{{0,-10},{-9,-3},{9,-3},{-5,8},{5,8},{0,0}};
        }
    }
}
