package ir.ac.sbu.riskai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    public static final String ROWS_KEY = "rk";
    public static final String COLS_KEY = "ck";
    public static final String REAL_PLAYERS_KEY = "real_k";
    public static final String AI_PLAYERS_KEY = "ai_k";

    private EditText rowsEditText;
    private EditText colsEditText;
    private EditText realPlayerEditText;
    private EditText aiPlayerEditText;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rowsEditText = findViewById(R.id.rowsEditText);
        colsEditText = findViewById(R.id.colsEditText);
        realPlayerEditText = findViewById(R.id.realPlayersEditText);
        aiPlayerEditText = findViewById(R.id.aiPlayersEditText);
        startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(view ->
        {
            if (colsEditText.getText().toString().trim().isEmpty() ||
                    rowsEditText.getText().toString().trim().isEmpty() ||
                    realPlayerEditText.getText().toString().trim().isEmpty() ||
                    aiPlayerEditText.getText().toString().trim().isEmpty())
            {
                Toast.makeText(this, "لطفا همه فیلدها را پر کنید"
                        , Toast.LENGTH_LONG).show();
            }

            else
            {
                int rows = Integer.valueOf(rowsEditText.getText().toString());
                int cols = Integer.valueOf(colsEditText.getText().toString());
                int realPlayers = Integer.valueOf(realPlayerEditText.getText().toString());
                int aiPlayers = Integer.valueOf(aiPlayerEditText.getText().toString());

                if (checkFields(rows, cols, realPlayers, aiPlayers))
                {
                    Intent intent = new Intent(this, GameActivity.class);

                    intent.putExtra(ROWS_KEY, rows);
                    intent.putExtra(COLS_KEY, cols);
                    intent.putExtra(REAL_PLAYERS_KEY, realPlayers);
                    intent.putExtra(AI_PLAYERS_KEY, aiPlayers);

                    startActivity(intent);
                }
            }
        });

    }

    private boolean checkFields(int rows, int cols, int realPlayers, int aiPlayers)
    {
        if ((rows * cols) % (realPlayers + aiPlayers) != 0)
        {
            Toast.makeText(this, "باید تعداد خانه ها بر تعداد بازیکنان بخش پذیر باشد"
                    , Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
