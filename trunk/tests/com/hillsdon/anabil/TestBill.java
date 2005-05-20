package com.hillsdon.anabil;

import java.util.Collections;

import junit.framework.TestCase;

import anabil.Bill;
import anabil.Call;
import anabil.Person;
import anabil.impl.SimpleBill;
import anabil.impl.SimpleCall;
import anabil.impl.SimplePerson;


/**
 * Test a bill.
 * 
 * @author mth
 */
public final class TestBill extends TestCase {

  /**
   * Tests that the totals are figured out and associated with the correct people.
   * The personResponsible should be used, or failing that the suggestedPerson.
   * Failing that the call's cost shouldn't be allocated to anyone.
   */
  public void testTotalsAllocatedToPeople() {
    Person person1 = new SimplePerson("person1");
    Person person2 = new SimplePerson("person2");
    
    Call[] calls = new Call[] {
        new SimpleCall("", "", "", 1),
        new SimpleCall("", "", "", 3),
        new SimpleCall("", "", "", 7),
    };
    calls[0].setPersonResponsible(person1);
    calls[1].setSuggestedPeople(Collections.singleton(person1));
    calls[2].setPersonResponsible(person2);
    
    Bill bill = new SimpleBill(calls);
    bill.updateTotals();
    assertEquals(4, bill.getTotalFor(person1), 0);
    assertEquals(7, bill.getTotalFor(person2), 0);
  }
  
  /**
   * Test that we don't make up totals for people we don't know.
   */
  public void testSomeRandomPersonGetsZero() {
    Bill bill = new SimpleBill(new Call[] {});
    bill.updateTotals();
    assertEquals(0, bill.getTotalFor(new SimplePerson("person")), 0);
  }
  
}
