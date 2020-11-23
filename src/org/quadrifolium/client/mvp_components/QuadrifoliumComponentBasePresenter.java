package org.quadrifolium.client.mvp_components;

import org.quadrifolium.client.event.GoToWorkshopComponentContent;
import org.quadrifolium.client.event.UserChangedEvent;
import org.quadrifolium.client.event.UserChangedEventHandler;
import org.quadrifolium.client.event.WorkshopConceptChangedEvent;
import org.quadrifolium.client.event.WorkshopConceptChangedEventHandler;
import org.quadrifolium.client.global.QuadrifoliumSupervisor;
import org.quadrifolium.client.mvp.QuadrifoliumWorkshopPresenterModel;
import org.quadrifolium.client.mvp.QuadrifoliumWorkshopViewModel;
import org.quadrifolium.client.mvp_components.QuadrifoliumComponentBaseDisplayModel.INTERFACETYPE;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.Panel;
import com.google.inject.Inject;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

public abstract class QuadrifoliumComponentBasePresenter<D extends QuadrifoliumComponentInterface> extends WidgetPresenter<D> 
{
	protected final DispatchAsync          _dispatcher ;
	protected final QuadrifoliumSupervisor _supervisor ;
	
	protected QuadrifoliumWorkshopPresenterModel<QuadrifoliumWorkshopViewModel> _parent ;
	
	protected boolean _bEditMode ;
	protected boolean _bAdding ;
			
	@Inject
	public QuadrifoliumComponentBasePresenter(D                            display, 
							                              EventBus                     eventBus,
							                              final DispatchAsync          dispatcher,
							                              final QuadrifoliumSupervisor supervisor) 
	{
		super(display, eventBus) ;
		
		_dispatcher = dispatcher ;
		_supervisor = supervisor ;
		
		_parent     = null ;
		
		_bEditMode  = false ;
		_bAdding    = false ;
	}
	
