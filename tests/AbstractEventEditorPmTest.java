package com.revere.det.core.ui.biz.healthcare.events;

import java.io.Serializable;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.junit.Assert;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.revere.basics.datetime.SimpleDay;
import com.revere.basics.datetime.SimplePeriod;
import com.revere.basics.datetime.support.SimplePeriodFormat;
import com.revere.det.core.data.DataCreationUtils;
import com.revere.det.core.domain.Company;
import com.revere.det.core.domain.Indication;
import com.revere.det.core.domain.Sector;
import com.revere.det.core.domain.Tradename;
import com.revere.det.core.domain.healthcare.AbstractHealthcareEvent;
import com.revere.det.core.domain.healthcare.EventCompany;
import com.revere.det.core.domain.healthcare.EventIndication;
import com.revere.det.core.domain.healthcare.EventSector;
import com.revere.det.core.domain.healthcare.EventTradename;
import com.revere.det.core.ui.biz.healthcare.events.AbstractEventEditorPm.FormModel;
import com.revere.det.test.AbstractConfiguredDetTest2;
import com.revere.det.test.DetTestingConfig;
import com.revere.gui.runtime.status.DefaultStatusManager;
import com.revere.gui.runtime.status.Severity;
import com.revere.gui.runtime.status.Status;
import com.revere.test.TestUtils;
import com.revere.ulc.workbench.ui.provisional.events.Key;

public abstract class AbstractEventEditorPmTest extends AbstractConfiguredDetTest2 {

	private static final SimplePeriodFormat PERIOD_FORMAT = new SimplePeriodFormat(true);
	protected AppService appService;
	protected int errorCounter;

	@Override
	protected void configure(DetTestingConfig config) {
		super.configure(config);
		config.addHibernateMappingResource("com/revere/det/core/domain/hibernate/DetCore.hbm.xml");
		config.addHibernateMappingResource("com/revere/det/core/domain/hibernate/HealthcareEvents.hbm.xml");
	}

	@Override
	public void buildTestData() throws Throwable {
		AppService service = new AppService();
		service.setSessionFactory(config.getCommandFrameworkSessionFactory());
		appService = TestUtils.getTransactionalInstance(service, config.getTransactionManager());
	}

	@Override
	public void tearDown() {
		appService = null;
		super.tearDown();
	}

	@SuppressWarnings("unchecked")
	protected void validateEventPeriod(AbstractEventEditorPm pm, FormModel fm) {
		fm.setEventPeriod(null);
		pm.validate(new DefaultStatusManager() {

			@Override
			public void add(Status status) {
				++errorCounter;
				Assert.assertEquals(Severity.ERROR, status.getSeverity());
				Assert.assertEquals("Event period is not defined", status.getMessage());
			}
		});
		Assert.assertTrue(errorCounter > 0);
	}

	@SuppressWarnings("unchecked")
	protected void validate(AbstractEventEditorPm pm, AbstractTestData data) {
		pm.load(EventEditorInput.editEvent(data.event.getId()));
		pm.validate(new DefaultStatusManager() {

			@Override
			public void add(Status status) {
				Assert.fail("Status is not expected : " + status);
			}
		});
	}

	@SuppressWarnings("unchecked")
	protected void validateIndications(AbstractEventEditorPm pm, final AbstractTestData data) {
		pm.load(EventEditorInput.editEvent(data.event.getId()));
		pm.getIndicationEditorPm().addIndication(data.indication);
		pm.getIndicationEditorPm().addIndication(data.indication);
		pm.validate(new DefaultStatusManager() {

			@Override
			public void add(Status status) {
				Assert.fail("Status is not expected : " + status);
			}
		});
	}

