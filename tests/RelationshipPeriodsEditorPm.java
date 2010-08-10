package com.revere.det.core.ui.biz.relationship;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.revere.cf.ILongUpdatableSession;
import com.revere.cf.ISessionFactory;
import com.revere.det.core.domain.Company;
import com.revere.det.core.domain.CompanyPeriod;
import com.revere.det.core.domain.RelationshipHistory;
import com.revere.det.core.domain.RelationshipNote;
import com.revere.det.core.domain.RelationshipPeriod;
import com.revere.det.core.domain.RelationshipType;
import com.revere.det.core.ui.AppService;
import com.revere.det.core.ui.biz.period.CompanyPeriodUtils;
import com.revere.det.core.util.UIUtils;
import com.revere.gui.runtime.status.IStatusManager;
import com.revere.gui.runtime.status.Status;
import com.revere.ulc.workbench.ui.provisional.events.Key;
import com.revere.ulc.workbench.ui.provisional.events.KeyAndRefs;
import com.revere.ulc.workbench.uiext.ValueChangeListenerSupport;
import com.ulcjava.base.application.event.ITableModelListener;
import com.ulcjava.base.application.event.IValueChangedListener;
import com.ulcjava.base.application.event.TableModelEvent;
import com.ulcjava.base.application.event.ValueChangedEvent;

public class RelationshipPeriodsEditorPm {

	private CompanyPeriod workcopy;

	private ISessionFactory sessionFactory;

	private ILongUpdatableSession updatableSession;

	private boolean newEditor;

	private static final String INIT_COMPANYPERIOD = "company,endDate,relationshipPeriods{revenuePercent,notes,relationship{companyTo}},relationshipNotes{relationshipType}";

	private ValueChangeListenerSupport changeListenerSupport = new ValueChangeListenerSupport();

	private Map<RelationshipType, RelationshipPeriodEditorPm> typeToPm = new HashMap<RelationshipType, RelationshipPeriodEditorPm>();

	private String companyPeriodTitle;
	private String title;
	private String shortTitle;
	private AppService service;

	@SuppressWarnings("serial")
	public RelationshipPeriodsEditorPm() {

		ITableModelListener listener = new ITableModelListener() {

			public void tableChanged(TableModelEvent event) {
				fireChanged();
			}
		};

		for (RelationshipType type : RelationshipType.values()) {
			RelationshipPeriodEditorPm pm = new RelationshipPeriodEditorPm(type);
			pm.setSessionFactory(sessionFactory);
			pm.getTableModel().addTableModelListener(listener);
			typeToPm.put(type, pm);
		}
	}

	public void setService(AppService service) {
		this.service = service;
	}

	public void setSessionFactory(ISessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		for (RelationshipType type : RelationshipType.values()) {
			typeToPm.get(type).setSessionFactory(sessionFactory);
		}
	}

	public void addValueChangeListener(IValueChangedListener listener) {
		changeListenerSupport.addValueChangeListener(listener);
	}

	public void removeValueChangeListener(IValueChangedListener listener) {
		changeListenerSupport.removeValueChangeListener(listener);
	}

	private void fireChanged() {
		changeListenerSupport.fireValueChangeEvent(new ValueChangedEvent(this));
	}

	public void load(RelationshipPeriodsEditorInput input) {
		changeListenerSupport.setEnabled(false);
		try {

			newEditor = (input.getType() == RelationshipPeriodsEditorInput.NEW_PERIOD);

			updatableSession = sessionFactory.getLongUpdatableSession("Edit Relationship Period");

			workcopy = updatableSession.get(CompanyPeriod.class, input.getCompanyPeriodId(), INIT_COMPANYPERIOD);
			UIUtils.assertEntityFound(workcopy, CompanyPeriod.class, input.getCompanyPeriodId());

			if (input.getPrototypeCompanyPeriodId() != null) {
				CompanyPeriod prototypeCompanyPeriod = service.getCompanyPeriodForEditor(input
						.getPrototypeCompanyPeriodId());
				UIUtils.assertEntityFound(prototypeCompanyPeriod, CompanyPeriod.class, input
						.getPrototypeCompanyPeriodId());

				cloneParticle(prototypeCompanyPeriod, workcopy);
			}

			// Reload RelationshipPeriodEditorPm(s)
			for (RelationshipType type : RelationshipType.values()) {
				RelationshipPeriodEditorPm pm = typeToPm.get(type);
				pm.load(workcopy);
			}

			companyPeriodTitle = CompanyPeriodUtils.getLabel(workcopy);

			Company c = workcopy.getCompany();
			title = "Relationship Period: " + c.getNameAndTickerText() + " - "
						+ CompanyPeriodUtils.getLabelForEditor(workcopy);
			shortTitle = "Rel Period: " + c.getTickerOrNameText() + " - "
						+ CompanyPeriodUtils.getShortLabelForEditor(workcopy);

		} finally {
			changeListenerSupport.setEnabled(true);
		}
	}

