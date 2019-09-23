package org.quadrifolium.server.guice;

import net.customware.gwt.dispatch.server.guice.ActionHandlerModule;

import org.quadrifolium.server.DbParameters;
import org.quadrifolium.server.Logger;
import org.quadrifolium.server.handler.CheckPseudoHandler;
import org.quadrifolium.server.handler.CheckUserHandler;
import org.quadrifolium.server.handler.GetLanguagesHandler;
import org.quadrifolium.server.handler.GetWelcomeTextHandler;
import org.quadrifolium.server.handler.IsValidPseudoHandler;
import org.quadrifolium.server.handler.RegisterUserHandler;
import org.quadrifolium.server.handler4ontology.GetDefinitionsTriplesForConceptHandler;
import org.quadrifolium.server.handler4ontology.GetFlexListFromTextHandler;
import org.quadrifolium.server.handler4ontology.GetFullSynonymsForConceptHandler;
import org.quadrifolium.server.handler4ontology.GetLanguageTagsHandler;
import org.quadrifolium.server.handler4ontology.GetLemmasForConceptHandler;
import org.quadrifolium.server.handler4ontology.GetSemanticTriplesForConceptHandler;
import org.quadrifolium.server.handler_special.LexiqueTo4foliumHandler;

import org.quadrifolium.shared.rpc.CheckPseudoAction;
import org.quadrifolium.shared.rpc.GetLanguagesAction;
import org.quadrifolium.shared.rpc.GetWelcomeTextAction;
import org.quadrifolium.shared.rpc.IsValidPseudoAction;
import org.quadrifolium.shared.rpc.RegisterUserAction;
import org.quadrifolium.shared.rpc.SendLoginAction;
import org.quadrifolium.shared.rpc4ontology.GetDefinitionsTriplesAction;
import org.quadrifolium.shared.rpc4ontology.GetFlexListAction;
import org.quadrifolium.shared.rpc4ontology.GetFullSynonymsForConceptAction;
import org.quadrifolium.shared.rpc4ontology.GetLanguageTagsAction;
import org.quadrifolium.shared.rpc4ontology.GetLemmasForConceptAction;
import org.quadrifolium.shared.rpc4ontology.GetSemanticTriplesAction;
import org.quadrifolium.shared.rpc_special.LexiqueTo4foliumAction;

import com.google.inject.Singleton;

/**
 * Module which binds the handlers and configurations
 *
 */
public class QuadrifoliumServerModule extends ActionHandlerModule 
{
	@Override
	protected void configureHandlers() 
	{
		bindHandler(CheckPseudoAction.class,         CheckPseudoHandler.class) ;
		bindHandler(GetLanguagesAction.class,        GetLanguagesHandler.class) ;
		bindHandler(IsValidPseudoAction.class,       IsValidPseudoHandler.class) ;
		bindHandler(RegisterUserAction.class,        RegisterUserHandler.class) ;
		bindHandler(SendLoginAction.class,           CheckUserHandler.class) ;
		bindHandler(GetWelcomeTextAction.class,      GetWelcomeTextHandler.class) ;
		
		// Ontology management
		// 
		bindHandler(GetFlexListAction.class,               GetFlexListFromTextHandler.class) ;
		bindHandler(GetLemmasForConceptAction.class,       GetLemmasForConceptHandler.class) ;
		bindHandler(GetFullSynonymsForConceptAction.class, GetFullSynonymsForConceptHandler.class) ;
		bindHandler(GetSemanticTriplesAction.class,        GetSemanticTriplesForConceptHandler.class) ;
		bindHandler(GetDefinitionsTriplesAction.class,     GetDefinitionsTriplesForConceptHandler.class) ;
		bindHandler(GetLanguageTagsAction.class,           GetLanguageTagsHandler.class) ;
		
		bindHandler(LexiqueTo4foliumAction.class,    LexiqueTo4foliumHandler.class) ;
		
		bind(Logger.class).toProvider(LoggerProvider.class).in(Singleton.class) ;
		bind(DbParameters.class).toProvider(DbParametersProvider.class).in(Singleton.class) ;
	}
}