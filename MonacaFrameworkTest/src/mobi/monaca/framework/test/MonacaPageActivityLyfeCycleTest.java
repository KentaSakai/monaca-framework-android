package mobi.monaca.framework.test;

import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import mobi.monaca.framework.MonacaPageActivity;

public class MonacaPageActivityLyfeCycleTest extends ActivityUnitTestCase<MonacaPageActivity> {
	public MonacaPageActivityLyfeCycleTest() {
		super(MonacaPageActivity.class);
	}

	public void testNormalLyfeCycle() {
		// test whether not destroyed by unexpected causes
		startActivity(new Intent(), null, null);
		MonacaPageActivity pageActivity = getActivity();
		Instrumentation i = getInstrumentation();
		i.callActivityOnStart(pageActivity);
		i.callActivityOnResume(pageActivity);
		i.callActivityOnPause(pageActivity);
		i.callActivityOnStop(pageActivity);
		i.callActivityOnDestroy(pageActivity);
		pageActivity.finish();
	}

	public void testResumingLyfeCycle() {
		// test whether not destroyed by unexpected causes
		startActivity(new Intent(), null, null);
		MonacaPageActivity pageActivity = getActivity();
		Instrumentation i = getInstrumentation();
		i.callActivityOnStart(pageActivity);
		for (int  k = 0; k < 10; k++) {
			i.callActivityOnResume(pageActivity);
			i.callActivityOnPause(pageActivity);
		}
		i.callActivityOnStop(pageActivity);
		i.callActivityOnDestroy(pageActivity);
		pageActivity.finish();
	}

	public void testBackgroundLyfeCycle() {
		// test whether not destroyed by unexpected causes
		startActivity(new Intent(), null, null);
		MonacaPageActivity pageActivity = getActivity();
		Instrumentation i = getInstrumentation();
		for (int  k = 0; k < 10; k++) {
			i.callActivityOnStart(pageActivity);
			i.callActivityOnResume(pageActivity);
			i.callActivityOnUserLeaving(pageActivity);
			i.callActivityOnPause(pageActivity);
			i.callActivityOnStop(pageActivity);
			i.callActivityOnRestart(pageActivity);
		}

		i.callActivityOnStart(pageActivity);
		i.callActivityOnResume(pageActivity);
		i.callActivityOnPause(pageActivity);
		i.callActivityOnStop(pageActivity);
		i.callActivityOnDestroy(pageActivity);
		pageActivity.finish();
	}
}
