package com.revere.det.core.ui.movecontainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.revere.ulc.workbench.uiext.AbstractObjectTableModel;
import com.revere.ulc.workbench.uiext.Decoration;
import com.revere.ulc.workbench.uiext.ITableRowDecorator;
import com.revere.ulc.workbench.uiext.ULCWorkbenchTable;
import com.ulcjava.base.application.ULCBoxPane;
import com.ulcjava.base.application.ULCButton;
import com.ulcjava.base.application.ULCCheckBox;
import com.ulcjava.base.application.ULCComponent;
import com.ulcjava.base.application.ULCFiller;
import com.ulcjava.base.application.ULCGridLayoutPane;
import com.ulcjava.base.application.ULCLabel;
import com.ulcjava.base.application.ULCScrollPane;
import com.ulcjava.base.application.ULCTable;
import com.ulcjava.base.application.event.ActionEvent;
import com.ulcjava.base.application.event.IActionListener;
import com.ulcjava.base.application.event.IListSelectionListener;
import com.ulcjava.base.application.event.IValueChangedListener;
import com.ulcjava.base.application.event.KeyEvent;
import com.ulcjava.base.application.event.ListSelectionEvent;
import com.ulcjava.base.application.util.Color;
import com.ulcjava.base.application.util.Font;
import com.ulcjava.base.application.util.KeyStroke;

public class TableEditorMoveContainer<E> {

	private ULCBoxPane pane;
	private ULCWorkbenchTable srcTable;
	private ULCWorkbenchTable destTable;

	private SourceTableModel srcModel;
	private DestinationTableModel destModel;
	private boolean destinationInitialized;

	private List<E> movedObjects;
	private List<E> destObjects;

	private List<IMovedListListener> listeners = new LinkedList<IMovedListListener>();
	private List<IListSelectionListener> sourceSelectionlisteners = new LinkedList<IListSelectionListener>();
	private List<IListSelectionListener> destSelectionlisteners = new LinkedList<IListSelectionListener>();
	private ULCButton moveAllButton;
	private ULCButton moveButton;
	private ULCButton returnButton;
	private ULCButton returnAllButton;

	private Comparator<E> comparator;

	private TableControlOption sourceControlOption;
	private TableControlOption destinationControlOption;
	private ULCComponent leftInfoBoxPane;
	private ULCComponent rightInfoBoxPane;
	private List<ITableRowDecorator> sourceTableRowDecorators = new ArrayList<ITableRowDecorator>();
	private List<ITableRowDecorator> destinationTableRowDecorators = new ArrayList<ITableRowDecorator>();
	private ItemListFilter<E> sourceListFilter;

	public TableEditorMoveContainer(ITableAdapter<E> tableAdapter) {
		this(tableAdapter, tableAdapter);
	}

	public TableEditorMoveContainer(ITableAdapter<E> tableAdapter, Comparator<E> comparator) {
		this(tableAdapter, tableAdapter);
		this.comparator = comparator;
	}

	public TableEditorMoveContainer(final ITableAdapter<E> srcTableAdapter, final ITableAdapter<E> destTableAdapter) {

		this.srcModel = new SourceTableModel(srcTableAdapter);
		this.destModel = new DestinationTableModel(destTableAdapter);

		movedObjects = new ArrayList<E>();
		destObjects = new ArrayList<E>();
	}

