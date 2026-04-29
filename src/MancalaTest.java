import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class MancalaTest {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new GameWindow().setVisible(true));
    }
}

class GameWindow extends JFrame {

    private final MancalaModel model;
    private final BoardStyle[] styles;
    private BoardStyle chosenStyle;

    private CardLayout cards;
    private JPanel mainPanel;
    private MancalaBoard boardPanel;
    private JLabel statusLabel;
    private JButton undoButton;

    GameWindow() {
        model  = new MancalaModel();
        styles = new BoardStyle[]{ new ClassicStyle(), new NeonStyle() };
        chosenStyle = styles[0];

        setTitle("Mancala");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        cards = new CardLayout();
        mainPanel = new JPanel(cards);
        mainPanel.add(buildSetupPanel(), "SETUP");
        mainPanel.add(buildGamePanel(),  "GAME");

        add(mainPanel);
        cards.show(mainPanel, "SETUP");
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel buildSetupPanel() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(new Color(22, 22, 42));
        root.setBorder(BorderFactory.createEmptyBorder(36, 50, 36, 50));

        JLabel title = new JLabel("MANCALA", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 48));
        title.setForeground(new Color(255, 210, 60));
        root.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(new Color(22, 22, 42));
        center.setBorder(BorderFactory.createEmptyBorder(24, 0, 24, 0));

        center.add(centeredLabel("Choose Board Style:", Color.WHITE, 16));
        center.add(Box.createVerticalStrut(12));

        JPanel styleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        styleRow.setBackground(new Color(22, 22, 42));
        ButtonGroup styleGroup = new ButtonGroup();
        JToggleButton[] styleBtns = new JToggleButton[styles.length];
        for (int i = 0; i < styles.length; i++) {
            final BoardStyle s = styles[i];
            JToggleButton btn = styledToggle(s.getStyleName(), 150, 42);
            if (i == 0) btn.setSelected(true);
            btn.addActionListener(e -> chosenStyle = s);
            styleGroup.add(btn);
            styleRow.add(btn);
            styleBtns[i] = btn;
        }
        center.add(styleRow);
        center.add(Box.createVerticalStrut(28));

        center.add(centeredLabel("Stones per pit:", Color.WHITE, 16));
        center.add(Box.createVerticalStrut(12));

        JPanel stoneRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        stoneRow.setBackground(new Color(22, 22, 42));
        ButtonGroup stoneGroup = new ButtonGroup();
        JToggleButton btn3 = styledToggle("3 Stones", 120, 42);
        JToggleButton btn4 = styledToggle("4 Stones", 120, 42);
        btn3.setSelected(true);
        stoneGroup.add(btn3); stoneGroup.add(btn4);
        stoneRow.add(btn3); stoneRow.add(btn4);
        center.add(stoneRow);
        center.add(Box.createVerticalStrut(32));

        JButton startBtn = new JButton("▶  Start Game");
        startBtn.setFont(new Font("SansSerif", Font.BOLD, 17));
        startBtn.setBackground(new Color(255, 210, 60));
        startBtn.setForeground(new Color(22, 22, 42));
        startBtn.setFocusPainted(false);
        startBtn.setBorderPainted(false);
        startBtn.setOpaque(true);
        startBtn.setPreferredSize(new Dimension(200, 50));
        startBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        startBtn.addActionListener(e -> {
            int stones = btn3.isSelected() ? 3 : 4;
            model.initBoard(stones);
            boardPanel.refreshStyle(chosenStyle);
            updateStatus();
            cards.show(mainPanel, "GAME");
            pack();
            setLocationRelativeTo(null);
        });
        JPanel startWrap = new JPanel(new FlowLayout(FlowLayout.CENTER));
        startWrap.setBackground(new Color(22, 22, 42));
        startWrap.add(startBtn);
        center.add(startWrap);

        root.add(center, BorderLayout.CENTER);