	@SuppressWarnings("unchecked")
	protected void validateRelatedIndications(AbstractEventEditorPm pm, final AbstractTestData data) {
		pm.load(EventEditorInput.editEvent(data.event.getId()));
		pm.getIndicationEditorPm().addIndication(data.indication);
		EventIndication ei = pm.getIndicationEditorPm().getTableModel().getObject(0);
		pm.getIndicationEditorPm().getRelIndTableModel().setInput(ei);
		pm.getIndicationEditorPm().getRelIndTableModel().addIndication(data.relIndication);
		pm.getIndicationEditorPm().getRelIndTableModel().addIndication(data.relIndication);

		pm.validate(new DefaultStatusManager() {

			@Override
			public void add(Status status) {
				Assert.assertEquals(Severity.ERROR, status.getSeverity());
				Assert.assertEquals("The same related indications detected in rows 1, 2 for indication in row 1",
						status.getMessage());
				++errorCounter;
			}
		});
		Assert.assertEquals(1, errorCounter);
	}

	protected <E extends AbstractHealthcareEvent> void testFormModel(FormModel<E> fm) {
		fm.setAgenda("agenda");
		Assert.assertEquals("agenda", fm.getAgenda());
		Assert.assertEquals("agenda", fm.workcopy.getAgenda());
		fm.setAgenda(null);
		Assert.assertNull(fm.getAgenda());
		Assert.assertNull(fm.workcopy.getAgenda());

		fm.setCanceled(true);
		Assert.assertTrue(fm.isCanceled());
		Assert.assertTrue(fm.workcopy.isCanceled());
		fm.setCanceled(false);
		Assert.assertFalse(fm.isCanceled());
		Assert.assertFalse(fm.workcopy.isCanceled());

		SimpleDay day = new SimpleDay(2009, 1, 2);
		fm.setCloseDate(day);
		Assert.assertEquals(day, fm.getCloseDate());
		Assert.assertEquals(day, fm.workcopy.getCloseDate());
		fm.setCloseDate(null);
		Assert.assertNull(fm.getCloseDate());
		Assert.assertNull(fm.workcopy.getCloseDate());

		fm.setConfirmed(true);
		Assert.assertTrue(fm.isConfirmed());
		Assert.assertTrue(fm.workcopy.isConfirmed());
		fm.setConfirmed(false);
		Assert.assertFalse(fm.isConfirmed());
		Assert.assertFalse(fm.workcopy.isConfirmed());

		SimplePeriod period = SimplePeriod.createDayPeriod(day);
		fm.setEventPeriod(period);
		Assert.assertEquals(period, fm.getEventPeriod());
		Assert.assertEquals(period, fm.workcopy.getEventPeriod());
		fm.setEventPeriod(null);
		Assert.assertNull(fm.getEventPeriod());
		Assert.assertNull(fm.workcopy.getEventPeriod());

		fm.setName("test");
		Assert.assertEquals("test", fm.getName());
		Assert.assertEquals("test", fm.workcopy.getName());
		fm.setName(null);
		Assert.assertNull(fm.getName());
		Assert.assertNull(fm.workcopy.getName());

		fm.setNotes("notes");
		Assert.assertEquals("notes", fm.getNotes());
		Assert.assertEquals("notes", fm.workcopy.getNotes());
		fm.setNotes(null);
		Assert.assertNull(fm.getNotes());
		Assert.assertNull(fm.workcopy.getNotes());

		SimplePeriod previousPeriod = SimplePeriod.createDayPeriod(new SimpleDay(2009, 8, 7));
		fm.setPreviousPeriod(previousPeriod);
		Assert.assertEquals(previousPeriod, fm.getPreviousPeriod());
		Assert.assertEquals(previousPeriod, fm.workcopy.getPreviousPeriod());
		fm.setPreviousPeriod(null);
		Assert.assertNull(fm.getPreviousPeriod());
		Assert.assertNull(fm.workcopy.getPreviousPeriod());

		fm.setPublished(true);
		Assert.assertTrue(fm.isPublished());
		Assert.assertTrue(fm.workcopy.isPublished());
		fm.setPublished(false);
		Assert.assertFalse(fm.isPublished());
		Assert.assertFalse(fm.workcopy.isPublished());

		fm.setUrl("url");
		Assert.assertEquals("url", fm.getUrl());
		Assert.assertEquals("url", fm.workcopy.getUrl());
		fm.setUrl(null);
		Assert.assertNull(fm.getUrl());
		Assert.assertNull(fm.workcopy.getUrl());
	}

