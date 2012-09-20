package eu.europeana.portal2.web.controllers;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

import eu.europeana.corelib.utils.ImageUtils;
import eu.europeana.portal2.web.controllers.utils.RSSFeedParser;
import eu.europeana.portal2.web.controllers.utils.RSSImage;
import eu.europeana.portal2.web.presentation.model.data.submodel.FeedEntry;

/**
 * Testing the blog parsing features
 * 
 * @author peter.kiraly@kb.nl
 */
public class BlogTest {
	
	// TODO: use it later, to test something else
	// private List<FeedEntry> pinterestEntries;
	// private Calendar pinterestAge;

	private final Logger log = Logger.getLogger(getClass().getName());

	private String blogFeedUrl = "http://blog.europeana.eu/feed/";
	private Integer blogItemLimit = 3;
	private RSSFeedParser parser;
	private int[] responsiveWidths = new int[]{200, 300, 700, 200};

	@Before
	public void runBeforeEveryTests() {
		parser = new RSSFeedParser(blogFeedUrl, blogItemLimit.intValue(), new String[0], new int[0]);
	}

	/**
	 * Tests the image extraction
	 */
	@Test
	public void testImageExtraction() {
		
		List<FeedEntry> newEntries = parser.readFeed();
		if ((newEntries != null) && (newEntries.size() > 0)) {
			for (FeedEntry entry : newEntries) {
				assertTrue(entry.getImages().size() > 0);
				for (RSSImage image : entry.getImages()) {
					assertNotNull(image.getSrc());
					assertTrue(image.getSrc().length() > 0);
				}
			}
		}
	}

	/**
	 * Tests the getPlainDescription function of FeedEntry object 
	 */
	@Test
	public void testPlainDescription() {
		List<FeedEntry> newEntries = parser.readFeed();
		if ((newEntries != null) && (newEntries.size() > 0)) {
			for (FeedEntry entry : newEntries) {
				assertTrue(entry.getPlainDescription().indexOf("<") == -1);
				assertTrue(entry.getPlainDescription().indexOf(">") == -1);
			}
		}
	}

	@Test
	public void testImageScale() {
		String[] locations = new String[]{
			"http://blog.europeana.eu/wp-content/uploads/2012/09/lord-kitchener-wants-you-1914-this-british-wwi-recruiting.jpg",
			"http://blog.europeana.eu/wp-content/uploads/2012/09/new-portal-europeana.jpg",
			"http://blog.europeana.eu/wp-content/uploads/2012/09/click-here-to-start.jpg",
			"http://blog.europeana.eu/wp-content/uploads/2012/09/Petrovic_Miodrag_photo.jpg",
			"http://blog.europeana.eu/wp-content/uploads/2012/09/11.jpg",
			"http://blog.europeana.eu/wp-content/uploads/2012/09/468px-2.jpg",
			"http://blog.europeana.eu/wp-content/uploads/2012/09/469px-3.jpg",
			"http://blog.europeana.eu/wp-content/uploads/2012/09/800px-4_1.jpg",
			"http://blog.europeana.eu/wp-content/uploads/2012/09/800px-4_22.jpg",
			"http://blog.europeana.eu/wp-content/uploads/2012/09/ram2.jpg"
		};

		for (String location : locations) {
			BufferedImage orig = null;
			try {
				orig = ImageIO.read(new URL(location));
			} catch (MalformedURLException e) {
				log.severe("MalformedURLException during reading in location: " + e.getLocalizedMessage());
				e.printStackTrace();
			} catch (IOException e) {
				log.severe("IOException during reading in location: " + e.getLocalizedMessage());
				e.printStackTrace();
			}

			for (int i = 0, len = responsiveWidths.length; i < len; i++) {
				BufferedImage responsive = null;
				try {
					int height = (int)Math.ceil((responsiveWidths[i] * orig.getHeight()) / orig.getWidth());
					responsive = ImageUtils.scale(orig, responsiveWidths[i], height);
					assertTrue(String.format("The expected (%d) and actual (%d) width should be in the same range", responsiveWidths[i], responsive.getWidth()),
							(responsiveWidths[i] <= responsive.getWidth()+1 || responsiveWidths[i] >= responsive.getWidth()+1));
					assertTrue(String.format("The expected (%d) and actual (%d) height should be in the same range", height, responsive.getHeight()),
							(height <= responsive.getHeight()+1 || height >= responsive.getHeight()+1));
					assertTrue(responsive.getHeight() > 1);
				} catch (IOException e) {
					log.severe("IOException during scaling image: " + e.getLocalizedMessage());
					e.printStackTrace();
				}
			}
		}
	}
}
