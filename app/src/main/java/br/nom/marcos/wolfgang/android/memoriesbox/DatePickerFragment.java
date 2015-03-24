package br.nom.marcos.wolfgang.android.memoriesbox;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import java.util.Calendar;

/**
 * Created by Wolfgang Marcos on 23/03/2015.
 */
public class DatePickerFragment extends DialogFragment{

  DatePickerDialog.OnDateSetListener listener;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    this.listener = (DatePickerDialog.OnDateSetListener) activity;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);

    return new DatePickerDialog(getActivity(), listener, year, month, day);
  }
}