	@Override
	protected void onBind()
	{
		// Message received when the concept at work changed
		//
		eventBus.addHandler(WorkshopConceptChangedEvent.TYPE, new WorkshopConceptChangedEventHandler() {
			public void onWorkshopConceptChanged(WorkshopConceptChangedEvent event)
			{
				// If the "concept changed" targets another workshop, discard it
				if (event.getTargetWorkshop() != _parent)
					return ;
					
				UpdateContent() ;
			}
		});
			
		// Message received when the user changed
		//
		eventBus.addHandler(UserChangedEvent.TYPE, new UserChangedEventHandler() {
			public void onUserChanged(UserChangedEvent event) {
				adaptToUserChange() ;
			}
		});
			
		/**
		 * Get key down from the button that switches from edit more to read-only mode 
		 */
		display.getEditButtonKeyDown().addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				switchEditMode() ;
			}
		});
			
		/**
		 * Get key down from the "add element" button. Take care that it doesn't exist for singletons 
		 */
		HasClickHandlers addElementClickHandler = display.getAddButtonKeyDown() ;
		if (null != addElementClickHandler)
		{
			addElementClickHandler.addClickHandler(new ClickHandler() {
				public void onClick(final ClickEvent event)
				{
					if (_bAdding)
						return ;
					
					addNewElement() ;
				}
			});
		}
			
		/**
		 * Get key down from the OK button from the "add element" panel. Take care that it doesn't exist for singletons
		 */
		HasClickHandlers addElementOkClickHandler = display.getAddOkButtonKeyDown() ;
		if (null != addElementOkClickHandler)
		{
			addElementOkClickHandler.addClickHandler(new ClickHandler() {
				public void onClick(final ClickEvent event)
				{
					if (false == _bAdding)
						return ;
					
					saveEditedElement() ;
				}
			});
		}
		
		/**
		 * Get key down from the Cancel button from the "add element" panel. Take care that it doesn't exist for singletons
		 */
		HasClickHandlers addElementCancelClickHandler = display.getAddCancelButtonKeyDown() ;
		if (null != addElementCancelClickHandler)
		{
			addElementCancelClickHandler.addClickHandler(new ClickHandler() {
				public void onClick(final ClickEvent event)
				{
					if (false == _bAdding)
						return ;
					
					closeEditingSession() ;
				}
			});
		}
			
		/**
		 * Get key down from the OK button from the error box
		 */
		display.getErrDialogBoxOkButton().addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				display.closeErrDialogBox() ;
			}
		});
			
		scheduleRefresh() ;
	}
	
	/**
	 * Install view inside workspace
	 */
	protected void connectToWorkshop(final GoToWorkshopComponentContent eventContent) throws NullPointerException
	{
		// If this presenter is already connected, discard this message
		//
		if (null != _parent)
			return ;
		
		if (null == eventContent)
			throw new NullPointerException() ;
		
		// Connect this presenter to the workshop
		//
		_parent = eventContent.getParent() ;
					
		initialize(eventContent.getWorkspace()) ;
	}
	
	/**
	 * Create a timer to ensure that the definitions list remains up to date
	 */
	protected void scheduleRefresh()
	{
		// Create a new timer that updates the definitions list
		//
		Timer t = new Timer() {
	    public void run() {
	    	UpdateContent() ;
	    }
	  };
	  
	  // Schedule the timer to run once in 10 seconds.
	  //
	  t.schedule(100000) ;
	}
	
	/**
	 * Install view inside workspace
	 */
	protected void initialize(Panel workspace)
	{
		if (null == workspace)
			return ;
		
		workspace.clear() ;
		workspace.add(display.asWidget()) ;
		
		if (_supervisor.isUserAnEditor())
			updateView(INTERFACETYPE.editableMode) ;
	}
	
	/**
	 * Set what is needed when user changed (or there is no longer a logged user)
	 */
	protected void adaptToUserChange()
	{
		// Constant is that we are never editing
		//
		_bEditMode = false ;
		
		updateView() ;
	}

	/**
	 * Get current interface mode
	 */
	protected INTERFACETYPE getInterfaceType() 
	{
		INTERFACETYPE iInterfaceType = INTERFACETYPE.readOnlyMode ;
		if (_supervisor.isUserAnEditor())
		{
			if (_bEditMode)
				iInterfaceType = INTERFACETYPE.editMode ;
			else
				iInterfaceType = INTERFACETYPE.editableMode ;
		}
		return iInterfaceType ;
	}
	
	/**
	 * Refresh the whole view
	 */
	protected void updateView() {
		updateView(INTERFACETYPE.undefined) ;
	}
	
	/**
	 * Refresh the whole view
	 * 
	 * @param iInterfaceType Forced interface type, or computed one if <code>undefined</code>
	 */
	protected void updateView(final INTERFACETYPE iInterfaceType)
	{
		// Find the interface type if undefined
		//
		INTERFACETYPE iProperInterfaceType = iInterfaceType ;
		if (INTERFACETYPE.undefined == iInterfaceType)
			iProperInterfaceType = getInterfaceType() ;
		
		// Update view
		//
		UpdateDisplay(iProperInterfaceType) ;
		
		// Connect buttons
		//
		connectButtonsClickHandlers() ;
	}
	
	/**
	 * Switch from edit mode to read-only mode or vice-versa
	 */
	protected void switchEditMode()
	{
		_bEditMode = !_bEditMode ;
		
		// When switching mode, the editing session should always be/get closed
		//
		closeEditingSession() ;
		
		// Change view state
		//
		updateView() ;
	}

	/**
	 * Close the editing session
	 */
	protected void closeEditingSession()
	{
		display.closeAddPanel() ;
		
		_bAdding   = false ;
	}
	
	/**
	 * Refresh content from server
	 * Nothing here for the generic class, must be defined by derived components
	 */
	protected void UpdateContent() {
	}
	
	/**
	 * Refresh view
	 * Nothing here for the generic class, must be defined by derived components
	 */
	protected void UpdateDisplay(final INTERFACETYPE iInterfaceType) {
	}
	
	/**
	 * Enter the process to create a new element
	 * Nothing here for the generic class, must be defined by derived components
	 */
	protected void addNewElement() {
	}
	
	/**
	 * Save currently edited element
	 * Nothing here for the generic class, must be defined by derived components
	 */
	protected void saveEditedElement() {
	}
	
	/**
	 * Connect the buttons that control actions on individual elements (typically edit and delete)
	 * Nothing here for the generic class, must be defined by derived components 
	 */
	protected void connectButtonsClickHandlers() {
	}
	
	public void setParent(QuadrifoliumWorkshopPresenterModel<QuadrifoliumWorkshopViewModel> parent) {
		_parent = parent ;
	}
	
	/**
	 * Information contained inside button's ID
	 * 
	 * @author Philippe
	 */
	protected class buttonControlInformation
	{
		protected String _sAction ;
		protected String _sId ;
		
		public buttonControlInformation()
		{
			_sAction = "" ;
			_sId     = "" ;
		}
		
		public buttonControlInformation(final String sAction, final String sId)
		{
			_sAction = sAction ;
			_sId     = sId ;
		}
		
		public String getAction() {
			return _sAction ;
		}
		public void setAction(final String sAction) {
			_sAction = sAction ;
		}
		
		public String getId() {
			return _sId ;
		}
		public void setId(final String sId) {
			_sId = sId ;
		}
	}
	
	/**
	 * Parse a button Id into a buttonControlInformation structure
	 * 
	 * @return <code>null</code> if something went wrong; the buttonControlInformation if not
	 */
	protected buttonControlInformation getControlInformation(final ButtonBase button) throws NullPointerException
	{
		if (null == button)
			throw new NullPointerException() ;
		
		String sId = button.getElement().getId() ;
		if ((null == sId) || ("".equals(sId)))
			return null ;
		
		String[] decomposition = sId.split("_") ;
		if (decomposition.length != 2)
			return null ;
			
		return new buttonControlInformation(decomposition[0], decomposition[1]) ;
	}
	
	@Override
	protected void onUnbind() {
	}

	@Override
	public void revealDisplay() {
	}
}
