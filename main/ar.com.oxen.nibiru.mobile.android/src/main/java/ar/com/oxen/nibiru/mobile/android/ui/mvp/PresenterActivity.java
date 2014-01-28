package ar.com.oxen.nibiru.mobile.android.ui.mvp;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContextScope;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import ar.com.oxen.nibiru.mobile.android.ui.place.IntentPlace;
import ar.com.oxen.nibiru.mobile.core.api.ui.mvp.Presenter;
import ar.com.oxen.nibiru.mobile.core.api.ui.mvp.PresenterMapper;
import ar.com.oxen.nibiru.mobile.core.api.ui.place.Place;

/**
 * An activity that delegates logic to a presenter.
 */
public class PresenterActivity extends RoboActivity {
	@Inject
	private PresenterMapper presenterMapper;

	@Inject
	protected ContextScope scope;

	private Presenter<?> presenter;
	private AndroidView view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		checkNotNull(savedInstanceState);
		super.onCreate(savedInstanceState);

		Place place = new IntentPlace(getIntent(), this);

		synchronized (ContextScope.class) {
			scope.enter(this);
			try {
				presenter = presenterMapper.getPresenter(place.getId());
			} finally {
				scope.exit(this);
			}
		}

		view = (AndroidView) presenter.getView();
		view.onCreate();
		setContentView(view.asNative());
		presenter.go(place);
	}

	@Override
	protected void onStop() {
		super.onStop();
		presenter.onStop();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return view.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return view.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return view.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		view.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return view.onContextItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		view.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		view.onPause();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		view.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		view.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
		view.onStart();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		view.onActivityResult(requestCode, resultCode, data);
	}
}
