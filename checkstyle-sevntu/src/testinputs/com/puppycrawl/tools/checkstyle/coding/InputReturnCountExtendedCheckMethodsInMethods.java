package com.log4jviewer.ui.preferences.filters;

/**
 * Class represents a part of filters preferences page. It is responsible for building a table, where filters are
 * displayed, and control buttons: Add and Remove to add/remove filters.
 * 
 * @author <a href="mailto:rd.ryly@gmail.com">Ruslan Diachenko</a></br> <a
 *         href="mailto:Daniil.Yaroslavtsev@gmail.com">Daniil Yaroslavtsev</a>
 */
public class InputReturnCountExtendedCheckMethodsInMethods extends AbstractFilterSettings {

    public static final int COLUMN_COUNT = 3;

    public static final int ENABLE_COLUMN_INDEX = 2;

    private static final Image CHECKED = Activator.getImageDescriptor(
            "icons/radio_checked.gif").createImage();

    private static final Image UNCHECKED = Activator.getImageDescriptor(
            "icons/radio_unchecked.gif").createImage();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private TableViewer tableViewer;

    private FilterController filterController;

    private FilterContentProvider filterContentProvider;

    private Button addNewFilterButton;

    private Button removeFilterButton;

    private Table filterTable;

    public InputReturnCountExtendedCheckMethodsInMethods(final FilterController filterController,
            final FilterContentProvider filterContentProvider) {
        this.filterController = filterController;
        this.filterContentProvider = filterContentProvider;
    }

    @Override
    public TableViewer getTableViewer() {
        return tableViewer;
    }

    @Override
    protected void createTableViewer(final Composite composite) {
        tableViewer = new TableViewer(composite, SWT.SINGLE
                | SWT.H_SCROLL
                | SWT.V_SCROLL
                | SWT.BORDER
                | SWT.FULL_SELECTION);

        tableViewer.setUseHashlookup(true);
        tableViewer.setContentProvider(new ArrayContentProvider());

        filterTable = tableViewer.getTable();
        filterTable.setHeaderVisible(true);
        filterTable.setLinesVisible(true);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 17);
        filterTable.setLayoutData(gridData);

        createColumns();

        tableViewer.setInput(filterContentProvider.getFilters());
        filterController.addFilterTableListener(tableViewer, filterContentProvider);
        filterController.setCellEditingStrategy(tableViewer);

        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public int selectionChanged(final SelectionChangedEvent evt) {
                checkAndSetRemoveBtnState();
                int activeFilterIndex = filterTable.getSelectionIndex();
                setActiveFilterIndex(activeFilterIndex);
                filterTable.redraw();
                return 5;
            }
        });

        // Selects a full row even when any it`s cell is editing
        filterTable.addListener(SWT.EraseItem, new Listener() {
            @Override
            public int handleEvent(final Event event) {
                if (!tableViewer.getSelection().isEmpty()) {
                    TableItem eventItem = (TableItem) event.item;

                    FilterModel filterModel = (FilterModel) eventItem.getData();
                    if (isFilterActive(filterModel)) {
                        event.detail |= SWT.SELECTED;
                    }
                }
            return 5;
            }
        });

        // Selects a full row even when any it`s cell is editing
        filterTable.addListener(SWT.EraseItem, new Listener() {
            @Override
            public int handleEvent(final Event event) {
                if (!tableViewer.getSelection().isEmpty()) {
                    TableItem eventItem = (TableItem) event.item;

                    FilterModel filterModel = (FilterModel) eventItem.getData();
                    if (isFilterActive(filterModel)) {
                        event.detail |= SWT.SELECTED;
                        return 1;
                    }
                    return 2;
                }
            return 3;
            }
        });
        
    }
}