	@SuppressWarnings("serial")
	private void createContents() {
		srcTable = new ULCWorkbenchTable(srcModel);
		srcTable.setDrawZebra(true);

		DefaultTableRowDecorator defaultDecorator = new DefaultTableRowDecorator();
		srcTable.addRowDecorator(defaultDecorator);
		for (ITableRowDecorator decorator : sourceTableRowDecorators)
			srcTable.addRowDecorator(decorator);

		destTable = new ULCWorkbenchTable(destModel);
		destTable.addRowDecorator(defaultDecorator);
		for (ITableRowDecorator decorator : destinationTableRowDecorators)
			destTable.addRowDecorator(decorator);
		destTable.setDrawZebra(true);

		moveAllButton = new ULCButton(">>>");
		moveAllButton.setEnabled(false);

		moveButton = new ULCButton(">>");
		moveButton.setEnabled(false);

		returnButton = new ULCButton("<<");
		returnButton.setEnabled(false);

		returnAllButton = new ULCButton("<<<");
		returnAllButton.setEnabled(false);

		for (IListSelectionListener listener : sourceSelectionlisteners) {
			srcTable.getSelectionModel().addListSelectionListener(listener);
		}
		// Set button enabling mechanism
		srcTable.getSelectionModel().addListSelectionListener(new IListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				setButtonEnablement();
			}
		});

		for (IListSelectionListener listener : destSelectionlisteners) {
			destTable.getSelectionModel().addListSelectionListener(listener);
		}
		destTable.getSelectionModel().addListSelectionListener(new IListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				setButtonEnablement();
			}
		});

		ULCGridLayoutPane buttonPane = new ULCGridLayoutPane(4, 1, 0, 5);
		buttonPane.add(moveAllButton);
		buttonPane.add(moveButton);
		buttonPane.add(returnButton);
		buttonPane.add(returnAllButton);

		int rowCount = 2;
		if (sourceControlOption != null || destinationControlOption != null) {
			++rowCount;
		}
		pane = new ULCBoxPane(3, rowCount);

		pane.add(ULCBoxPane.BOX_LEFT_TOP, new ULCLabel("Source:"));
		pane.add(ULCBoxPane.BOX_LEFT_TOP, ULCFiller.createGlue());
		pane.add(ULCBoxPane.BOX_LEFT_TOP, new ULCLabel("Destination:"));

		pane.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCScrollPane(srcTable));
		pane.add(ULCBoxPane.BOX_CENTER_CENTER, buttonPane);
		pane.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCScrollPane(destTable));

		// Add control options if any
		if (sourceControlOption != null || destinationControlOption != null) {
			if (sourceControlOption != null) {
				ULCCheckBox controlOption = new ULCCheckBox(sourceControlOption.name);
				controlOption.addValueChangedListener(sourceControlOption.listener);
				controlOption.setSelected(sourceControlOption.selected);
				pane.add(ULCBoxPane.BOX_LEFT_TOP, controlOption);
			} else {
				pane.add(ULCBoxPane.BOX_LEFT_TOP, ULCFiller.createGlue());
			}
			pane.add(ULCBoxPane.BOX_LEFT_TOP, ULCFiller.createGlue());
			if (destinationControlOption != null) {
				ULCCheckBox controlOption = new ULCCheckBox(destinationControlOption.name);
				controlOption.addValueChangedListener(destinationControlOption.listener);
				controlOption.setSelected(destinationControlOption.selected);
				pane.add(ULCBoxPane.BOX_LEFT_TOP, controlOption);
			} else {
				pane.add(ULCBoxPane.BOX_LEFT_TOP, ULCFiller.createGlue());
			}
		}

		if (leftInfoBoxPane != null && rightInfoBoxPane != null) {
			pane.add(ULCBoxPane.BOX_EXPAND_CENTER, leftInfoBoxPane);
			pane.add(ULCFiller.createHorizontalGlue());
			pane.add(ULCBoxPane.BOX_EXPAND_CENTER, rightInfoBoxPane);
		}

		moveButton.addActionListener(new IActionListener() {
			public void actionPerformed(ActionEvent event) {
				moveSelected();
			}
		});

		returnButton.addActionListener(new IActionListener() {
			public void actionPerformed(ActionEvent event) {
				returnSelected();
			}
		});

		moveAllButton.addActionListener(new IActionListener() {
			public void actionPerformed(ActionEvent event) {
				moveAll();
			}
		});

		returnAllButton.addActionListener(new IActionListener() {
			public void actionPerformed(ActionEvent event) {
				returnAll();
			}
		});

		srcTable.registerKeyboardAction(new IActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (moveButton.isEnabled()) {
					moveButton.doClick();
				}
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), 0);

		destTable.registerKeyboardAction(new IActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (returnButton.isEnabled()) {
					returnButton.doClick();
				}
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), 0);
	}

	public void moveSelected() {
		if (srcTable.getSelectedRowCount() > 0) {
			// move objects
			List<E> list = new ArrayList<E>();
			int[] rows = srcTable.getSelectedRows();
			for (int i = rows.length - 1; i >= 0; i--) {
				list.add(srcModel.getObject(rows[i]));
			}
			moveItems(list);
		}
	}

	public void moveItems(List<E> list) {
		for (E item : list) {
			movedObjects.add(0, item);
			srcModel.getObjects().remove(item);
		}
		srcModel.fireTableDataChanged();
		destModel.fireTableDataChanged(); // sort rows
		// restore selection
		if (srcModel.getRowCount() > 0)
			srcTable.setRowSelectionInterval(0, 0);

		if (destTable.getRowCount() > 0)
			destTable.setRowSelectionInterval(0, 0);

		fireObjectMoved();
	}

	public void returnSelected() {
		if (destTable.getSelectedRowCount() > 0) {
			// return objects
			ArrayList<E> returnedItems = new ArrayList<E>();
			int[] rows = destTable.getSelectedRows();
			for (int i = rows.length - 1; i >= 0; i--) {
				returnedItems.add(movedObjects.remove(rows[i]));
				destModel.fireTableRowsDeleted(rows[i], rows[i]);
			}

			srcModel.getObjects().addAll(0, filterSourceItems(returnedItems));
			srcModel.fireTableDataChanged(); // sort rows

			// restore selection
			int row = (rows[0] >= movedObjects.size()) ? movedObjects.size() - 1 : rows[0];
			if (row > -1) {
				destTable.setRowSelectionInterval(row, row);
			}
			if (srcTable.getRowCount() > 0) {
				srcTable.setRowSelectionInterval(0, 0);
			}

			fireObjectMoved();
		}
	}

	public void moveAll() {
		movedObjects.addAll(srcModel.getObjects());
		srcModel.getObjects().clear();
		// fire change events
		srcModel.fireTableDataChanged();
		destModel.fireTableDataChanged();
		fireObjectMoved();
	}

	public void returnAll() {
		// renew source model
		srcModel.getObjects().addAll(filterSourceItems(movedObjects));
		// clear moved objects
		movedObjects.clear();
		// fire change events
		srcModel.fireTableDataChanged();
		destModel.fireTableDataChanged();
		fireObjectMoved();
	}

	private void setButtonEnablement() {
		// move button
		moveButton.setEnabled(destinationInitialized && srcTable.getSelectedRowCount() > 0);

		// move all button
		moveAllButton.setEnabled(destinationInitialized && srcModel.getRowCount() > 0);

		// return button
		int[] rows = destTable.getSelectedRows();
		boolean returnEnabled = rows.length > 0;
		for (int row : rows) {
			if (row >= movedObjects.size()) {
				returnEnabled = false;
			}
		}
		returnButton.setEnabled(returnEnabled);

		// return all button
		returnAllButton.setEnabled(movedObjects.size() > 0);

	}

	private void setDefaultSelection() {
		if (srcTable.getSelectedRowCount() == 0 && srcTable.getRowCount() > 0) {
			srcTable.setRowSelectionInterval(0, 0);
		}

		if ((movedObjects.size() > 0) && (destTable.getSelectedRowCount() == 0)) {
			destTable.setRowSelectionInterval(0, 0);
		}
	}

	public void setSourceObjects(List<E> objects) {
		// set to table all, except moved
		srcModel.getObjects().clear();
		srcModel.getObjects().addAll(filterSourceItems(objects));
		srcModel.getObjects().removeAll(movedObjects);
		// fire change events
		srcModel.fireTableDataChanged();
		setButtonEnablement();
		setDefaultSelection();
	}

	private List<E> filterSourceItems(List<E> objects) {
		List<E> filtered = objects;
		if (null != sourceListFilter) {
			filtered = sourceListFilter.apply(objects);
		}
		return filtered;
	}

	public void setDestinationObjects(List<E> objects) {
		if (!destinationInitialized) {
			destinationInitialized = true;
		}
		destObjects.clear();
		destObjects.addAll(objects);
		destModel.fireTableDataChanged();
		setButtonEnablement();
		setDefaultSelection();
	}

	public void updateSourceTable() {
		srcModel.fireTableStructureChanged();
	}

	public void updateDestinationTable() {
		destModel.fireTableStructureChanged();
	}

	public List<E> getMoved() {
		return movedObjects;
	}

	public List<E> getSourceObjects() {
		return srcModel.getObjects();
	}

	public List<E> getDestinationObjects() {
		return destObjects;
	}

	public E getDestinationObject(int index) {
		return destModel.getObject(index);
	}

	public E getSourceObject(int index) {
		return srcModel.getObjects().get(index);
	}

	public void addChangeListener(IMovedListListener listener) {
		listeners.add(listener);
	}

	public void removeChangeListener(IMovedListListener listener) {
		listeners.remove(listener);
	}

	protected void fireObjectMoved() {
		for (IMovedListListener listener : listeners) {
			listener.movedListChanged();
		}
		setButtonEnablement();
		setDefaultSelection();
	}

	public ULCComponent getComponent() {
		if (pane == null) {
			createContents();
		}
		return pane;
	}

	public void setSourceControlOption(String name, IValueChangedListener listener, boolean selected) {
		this.sourceControlOption = new TableControlOption(name, listener, selected);
	}

	public void setDestinationControlOption(String name, IValueChangedListener listener, boolean selected) {
		this.destinationControlOption = new TableControlOption(name, listener, selected);
	}

	public void addSourceTableRowDecorator(ITableRowDecorator decorator) {
		sourceTableRowDecorators.add(decorator);
	}

	public void addDestinationTableRowDecorator(ITableRowDecorator decorator) {
		destinationTableRowDecorators.add(decorator);
	}

	private final class SourceTableModel extends AbstractObjectTableModel<E> {
		private static final long serialVersionUID = 1L;

		private ITableAdapter<E> tableAdapter;

		private SourceTableModel(ITableAdapter<E> tableAdapter) {
			this.tableAdapter = tableAdapter;
		}

		@Override
		protected Object getObjectValue(E object, int column) {
			return tableAdapter.getObjectValue(object, column);
		}

		public int getColumnCount() {
			return tableAdapter.getColumnCount();
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return tableAdapter.getColumnClass(columnIndex);
		}

		@Override
		public String getColumnName(int column) {
			return tableAdapter.getColumnName(column);
		}

		@Override
		public List<E> getObjects() {
			return super.getObjects();
		}

		@Override
		public void fireTableDataChanged() {
			if (comparator != null) {
				Collections.sort(srcModel.getObjects(), comparator);
			}
			super.fireTableDataChanged();
		}
	}

	private final class DestinationTableModel extends AbstractObjectTableModel<E> {
		private static final long serialVersionUID = 1L;

		private ITableAdapter<E> tableAdapter;

		private DestinationTableModel(ITableAdapter<E> tableAdapter) {
			this.tableAdapter = tableAdapter;
		}

		@Override
		protected Object getObjectValue(E object, int column) {
			return tableAdapter.getObjectValue(object, column);
		}

		public int getColumnCount() {
			return tableAdapter.getColumnCount();
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return tableAdapter.getColumnClass(columnIndex);
		}

		@Override
		public String getColumnName(int column) {
			return tableAdapter.getColumnName(column);
		}

		@Override
		public int getRowCount() {
			return movedObjects.size() + destObjects.size();
		}

		@Override
		public E getObject(int row) {
			int movedSize = movedObjects.size();
			if (row < movedSize) {
				return movedObjects.get(row);
			} else {
				return destObjects.get(row - movedSize);
			}
		}

		@Override
		public Object getValueAt(int row, int column) {
			E object = getObject(row);
			return getObjectValue(object, column);
		}

		@Override
		public void fireTableDataChanged() {
			if (comparator != null) {
				Collections.sort(destObjects, comparator);
				Collections.sort(movedObjects, comparator);
			}
			super.fireTableDataChanged();
		}
	}

	public interface IMovedListListener {
		void movedListChanged();
	}

	public static interface ItemListFilter<T> {
		public List<T> apply(List<T> items);
	}

	private class TableControlOption {
		private String name;
		private IValueChangedListener listener;
		private boolean selected;

		TableControlOption(String name, IValueChangedListener listener, boolean selected) {
			this.name = name;
			this.listener = listener;
			this.selected = selected;
		}
	}

	public static class MovedItemTableRowDecorator implements ITableRowDecorator {

		private TableEditorMoveContainer<?> movContainer;

		public MovedItemTableRowDecorator(TableEditorMoveContainer<?> movContainer) {
			this.movContainer = movContainer;
		}

		@SuppressWarnings("unchecked")
		public Decoration decorateRow(ULCTable table, boolean isSelected, boolean hasFocus, int row) {
			Object item = ((AbstractObjectTableModel) table.getModel()).getObject(row);
			Decoration decor = null;
			if (movContainer.getMoved().contains(item)) {
				decor = new Decoration();
				decor.setFontStyle(Font.BOLD);
			}
			return decor;
		}

	}

	public static class DefaultTableRowDecorator implements ITableRowDecorator {

		public Decoration decorateRow(ULCTable table, boolean isSelected, boolean hasFocus, int row) {
			Decoration decor = new Decoration();
			decor.setColor(Color.black);
			return decor;
		}
	};

	public void addSourceSelectionlisteners(IListSelectionListener linstener) {
		sourceSelectionlisteners.add(linstener);
	}

	public void removeSourceSelectionlisteners(IListSelectionListener linstener) {
		sourceSelectionlisteners.remove(linstener);
	}

	public void addDestSelectionlisteners(IListSelectionListener linstener) {
		destSelectionlisteners.add(linstener);
	}

	public void removeDestSelectionlisteners(IListSelectionListener linstener) {
		destSelectionlisteners.remove(linstener);
	}

	public void setLeftInfoBoxPane(ULCComponent leftInfoBoxPane) {
		this.leftInfoBoxPane = leftInfoBoxPane;
	}

	public void setRightInfoBoxPane(ULCComponent rightInfoBoxPane) {
		this.rightInfoBoxPane = rightInfoBoxPane;
	}

	public ItemListFilter<E> getSourceListFilter() {
		return sourceListFilter;
	}

	public void setSourceListFilter(ItemListFilter<E> sourceListFilter) {
		this.sourceListFilter = sourceListFilter;
	}

}
