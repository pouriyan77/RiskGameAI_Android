package ir.ac.sbu.riskai;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity implements
        TroopsPickerDialog.OnAttackSelectedListener, WarDialogFragment.OnAttackEndListener
{
    private enum Mode
    {INCREASE_MODE, ATTACK_MODE}

    private GridLayout mapGrid;
    private TextView gameStatusTextView;
    private TextView turnsTextView;
    private TextView numberOfIncreaseTextView;
    private Button endAttackButton;

    private int rows;
    private int cols;
    private int realPlayers;
    private int aiPlayers;
    private TerritoryModel[][] territories;
    private View[][] territorieViews;
    private int[] colors;
    private List<PlayerModel> playerList;
    private List<Integer> turns;
    private int turn;
    private boolean isFirst = true;
    private boolean endGame = false;
    private boolean isFirstAttack;
    private Mode mode = Mode.INCREASE_MODE;
    private TerritoryModel attackerTerritory = null;
    private TerritoryModel defenderTerritory = null;
    private List<TerritoryModel> neighbors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        findViews();
        setupColors();
        configure();
        setupPlayers();
        setupRegions();
        setupGridView();
        turn = -1;
        nextTurn();
    }

    private void findViews()
    {
        mapGrid = findViewById(R.id.mapGrid);
        gameStatusTextView = findViewById(R.id.gameStatusTextView);
        turnsTextView = findViewById(R.id.turnTextView);
        numberOfIncreaseTextView = findViewById(R.id.numberOfIncreaseTextView);
        endAttackButton = findViewById(R.id.endAttackButton);
        endAttackButton.setOnClickListener(v -> {
            clearAttacker();
            neighbors.clear();
            attackerTerritory = null;
            nextTurn();
        });
    }

    private void setupPlayers()
    {
        playerList = new ArrayList<>();
        for (int i = 0 ; i < realPlayers ; i++)
        {
            PlayerModel player = new PlayerModel();
            player.setPlayerMode(false);
            player.setPlayerNumber(i + 1);
            playerList.add(player);
        }
        for (int i = 0 ; i < aiPlayers ; i++)
        {
            PlayerModel player = new AiPlayer(territories);
            player.setPlayerMode(true);
            player.setPlayerNumber(realPlayers + i + 1);
            playerList.add(player);
        }


        turns = new ArrayList<>();
        for (int i = 0 ; i < realPlayers + aiPlayers ; i++)
        {
            turns.add(i);
        }
        Collections.shuffle(turns);

//        turn = 0;
//        PlayerModel player = playerList.get(turns.get(turn));
//        turnsTextView.setText(player.getName());
//        turnsTextView.setBackgroundColor(colors[player.getPlayerNumber() - 1]);
//        gameStatusTextView.setText("وضعیت : افزایش نیرو");
    }

    private void setupColors()
    {
        colors = new int[20];
        colors[0] = getResources().getColor(R.color.colorPlayer1);
        colors[1] = getResources().getColor(R.color.colorPlayer2);
        colors[2] = getResources().getColor(R.color.colorPlayer3);
        colors[3] = getResources().getColor(R.color.colorPlayer4);
        colors[4] = getResources().getColor(R.color.colorPlayer5);
        colors[5] = getResources().getColor(R.color.colorPlayer6);
        colors[6] = getResources().getColor(R.color.colorPlayer7);
        colors[7] = getResources().getColor(R.color.colorPlayer8);
        colors[8] = getResources().getColor(R.color.colorPlayer9);
        colors[9] = getResources().getColor(R.color.colorPlayer10);
        colors[10] = getResources().getColor(R.color.colorPlayer11);
        colors[11] = getResources().getColor(R.color.colorPlayer12);
        colors[12] = getResources().getColor(R.color.colorPlayer13);
        colors[13] = getResources().getColor(R.color.colorPlayer14);
        colors[14] = getResources().getColor(R.color.colorPlayer15);
        colors[15] = getResources().getColor(R.color.colorPlayer16);
        colors[16] = getResources().getColor(R.color.colorPlayer17);
        colors[17] = getResources().getColor(R.color.colorPlayer18);
        colors[18] = getResources().getColor(R.color.colorPlayer19);
        colors[19] = getResources().getColor(R.color.colorPlayer20);
    }

    private void setupGridView()
    {
        mapGrid.setColumnCount(cols);
        mapGrid.setRowCount(rows);
        for (int i = 0 ; i < rows ; i++)
        {
            for (int j = 0 ; j < cols ; j++)
            {
                View view = this.getLayoutInflater().inflate(R.layout.map_item, null);
                TextView mapItemID = view.findViewById(R.id.mapItemID);
                TextView mapItemButton = view.findViewById(R.id.mapItemButton);
                RelativeLayout mapItemRelativeLayout = view.findViewById(R.id.mapItemRelativeLayout);
                mapItemRelativeLayout.setBackgroundColor(colors[territories[i][j].getHostPlayer() - 1]);
                mapItemButton.setText(String.valueOf(territories[i][j].getTroops()));
                mapItemID.setText("#".concat(String.valueOf(territories[i][j].getId())));
                mapGrid.addView(view);
                territorieViews[i][j] = view;
                int finalI = i;
                int finalJ = j;
                view.setOnClickListener(v -> {
                    TerritoryModel territory = territories[finalI][finalJ];
                    int hostPlayerNumber = territory.getHostPlayer();
                    PlayerModel currentPlayer = playerList.get(turns.get(turn));
                    if (hostPlayerNumber == currentPlayer.getPlayerNumber() &&
                            !currentPlayer.isPlayerMode())
                    {
                        if (mode == Mode.INCREASE_MODE && currentPlayer.getNumberOfIncreases() > 0)
                        {
                            currentPlayer.setNumberOfIncreases(
                                currentPlayer.getNumberOfIncreases() - 1);
                            territory.setTroops(territory.getTroops() + 1);
                            mapItemButton.setText(String.valueOf(territory.getTroops()));
                            numberOfIncreaseTextView.setText("نیروهای باقی مانده : ".concat(
                                    String.valueOf(currentPlayer.getNumberOfIncreases())));

                            if (isFirst)
                            {
                                if (turn == turns.size() - 1 &&
                                        currentPlayer.getNumberOfIncreases() == 0)
                                {
                                    isFirst = false;
                                    isFirstAttack = true;
                                }
                                nextTurn();
                            }
                            else
                            {
                                if (currentPlayer.getNumberOfIncreases() == 0)
                                {
                                    mode = Mode.ATTACK_MODE;
                                    gameStatusTextView.setText("وضعیت : حمله");
                                    endAttackButton.setVisibility(View.VISIBLE);
                                    numberOfIncreaseTextView.setVisibility(View.GONE);
                                }
                            }
                        }
                        else if (mode == Mode.ATTACK_MODE)
                        {
                            clearAttacker();

                            if (territory.getTroops() == 1 || attackerTerritory == territory)
                            {
                                attackerTerritory = null;
                                neighbors.clear();
                            }
                            else
                            {
                                fillNeighborsList(finalI, finalJ);
                            }
                        }
                    }
                    else if (!currentPlayer.isPlayerMode())
                    {
                        if (attackerTerritory != null)
                        {
                            if (neighbors.contains(territory))
                            {
                                defenderTerritory = territory;
                                TroopsPickerDialog troopsPickerDialog = new TroopsPickerDialog(
                                        this, attackerTerritory.getTroops() - 1);
                                troopsPickerDialog.show(getSupportFragmentManager(), null);
                            }
                            else
                            {
                                Toast.makeText(this, "به این خانه نمی توانید حمله کنید",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
//                    if (v.getAlpha() == 1)
//                    {
//                        v.setAlpha(0.7f);
//                    }
//                    else
//                    {
//                        v.setAlpha(1);
//                    }
                });
            }
        }
    }

    private void clearAttacker()
    {
        if (attackerTerritory != null)
        {
            View previousView = territorieViews
                    [attackerTerritory.getRow()][attackerTerritory.getCol()];
            ImageView rightImg = previousView.findViewById(R.id.rightImg);
            rightImg.setVisibility(View.GONE);
            ImageView rightUpImg = previousView.findViewById(R.id.rightUpImg);
            rightUpImg.setVisibility(View.GONE);
            ImageView rightDownImg = previousView.findViewById(R.id.rightDownImg);
            rightDownImg.setVisibility(View.GONE);
            ImageView leftImg = previousView.findViewById(R.id.leftImg);
            leftImg.setVisibility(View.GONE);
            ImageView leftUpImg = previousView.findViewById(R.id.leftUpImg);
            leftUpImg.setVisibility(View.GONE);
            ImageView leftDownImg = previousView.findViewById(R.id.leftDownImg);
            leftDownImg.setVisibility(View.GONE);
            ImageView downImg = previousView.findViewById(R.id.downImg);
            downImg.setVisibility(View.GONE);
            ImageView upImg = previousView.findViewById(R.id.upImg);
            upImg.setVisibility(View.GONE);
        }
    }

    private void fillNeighborsList(int row, int col)
    {
        neighbors.clear();
        TerritoryModel territory = territories[row][col];
        attackerTerritory = territory;
        View territoryView = territorieViews[row][col];

        if (isEnemy(row - 1, col - 1, territory.getHostPlayer()))
        {
            ImageView leftUpImg = territoryView.findViewById(R.id.leftUpImg);
            leftUpImg.setVisibility(View.VISIBLE);
            neighbors.add(territories[row - 1][col - 1]);
        }
        if (isEnemy(row - 1, col, territory.getHostPlayer()))
        {
            ImageView upImg = territoryView.findViewById(R.id.upImg);
            upImg.setVisibility(View.VISIBLE);
            neighbors.add(territories[row - 1][col]);
        }
        if (isEnemy(row - 1, col + 1, territory.getHostPlayer()))
        {
            ImageView rightUpImg = territoryView.findViewById(R.id.rightUpImg);
            rightUpImg.setVisibility(View.VISIBLE);
            neighbors.add(territories[row - 1][col + 1]);
        }
        if (isEnemy(row, col - 1, territory.getHostPlayer()))
        {
            ImageView leftImg = territoryView.findViewById(R.id.leftImg);
            leftImg.setVisibility(View.VISIBLE);
            neighbors.add(territories[row][col - 1]);
        }
        if (isEnemy(row, col + 1, territory.getHostPlayer()))
        {
            ImageView rightImg = territoryView.findViewById(R.id.rightImg);
            rightImg.setVisibility(View.VISIBLE);
            neighbors.add(territories[row][col + 1]);
        }
        if (isEnemy(row + 1, col - 1, territory.getHostPlayer()))
        {
            ImageView leftDownImg = territoryView.findViewById(R.id.leftDownImg);
            leftDownImg.setVisibility(View.VISIBLE);
            neighbors.add(territories[row + 1][col - 1]);
        }
        if (isEnemy(row + 1, col, territory.getHostPlayer()))
        {
            ImageView downImg = territoryView.findViewById(R.id.downImg);
            downImg.setVisibility(View.VISIBLE);
            neighbors.add(territories[row + 1][col]);
        }
        if (isEnemy(row + 1, col + 1, territory.getHostPlayer()))
        {
            ImageView rightDownImg = territoryView.findViewById(R.id.rightDownImg);
            rightDownImg.setVisibility(View.VISIBLE);
            neighbors.add(territories[row + 1][col + 1]);
        }
        if (neighbors.isEmpty())
        {
            attackerTerritory = null;
        }
    }

    private boolean isEnemy(int row, int col, int hostPlayer)
    {
        try
        {
            if (territories[row][col].getHostPlayer() != hostPlayer)
            {
                return true;
            }
        }catch (ArrayIndexOutOfBoundsException e)
        {
            return false;
        }
        return false;
    }

    private void configure()
    {
        rows = getIntent().getIntExtra(MainActivity.ROWS_KEY, 0);
        cols = getIntent().getIntExtra(MainActivity.COLS_KEY, 0);
        realPlayers = getIntent().getIntExtra(MainActivity.REAL_PLAYERS_KEY, 0);
        aiPlayers = getIntent().getIntExtra(MainActivity.AI_PLAYERS_KEY, 0);
        territories = new TerritoryModel[rows][cols];
        territorieViews = new View[rows][cols];
        for (int i = 0 ; i < rows ; i++)
        {
            for (int j = 0 ; j < cols ; j++)
            {
                territories[i][j] = new TerritoryModel(1,
                        0, i, j, i * cols + j + 1);
            }
        }
    }

    private void setupRegions()
    {
        int regionsForEveryPlayer = (rows * cols) / (realPlayers + aiPlayers);
        List<Integer> regions = new ArrayList<>(rows * cols);
        for(int i = 0 ; i < rows * cols ; i++)
        {
            regions.add(i);
        }
        Collections.shuffle(regions);

        for(int i = 0 ; i < regions.size() ; i++)
        {
            int region = regions.get(i);
            int row = region / cols;
            int col = region % cols;
            int playerNumber = (i / regionsForEveryPlayer) + 1;
            playerList.get(playerNumber - 1).addTerritory(territories[row][col]);
            territories[row][col].setHostPlayer(playerNumber);
        }

        for (int i = 0 ; i < playerList.size() ; i++)
        {
            PlayerModel player = playerList.get(i);
            player.setNumberOfIncreases(player.getTerritories().size());
        }

        PlayerModel player = playerList.get(turns.get(turn));
        numberOfIncreaseTextView.setText("نیروهای باقی مانده : ".concat(
                String.valueOf(player.getTerritories().size())));
    }

    private void nextTurn()
    {
        if (isFirstAttack)
        {
            mode = Mode.ATTACK_MODE;
            gameStatusTextView.setText("وضعیت : حمله");
            numberOfIncreaseTextView.setVisibility(View.GONE);
            endAttackButton.setVisibility(View.VISIBLE);
        }
        else
        {
            mode = Mode.INCREASE_MODE;
            gameStatusTextView.setText("وضعیت : افزایش نیرو");
            numberOfIncreaseTextView.setVisibility(View.VISIBLE);
            endAttackButton.setVisibility(View.GONE);
        }
        do
        {
            turn++;
            if (turn == turns.size())
            {
                turn = 0;
            }
        }while (!playerList.get(turns.get(turn)).isAlive());
        PlayerModel player = playerList.get(turns.get(turn));
        turnsTextView.setText(player.getName());
        turnsTextView.setBackgroundColor(colors[player.getPlayerNumber() - 1]);
        if (!isFirst)
        {
            if (isFirstAttack && turn == turns.size() - 1)
            {
                isFirstAttack = false;
            }
            else
            {
                player.setNumberOfIncreases(player.getTerritories().size());
            }
        }
        numberOfIncreaseTextView.setText("نیروهای باقی مانده : ".concat(
                String.valueOf(player.getNumberOfIncreases())));

        checkEndGame();

        if (isFirst && player instanceof AiPlayer && !endGame)
        {
            TerritoryModel territory = ((AiPlayer) player).deployFirstTime(true);
            View territoryView = territorieViews[territory.getRow()][territory.getCol()];
            TextView textView = territoryView.findViewById(R.id.mapItemButton);
            player.setNumberOfIncreases(player.getNumberOfIncreases() - 1);
            territory.setTroops(territory.getTroops() + 1);
            textView.setText(String.valueOf(territory.getTroops()));

            if (turn == turns.size() - 1 && player.getNumberOfIncreases() == 0)
            {
                isFirst = false;
                isFirstAttack = true;
            }
            nextTurn();
        }
        else if (player instanceof AiPlayer && !endGame && mode == Mode.ATTACK_MODE)
        {
            aiAttack((AiPlayer) player);
        }
        else if (player instanceof AiPlayer && !endGame && mode == Mode.INCREASE_MODE)
        {
            List<TerritoryModel> deployTerritories = ((AiPlayer) player).deploy(player.getNumberOfIncreases());
            for (TerritoryModel territory : deployTerritories)
            {
                View territoryView = territorieViews[territory.getRow()][territory.getCol()];
                TextView textView = territoryView.findViewById(R.id.mapItemButton);
                player.setNumberOfIncreases(player.getNumberOfIncreases() - 1);
                territory.setTroops(territory.getTroops() + 1);
                textView.setText(String.valueOf(territory.getTroops()));
            }

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                aiAttack((AiPlayer) player);
            }, 3000);

        }

    }
    private void aiAttack(AiPlayer player)
    {
        AiPlayer.AttackProperties attackProperties = player.attack();
        if(attackProperties != null)
        {
            attackerTerritory = attackProperties.getAttackerTerritory();
            defenderTerritory = attackProperties.getDefenderTerritory();
            onAttackSelected(attackProperties.getAttackTroops());
        }else
        {
            nextTurn();
        }
    }
    private void checkEndGame()
    {
        int alives = 0;
        for (PlayerModel player1 : playerList)
        {
            alives = player1.isAlive() ? alives + 1 : alives;
        }
        if (alives == 1)
        {
            endGame = true;
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(playerList.get(turns.get(turn)).getName().concat(" برنده شد. "))
                    .setCancelable(false)
                    .setPositiveButton("متوجه شدم", (dialog1, which) -> {
                        finish();
                    })
                    .create();
            dialog.show();
        }
    }

    @Override
    public void onAttackSelected(int count)
    {
        View view = territorieViews[attackerTerritory.getRow()][attackerTerritory.getCol()];
        TextView textView = view.findViewById(R.id.mapItemButton);
        attackerTerritory.setTroops(attackerTerritory.getTroops() - count);
        textView.setText(String.valueOf(attackerTerritory.getTroops()));
        WarDialogFragment warDialogFragment = new WarDialogFragment(
                playerList.get(attackerTerritory.getHostPlayer() - 1),
                playerList.get(defenderTerritory.getHostPlayer() - 1),
                defenderTerritory, count, colors[attackerTerritory.getHostPlayer() - 1],
                colors[defenderTerritory.getHostPlayer() - 1], this,
                attackerTerritory.getId());
        warDialogFragment.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onAttackEnd()
    {
        View view = territorieViews[defenderTerritory.getRow()][defenderTerritory.getCol()];
        TextView textView = view.findViewById(R.id.mapItemButton);
        RelativeLayout mapItemRelativeLayout = view.findViewById(R.id.mapItemRelativeLayout);
        mapItemRelativeLayout.setBackgroundColor(colors[defenderTerritory.getHostPlayer() - 1]);
        textView.setText(String.valueOf(defenderTerritory.getTroops()));
//        textView.setBackgroundColor(colors[defenderTerritory.getHostPlayer() - 1]);
        clearAttacker();
        attackerTerritory = null;
        neighbors.clear();
        if (playerList.get(turns.get(turn)) instanceof AiPlayer)
        {
            aiAttack((AiPlayer) playerList.get(turns.get(turn)));
        }
    }
//    private int dpToPx(int dp) {
//        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
//        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
//    }
}
