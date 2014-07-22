package com.googlecode.kanbanik.client.components;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.googlecode.kanbanik.client.KanbanikResources;

public class PanelContainingDialog extends DialogBox implements Closable,
		NativePreviewHandler {

    public static final String MINIMIZED = "minimized";
    public static final String EXPANDED = "expanded";
    public static final String PREFERRED_WINDOW_SIZE = "preferred_window_size";
    private Widget contentPanel;
    private boolean hasChangeSize;

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

    @UiField
    PushButton changeSizeButton;

    @UiField
    PushButton closeButton;

    private int minWidth;

    private int minHeight;

	private HandlerRegistration registration;

    private boolean expanded = false;

    private static final Storage storage = Storage.getLocalStorageIfSupported();

	public PanelContainingDialog(String title, Widget contentPanel) {
		this(title, contentPanel, null);
	}

    public PanelContainingDialog(String title, Widget contentPanel,
                                 FocusWidget focusWidget) {
        this(title, contentPanel, focusWidget, false, 0, 0);
    }

	public PanelContainingDialog(String title, Widget contentPanel,
			FocusWidget focusWidget, boolean hasChangeSize, int minWidth, int minHeight) {

        this.contentPanel = contentPanel;
        this.hasChangeSize = hasChangeSize;
        this.minWidth = minWidth;
        this.minHeight = minHeight;

        setWidget(uiBinder.createAndBindUi(this));

        if (hasChangeSize) {
            contentPanel.setHeight("100%");
            contentPanel.getElement().getStyle().setProperty("display", "flex");
            contentPanel.getElement().getStyle().setProperty("flexDirection", "column");

            initImageButtonsButton();
            expanded = false;
            setupToMinSize();
        }

		this.focusWidget = focusWidget;

		contentWrapper.add(contentPanel);

		setupButtons();

		setText(title);
		setAnimationEnabled(true);
		setGlassEnabled(true);

	}

    private void initImageButtonsButton() {
        changeSizeButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.maximize()));
        changeSizeButton.getElement().getStyle().setDisplay(Style.Display.BLOCK);

        changeSizeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                swithWindowSize();
                storePreferredSize();
            }
        });

        closeButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.deleteButtonImage()));
        closeButton.addClickHandler(new CancelButtonHandler());

    }

    private void initPreferredSize() {
        if (storage != null) {
            String size = storage.getItem(PREFERRED_WINDOW_SIZE);
            if (size == null || "".equals(size)) {
                // leave default
                swithWindowSize();
                return;
            }

            if (EXPANDED.equals(size) && !expanded) {
                swithWindowSize();
            } else if (MINIMIZED.equals(size) && expanded) {
                swithWindowSize();
            }
        }
    }

    private void swithWindowSize() {
        if (!hasChangeSize) {
            return;
        }
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                if (!expanded) {
                    contentWrapper.getElement().getStyle().setWidth(Window.getClientWidth() - 20, Style.Unit.PX);
                    contentWrapper.getElement().getStyle().setHeight(Window.getClientHeight() - 70, Style.Unit.PX);
                    setPopupPosition(0, 0);
                    changeSizeButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.minimize()));
                    expanded = true;
                    storePreferredSize();
                } else {
                    setupToMinSize();
                    changeSizeButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.maximize()));
                    expanded = false;
                    storePreferredSize();
                    center();
                }
            }
        });
    }

    private void setupToMinSize() {
        contentWrapper.getElement().getStyle().setWidth(minWidth, Style.Unit.PX);
        contentWrapper.getElement().getStyle().setHeight(minHeight, Style.Unit.PX);
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
	}
	
	@Override
	public void hide() {
		super.hide();
		
		// to make sure that handler will never stay there
		deactivateEnterEscapeBinding();

        storePreferredSize();
	}

    private void storePreferredSize() {
        if (!hasChangeSize) {
            return;
        }

        if (storage != null) {
            storage.setItem(PREFERRED_WINDOW_SIZE, expanded ? EXPANDED : MINIMIZED);
        }
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

        initPreferredSize();
	}

	@Override
	public void onPreviewNativeEvent(NativePreviewEvent event) {
		super.onPreviewNativeEvent(event);

		boolean cancelled = event.isCanceled();
		boolean isKeyUp = (event.getTypeInt() == Event.ONKEYDOWN);
		boolean isEnter = event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER;
		boolean isEscape = event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE;

		if (!cancelled && isKeyUp && isShowing()) {
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