	private static void cloneParticle(CompanyPeriod src, CompanyPeriod dst) {

		Set<RelationshipNote> relNoteSet = new HashSet<RelationshipNote>();
		for (RelationshipNote original : src.getRelationshipNotes()) {
			RelationshipNote newRn = new RelationshipNote();
			newRn.setCompanyPeriod(dst);
			newRn.setRelationshipType(original.getRelationshipType());
			newRn.setNotes(original.getNotes());
			relNoteSet.add(newRn);
		}
		dst.setRelationshipNotes(relNoteSet);

		Set<RelationshipPeriod> relPeriod = new HashSet<RelationshipPeriod>();
		for (RelationshipPeriod original : src.getRelationshipPeriods()) {
			RelationshipPeriod newRp = new RelationshipPeriod();
			newRp.setCompanyPeriod(dst);
			newRp.setRevenuePercent(original.getRevenuePercent());
			newRp.setNotes(original.getNotes());
			newRp.setRelationship(original.getRelationship());
			relPeriod.add(newRp);
		}
		dst.setRelationshipPeriods(relPeriod);
	}

	public void save() {
		for (RelationshipType type : RelationshipType.values()) {
			RelationshipPeriodEditorPm pm = typeToPm.get(type);

			// Delete removed periods
			for (RelationshipPeriod rp : workcopy.getRelationshipPeriods()) {
				if (rp.getRelationship().getType().equals(type)
						&& findRelationshipPeriod(rp, pm.getTableModel().getRelationshipPeriods()) < 0) {
					updatableSession.delete(rp);
				}
			}

			// Create new RelationshipPeriod(s)
			for (RelationshipPeriod rp : pm.getTableModel().getRelationshipPeriods()) {
				if (rp.getCompanyPeriod() == null) {
					RelationshipPeriod newRelPeriod = updatableSession.create(RelationshipPeriod.class);
					newRelPeriod.setCompanyPeriod(workcopy);
					newRelPeriod.setRelationship(rp.getRelationship());
					newRelPeriod.setNotes(rp.getNotes());
					newRelPeriod.setRevenuePercent(rp.getRevenuePercent());
				}
			}

			// Apply changes to RelationshipNote(s)
			if (pm.getRelationshipNotes() == null) {
				if (workcopy.getRelationshipNote(type) != null)
					updatableSession.delete(workcopy.getRelationshipNote(type));
			} else {
				RelationshipNote relNote = workcopy.getRelationshipNote(type);
				if (relNote == null) {
					relNote = updatableSession.create(RelationshipNote.class);
					relNote.setCompanyPeriod(workcopy);
					relNote.setRelationshipType(type);
				}
				relNote.setNotes(pm.getRelationshipNotes());
			}
		}
		updatableSession.save();

		load(RelationshipPeriodsEditorInput.editRelationshipPeriod(workcopy.getId()));
	}

	private static int findRelationshipPeriod(RelationshipPeriod rp, List<RelationshipPeriod> periods) {
		for (int index = 0; index < periods.size(); ++index) {
			if (rp == periods.get(index))
				return index;
		}
		return -1;
	}

	public void validate(IStatusManager statusManager) {
		// validate averall empty data (DET-1387)
		boolean empty = true;
		for (RelationshipType type : RelationshipType.values()) {
			empty = ((typeToPm.get(type).getRelationshipNotes() == null || typeToPm.get(type).getRelationshipNotes()
					.length() == 0) && typeToPm.get(type).getTableModel().getRowCount() == 0);
			if (!empty) {
				break;
			}
		}
		if (empty) {
			statusManager.add(Status.newError("The content could not be empty at all"));
			return;
		}

		// validate each pane
		for (RelationshipType type : RelationshipType.values()) {
			typeToPm.get(type).validate(statusManager);
		}
	}

	public String getTitle() {
		if (isNewEditor())
			return "New " + title;
		return title;
	}

	public String getShortTitle() {
		if (isNewEditor())
			return "New " + shortTitle;
		return shortTitle;
	}

	public boolean isNewEditor() {
		return newEditor;
	}

	public KeyAndRefs getKeyAndRefs() {
		return getKeyAndRefs(workcopy);
	}

	public static KeyAndRefs getKeyAndRefs(CompanyPeriod companyPeriod) {
		return new KeyAndRefs(new Key(RelationshipHistory.class, companyPeriod.getId()),
				new Key(Company.class, companyPeriod.getCompany().getId()));
	}

	public RelationshipPeriodsEditorInput createInput() {
		return RelationshipPeriodsEditorInput.editRelationshipPeriod(workcopy.getId());
	}

	public String getEntityName() {
		return "Relationship Period " + CompanyPeriodUtils.getLabel(workcopy);
	}

	public String getCompanyPeriodTitle() {
		return companyPeriodTitle;
	}

	public String getCustomerNotesText() {
		return typeToPm.get(RelationshipType.CUSTOMER).getRelationshipNotes();
	}

	public void setCustomerNotesText(String text) {
		typeToPm.get(RelationshipType.CUSTOMER).setRelationshipNotes(text);
	}

	public String getCompetitorNotesText() {
		return typeToPm.get(RelationshipType.COMPETITOR).getRelationshipNotes();
	}

	public void setCompetitorNotesText(String text) {
		typeToPm.get(RelationshipType.COMPETITOR).setRelationshipNotes(text);
	}

	public String getPartnerNotesText() {
		return typeToPm.get(RelationshipType.PARTNER).getRelationshipNotes();
	}

	public void setPartnerNotesText(String text) {
		typeToPm.get(RelationshipType.PARTNER).setRelationshipNotes(text);
	}

	public RelationshipPeriodEditorPm getRelationshipPeriodEditorPm(RelationshipType type) {
		return typeToPm.get(type);
	}

	public String getCompanyId() {
		return workcopy.getCompany().getId();
	}

}