	@SuppressWarnings("unchecked")
	protected void testAddIndication(AbstractEventEditorPm pm, final AbstractTestData data) {

		pm.getIndicationEditorPm().addIndication(data.indication);
		pm.save();

		Key[] refs = pm.getKeyAndRefs().getRefs();
		TestUtils.assertBagsEqual(array(new Key(Company.class, data.company1.getId())), refs);

		// Check Event
		final Key key = pm.getKeyAndRefs().getKey();
		doInHibernateSession(new HibernateCallback() {

			public Object doInHibernate(Session session) throws HibernateException, SQLException {

				AbstractHealthcareEvent e = (AbstractHealthcareEvent) session.get(key.getType(), (Serializable) key
						.getId());

				Assert.assertEquals(1, e.getIndications().size());
				Assert.assertEquals(data.indication, e.getIndications().get(0).getIndication());

				session.clear();
				return null;
			}
		});
	}

	@SuppressWarnings("unchecked")
	protected void testRemoveIndication(AbstractEventEditorPm pm, final AbstractTestData data) {

		pm.getIndicationEditorPm().addIndication(data.indication);
		pm.save();

		pm.getIndicationEditorPm().removeIndications(new int[] { 0 });
		pm.save();

		Key[] refs = pm.getKeyAndRefs().getRefs();
		Assert.assertEquals(0, refs.length);

		// Check Event
		final Key key = pm.getKeyAndRefs().getKey();
		doInHibernateSession(new HibernateCallback() {

			public Object doInHibernate(Session session) throws HibernateException, SQLException {

				AbstractHealthcareEvent e = (AbstractHealthcareEvent) session.get(key.getType(), (Serializable) key
						.getId());
				Assert.assertEquals(0, e.getIndications().size());

				session.clear();
				return null;
			}
		});
	}

	@SuppressWarnings("unchecked")
	protected void testAddRelatedIndication(AbstractEventEditorPm pm, final AbstractTestData data) {

		pm.getIndicationEditorPm().addIndication(data.indication);
		pm.getIndicationEditorPm().getRelIndTableModel().setInput(pm.getIndicationEditorPm().getEventIndication(0));
		pm.getIndicationEditorPm().addRelatedIndication(data.relIndication);
		pm.save();

		Key[] refs = pm.getKeyAndRefs().getRefs();
		TestUtils.assertBagsEqual(array(new Key(Company.class, data.company1.getId())), refs);

		// Check Event
		final Key key = pm.getKeyAndRefs().getKey();
		doInHibernateSession(new HibernateCallback() {

			public Object doInHibernate(Session session) throws HibernateException, SQLException {

				AbstractHealthcareEvent e = (AbstractHealthcareEvent) session.get(key.getType(), (Serializable) key
						.getId());

				Assert.assertEquals(1, e.getIndications().size());
				EventIndication eind = e.getIndications().get(0);
				Assert.assertEquals(data.indication, eind.getIndication());
				Assert.assertEquals(1, eind.getRelatedIndications().size());
				Assert.assertEquals(data.relIndication, eind.getRelatedIndications().get(0).getIndication());

				session.clear();
				return null;
			}
		});

	}

