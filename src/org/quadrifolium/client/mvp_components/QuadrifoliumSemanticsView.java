package org.quadrifolium.client.mvp_components;

import java.util.ArrayList;

import org.quadrifolium.client.loc.QuadrifoliumConstants;
import org.quadrifolium.shared.ontology.TripleWithLabel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class QuadrifoliumSemanticsView extends FlowPanel implements QuadrifoliumSemanticsPresenter.Display 
{
	private final QuadrifoliumConstants constants = GWT.create(QuadrifoliumConstants.class) ;
	
	protected SimplePanel                _CommandPanel ;
	
	// Semantic network display area
	//
	private   FlowPanel                  _semanticNetworkPanel ;
	private   CellTable<TripleWithLabel> _AsObjectTable ;
	private   CellTable<TripleWithLabel> _AsSubjectTable ;
	
	//Is it a read-only view?
	//
	protected boolean                    _bReadOnly ;
	
	public QuadrifoliumSemanticsView() 
	{
		_bReadOnly = true ;
		
		addStyleName("semanticsWorshopPanel") ;
		
		// Command panel
		//
		initCommandPanel() ;
		initSemanticPanel() ;
	}
	
	/**
	 * Initialize the semantic network panel
	 */
	protected void initSemanticPanel()
	{
		_semanticNetworkPanel = new FlowPanel() ;
		
		// Create the "left table" (where current concept is the object of displayed triples)
		//
		_AsObjectTable = new  CellTable<TripleWithLabel>()  ;
		_AsObjectTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED) ;
		_AsObjectTable.addStyleName("semanticTable") ;
		
    // Add a text column to show the subject
    TextColumn<TripleWithLabel> subjectColumn = new TextColumn<TripleWithLabel>() {
      @Override
      public String getValue(TripleWithLabel object) {
      	if (null == object)
      		return "" ;
        return object.getSubjectLabel() ;
      }
    };
    _AsObjectTable.addColumn(subjectColumn, constants.tripleSubject()) ;

    // Add a text column to show the predicate
    TextColumn<TripleWithLabel> lPredicateColumn = new TextColumn<TripleWithLabel>() {
      @Override
      public String getValue(TripleWithLabel object) {
      	if (null == object)
      		return "" ;
        return object.getPredicateLabel() ;
      }
    };
    _AsObjectTable.addColumn(lPredicateColumn, constants.triplePredicate()) ;
		
    // Create the "right table" (where current concept is the subject of displayed triples)
		//
    _AsSubjectTable = new  CellTable<TripleWithLabel>()  ;
    _AsSubjectTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED) ;
    _AsSubjectTable.addStyleName("semanticTable") ;
			
    // Add a text column to show the predicate
    TextColumn<TripleWithLabel> rPredicateColumn = new TextColumn<TripleWithLabel>() {
      @Override
      public String getValue(TripleWithLabel object) {
      	if (null == object)
      		return "" ;
        return object.getPredicateLabel() ;
      }
    };
    _AsSubjectTable.addColumn(rPredicateColumn, constants.triplePredicate()) ;
    
    // Add a text column to show the subject
    TextColumn<TripleWithLabel> objectColumn = new TextColumn<TripleWithLabel>() {
    	@Override
    	public String getValue(TripleWithLabel object) {
    		if (null == object)
      		return "" ;
    		return object.getObjectLabel() ;
    	}
    };
    _AsSubjectTable.addColumn(objectColumn, constants.tripleObject()) ;

    // Add both tables to the semantic network panel
    //
    _semanticNetworkPanel.add(_AsObjectTable) ;
    _semanticNetworkPanel.add(_AsSubjectTable) ;
    
		// _displayPanel.addEast(_semanticNetworkPanel, 500) ;
    add(_semanticNetworkPanel) ;
	}
	
	/**
	 * Initialize the command panel
	 */
	protected void initCommandPanel()
	{
		_CommandPanel = new SimplePanel() ;
		_CommandPanel.addStyleName("semanticsCommand") ;
		
		Label caption = new Label(constants.captionSemantics()) ;
		
		// if (_bReadOnly)
		// {
			_CommandPanel.add(caption) ;
		// }
			
		add(_CommandPanel) ;
	}
	
	/**
	 * Feed and refresh the left semantic table (the one with current concept as the object of all triples)
	 */
	@Override
	public void feedLeftSemanticTable(final ArrayList<TripleWithLabel> aTriples)
	{
		// clearLeftSemanticTable() ;
		
		if (null != aTriples)
			_AsObjectTable.setRowData(aTriples) ;
		
		_AsObjectTable.redraw() ;
	}
	
	/**
	 * Feed and refresh the right semantic table (the one with current concept as the subject of all triples)
	 */
	@Override
	public void feedRightSemanticTable(final ArrayList<TripleWithLabel> aTriples)
	{
		// clearLeftSemanticTable() ;
		
		if (null != aTriples)
			_AsSubjectTable.setRowData(aTriples) ;
		
		_AsSubjectTable.redraw() ;
	}
	
	/**
	 * Clear the left semantic table (where current concept is the object)
	 */
	protected void clearLeftSemanticTable() {
		// clearSemanticTable(_AsObjectTable) ;
	}
	
	/**
	 * Clear the right semantic table (where current concept is the subject)
	 */
	protected void clearRightSemanticTable() {
		// clearSemanticTable(_AsSubjectTable) ;
	}
	
	/**
	 * Clear a semantic table
	 * 
	 * @param table The table to reset
	 */
	protected void clearSemanticTable(CellTable<TripleWithLabel> table)
	{
		int iRowCount = table.getRowCount() ;
		if (iRowCount <= 0)
			return ;
		
		table.setRowCount(0) ;
	}
		
	public void reset() {	
	}

	public Widget asWidget() {
		return this;
	}
}