        JLabel info = new JLabel(
            "<html><center><font color='#9999bb'>" +
            "Two players, one mouse. Click a pit on your side to sow stones counter-clockwise.<br>" +
            "Land in your Mancala for a free turn.&nbsp; Land in your own empty pit to capture.<br>" +
            "Game ends when one side is cleared. Most stones wins!" +
            "</font></center></html>", SwingConstants.CENTER);
        info.setFont(new Font("SansSerif", Font.PLAIN, 12));
        root.add(info, BorderLayout.SOUTH);

        return root;
    }

    private JPanel buildGamePanel() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(new Color(15, 15, 30));

        boardPanel = new MancalaBoard(model, chosenStyle);
        root.add(boardPanel, BorderLayout.CENTER);

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 8));
        bar.setBackground(new Color(15, 15, 30));

        undoButton = new JButton("↩  Undo");
        undoButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        undoButton.setBackground(new Color(190, 60, 30));
        undoButton.setForeground(Color.WHITE);
        undoButton.setFocusPainted(false);
        undoButton.setBorderPainted(false);
        undoButton.setOpaque(true);
        undoButton.setPreferredSize(new Dimension(110, 36));
        undoButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        undoButton.addActionListener(e -> {
            if (!model.undo()) {
                String reason = !model.canUndo() ? "No move to undo, or already undone this step." : "You have used all 3 undos for this turn.";JOptionPane.showMessageDialog(this, reason, "Undo Not Allowed", JOptionPane.WARNING_MESSAGE);
            }
        });

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 13));
        statusLabel.setForeground(new Color(170, 170, 210));
        statusLabel.setPreferredSize(new Dimension(280, 30));

        JButton newGame = new JButton("⟳  New Game");
        newGame.setFont(new Font("SansSerif", Font.BOLD, 14));
        newGame.setBackground(new Color(40, 110, 50));
        newGame.setForeground(Color.WHITE);
        newGame.setFocusPainted(false);
        newGame.setBorderPainted(false);
        newGame.setOpaque(true);
        newGame.setPreferredSize(new Dimension(130, 36));
        newGame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        newGame.addActionListener(e -> {
            cards.show(mainPanel, "SETUP");
            pack();
            setLocationRelativeTo(null);
        });

        bar.add(undoButton);
        bar.add(statusLabel);
        bar.add(newGame);
        root.add(bar, BorderLayout.SOUTH);

        model.addChangeListener(e -> {
            updateStatus();
            if (model.isGameOver())
                SwingUtilities.invokeLater(this::showGameOver);
        });

        return root;
    }

    private void updateStatus() {
        if (!model.isGameStarted() || model.isGameOver()) {
            statusLabel.setText("");
            return;
        }
        String player = (model.getCurrentPlayer() == 0) ? "Player A" : "Player B";
        int used = model.getUndoCountThisTurn();
        int left = 3 - used;
        String undoStr = model.canUndo() ? "  |  Undos left this turn: " + left : "  |  Make a move to unlock undo";
        statusLabel.setText(player + "'s turn" + undoStr);
    }

    private void showGameOver() {
        int winner = model.getWinner();
        int[] board  = model.getBoard();
        String result;
        if (winner == 2) {
            result = "It's a TIE!";
        } else {
            result = (winner == 0 ? "Player A" : "Player B") + " wins!";
        }
        String msg = result + "\n\nPlayer A Mancala: " + board[MancalaModel.MANCALA_A] + "\nPlayer B Mancala: " + board[MancalaModel.MANCALA_B];
        int choice = JOptionPane.showOptionDialog(this, msg, "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{"New Game", "Quit"}, "New Game");
        if (choice == JOptionPane.YES_OPTION) {
            cards.show(mainPanel, "SETUP");
            pack();
            setLocationRelativeTo(null);
        } else {
            System.exit(0);
        }
    }

    private JLabel centeredLabel(String text, Color color, int size) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, size));
        lbl.setForeground(color);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JToggleButton styledToggle(String text, int w, int h) {
        JToggleButton btn = new JToggleButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(w, h));
        btn.setBackground(new Color(55, 55, 85));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
