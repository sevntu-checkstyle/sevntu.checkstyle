package com.revere.ulc.workbench.client.ximpl.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.revere.ulc.workbench.client.ximpl.ui.ActionsHelper.ActionHolder;
import com.revere.ulc.workbench.client.ximpl.uiext.UIPrintable;
import com.revere.ulc.workbench.shared.layout.ILayout.Side;
import com.revere.ulc.workbench.shared.layout.ILayout.State;
import com.ulcjava.base.client.IDirtyDataOwner;
import com.ulcjava.base.client.UIComponent;
import com.ulcjava.base.shared.UlcEventConstants;

public class UIWorkbenchPartSite extends UIWorkbenchComponentSite implements IDirtyDataOwner {

	public static final String PARTID = "partId";

	public static final String INPUT = "input";

	public static final String CLOSE_DISABLED = "closeDisabled";

	public static final String PART_COMPONENT = "partComponent";

	public static final String EDITOR = "editor";

	public static final String SAVEABLE = "saveable";

	public static final String DIRTY = "dirty";

	public static final String GLOBAL = "global";

	public static final String OPEN_IN_PERSPECTIVE_ID = "openInPerspectiveId";

	public static final String PREFERRED_SIDE = "preferredSide";

	public static final String INITIAL_STATE = "initialState";

	public static final String TOOLBAR_ADVISER = "toolbarAdviser";

	public static final String PREFERRED_WIDTH = "preferredWidth";

	public static final String PREFERRED_HEIGHT = "preferredHeight";

	private int preferredWidth = -1;

	private int preferredHeight = -1;

	//private final Log log = LogFactory.getLog(getClass());

	private UIComponent partComponent;

	private boolean editor;

	private boolean saveable;

	private boolean dirty;

	private IPartContext context;

	private ActionsHelper globalActions = new ActionsHelper(this);

	private IPartToolbarAdviser toolbarAdviser = null;

	private String partId;

	private boolean global;

	private String openInPerspectiveId;

	private Side preferredSide;

	private State initialState = State.DOCKED;

	private IPart ui;

	private String input;

	private String loadErrorMessage;

	private String stackTrace;

	private boolean active;

	private boolean closeDisabled;

	private UIPrintable printableAdapter;

	//private boolean closable;

	public UIComponent getPartComponent() {
		return partComponent;
	}

	public void setPartComponent(UIComponent partComponent) {
		this.loadErrorMessage = null;
		UIComponent oldPartComponent = this.partComponent;
		this.partComponent = partComponent;
		firePropertyChangedEvent(PART_COMPONENT, oldPartComponent, partComponent);
	}

	public boolean isCloseDisabled() {
		return closeDisabled;
	}

	public void setCloseDisabled(boolean closeDisabled) {
		boolean oldCloseDisabled = this.closeDisabled;
		this.closeDisabled = closeDisabled;
		firePropertyChangedEvent(CLOSE_DISABLED, oldCloseDisabled, closeDisabled);
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		String oldInput = this.input;
		this.input = input;
		firePropertyChangedEvent(INPUT, oldInput, input);
	}

	public boolean isSaveable() {
		return saveable;
	}

	public void setSaveable(boolean saveable) {
		// theoretically this can never change
		boolean oldSaveable = this.saveable;
		this.saveable = saveable;
		firePropertyChangedEvent(SAVEABLE, oldSaveable, saveable);
	}

	public boolean isEditor() {
		return editor;
	}

	public void setEditor(boolean editor) {
		// theoretically this can never change
		boolean oldEditor = this.editor;
		this.editor = editor;
		firePropertyChangedEvent(EDITOR, oldEditor, editor);
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		boolean oldDirty = this.dirty;
		this.dirty = dirty;
		firePropertyChangedEvent(DIRTY, oldDirty, dirty);
	}

	/**
	 * updates server
	 */
	public void updateDirty(boolean dirty) {
		boolean oldDirty = this.dirty;
		setDirty(dirty);
		if (oldDirty != dirty) {
			// (DV) there's no urgency in sending dirty state to the server - it will be updated there anyway
			// But sending dirty state right away will force premature submit of values to the server that 
			// could lead to premature error reporting
			// updateStateULC("dirty", dirty);
			getSession().addDirtyDataOwner(this);
		}
	}

	public void flushDirtyData() {
		updateStateULC("dirty", dirty);
	}

//	/**
//	 * updates server
//	 */
//	public void updateDirtyQuite(boolean dirty) {
//		boolean oldDirty = this.dirty;
//		setDirty(dirty);
//		if (oldDirty != dirty) {
//			invokeULC(UlcEventConstants.ASYNCHRONOUS_MODE, "updateDirtyQuite", new Object[]{dirty});
//		}
//	}

	public boolean isGlobal() {
		return global;
	}

	public void setGlobal(boolean global) {
		this.global = global;
		firePropertyChangedEvent(GLOBAL, !global, global);
	}

	public String getOpenInPerspectiveId() {
		return openInPerspectiveId;
	}

	public void setOpenInPerspectiveId(String openInPerspectiveId) {
		String oldOpenInPerspectiveId = this.openInPerspectiveId;
		this.openInPerspectiveId = openInPerspectiveId;
		firePropertyChangedEvent(OPEN_IN_PERSPECTIVE_ID, oldOpenInPerspectiveId, openInPerspectiveId);
	}

	public Side getPreferredSide() {
		return preferredSide;
	}

