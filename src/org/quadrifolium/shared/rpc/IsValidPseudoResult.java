package org.quadrifolium.shared.rpc;

import org.quadrifolium.shared.database.Person;

import net.customware.gwt.dispatch.shared.Result;

/**
 * Object that comes back from a call to the isValidPseudoHandler servlet
 * 
 * @author PA
 *
 */
public class IsValidPseudoResult implements Result 
{
	/**
	 * <code>true</code> if everything went well and the pseudo exists, <code>false</code> if a problem occurred or the pseudo was not found
	 */
	private boolean _bSuccess = false ;
	
	/**
	 * It everything went well, informations from the database entry attached to the pseudo 
	 */
	private Person  _Person   = new Person() ;
	
	/**
	 * A reminder of the pseudo that was searched for, useful, since calls are asynchronous, in order to know, for example, what pseudo was not found
	 */
	private String  _sPseudo  = "" ;
	
	public IsValidPseudoResult() {
		super() ;
	}
	
	public IsValidPseudoResult(final Person person, final String sPseudo) 
	{
		super() ;
		setPerson(person) ;
		_sPseudo = sPseudo ;
	}

	public Person getPerson() {
		return _Person ;
	}
	public void setPerson(final Person person) {
		_Person.initFromPerson(person) ;
	}
	
	public boolean wasSuccessful() {
		return _bSuccess ;
	}
	public void setSuccess(boolean bSuccess) {
		_bSuccess = bSuccess ;
	}
	
	public String getPseudo() {
		return _sPseudo ;
	}
	public void setPseudo(final String sPseudo) {
		_sPseudo = sPseudo ;
	}
}
