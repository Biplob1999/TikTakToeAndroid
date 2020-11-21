package com.example.tiktaktoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    /**
     * "state" array contains states of 0-X grid.
     * Each value in state array has 3 state:-
     *                                      (i) -1 indicates the position is empty
     *                                      (ii) 0 indicates the position has been occupied by '0'
     *                                      (iii) 1 indicates the position has been occupied by 'X'
     */
    private int[] state;

    /**
     * "count" variable counts the total number of place(0 or X) filled
     * When game stats, count = 0, it will be incremented post successful turn
     * Any one doesn't win yet but count == 9, then game has been drawn
     */
    private int count;

    /**
     * starting = {0, 1}, where:-
     *      0 ===> Player1 started with '0'
     *      1 ===> Player1 started with 'X'
     */
    private int starting = 0;   //Let say, player1 chooses '0' at starting

    /**
     * currTurn = {0, 1}, where:-
     *      0 ===> Turn for Player1
     *      1 ===> Turn for Player2
     */
    private int currTurn;

    /**
     * All the View objects that are needed
     */
    private TextView player1, player2, currTurnView;
    private ImageView box1, box2, box3, box4, box5, box6, box7, box8, box9;
    private ImageButton swapPlayer;

    private void initVars() {
        state = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1};
        count = 0;
        currTurn = starting;
        box1.setImageDrawable(null);
        box2.setImageDrawable(null);
        box3.setImageDrawable(null);
        box4.setImageDrawable(null);
        box5.setImageDrawable(null);
        box6.setImageDrawable(null);
        box7.setImageDrawable(null);
        box8.setImageDrawable(null);
        box9.setImageDrawable(null);
        swapPlayer.setEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    /**
     * All initialization related activity is done here
     */
    private void init() {
        player1 = findViewById(R.id.player1);
        player2 = findViewById(R.id.player2);
        box1 = findViewById(R.id.box1);
        box2 = findViewById(R.id.box2);
        box3 = findViewById(R.id.box3);
        box4 = findViewById(R.id.box4);
        box5 = findViewById(R.id.box5);
        box6 = findViewById(R.id.box6);
        box7 = findViewById(R.id.box7);
        box8 = findViewById(R.id.box8);
        box9 = findViewById(R.id.box9);
        currTurnView = findViewById(R.id.curr_turn);
        swapPlayer = findViewById(R.id.swapPlayer);
        initVars();
        initPlayer();
    }

    /**
     * Showing player's names in the TextBoxes is done here
     */
    private void initPlayer() {
        if (starting == 1) {
            //Player1 has chosen X in first turn.
            player1.setText("Player 1(X)");
            player2.setText("Player 2(0)");
            currTurnView.setText("Current Turn: Player 1");
        } else {
            //Player1 has chosen 0 in first turn.
            player1.setText("Player 1(0)");
            player2.setText("Player 2(X)");
            currTurnView.setText("Current Turn: Player 2");
        }
    }

    /**
     * onclick method in horizontal swap ImageButton. So it will trigger automatically whenever it will be clicked
     * The function is used to swap the two players
     * @param view - The ImageButton what has been clicked.
     */
    public void swapPlayer(View view) {
        if (count == 0) {
            //Game hasn't started yet, so players can be swap
            starting = 1 - starting;    //if starting = 0, then it will become 1, if it is 1, then it will become 0
            currTurn = starting;
            initPlayer();
        }
    }

    /**
     * onclick method in all the 9 ImageViews in grid. So it will trigger automatically whenever any of these will be clicked.
     * @param view - The ImageView in the grid which has been clicked
     */
    public void click(View view) {
        ImageView box = (ImageView) view;
        int pos = Integer.parseInt((String) box.getTag());

        if (state[pos] == -1) {
            //This position has not occupied yet or image in the ImageView in the position of the grid is empty.
            box.setTranslationY(-1000f);
            if (currTurn == 0) {
                //'0' should be fill
                state[pos] = 0;
                box.setImageResource(R.drawable.o);
            } else {
                //currTurn == 1
                //'X' should be fill
                state[pos] = 1;
                box.setImageResource(R.drawable.x);
            }
            box.animate().translationYBy(1000f).setDuration(100);
            count++;
            if (count == 1) {
                swapPlayer.setEnabled(false);
            }
            if (checkWin(pos)) {
                if (starting == currTurn) {
                    message("Congratulations! Player 1 wins");
                } else {
                    message("Congratulations! Player 2 wins");
                }
            }
            else if (count == 9) {
                message("Match has been drawn");
            }
            currTurn = 1 - currTurn;
            currTurnView.setText("Current Turn: Player " + (currTurn + 1));
        } else {
            Toast.makeText(this, "This cell/box is already occupied", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Used to display a message for the players after the match ending
     * @param s - A message of type String
     */
    private void message(String s) {
        new AlertDialog.Builder(this)
                .setMessage(s)
                .setPositiveButton("New Game", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initPlayer();
                        initVars();
                    }
                })
                .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    /**
     * @param pos - Position of the box(ImageView) in the grid which has been clicked as a valid turn by a player
     * @return - boolean true if the player has won the match, else boolean false
     */
    private boolean checkWin(int pos) {
        if (pos % 4 == 0 && state[0] == state[4] && state[4] == state[8]) {
            //principle diagonal checking
            return true;
        }
        if ((pos % 2 == 0 && pos % 8 != 0) && (state[2] == state[4] && state[4] == state[6])) {
            //center box clicked and non principle diagonal checking
            return true;
        }
        int col = pos % 3;
        int col0Pos = pos - col;
        //Row checking
        if (state[col0Pos] == state[col0Pos + 1] && state[col0Pos] == state[col0Pos + 2]) {
            return true;
        }
        //column checking
        if (state[(9 + pos - 3) % 9] == state[pos] && state[pos] == state[(pos + 3) % 9]) {
            return true;
        }
        //if all checking fails
        return false;
    }
}
