package ir.ac.sbu.riskai;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class TroopsPickerDialog extends DialogFragment
{
    private SeekBar seekBar;
    private TextView countTextView;
    private TextView maxTextView;
    private Button attackButton;
    private int count;
    private int maxTroops;
    private OnAttackSelectedListener onAttackSelectedListener;

    public TroopsPickerDialog(OnAttackSelectedListener onAttackSelectedListener, int maxTroops)
    {
        this.count = 1;
        this.maxTroops = maxTroops;
        this.onAttackSelectedListener = onAttackSelectedListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.troops_picker_dialog, container, false);
        seekBar = view.findViewById(R.id.seekBar);
        countTextView = view.findViewById(R.id.countTextView);
        maxTextView = view.findViewById(R.id.maxTextView);
        attackButton = view.findViewById(R.id.attackButton);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        setCancelable(true);
        maxTextView.setText(String.valueOf(maxTroops));
        countTextView.setText("1");
        count = 1;
        seekBar.setMax(maxTroops - 1);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                countTextView.setText(String.valueOf(progress + 1));
                count = progress + 1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });
        attackButton.setOnClickListener(v ->{
            onAttackSelectedListener.onAttackSelected(count);
            dismiss();
        });
    }

    public interface OnAttackSelectedListener
    {
        void onAttackSelected(int count);
    }
}
