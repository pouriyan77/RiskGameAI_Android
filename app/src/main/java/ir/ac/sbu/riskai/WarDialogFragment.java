package ir.ac.sbu.riskai;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class WarDialogFragment extends DialogFragment
{
    private TextView attackerTextView;
    private TextView defenderTextView;
    private TextView attackerIdTextView;
    private TextView defenderIdTextView;
    private ImageView warImageView;

    private int attackerColor;
    private int defenderColor;
    private TerritoryModel defenderTerritory;
    private PlayerModel attacker;
    private PlayerModel defender;
    private int attackerTroops;
    private OnAttackEndListener onAttackEndListener;
    private int attackerId;

    public WarDialogFragment(PlayerModel attacker, PlayerModel defender,
                             TerritoryModel defenderTerritory,
                             int attackerTroops,
                             int attackerColor, int defenderColor,
                             OnAttackEndListener onAttackEndListener,
                             int attackerId)
    {
        this.attackerColor = attackerColor;
        this.defenderColor = defenderColor;
//        this.attackerTerritory = attackerTerritory;
        this.defenderTerritory = defenderTerritory;
        this.attacker = attacker;
        this.defender = defender;
        this.attackerTroops = attackerTroops;
        this.onAttackEndListener = onAttackEndListener;
        this.attackerId = attackerId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.war_dialog, container, false);
        attackerTextView = view.findViewById(R.id.attackerTextView);
        defenderTextView = view.findViewById(R.id.defenderTextView);
        attackerIdTextView = view.findViewById(R.id.attackerIdTextView);
        defenderIdTextView = view.findViewById(R.id.defenderIdTextView);
        attackerIdTextView.setText("#".concat(String.valueOf(attackerId)));
        defenderIdTextView.setText("#".concat(String.valueOf(defenderTerritory.getId())));
        warImageView = view.findViewById(R.id.warImageView);
        attackerTextView.setBackgroundColor(attackerColor);
        defenderTextView.setBackgroundColor(defenderColor);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(false);
        Handler handler = new Handler();
        attack(handler);
    }


    private void attack(Handler handler)
    {
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
//                double attackerChance = (double)attackerTroops
//                        / (double)(attackerTroops + defenderTerritory.getTroops());
                if (attackerTroops != 0 && defenderTerritory.getTroops() != 0)
                {
                    Random random = new Random();
                    int attackerDice = random.nextInt(6) + 1;
                    int defenderDice = random.nextInt(6) + 1;
                    attackerTextView.setText(String.valueOf(attackerDice));
                    defenderTextView.setText(String.valueOf(defenderDice));

                    if (attackerDice > defenderDice)
                    {
                        warImageView.setImageResource(R.drawable.arrow_left_black);
                        defenderTerritory.setTroops(defenderTerritory.getTroops() - 1);
                    }
                    else
                    {
                        warImageView.setImageResource(R.drawable.arrow_right_black);
                        attackerTroops--;
                    }
//                    double dice = Math.random();
//                    if (dice < attackerChance)
//                    {
//                        int attackerDice = (int)(Math.random() * 5 + 2);
//                        int defenderDice = (int)(Math.random() * (attackerDice - 1) + 1);
//                        attackerTextView.setText(String.valueOf(attackerDice));
//                        defenderTextView.setText(String.valueOf(defenderDice));
//                        warImageView.setImageResource(R.drawable.arrow_left_black);
//                        defenderTerritory.setTroops(defenderTerritory.getTroops() - 1);
//                    }
//                    else
//                    {
//                        int defenderDice = (int)(Math.random() * 6 + 1);
//                        int attackerDice = (int)(Math.random() * defenderDice + 1);
//                        attackerTextView.setText(String.valueOf(attackerDice));
//                        defenderTextView.setText(String.valueOf(defenderDice));
//                        warImageView.setImageResource(R.drawable.arrow_right_black);
//                        attackerTroops--;
//                    }
                    handler.postDelayed(this, 1500);
                }
                else
                {
                    if (defenderTerritory.getTroops() == 0)
                    {
                        defenderTerritory.setTroops(attackerTroops);
                        defenderTerritory.setHostPlayer(attacker.getPlayerNumber());
                        attacker.addTerritory(defenderTerritory);
                        defender.removeTerritory(defenderTerritory);
                    }
                    onAttackEndListener.onAttackEnd();
                    dismiss();
                    handler.removeCallbacksAndMessages(null);
                }
            }
        }, 0);

    }

    public interface OnAttackEndListener
    {
        void onAttackEnd();
    }
}
