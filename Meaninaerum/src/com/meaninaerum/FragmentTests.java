package com.meaninaerum;

import java.util.Collections;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentTests extends Fragment {
	
	public static final String EXTRA_DICT_ID = "dict_id";
	
	private ArrayAdapter<Word> adapter;
	private Handler handler;
	private TextView tv_timer;
	private int timer;
	private int totalTestsCount;
	private int rightAnswers;
	private long dictId;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getActivity().getActionBar().setTitle(R.string.tests);
		View layout = inflater.inflate(R.layout.fragment_tests, null);
		ListView lv = (ListView) layout.findViewById(R.id.lv);
		tv_timer = (TextView) layout.findViewById(R.id.tv_timer);
		dictId = getArguments().getLong(EXTRA_DICT_ID);
		List<Word> words = DBHelper.getDBHelper(getActivity()).getWords(dictId);
		Collections.shuffle(words);
		totalTestsCount = words.size();
		adapter = new ArrayAdapter<Word>(getActivity(), android.R.layout.simple_list_item_1, words);
		lv.setAdapter(adapter);
		handler = new Handler();
		handler.postDelayed(timerTask, 1000);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Word word = adapter.getItem(position);
				DialogManager.showAnswerDialog(FragmentTests.this, word);
				adapter.remove(word);
				adapter.notifyDataSetChanged();
			}
		});
		return layout;
	}
	
	Runnable timerTask = new Runnable() {
		@Override
		public void run() {
			int minutes = timer / 60;
			int seconds = timer++ % 60;
			String appendix = (seconds < 10) ? "0" : "";
			tv_timer.setText(minutes + ":" + appendix + seconds);
			handler.postDelayed(this, 1000);
		}
	};
	
	public void handleAnswer(boolean right) {
		if (right) {
			rightAnswers++;
		}
		if (adapter.isEmpty()) {
			handler.removeCallbacks(timerTask);
			int score = (int) (10000 * ((float)rightAnswers / totalTestsCount) / (1 + timer));
			long userId = getArguments().getLong(ActivityMain.EXTRA_USER_ID);
			DBHelper.getDBHelper(getActivity()).addResultToStats(userId, dictId, score);
			DialogManager.showResultDialog((ActivityMain) getActivity(), score, dictId);
		}
	}

	@Override
	public void onDestroy() {
		handler.removeCallbacks(timerTask);
		super.onDestroy();
	}
	
}