	public void setPreferredSide(Side preferredSide) {
		Side oldPreferredSide = this.preferredSide;
		this.preferredSide = preferredSide;
		firePropertyChangedEvent(PREFERRED_SIDE, oldPreferredSide, preferredSide);
	}

	public void setPreferredSideName(String preferredSideName) {
		setPreferredSide(preferredSideName != null ? Side.valueOf(preferredSideName) : null);
	}

	public State getInitialState() {
		return initialState;
	}

	public void setInitialState(State initialState) {
		State oldInitialState = this.initialState;
		this.initialState = initialState;
		firePropertyChangedEvent(INITIAL_STATE, oldInitialState, initialState);
	}

	public void setInitialStateName(String initialStateName) {
		setInitialState(initialStateName != null ? State.valueOf(initialStateName) : null);
	}

	public void setContext(IPartContext context) {
		this.context = context;
	}

	public void registerGlobalActionHandler(String actionId, Object target, ActionListener clientAction) {
		ActionListener handler = clientAction;
		if (clientAction == null) {
			// server-side action
			handler = new ActionHandler(actionId, target);
		}
		globalActions.registerHandler(actionId, target, handler);
		if (context != null) {
			context.registerGlobalActionHandler(actionId, target, handler);
		}
	}

	public void unregisterGlobalActionHandler(String actionId, Object target) {
		globalActions.unregisterHandler(actionId, target);
		if (context != null) {
			context.unregisterGlobalActionHandler(actionId, target);
		}
	}

	public void runGlobalActionOnServer(String actionId, Object target, ActionEvent event) {
		invokeULC("runGlobalAction", new Object[] { actionId, target, event.getActionCommand() });
	}

	private class ActionHandler implements ActionListener {

		private String actionId;
		private Object target;

		public ActionHandler(String actionId, Object target) {
			this.actionId = actionId;
			this.target = target;
		}

		public void actionPerformed(ActionEvent e) {
			runGlobalActionOnServer(actionId, target, e);
		}
	}

	public void activate() {
		this.active = true;
//		if (log.isDebugEnabled()) {
//			log.debug("part activated " + getTitle());
//		}

		for (ActionHolder h : globalActions.getActionHolders()) {
			context.registerGlobalActionHandler(h.getActionId(), h.getTarget(), h.getHandler());
		}
		invokeULC(UlcEventConstants.DEFERRED_MODE, "activate", new Object[] {});
	}

	public void deactivate() {
		this.active = false;
		if (context != null) {
//			if (log.isDebugEnabled()) {
//				log.debug("part deactivated " + getTitle());
//			}

			for (ActionHolder h : globalActions.getActionHolders()) {
				context.unregisterGlobalActionHandler(h.getActionId(), h.getTarget());
			}
		}
		invokeULC(UlcEventConstants.DEFERRED_MODE, "deactivate", new Object[] {});
	}

	public boolean isActive() {
		return active;
	}

	public void sendNotifyState(boolean state) {
		if (ui != null && context != null) {
			context.sendNotifyState(ui, state);
		}
	}

	public void loadPart(IResult<Boolean> result) {
		invokeULC("loadPart", new Object[] { allocGlobalId(result) });
	}

	public void loadFailed(String errorMessage, String trace) {
		this.loadErrorMessage = errorMessage;
		this.stackTrace = trace;
	}

	public String getLoadErrorMessage() {
		return loadErrorMessage;
	}

	public String getErrorDetails() {
		return stackTrace;
	}

	public void savePart(final IResult<Boolean> result) {
		invokeULC(UlcEventConstants.SYNCHRONOUS_MODE, "savePart", new Object[] { allocGlobalId(result) });
	}

	public void partClosed() {
//		if (log.isDebugEnabled()) {
//			log.debug("part closed: " + this);
//		}

		sendMarkCollectableULC();

		// UlcEventConstants.DEFERRED_MODE could not be used as we need close event for Views(Review)
		invokeULC(UlcEventConstants.SYNCHRONOUS_MODE, "partClosed", new Object[0]);
	}

	public void setUI(IPart ui) {
		this.ui = ui;
	}

	public IPart getUI() {
		return ui;
	}

	public UIPrintable getPrintableAdapter() {
		return printableAdapter;
	}

	public void setPrintableAdapter(UIPrintable printableAdapter) {
		this.printableAdapter = printableAdapter;
	}

	public String getPartId() {
		return partId;
	}

	public void setPartId(String id) {
		String oldId = this.partId;
		this.partId = id;
		firePropertyChangedEvent(PARTID, oldId, id);
	}

	public void setPreferredWidth(int width) {
		int oldWidth = this.preferredWidth;
		this.preferredWidth = width;
		firePropertyChangedEvent(PREFERRED_WIDTH, oldWidth, width);
	}

	public void setPreferredHeight(int height) {
		int oldHeight = this.preferredHeight;
		this.preferredHeight = height;
		firePropertyChangedEvent(PREFERRED_HEIGHT, oldHeight, height);
	}

	public int getPreferredWidth() {
		return preferredWidth;
	}

	public int getPreferredHeight() {
		return preferredHeight;
	}

	public void setToolbarAdviser(UIWorkbenchPartToolbarAdviserSite toolbarAdviser) {
		this.toolbarAdviser = new WorkbenchPartToolbarAdviserDelegate(toolbarAdviser);
	}

	public IPartToolbarAdviser getToolbarAdviser() {
		return toolbarAdviser;
	}
}
