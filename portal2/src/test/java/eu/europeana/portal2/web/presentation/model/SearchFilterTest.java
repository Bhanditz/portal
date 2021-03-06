package eu.europeana.portal2.web.presentation.model;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import eu.europeana.corelib.definitions.solr.model.Query;
import eu.europeana.portal2.web.presentation.model.submodel.SearchFilter;
import eu.europeana.portal2.web.presentation.model.submodel.impl.BriefBeanViewImpl;

@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/portal2-test.xml"})
@Ignore // disabled as portal project is deprecated
public class SearchFilterTest {

	BriefBeanViewImpl view;
	Map<String, String[]> urlParams;

	@Before
	public void runBeforeEveryTests() {
		view = new BriefBeanViewImpl();
		urlParams = new HashMap<String, String[]>();
	}

	@Test
	public void testSimple() {
		Query query = new Query("paris");
		view.makeFilters(query, urlParams);
		List<SearchFilter> filters = view.getSearchFilters();

		assertEquals(1, filters.size());

		SearchFilter paris = filters.get(0);
		assertEquals("/portal/search.html", paris.getRemoveLinkUrl());
		assertEquals(null, paris.getLabelObject().getField());
		assertEquals(null, paris.getLabelObject().getFieldCode());
		assertEquals("paris", paris.getLabelObject().getValue());
		assertEquals("paris", paris.getLabelObject().getLabel());
		assertEquals(null, paris.getField());
		assertEquals(null, paris.getFieldCode());
		assertEquals("paris", paris.getValue());
		assertEquals("paris", paris.getLabel());
	}

	@Test
	public void testOneRefinements() {
		Query query = new Query("paris").addRefinement("szolnok");
		view.makeFilters(query, urlParams);
		List<SearchFilter> filters = view.getSearchFilters();

		assertEquals(2, filters.size());

		// check first item
		SearchFilter paris = filters.get(0);
		assertEquals("/portal/search.html?qf=szolnok", paris.getRemoveLinkUrl());
		assertEquals(null, paris.getLabelObject().getField());
		assertEquals(null, paris.getLabelObject().getFieldCode());
		assertEquals("paris", paris.getLabelObject().getValue());
		assertEquals("paris", paris.getLabelObject().getLabel());
		assertEquals(null, paris.getField());
		assertEquals(null, paris.getFieldCode());
		assertEquals("paris", paris.getValue());
		assertEquals("paris", paris.getLabel());

		// check second item
		SearchFilter szolnok = filters.get(1);
		assertEquals("/portal/search.html?query=paris", szolnok.getRemoveLinkUrl());
		assertEquals(null, szolnok.getLabelObject().getField());
		assertEquals(null, szolnok.getLabelObject().getFieldCode());
		assertEquals("szolnok", szolnok.getLabelObject().getValue());
		assertEquals("szolnok", szolnok.getLabelObject().getLabel());
		assertEquals(null, szolnok.getField());
		assertEquals(null, szolnok.getFieldCode());
		assertEquals("szolnok", szolnok.getValue());
		assertEquals("szolnok", szolnok.getLabel());
	}

	@Test
	public void testOneFieldedRefinement() {
		Query query = new Query("paris").addRefinement("COUNTRY:hungary");
		view.makeFilters(query, urlParams);
		List<SearchFilter> filters = view.getSearchFilters();

		assertEquals(2, filters.size());
		
		// check first item
		SearchFilter paris = filters.get(0);
		assertEquals("/portal/search.html?qf=COUNTRY%3Ahungary", paris.getRemoveLinkUrl());
		assertEquals(null, paris.getLabelObject().getField());
		assertEquals(null, paris.getLabelObject().getFieldCode());
		assertEquals("paris", paris.getLabelObject().getValue());
		assertEquals("paris", paris.getLabelObject().getLabel());
		assertEquals(null, paris.getField());
		assertEquals(null, paris.getFieldCode());
		assertEquals("paris", paris.getValue());
		assertEquals("paris", paris.getLabel());

		// check second item
		SearchFilter hungary = filters.get(1);
		assertEquals("/portal/search.html?query=paris", hungary.getRemoveLinkUrl());
		assertEquals("COUNTRY", hungary.getLabelObject().getField());
		assertEquals("ByCountry_t", hungary.getLabelObject().getFieldCode());
		assertEquals("hungary", hungary.getLabelObject().getValue());
		assertEquals("hungary", hungary.getLabelObject().getLabel());
		assertEquals("COUNTRY", hungary.getField());
		assertEquals("ByCountry_t", hungary.getFieldCode());
		assertEquals("hungary", hungary.getValue());
		assertEquals("hungary", hungary.getLabel());
	}
	
