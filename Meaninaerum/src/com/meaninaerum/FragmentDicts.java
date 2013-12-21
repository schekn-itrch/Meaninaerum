package com.meaninaerum;

import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FragmentDicts extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getActivity().getActionBar().setTitle(R.string.dictionaries);
		View layout = inflater.inflate(R.layout.fragment_dicts, null);
		ListView lv = (ListView) layout.findViewById(R.id.lv);
		List<Dictionary> dictionaries = DBHelper.getDBHelper(getActivity()).getDictionaries();
		final ArrayAdapter<Dictionary> adapter = new ArrayAdapter<Dictionary>(getActivity(), android.R.layout.simple_list_item_1, dictionaries);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Bundle args = new Bundle();
				args.putLong(FragmentTests.EXTRA_DICT_ID, adapter.getItem(position).getId());
				args.putLong(ActivityMain.EXTRA_USER_ID, getArguments().getLong(ActivityMain.EXTRA_USER_ID));
				Fragment fragment = Fragment.instantiate(getActivity(), FragmentTests.class.getName(), args);
				getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fl_container, fragment).commit();
			}
		});
		return layout;
	}
}
