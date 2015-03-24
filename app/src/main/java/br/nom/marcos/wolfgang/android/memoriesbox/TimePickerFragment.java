package br.nom.marcos.wolfgang.android.memoriesbox;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;

import java.util.Calendar;

/**
 * Created by Wolfgang Marcos on 24/03/2015.
 */
public class TimePickerFragment extends DialogFragment{

  TimePickerDialog.OnTimeSetListener listener;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    this.listener = (TimePickerDialog.OnTimeSetListener) activity;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Calendar calendar = Calendar.getInstance();
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);

    return new TimePickerDialog(getActivity(), listener, hour, minute, true);
  }
}
