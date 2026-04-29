import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MancalaModel {

    public static final int PITS_PER_SIDE = 6;
    public static final int TOTAL_PITS = 14;
    public static final int MANCALA_A = 6;
    public static final int MANCALA_B = 13;

    private int[] board;
    private int[] previousBoard;
    private int previousPlayer;
    private int previousUndoCount;

    private int currentPlayer;
    private boolean gameStarted;
    private boolean gameOver;
    private int undoCountThisTurn;
    private boolean canUndo;

    private ArrayList<ChangeListener> listeners;

    public MancalaModel() {
        board = new int[TOTAL_PITS];
        previousBoard = new int[TOTAL_PITS];
        listeners = new ArrayList<>();
        resetState();
    }

    private void resetState() {
        currentPlayer = 0;
        gameStarted = false;
        gameOver = false;
        undoCountThisTurn = 0;
        canUndo = false;
        previousPlayer = 0;
        previousUndoCount = 0;
    }

    public void addChangeListener(ChangeListener l) {
        listeners.add(l);
    }

    private void notifyListeners() {
        ChangeEvent e = new ChangeEvent(this);
        for (ChangeListener l : listeners) l.stateChanged(e);
    }

    public void initBoard(int stonesPerPit) {
        for (int i = 0; i < TOTAL_PITS; i++)
            board[i] = (i == MANCALA_A || i == MANCALA_B) ? 0 : stonesPerPit;
        resetState();
        gameStarted = true;
        notifyListeners();
    }

    public boolean makeMove(int pitIndex) {
        if (!gameStarted || gameOver)
            return false;
        if (!isOwnPit(pitIndex, currentPlayer))
            return false;
        if (board[pitIndex] == 0)
            return false;

        System.arraycopy(board, 0, previousBoard, 0, TOTAL_PITS);
        previousPlayer = currentPlayer;
        previousUndoCount = undoCountThisTurn;

        int stones = board[pitIndex];
        board[pitIndex] = 0;

        int myMancala = (currentPlayer == 0) ? MANCALA_A : MANCALA_B;
        int oppMancala = (currentPlayer == 0) ? MANCALA_B : MANCALA_A;

        int pos = pitIndex;
        while (stones > 0) {
            pos = (pos + 1) % TOTAL_PITS;
            if (pos == oppMancala) continue;
            board[pos]++;
            stones--;
        }

        boolean freeTurn = (pos == myMancala);

        if (!freeTurn && isOwnPit(pos, currentPlayer) && board[pos] == 1) {
            int opp = opposite(pos);
            if (board[opp] > 0) {
                board[myMancala] += board[opp] + 1;
                board[opp] = 0;
                board[pos] = 0;
            }
        }
        if (checkGameOver()) {
            gameOver = true;
            notifyListeners();
            return true;
        }
        if (!freeTurn) {
            currentPlayer = 1 - currentPlayer;
            undoCountThisTurn = 0;
        }
        canUndo = true;
        notifyListeners();
        return true;
    }

    public boolean undo() {
        if (!canUndo || undoCountThisTurn >= 3)
            return false;
        System.arraycopy(previousBoard, 0, board, 0, TOTAL_PITS);
        currentPlayer = previousPlayer;
        undoCountThisTurn = previousUndoCount + 1;
        canUndo = false;
        notifyListeners();
        return true;
    }

    private boolean isOwnPit(int index, int player) {
        if (player == 0)
            return index >= 0 && index < MANCALA_A;
        else
            return index > MANCALA_A && index < MANCALA_B;
    }


    private int opposite(int index) {
        return 12 - index;
    }
    private boolean checkGameOver() {
        boolean aSideEmpty = true;
        for (int i = 0; i < MANCALA_A; i++)
            if (board[i] > 0) {
                aSideEmpty = false;
                break;
            }
        boolean bSideEmpty = true;
        for (int i = MANCALA_A + 1; i < MANCALA_B; i++)
            if (board[i] > 0) {
                bSideEmpty = false;
                break;
            }

        if (aSideEmpty) {
            for (int i = MANCALA_A + 1; i < MANCALA_B; i++) {
                board[MANCALA_B] += board[i]; board[i] = 0;
            }
            return true;
        }
        if (bSideEmpty) {
            for (int i = 0; i < MANCALA_A; i++) {
                board[MANCALA_A] += board[i]; board[i] = 0;
            }
            return true;
        }
        return false;
    }
    public int[] getBoard(){
        return board;
    }
    public int getCurrentPlayer(){
        return currentPlayer;
    }
    public boolean isGameStarted(){
        return gameStarted;
    }
    public boolean isGameOver(){
        return gameOver;
    }
    public boolean canUndo(){
        return canUndo && undoCountThisTurn < 3;
    }
    public int getUndoCountThisTurn() {
        return undoCountThisTurn;
    }

    public int getWinner() {
        if (!gameOver) return -1;
        if (board[MANCALA_A] > board[MANCALA_B]) return 0;
        if (board[MANCALA_B] > board[MANCALA_A]) return 1;
        return 2;
    }
}