	@SuppressWarnings("unchecked")
	protected void testRemoveRelatedIndication(AbstractEventEditorPm pm, final AbstractTestData data) {

		pm.getIndicationEditorPm().addIndication(data.indication);
		pm.getIndicationEditorPm().getRelIndTableModel().setInput(pm.getIndicationEditorPm().getEventIndication(0));
		pm.getIndicationEditorPm().addRelatedIndication(data.relIndication);
		pm.save();

		pm.getIndicationEditorPm().getRelIndTableModel().removeIndications(new int[] { 0 });
		pm.save();

		Key[] refs = pm.getKeyAndRefs().getRefs();
		TestUtils.assertBagsEqual(array(new Key(Company.class, data.company1.getId())), refs);

		// Check Event
		final Key key = pm.getKeyAndRefs().getKey();
		doInHibernateSession(new HibernateCallback() {

			public Object doInHibernate(Session session) throws HibernateException, SQLException {

				AbstractHealthcareEvent e = (AbstractHealthcareEvent) session.get(key.getType(), (Serializable) key
						.getId());

				Assert.assertEquals(1, e.getIndications().size());
				EventIndication eind = e.getIndications().get(0);
				Assert.assertEquals(data.indication, eind.getIndication());
				Assert.assertEquals(0, eind.getRelatedIndications().size());

				session.clear();
				return null;
			}
		});
	}

	@SuppressWarnings("unchecked")
	protected void testRefs(AbstractEventEditorPm pm, AbstractTestData data) {
		pm.getIndicationEditorPm().addIndication(data.indication);
		pm.getIndicationEditorPm().addIndication(data.relIndication);
		pm.save();

		Key[] refs = pm.getKeyAndRefs().getRefs();
		TestUtils.assertBagsEqual(array(new Key(Company.class, data.company1.getId()), new Key(Company.class,
				data.company2.getId())), refs);
	}

