package com.googlecode.kanbanik.client.components;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Widget;

public class PanelContainingDialog extends DialogBox implements Closable,
		NativePreviewHandler {

	interface MyUiBinder extends UiBinder<Widget, PanelContainingDialog> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private List<PanelContainingDialolgListener> listeners;

	private final FocusWidget focusWidget;

	@UiField
	Button cancelButton;

	@UiField
	Button okButton;

	@UiField
	FlowPanel contentWrapper;

	private HandlerRegistration registration;
	
	public PanelContainingDialog(String title, Widget contentPanel) {
		this(title, contentPanel, null);
	}

	public PanelContainingDialog(String title, Widget contentPanel,
			FocusWidget focusWidget) {
		super();

		setWidget(uiBinder.createAndBindUi(this));

		this.focusWidget = focusWidget;

		contentWrapper.add(contentPanel);

		setupButtons();

		setText(title);
		setAnimationEnabled(true);
		setGlassEnabled(true);

	}

	private void setupButtons() {
		okButton.addClickHandler(new OKButtonHandler());
		okButton.setText(" OK ");

		cancelButton.addClickHandler(new CancelButtonHandler());
		cancelButton.setText(" Cancel ");
	}

	public void addListener(PanelContainingDialolgListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<PanelContainingDialog.PanelContainingDialolgListener>();
		}

		listeners.add(listener);
	}

	class CancelButtonHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			performCancelAction();

		}
	}

	class OKButtonHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			performOkAction();
		}

	}

	private void performCancelAction() {
		hide();

		if (listeners == null) {
			return;
		}

		for (PanelContainingDialolgListener listener : listeners) {
			listener.cancelClicked(PanelContainingDialog.this);
		}
	}
	
	private void performOkAction() {
		if (listeners == null) {
			return;
		}

		for (PanelContainingDialolgListener listener : listeners) {
			listener.okClicked(PanelContainingDialog.this);
		}
	}
	
	public static interface PanelContainingDialolgListener {

		void okClicked(PanelContainingDialog dialog);

		void cancelClicked(PanelContainingDialog dialog);
	}

	@Override
	public void close() {
		hide();
		deactivateEnterEscapeBinding();
	}
	
	public void activateEnterEscapeBinding() {
		if (registration == null) {
			registration = Event.addNativePreviewHandler(this);	
		}
	}
	
	public void deactivateEnterEscapeBinding() {
		if (registration != null) {
			registration.removeHandler();
			registration = null;
		}
	}

	@Override
	public void center() {
		super.center();

		activateEnterEscapeBinding();

		// it has to be scheduled deferred because the focus has to be taken
		// after the button takes it
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				if (focusWidget != null) {
					focusWidget.setFocus(true);
				} else {
					cancelButton.setFocus(true);
				}
			}
		});
	}

	@Override
	public void onPreviewNativeEvent(NativePreviewEvent event) {
		super.onPreviewNativeEvent(event);

		boolean cancelled = event.isCanceled();
		boolean isKeyUp = (event.getTypeInt() == Event.ONKEYDOWN);
		boolean isEnter = event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER;
		boolean isEscape = event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE;

		if (!cancelled && isKeyUp) {
			if (isEnter) {
				performOkAction();
				event.cancel();
			} else if (isEscape) {
				performCancelAction();
				event.cancel();
			}
		}

	}

}
