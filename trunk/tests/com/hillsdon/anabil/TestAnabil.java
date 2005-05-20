package com.hillsdon.anabil;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import anabil.Anabil;
import anabil.Call;
import anabil.Person;
import anabil.impl.SimpleCall;
import anabil.impl.SimplePerson;


/**
 * Tests the bill analyser component.
 */
public final class TestAnabil extends TestCase {

  /**
   * The bill analyser under test.
   */
  private Anabil _anabil;

  protected void setUp() throws Exception {
    _anabil = new Anabil();
  }
  
  /**
   * It would be sad to forget.
   */
  public void testRemembersPeople() {
    Person person = new SimplePerson("person");
    _anabil.addUser(person);
    assertEquals(person, _anabil.getUser("person"));
    assertEquals(person, _anabil.getUsers()[0]);
  }
  
  /**
   * There are many more cases to be covered here.
   */
  public void testIdentifiesCalls() {
    //Set up people.
    Person person1 = new SimplePerson("person1");
    person1.addLocation("earth");
    person1.addLocation("mars");
    person1.addNumber("0");
    Person person2 = new SimplePerson("person2");
    person2.addLocation("earth");
    person1.addNumber("1");
    Set bothPeople = new HashSet();
    bothPeople.add(person1);
    bothPeople.add(person2);
    _anabil.addUser(person1);
    _anabil.addUser(person2);
    
    //Set up calls.
    //More possibilities to come.
    Call sharedLocationUnknownNumber = new SimpleCall("", "earth", "22", 10);
    Call sharedLocationButIdentfiedByNumber = new SimpleCall("", "earth", "0", 10);
    
    _anabil.identifyCalls(new Call[] {sharedLocationButIdentfiedByNumber, sharedLocationUnknownNumber});
    
    assertEquals(bothPeople, sharedLocationUnknownNumber.getSuggestedPeople());
    assertNull(sharedLocationUnknownNumber.getSuggestedPerson());
    assertEquals(Collections.singleton(person1), sharedLocationButIdentfiedByNumber.getSuggestedPeople());
    assertEquals(person1, sharedLocationButIdentfiedByNumber.getSuggestedPerson());
    
    assertNull(sharedLocationButIdentfiedByNumber.getPersonResponsible());
    assertNull(sharedLocationUnknownNumber.getPersonResponsible());
    
    // Now explicitly set a responsible person.
    sharedLocationUnknownNumber.setPersonResponsible(person1);
    assertEquals(person1, sharedLocationUnknownNumber.getPersonResponsible());
    // Check that the _suggestions_ haven't changed.
    assertEquals(bothPeople, sharedLocationUnknownNumber.getSuggestedPeople());
  }
  
}