	@Test
	public void testTwoFieldedRefinement() {
		Query query = new Query("paris").addRefinement("COUNTRY:hungary").addRefinement("TYPE:document");
		view.makeFilters(query, urlParams);
		List<SearchFilter> filters = view.getSearchFilters();

		assertEquals(3, filters.size());
		
		// check first item
		SearchFilter paris = filters.get(0);
		assertEquals("/portal/search.html?qf=COUNTRY%3Ahungary&qf=TYPE%3Adocument", paris.getRemoveLinkUrl());
		assertEquals(null, paris.getLabelObject().getField());
		assertEquals(null, paris.getLabelObject().getFieldCode());
		assertEquals("paris", paris.getLabelObject().getValue());
		assertEquals("paris", paris.getLabelObject().getLabel());
		assertEquals(null, paris.getField());
		assertEquals(null, paris.getFieldCode());
		assertEquals("paris", paris.getValue());
		assertEquals("paris", paris.getLabel());

		// check second item
		SearchFilter hungary = filters.get(1);
		assertEquals("/portal/search.html?query=paris&qf=TYPE%3Adocument", hungary.getRemoveLinkUrl());
		assertEquals("COUNTRY", hungary.getLabelObject().getField());
		assertEquals("ByCountry_t", hungary.getLabelObject().getFieldCode());
		assertEquals("hungary", hungary.getLabelObject().getValue());
		assertEquals("hungary", hungary.getLabelObject().getLabel());
		assertEquals("COUNTRY", hungary.getField());
		assertEquals("ByCountry_t", hungary.getFieldCode());
		assertEquals("hungary", hungary.getValue());
		assertEquals("hungary", hungary.getLabel());

		// check second item
		SearchFilter document = filters.get(2);
		assertEquals("/portal/search.html?query=paris&qf=COUNTRY%3Ahungary", document.getRemoveLinkUrl());
		assertEquals("TYPE", document.getLabelObject().getField());
		assertEquals("ByMediatype_t", document.getLabelObject().getFieldCode());
		assertEquals("document", document.getLabelObject().getValue());
		assertEquals("document", document.getLabelObject().getLabel());
		assertEquals("TYPE", document.getField());
		assertEquals("ByMediatype_t", document.getFieldCode());
		assertEquals("document", document.getValue());
		assertEquals("document", document.getLabel());
	}

	@Test
	public void testUrlParams() {
		Query query = new Query("paris");
		urlParams.put("sort", new String[]{"title"});
		view.makeFilters(query, urlParams);
		List<SearchFilter> filters = view.getSearchFilters();

		assertEquals(1, filters.size());

		SearchFilter paris = filters.get(0);
		assertEquals("/portal/search.html?sort=title", paris.getRemoveLinkUrl());
		assertEquals(null, paris.getLabelObject().getField());
		assertEquals(null, paris.getLabelObject().getFieldCode());
		assertEquals("paris", paris.getLabelObject().getValue());
		assertEquals("paris", paris.getLabelObject().getLabel());
		assertEquals(null, paris.getField());
		assertEquals(null, paris.getFieldCode());
		assertEquals("paris", paris.getValue());
		assertEquals("paris", paris.getLabel());
	}

}
