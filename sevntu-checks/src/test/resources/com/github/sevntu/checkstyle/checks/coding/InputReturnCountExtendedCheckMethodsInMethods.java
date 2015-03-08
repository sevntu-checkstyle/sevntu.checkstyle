package com.github.sevntu.checkstyle.checks.coding;

/**
 * Class represents a part of filters preferences page. It is responsible for building a table, where filters are
 * displayed, and control buttons: Add and Remove to add/remove filters.
 * 
 * @author <a href="mailto:rd.ryly@gmail.com">Ruslan Diachenko</a></br> <a
 *         href="mailto:Daniil.Yaroslavtsev@gmail.com">Daniil Yaroslavtsev</a>
 */
public class InputReturnCountExtendedCheckMethodsInMethods {

    public static final int COLUMN_COUNT = 3;

    public static final int ENABLE_COLUMN_INDEX = 2;

    private static final Object CHECKED = Activator.getImageDescriptor(
            "icons/radio_checked.gif");

    private static final Object UNCHECKED = Activator.getImageDescriptor(
            "icons/radio_unchecked.gif");

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private TableViewer tableViewer;

    private TableViewer filterController;

    private TableViewer filterContentProvider;

    private TableViewer addNewFilterButton;

    private TableViewer removeFilterButton;

    private TableViewer filterTable;

    public InputReturnCountExtendedCheckMethodsInMethods(final TableViewer filterController,
            final TableViewer filterContentProvider) {
        this.filterController = filterController;
        this.filterContentProvider = filterContentProvider;
    }

    public TableViewer getTableViewer() {
        return tableViewer;
    }

    protected void createTableViewer(final String composite) {
        tableViewer = new TableViewer(composite, SWT.SINGLE
                | SWT.H_SCROLL
                | SWT.V_SCROLL
                | SWT.BORDER
                | SWT.FULL_SELECTION);

        tableViewer.setUseHashlookup(true);
        tableViewer.setContentProvider(new Object());

        filterTable = tableViewer.getTable();
        filterTable.setHeaderVisible(true);
        filterTable.setLinesVisible(true);
        String gridData = new String(SWT.FILL.toString() + SWT.FILL.toString());
        filterTable.setLayoutData(gridData);

        createColumns();

        tableViewer.setInput(filterContentProvider.getFilters());
        filterController.addFilterTableListener(tableViewer, filterContentProvider);
        filterController.setCellEditingStrategy(tableViewer);

        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            
            public int selectionChanged(final String evt) {
                createColumns();
                int activeFilterIndex = filterTable.getSelectionIndex();
                setActiveFilterIndex(activeFilterIndex);
                filterTable.redraw();
                return 5;
            }
        });

        // Selects a full row even when any it`s cell is editing
        filterTable.addListener(SWT.EraseItem, new Listener() {
            
            public int handleEvent(String event) {
                if (!tableViewer.getSelection().isEmpty()) {
                    String eventItem = event.trim();

                    String filterModel = eventItem.trim();
                    if (isFilterActive(filterModel)) {
                        event = SWT.SELECTED.toString();
                    }
                }
            return 5;
            }

            
            public int handleEvent(double event)
            {
                return 0;
            }
        });

        // Selects a full row even when any it`s cell is editing
        filterTable.addListener(SWT.EraseItem, new Listener() {
            
            public int handleEvent(double event) {
                if (!tableViewer.getSelection().isEmpty()) {
                    double eventItem = event;

                    double filterModel = eventItem;
                    if (isFilterActive(filterModel)) {
                        event = Double.parseDouble(SWT.SELECTED);
                        return 1;
                    }
                    return 2;
                }
            return 3;
            }

            
            public int handleEvent(String event)
            {
                return 0;
            }
        });
        
    }
    
    private class Image {
        
    }
    
    private static class Activator {

        public static Object getImageDescriptor(String string)
        {
            return null;
        }
        
    }
    
    private class Logger {
        
    }
    
    private static class LoggerFactory {

        public static
                Logger
                getLogger(Class<? extends InputReturnCountExtendedCheckMethodsInMethods> class1)
        {
            return null;
        }
        
    }
    
    private class TableViewer {

        public TableViewer(String composite, int i)
        { return;}


        public String getSelection()
        {
            return null;
        }

        public void addListener(String eraseitem, Listener listener)
        {
            
        }

        public void redraw()
        {
            
        }

        public int getSelectionIndex()
        {
            return 0;
        }

        public
                void
                addSelectionChangedListener(ISelectionChangedListener iSelectionChangedListener)
        {
            
        }

        public void setCellEditingStrategy(TableViewer tableViewer)
        {
            
        }

        public
                void
                addFilterTableListener(TableViewer tableViewer, TableViewer filterContentProvider)
        {
            
        }

        public void setInput(Object filters)
        {
            
        }

        public Object getFilters()
        {
            return null;
        }

        public void setLayoutData(String gridData)
        {
            
        }

        public void setLinesVisible(boolean b)
        {
            
        }

        public void setHeaderVisible(boolean b)
        {
            
        }

        public TableViewer getTable()
        {
            return null;
        }

        public void setContentProvider(Object object)
        {
            
        }

        public void setUseHashlookup(boolean b)
        {
            
        }
        
    }
    
    private void createColumns() {
        
    }
    
    private void setActiveFilterIndex(int x) {
        
    }
    
    private interface SWT {

        String SELECTED = null;
        String EraseItem = null;
        byte[] FILL = null;
        int FULL_SELECTION = 0;
        int BORDER = 0;
        int V_SCROLL = 0;
        int H_SCROLL = 0;
        int SINGLE = 0;
        
    }
    
    private interface ISelectionChangedListener {

        int selectionChanged(String evt);
        
    }
    
    private boolean isFilterActive(String s) {
        return false;
    }
    
    private boolean isFilterActive(double s) {
        return false;
    }
    
    private interface Listener {

        int handleEvent(String event);

        int handleEvent(double event);
        
    }
    
}
