package br.ufpe.cin.rssv1;

import android.app.Activity;
import android.os.Bundle;
import br.ufpe.cin.rssv1.MainActivity.PrefsFragment;

public class SetPreferenceActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content,
				new PrefsFragment()).commit();
	}

}	