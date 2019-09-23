package org.quadrifolium.server.ontology_base;

import junit.framework.TestCase ;

public class OntologyLexiconTest extends TestCase
{
  public void testDisplayLabel() 
  {
  	// Valid
  	//
  	OntologyLexicon lexicon = new OntologyLexicon("genre|s| grammatical|l/ux|", "", "MS", "", "") ;
  	assertEquals("genre grammatical", lexicon.getDisplayLabelForNoon(OntologyLexicon.Declination.singular, "fr")) ;
  	assertEquals("genres grammaticaux", lexicon.getDisplayLabelForNoon(OntologyLexicon.Declination.plural, "fr")) ;
  	
  	OntologyLexicon lexicon2 = new OntologyLexicon("Crohn [maladie|s|][de] {détail}", "", "MS", "", "") ;
  	assertEquals("maladie de Crohn", lexicon2.getDisplayLabelForNoon(OntologyLexicon.Declination.singular, "fr")) ;
  	assertEquals("maladies de Crohn", lexicon2.getDisplayLabelForNoon(OntologyLexicon.Declination.plural, "fr")) ;  	
  }
  
  public void testGetMethods() 
  {
  	assertEquals("Crohn [maladie|s|][de]", OntologyLexicon.removeTrailingComments("Crohn [maladie|s|][de] {détail}")) ;
  	assertEquals("Crohn [maladie|s|][de]", OntologyLexicon.removeTrailingComments("Crohn [maladie|s|][de]{détail}")) ;
  }
  
  public void testGetMeaningClarification() 
  {
  	// Valid
  	//
  	OntologyLexicon lexicon = new OntologyLexicon("Crohn [maladie|s|][de] {détail}", "", "MS", "", "") ;
  	assertEquals("détail", lexicon.getMeaningClarification()) ;
  	
  	lexicon.setLabel("Crohn [maladie|s|][de] {|grammar} {détail}") ;
  	assertEquals("détail", lexicon.getMeaningClarification()) ;
  	
  	lexicon.setLabel("Crohn [maladie|s|][de] {|grammar}") ;
  	assertEquals("", lexicon.getMeaningClarification()) ;
  	
  	// Invalid
   	//
  	lexicon.setLabel("Crohn [maladie|s|][de] {|grammar") ;
  	assertEquals("", lexicon.getMeaningClarification()) ;
  	
  	lexicon.setLabel("Crohn [maladie|s|][de] {|grammar} {détail") ;
  	assertEquals("", lexicon.getMeaningClarification()) ;
  }
}
