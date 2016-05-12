package org.nibiru.mobile.ios.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Provider;

import org.nibiru.mobile.core.api.async.Callback;
import org.nibiru.mobile.core.api.ui.AlertManager;

import com.google.common.base.Strings;

import ios.uikit.UIAlertAction;
import ios.uikit.UIAlertController;
import ios.uikit.UIApplication;
import ios.uikit.UINavigationController;
import ios.uikit.UITextView;
import ios.uikit.enums.UIAlertActionStyle;

// TODO: Internationalization.
public class UIAlertControllerAlertManager implements AlertManager {
	private final Provider<UINavigationController> navigationControllerProvider;

	@Inject
	public UIAlertControllerAlertManager(Provider<UINavigationController> navigationControllerProvider) {
		this.navigationControllerProvider = checkNotNull(navigationControllerProvider);
	}

	@Override
	public void showMessage(String message) {
		checkNotNull(message);
		show(messageAlertController(null, message));
	}

	@Override
	public void showNotification(String message) {
		showMessage(message);
	}

	@Override
	public void showException(Exception exception) {
		checkNotNull(exception);
		show(messageAlertController(exception.getClass().getName(), exception.getMessage()));
	}

	@Override
	public void prompt(String title, String message, final Callback<String> callback) {
		UIAlertController alertController = alertController(title, message);
		final UITextView textView = UITextView.alloc().init();
		alertController.view().addSubview(textView);
		alertController.addAction(UIAlertAction.actionWithTitleStyleHandler("Ok", UIAlertActionStyle.Default, (UIAlertAction action) -> {
			close();
			callback.onSuccess(Strings.nullToEmpty(textView.text()));
		}));
		alertController.addAction(UIAlertAction.actionWithTitleStyleHandler("Cancel", UIAlertActionStyle.Cancel, (UIAlertAction action) -> {
			close();
		}));
		show(alertController);
	}

	@Override
	public void confirm(String title, String message, final Callback<Boolean> callback) {
		UIAlertController alertController = alertController(title, message);
		alertController.addAction(UIAlertAction.actionWithTitleStyleHandler("Ok", UIAlertActionStyle.Default, (UIAlertAction action) -> {
			close();
			callback.onSuccess(true);
		}));
		alertController.addAction(UIAlertAction.actionWithTitleStyleHandler("Cancel", UIAlertActionStyle.Cancel, (UIAlertAction action) -> {
			close();
			callback.onSuccess(false);
		}));
		show(alertController);
	}

	private UIAlertController messageAlertController(String title, String message) {
		UIAlertController alertController = alertController(title, message);
		alertController.addAction(UIAlertAction.actionWithTitleStyleHandler("Ok", UIAlertActionStyle.Default, (UIAlertAction action) -> {
			close();
		}));
		return alertController;
	}

	private UIAlertController alertController(String title, String message) {
		UIAlertController alertController = UIAlertController.alloc().init();
		alertController.setTitle(title);
		alertController.setMessage(message);
		return alertController;
	}

	private void show(UIAlertController alertController) {
		UIApplication.sharedApplication().keyWindow().rootViewController().presentViewControllerAnimatedCompletion(alertController, true, null);
		//navigationControllerProvider.get().presentViewController(alertController, true, null);
	}

	private void close() {
		navigationControllerProvider.get().popViewControllerAnimated(true);
	}
}
