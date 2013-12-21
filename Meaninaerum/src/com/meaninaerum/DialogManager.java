package com.meaninaerum;

import java.util.Random;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class DialogManager {

	public static void showErrorDialog(Context context, String message) {
		AlertDialog dialog = new AlertDialog.Builder(context)
		.setTitle(R.string.error)
		.setMessage(message)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).create();
		dialog.show();
	}
	
	public static void showAnswerDialog(final FragmentTests fragmentTests, Word word) {
		final boolean swap = new Random().nextBoolean();
		View layout = ((LayoutInflater) fragmentTests.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_answer, null);
		final Button btn_option_a = (Button) layout.findViewById(R.id.btn_option_a);
		final Button btn_option_b = (Button) layout.findViewById(R.id.btn_option_b);
		if (swap) {
			btn_option_a.setText(word.getOptionWrong());
			btn_option_b.setText(word.getOptionRight());
		} else {
			btn_option_a.setText(word.getOptionRight());
			btn_option_b.setText(word.getOptionWrong());
		}
		final AlertDialog dialog = new AlertDialog.Builder(fragmentTests.getActivity())
		.setTitle(word.getName())
		.setView(layout)
		.setCancelable(false)
		.create();
		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean right = (v.getId() == btn_option_a.getId() && !swap) || (v.getId() == btn_option_b.getId() && swap);
				int color = right ? Color.GREEN : Color.RED;
				v.setBackgroundColor(color);
				fragmentTests.handleAnswer(right);
				v.postDelayed(new Runnable() {
					@Override
					public void run() {
						dialog.dismiss();
					}
				}, 500);
			}
		};
		btn_option_a.setOnClickListener(onClickListener);
		btn_option_b.setOnClickListener(onClickListener);
		dialog.show();
	}
	
	public static void showResultDialog(final ActivityMain activity, int score, final long dictId) {
		AlertDialog dialog = new AlertDialog.Builder(activity)
		.setMessage(activity.getString(R.string.your_score) + " " + score)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				activity.getFragmentManager().popBackStackImmediate();
				dialog.dismiss();
				showLeaderboardDialog(activity, dictId);
			}
		}).create();
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.show();
	}
	
	private static void showLeaderboardDialog(Context context, long dictId) {
		String leaders = DBHelper.getDBHelper(context).getLeaders(dictId);
		AlertDialog dialog = new AlertDialog.Builder(context)
		.setTitle(R.string.leaderboard)
		.setMessage(leaders)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).create();
		dialog.show();
	}
	
	public static void showStatsDialog(Context context, long userId) {
		String stats = DBHelper.getDBHelper(context).getStats(userId);
		AlertDialog dialog = new AlertDialog.Builder(context)
		.setTitle(R.string.statistics)
		.setMessage(stats)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).create();
		dialog.show();
	}

}
