package org.goobi.production.flow.statistics.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Observable;

import org.hibernate.Criteria;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("All tests crashing")
public class UserDefinedFilterTest {

	UserDefinedFilter filter = new UserDefinedFilter("stepdone:5");
	List<Integer> globalId = filter.getIDList();
	UserDefinedFilter filter2 = new UserDefinedFilter(globalId);

	@Test
	public void testUserDefinedFilterListOfInteger() {
		List<Integer> id = filter.getIDList();
		UserDefinedFilter filterWithIds = new UserDefinedFilter(id);
		assertNotNull(filterWithIds);
	}

	@Test
	public void testUserDefinedFilterString() {
		UserDefinedFilter filterWithString = new UserDefinedFilter("stepdone:5");
		assertNotNull(filterWithString);
	}

	@Test
	public void testGetCriteria() {
		Criteria crit1, crit2;
		crit1 = filter.getCriteria();
		crit2 = filter2.getCriteria();
		assertEquals(
			"CriteriaImpl(de.sub.goobi.Beans.Prozess:this[Subcriteria(projekt:proj), Subcriteria(schritte:steps)][steps.reihenfolge=5 and steps.bearbeitungsstatus=3, ()])",
			crit1.toString());
		assertNotNull(crit2);
	}

	@Test
	public void testGetName() {
		filter.setName("test");
		assertEquals("test", filter.getName());
	}

	@Test
	public void testSetName() {
		filter.setName("test");
		assertEquals("test", filter.getName());
	}

	@Test
	public void testSetFilter() {
		filter.setFilter("stepdone:5");
		assertEquals("stepdone:5", filter.getFilter());
	}

	@Test
	public void testGetFilter() {
		filter.setFilter("stepdone:5");
		assertEquals("stepdone:5", filter.getFilter());
	}

	@Test
	public void testClone() {
		UserDefinedFilter clone = filter.clone();
		assertNotNull(clone);
	}

	@Test
	public void testGetSourceData() {
		List<Object> sourceList = filter.getSourceData();
		assertNotNull(sourceList);
	}

	@Test
	public void testGetIDList() {
		List<Integer> idList = filter.getIDList();
		assertNotNull(idList);
	}

	@Test
	public void testGetObservable() {
		Observable obs = filter.getObservable();
		assertNotNull(obs);
	}

	@Test
	public void testStepDone() {
		int stepdone = filter.stepDone();
		assertNotNull(stepdone);
		assertEquals(5, stepdone);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testSetSQL() {
		filter.setSQL("someString");
	}

}
