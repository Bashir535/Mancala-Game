import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * MancalaModel class to store game data
 */
public class MancalaModel {
    public static final int PITS_PER_SIDE = 6;
    public static final int TOTAL_PITS = 14;
    public static final int MANCALA_A = 6;
    public static final int MANCALA_B = 13;

    private int[] board;
    private int[] previousBoard;
    private int previousPlayer;

    private int currentPlayer;
    private boolean gameStarted;
    private boolean gameOver;
    private int[] undoCountThisTurn;
    private boolean canUndo;

    private ArrayList<ChangeListener> listeners;

    /**
     * Default constructor
     */
    public MancalaModel() {
        board = new int[TOTAL_PITS];
        previousBoard = new int[TOTAL_PITS];
        listeners = new ArrayList<>();
        undoCountThisTurn = new int[2];
        resetState();
    }

    /**
     * Resets the model's data
     */
    private void resetState() {
        currentPlayer = 0;
        gameStarted = false;
        gameOver = false;
        undoCountThisTurn = new int[2];
        canUndo = false;
        previousPlayer = 0;
    }

    /**
     * Adds a viewer to the model
     * @param l ChangeListener viewer
     */
    public void addChangeListener(ChangeListener l) {
        listeners.add(l);
    }

    /**
     * Notifies all viewers in listeners of a change
     */
    private void notifyListeners() {
        ChangeEvent e = new ChangeEvent(this);
        for (ChangeListener l : listeners) l.stateChanged(e);
    }

    /**
     * Initiates pits with given stones per pit
     * @param stonesPerPit stones per pit
     */
    public void initBoard(int stonesPerPit) {
        for (int i = 0; i < TOTAL_PITS; i++)
            board[i] = (i == MANCALA_A || i == MANCALA_B) ? 0 : stonesPerPit;
        resetState();
        gameStarted = true;
        notifyListeners();
    }

    /**
     * Moves the stones of a given pit to the next pit
     * @param pitIndex location of pit
     * @return true if move is valid, false otherwise
     */
    public boolean makeMove(int pitIndex) {
        if (!gameStarted || gameOver)
            return false;
        if (!isOwnPit(pitIndex, currentPlayer))
            return false;
        if (board[pitIndex] == 0)
            return false;

        System.arraycopy(board, 0, previousBoard, 0, TOTAL_PITS);
        previousPlayer = currentPlayer;

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
            undoCountThisTurn[currentPlayer] = 0;
        }
        canUndo = true;
        notifyListeners();
        return true;
    }

    /**
     * undo function to undo a player's move
     * @return true if undo was done, false otherwise
     */
    public boolean undo() {
        if (!canUndo || undoCountThisTurn[previousPlayer] >= 3)
            return false;
        System.arraycopy(previousBoard, 0, board, 0, TOTAL_PITS);
        currentPlayer = previousPlayer;
        undoCountThisTurn[currentPlayer]++;
        canUndo = false;
        notifyListeners();
        return true;
    }

    /**
     * checks if a pit is either player A or player B
     * @param index index of pit
     * @param player either player A or player B
     * @return true if it is the player's pit, false otherwise
     */
    private boolean isOwnPit(int index, int player) {
        if (player == 0)
            return index >= 0 && index < MANCALA_A;
        else
            return index > MANCALA_A && index < MANCALA_B;
    }


    /**
     * gives the index of a pit on the opposite side
     * @param index of pit
     * @return index of opposite pit
     */
    private int opposite(int index) {
        return 12 - index;
    }

    /**
     * checks if a player has won
     * @return true if a player won, false otherwise
     */
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

    /**
     * gets current board
     * @return int[] of board
     */
    public int[] getBoard(){
        return board;
    }

    /**
     * gets current player
     * @return int of player
     */
    public int getCurrentPlayer(){
        return currentPlayer;
    }

    /**
     * gets if game started
     * @return boolean true if game started, false otherwise
     */
    public boolean isGameStarted(){
        return gameStarted;
    }

    /**
     * gets if game is over
     * @return boolean true if game is over, false otherwise
     */
    public boolean isGameOver(){
        return gameOver;
    }

    /**
     * gets if player can undo
     * @return boolean true if can undo, false otherwise
     */
    public boolean canUndo(){
        return canUndo && undoCountThisTurn[previousPlayer] < 3;
    }

    /**
     * gets undo count
     * @return int count of undos done
     */
    public int getUndoCountThisTurn() {
        return undoCountThisTurn[previousPlayer];
    }

    /**
     * gets winner of mancala
     * @return int the player that won
     */
    public int getWinner() {
        if (!gameOver) return -1;
        if (board[MANCALA_A] > board[MANCALA_B]) return 0;
        if (board[MANCALA_B] > board[MANCALA_A]) return 1;
        return 2;
    }
}
