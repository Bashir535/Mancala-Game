import java.awt.*;


public interface BoardStyle {

    String getStyleName();

    Color getBoardColor();

    Color getPitColor();

    Color getPitBorderColor();

    Color getMancalaColor();

    Color getStoneColor();

    Color getStoneAccentColor();

    Color getLabelColor();

    Font getLabelFont();

    Font getMancalaFont();

    void drawPit(Graphics2D g2, int x, int y, int w, int h,
                 int stones, String label,
                 boolean isCurrentPlayerPit, boolean isHovered);

    void drawMancala(Graphics2D g2, int x, int y, int w, int h,
                     int stones, String label);
}
