package org.quadrifolium.shared.util;

import junit.framework.TestCase ;

public class QuadrifoliumFctsTest extends TestCase
{
	public void testValidateFunctions() 
  {
		// Check that length is properly tested
		// 
		assertTrue(QuadrifoliumFcts.isValidCode(MiscellanousFcts.getNChars(QuadrifoliumFcts.CONCEPT_CODE_LEN, 'A'), 0)) ;
		assertFalse(QuadrifoliumFcts.isValidCode(MiscellanousFcts.getNChars(QuadrifoliumFcts.CONCEPT_CODE_LEN-1, 'A'), 0)) ;
		
		assertTrue(QuadrifoliumFcts.isValidCode(MiscellanousFcts.getNChars(QuadrifoliumFcts.LEMMA_CODE_LEN, 'A'), 0)) ;
		assertFalse(QuadrifoliumFcts.isValidCode(MiscellanousFcts.getNChars(QuadrifoliumFcts.LEMMA_CODE_LEN-1, 'A'), 0)) ;
		
		assertTrue(QuadrifoliumFcts.isValidCode(MiscellanousFcts.getNChars(QuadrifoliumFcts.FLEX_CODE_LEN, 'A'), 0)) ;
		assertFalse(QuadrifoliumFcts.isValidCode(MiscellanousFcts.getNChars(QuadrifoliumFcts.FLEX_CODE_LEN-1, 'A'), 0)) ;
		
		// Check that content is properly tested
		//
		assertFalse(QuadrifoliumFcts.isValidCode(MiscellanousFcts.getNChars(QuadrifoliumFcts.CONCEPT_CODE_LEN, 'a'), 0)) ;
		assertFalse(QuadrifoliumFcts.isValidCode(MiscellanousFcts.getNChars(QuadrifoliumFcts.CONCEPT_CODE_LEN, '+'), 0)) ;
		
		// Check free text related validators
		//
		String sValidFreeTextCode = "AAAAAAA+BBBBB" ;
		
		assertTrue(QuadrifoliumFcts.isFreeTextHeaderCode(sValidFreeTextCode)) ;
		assertFalse(QuadrifoliumFcts.isFlexCode(sValidFreeTextCode)) ;
		assertFalse(QuadrifoliumFcts.isLemmaCode(sValidFreeTextCode)) ;
		assertFalse(QuadrifoliumFcts.isConceptCode(sValidFreeTextCode)) ;
  }
	
  public void testCodesFunctions() 
  {
  	// Valid
  	//
  	String sLemmaCode = "AAAAAAABBBB" ;
  	String sFlexCode  = "AAAAAAABBBBCC" ;
  	
  	assertEquals("AAAAAAA",     QuadrifoliumFcts.getConceptCode(sLemmaCode)) ;
  	assertEquals("AAAAAAA",     QuadrifoliumFcts.getConceptCode(sFlexCode)) ;
  	assertEquals("AAAAAAABBBB", QuadrifoliumFcts.getFullLemmaCode(sLemmaCode)) ;
  	assertEquals("AAAAAAABBBB", QuadrifoliumFcts.getFullLemmaCode(sFlexCode)) ;
  	assertEquals("BBBB",        QuadrifoliumFcts.getSpecificLemmaCode(sLemmaCode)) ;
  	assertEquals("BBBB",        QuadrifoliumFcts.getSpecificLemmaCode(sFlexCode)) ;
  	assertEquals("CC",          QuadrifoliumFcts.getSpecificFlexCode(sFlexCode)) ;
  	
  	// Invalid
   	//
  	String sConceptCode = "AAAAAAA" ;
  	
  	assertEquals("", QuadrifoliumFcts.getFullLemmaCode(sConceptCode)) ;
  	assertEquals("", QuadrifoliumFcts.getSpecificLemmaCode(sConceptCode)) ;
  	assertEquals("", QuadrifoliumFcts.getSpecificFlexCode(sLemmaCode)) ;
  	assertEquals("", QuadrifoliumFcts.getFullLemmaCode(null)) ;
  	assertEquals("", QuadrifoliumFcts.getSpecificLemmaCode(null)) ;
  	assertEquals("", QuadrifoliumFcts.getSpecificFlexCode(null)) ;
  	
  	// Valid for free text
   	//
   	String sFreeTextHeaderCode  = "AAAAAAA+BBBBB" ;
   	String sFreeTextChainedCode = "AAAAAAABBBBBB" ;
   	
   	assertEquals("AAAAAAA", QuadrifoliumFcts.getConceptCode(sFreeTextHeaderCode)) ;
   	assertEquals("AAAAAAA", QuadrifoliumFcts.getConceptCode(sFreeTextChainedCode)) ;
   	assertEquals("+BBBBB",  QuadrifoliumFcts.getSpecificFreeTextCode(sFreeTextHeaderCode)) ;
   	assertEquals("BBBBBB",  QuadrifoliumFcts.getSpecificFreeTextCode(sFreeTextChainedCode)) ;
   	assertEquals("BBBBB",   QuadrifoliumFcts.getSpecificFreeTextHeaderCode(sFreeTextHeaderCode)) ;
   	
   	// Invalid
    //
   	assertEquals("", QuadrifoliumFcts.getSpecificFreeTextHeaderCode(sFreeTextChainedCode)) ;
   	assertEquals("", QuadrifoliumFcts.getSpecificFreeTextCode(sConceptCode)) ;
  }
  
  public void testLexiqueCodesFunctions() 
  {
  	// Valid
  	//
  	String sLexiqueCode = "aaaaab" ;
  	
  	assertEquals("aaaaa", QuadrifoliumFcts.getLexiqueConceptCode(sLexiqueCode)) ;
  	assertEquals("b", QuadrifoliumFcts.getLexiqueSpecificCode(sLexiqueCode)) ;
  	
  	// Invalid
   	//
  	String sWrongCode = "aaaa" ;
  	
  	assertEquals("", QuadrifoliumFcts.getLexiqueConceptCode(sWrongCode)) ;
  	assertEquals("", QuadrifoliumFcts.getLexiqueSpecificCode(sWrongCode)) ;
  	assertEquals("", QuadrifoliumFcts.getLexiqueConceptCode(null)) ;
  }
}

