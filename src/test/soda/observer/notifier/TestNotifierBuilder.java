package test.soda.observer.notifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import soda.observer.notifier.NotifierBuilder;



/**
 * This Test aim to exercise NotifierBuilder class.
 * This Test has achieved Code Coverage
 * 
 * @author vong vithyea srey
 *
 */
public class TestNotifierBuilder {

	@Test
	public void testSetEmailNotifier() {
		NotifierBuilder nbuilder = new NotifierBuilder();
		nbuilder.setEmailNotifier("abc@gmail.com");
		assertEquals(nbuilder.getNotifiers().size(), 1);
		assertTrue(nbuilder.getNotifiers().get(0).toString().startsWith("soda.observer.notifier.EmailNotifier"));
		
		try{
			nbuilder.setEmailNotifier("   ");
			fail("Expect IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
		
		try{
			nbuilder.setEmailNotifier(null);
			fail("Expect IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
		
		try{
			nbuilder.setEmailNotifier("abc");
			fail("Expect IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
	}

}
