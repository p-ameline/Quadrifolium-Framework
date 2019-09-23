package org.quadrifolium.client.loc ;

import com.google.gwt.i18n.client.Constants;

public interface QuadrifoliumConstants extends Constants
{
	// all methods corresponding to LoginView.properties's items
	String userName() ;
	String passWord() ;
	String loginBtn() ;
	String loginFailed() ;
	String loginSendIds() ;
	
	String verbatim() ;
	
	String Undefined() ;
		
	String generalMonthJanuary() ;
	String generalMonthFebruary() ;
	String generalMonthMarch() ;
	String generalMonthApril() ;
	String generalMonthMay() ;
	String generalMonthJune() ;
	String generalMonthJully() ;
	String generalMonthAugust() ;
	String generalMonthSeptember() ;
	String generalMonthOctober() ;
	String generalMonthNovember() ;
	String generalMonthDecember() ;
	String generalDayUndefined() ;
	String generalMonthUndefined() ;
	String generalYearUndefined() ;
	
	String generalTimeSeparator() ;

	String generalPassword() ;
	String generalPasswordConf() ;
	String generalPseudo() ;
	String generalEmail() ;
	String generalEmailConfirmed() ;
	String generalLanguage() ;
	
	String editUserData() ;
	String buildCsv() ;
	
	String backHome() ;

	String captionLemmas() ;
	String captionOtherLemmas() ;
	String captionDefinitions() ;
	String captionSemantics() ;
	String captionTerminologies() ;
	String captionDomain() ;
	
	String generalOk() ;
	String generalSave() ;
	String generalCancel() ;
	String generalYes() ;
	String generalNo() ;
	String generalRefus() ;
	String generalOpenFile() ;
	String generalRefresh() ;
	String generalDisplay() ;
	String generalEdit() ;
	String generalReadOnly() ;
	String generalLeave() ;
	
	String welcomePseudo() ;
	
	String warning() ;
	
	String incorrectVersionNumber() ;

	String SelectThisTerm() ;
	
	String registerErrDifMails() ;
	String registerErrDifPass() ;
	String registerErrMailInvalid() ;
	String registerErrPassInvalid() ;
	String registerErrPseudoInvalid() ;
	String registerErrPseudoCheck() ;
	String registerErrPseudoNotAvailable() ;
	String registerMessageSent() ;
	String registerRegister() ;
	String registerCancel() ;
	
	String definitionErrEmptyLabel() ;
	String definitionErrNoLanguage() ;
	String definitionErrLanguageExists() ;
	
	String tripleSubject() ;
	String triplePredicate() ;
	String tripleObject() ;      
}
