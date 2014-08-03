package test.soda.observer.notifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import soda.observer.notifier.EmailNotifier;



/**
 * This Test aim to exercise QueryProcessor class.
 * This Test has achieved Code Coverage
 * 
 * @author vong vithyea srey
 *
 */
public class TestEmailNotifier {

	@Test
	public void testConstructor() {
		EmailNotifier emn = new EmailNotifier("abc@gmail.com");
		assertEquals(emn.getContact(), "abc@gmail.com");
		assertEquals(emn.getNotifierType(), "email");
		
		try{
			new EmailNotifier("abcl");
			fail("Expect IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
		
		try{
			new EmailNotifier("   ");
			fail("Expect IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
		
		try{
			new EmailNotifier(null);
			fail("Expect IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
	}
	
	
	
	@Test
	public void testIsValidEmailAddress(){
		assertTrue(!EmailNotifier.isValidEmailAddress("abc"));
		assertTrue(EmailNotifier.isValidEmailAddress("abc@gmail.com"));
		assertTrue(!EmailNotifier.isValidEmailAddress("  "));
	}
	
	
	
	@Test
	public void testSendingNotification(){
		EmailNotifier em = new EmailNotifier("srey.vongvithyea@gmail.com");
		
		try{
			em.sendNotification("   ");
			fail("Expect IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
		
		try{
			em.sendNotification(null);
			fail("Expect IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
		
		try{
			em.sendNotification("test case");
		} catch (IllegalArgumentException e) {
			fail("Expect IllegalArgumentException");
		}
	}
	

}