	@SuppressWarnings("unchecked")
	protected void testSaveExisting(AbstractEventEditorPm pm, FormModel fm) {

		fm.setAgenda("ag1");
		fm.setCanceled(true);
		final SimpleDay closeDate = new SimpleDay();
		fm.setCloseDate(closeDate);
		fm.setConfirmed(true);
		final SimplePeriod eventPeriod = SimplePeriod.createDayPeriod(new SimpleDay(2009, 5, 5));
		fm.setEventPeriod(eventPeriod);
		fm.setName("name1");
		fm.setNotes("notes1");
		final SimplePeriod previousPeriod = SimplePeriod.createDayPeriod(new SimpleDay(2008, 5, 5));
		fm.setPreviousPeriod(previousPeriod);
		fm.setPublished(true);
		fm.setUrl("url1");

		pm.save();

		Assert.assertFalse(pm.isNewEditor());

		// Check Event
		final Key key = pm.getKeyAndRefs().getKey();
		doInHibernateSession(new HibernateCallback() {

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				AbstractHealthcareEvent e = (AbstractHealthcareEvent) session.get(key.getType(), (Serializable) key
						.getId());

				Assert.assertEquals("ag1", e.getAgenda());
				Assert.assertTrue(e.isCanceled());
				Assert.assertTrue(e.isConfirmed());
				Assert.assertTrue(e.isPublished());
				Assert.assertEquals("name1", e.getName());
				Assert.assertEquals("notes1", e.getNotes());
				Assert.assertEquals("url1", e.getUrl());

				Assert.assertEquals(eventPeriod, e.getEventPeriod());
				Assert.assertEquals(previousPeriod, e.getPreviousPeriod());
				Assert.assertEquals(closeDate, e.getCloseDate());

				session.clear();
				return null;
			}
		});
	}

	protected void testSaveNew(AbstractEventEditorPm<? extends AbstractHealthcareEvent> pm,
			FormModel<? extends AbstractHealthcareEvent> fm) {

		fm.setAgenda("ag2");
		fm.setCanceled(true);
		final SimpleDay closeDate = new SimpleDay();
		fm.setCloseDate(closeDate);
		fm.setConfirmed(true);
		final SimplePeriod eventPeriod = SimplePeriod.createDayPeriod(new SimpleDay(2009, 5, 5));
		fm.setEventPeriod(eventPeriod);
		fm.setName("name2");
		fm.setNotes("notes2");
		final SimplePeriod previousPeriod = SimplePeriod.createDayPeriod(new SimpleDay(2008, 5, 5));
		fm.setPreviousPeriod(previousPeriod);
		fm.setPublished(true);
		fm.setUrl("url2");

		pm.save();

		Assert.assertFalse(pm.isNewEditor());
		Assert.assertNotNull(pm.getKeyAndRefs().getKey().getId());
		Assert.assertEquals(pm.getEventType(), pm.getKeyAndRefs().getKey().getType());

		Assert.assertEquals(EventEditorInput.editEvent((String) pm.getKeyAndRefs().getKey().getId()), pm.createInput());

		// Check Event
		final Key key = pm.getKeyAndRefs().getKey();

		doInHibernateSession(new HibernateCallback() {

			public Object doInHibernate(Session session) throws HibernateException, SQLException {

				AbstractHealthcareEvent e = (AbstractHealthcareEvent) session.get(key.getType(), (Serializable) key
						.getId());

				Assert.assertEquals("ag2", e.getAgenda());
				Assert.assertTrue(e.isCanceled());
				Assert.assertTrue(e.isConfirmed());
				Assert.assertTrue(e.isPublished());
				Assert.assertEquals("name2", e.getName());
				Assert.assertEquals("notes2", e.getNotes());
				Assert.assertEquals("url2", e.getUrl());

				Assert.assertEquals(eventPeriod, e.getEventPeriod());
				Assert.assertEquals(previousPeriod, e.getPreviousPeriod());
				Assert.assertEquals(closeDate, e.getCloseDate());

				session.clear();
				return null;
			}
		});
	}

	@SuppressWarnings("unchecked")
	protected void testLoadExisting(AbstractEventEditorPm pm, FormModel fm, AbstractTestData data) {
		Assert.assertFalse(pm.isNewEditor());
		Assert.assertEquals(new Key(data.event.getClass(), data.event.getId()), pm.getKeyAndRefs().getKey());
		Assert.assertEquals(0, pm.getKeyAndRefs().getRefs().length);

		Assert.assertEquals("agenda", fm.getAgenda());
		Assert.assertEquals("notes", fm.getNotes());
		Assert.assertNull(fm.getName());
		Assert.assertNull(fm.getUrl());
		Assert.assertNull(fm.getCloseDate());
		Assert.assertNull(fm.getPreviousPeriod());
		Assert.assertEquals(data.period, fm.getEventPeriod());
	}

	@SuppressWarnings("unchecked")
	protected void testLoadNew(AbstractEventEditorPm pm, FormModel fm) {
		Assert.assertTrue(pm.isNewEditor());
		Assert.assertEquals(new Key(pm.getEventType(), null), pm.getKeyAndRefs().getKey());
		Assert.assertEquals(0, pm.getKeyAndRefs().getRefs().length);

		Assert.assertNull(fm.getAgenda());
		Assert.assertNull(fm.getNotes());
		Assert.assertNull(fm.getName());
		Assert.assertNull(fm.getUrl());
		Assert.assertNull(fm.getCloseDate());
		Assert.assertNull(fm.getPreviousPeriod());
		Assert.assertNull(fm.getEventPeriod());
	}

	@SuppressWarnings("unchecked")
	protected void testLoadNewWithEventPeriod(AbstractEventEditorPm pm, FormModel fm, SimplePeriod p) {
		Assert.assertTrue(pm.isNewEditor());
		Assert.assertEquals(new Key(pm.getEventType(), null), pm.getKeyAndRefs().getKey());
		Assert.assertEquals(0, pm.getKeyAndRefs().getRefs().length);

		Assert.assertNull(fm.getAgenda());
		Assert.assertNull(fm.getNotes());
		Assert.assertNull(fm.getName());
		Assert.assertNull(fm.getUrl());
		Assert.assertNull(fm.getCloseDate());
		Assert.assertNull(fm.getPreviousPeriod());
		Assert.assertEquals(p, fm.getEventPeriod());
	}

	protected boolean isCompanyAttachedToEvent(final Key key, final Company c) {
		return (Boolean) doInTransaction(new TransactionCallback() {

			public Object doInTransaction(TransactionStatus status) {
				Session session = getConfig().getHibernateSessionFactory().getCurrentSession();
				AbstractHealthcareEvent e = (AbstractHealthcareEvent) session.get(key.getType(), (Serializable) key
						.getId());
				for (EventCompany ec : e.getCompanies()) {
					if (ec.getCompany().equals(c))
						return Boolean.TRUE;
				}
				return Boolean.FALSE;
			}
		});
	}

	protected boolean isTradenameAttachedToEvent(final Key key, final Tradename t) {
		return (Boolean) doInTransaction(new TransactionCallback() {

			public Object doInTransaction(TransactionStatus status) {
				Session session = getConfig().getHibernateSessionFactory().getCurrentSession();
				AbstractHealthcareEvent e = (AbstractHealthcareEvent) session.get(key.getType(), (Serializable) key
						.getId());
				for (EventTradename et : e.getTradenames()) {
					if (et.getTradename().equals(t))
						return Boolean.TRUE;
				}
				return Boolean.FALSE;
			}
		});
	}

	protected boolean isSectorAttachedToEvent(final Key key, final Sector s) {
		return (Boolean) doInTransaction(new TransactionCallback() {

			public Object doInTransaction(TransactionStatus status) {
				Session session = getConfig().getHibernateSessionFactory().getCurrentSession();
				AbstractHealthcareEvent e = (AbstractHealthcareEvent) session.get(key.getType(), (Serializable) key
						.getId());
				for (EventSector es : e.getSectors()) {
					if (es.getSector().equals(s))
						return Boolean.TRUE;
				}
				return Boolean.FALSE;
			}
		});
	}

	protected static String getPeriodLabel(SimplePeriod period) {
		if (null != period)
			return PERIOD_FORMAT.format(period);
		return "";
	}

	@SuppressWarnings("unchecked")
	protected <E extends AbstractHealthcareEvent> E doWithEvent(final E e, final EventCallback<E> callback) {

		return (E) doInTransaction(new TransactionCallback() {

			public Object doInTransaction(TransactionStatus status) {
				Session session = getConfig().getHibernateSessionFactory().getCurrentSession();

				boolean isNew = (null == e.getId());
				E event;
				if (isNew)
					event = e;
				else
					event = (E) session.get(e.getClass(), e.getId());

				callback.execute(event);

				if (isNew)
					session.save(event);
				return event;
			}
		});
	}

	public static interface EventCallback<T> {
		public void execute(T e);
	}

	public abstract static class AbstractTestData<E extends AbstractHealthcareEvent> {
		protected SimplePeriod period;
		protected Indication indication;
		protected Indication relIndication;
		protected Company company1;
		protected Company company2;
		protected E event;
		protected Tradename tradename1;
		protected Tradename tradename2;
		protected Sector sector1;

		public AbstractTestData(Session session) {
			period = SimplePeriod.createDayPeriod(new SimpleDay(2009, 1, 1));
			{
				company1 = DataCreationUtils.createCompany(session, "DeleteMeCompany001", "DMC001");
				tradename1 = DataCreationUtils.createTradename(session, company1, "Product1");
				indication = DataCreationUtils.createIndication(session, tradename1, "indication1");
			}

			{
				company2 = DataCreationUtils.createCompany(session, "DeleteMeCompany002", "DMC002");
				tradename2 = DataCreationUtils.createTradename(session, company2, "Product2");
				relIndication = DataCreationUtils.createIndication(session, tradename2, "indication2");
			}

			sector1 = new Sector("DMS001", "sector 0001");
			session.save(sector1);
		}
	}

}
