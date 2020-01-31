package com.proofd.knowledgbase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.UUID;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NsIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.vocabulary.RDF;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class UniversityCateringPlan {
    
	String pt="https://github.com/proofd-it/prov-trace#";
	String prov= "http://www.w3.org/ns/prov#";
	String ucp ="https://github.com/proofd-it/Ontologies/deliveryCateringPlan1.ttl#";
	String epplan ="https://w3id.org/ep-plan#";
	Model m;
	
	
	
	public UniversityCateringPlan (JsonObject payload) {
		Model m = new EmbeddedModel().getModel();
		loadPlanIntoModel (m);	
	    createExecutionActivityRecord (payload, m);
	    this.m = m;
	   // m.write (System.out, RDFLanguages.JSONLD.getLabel());
	   // RDFDataMgr.write(System.out, m, RDFFormat.TTL);
	}
		

	
   private void createExecutionActivityRecord(JsonObject payload, Model m) {
	   Resource puckAgent = null;
	  if (payload.get("puckID")!=null) {
	   puckAgent =  m.createResource(pt+ "Puck/"+payload.get("puckID").getAsString());
	  }
	  else {
		  puckAgent =  m.createResource(pt+ UUID.randomUUID()); 
	  }
	  puckAgent.addProperty(RDF.type, m.getResource(prov+"Agent"));
	  
	   
	  Resource execActivity =  m.createResource(pt+ UUID.randomUUID());
	  execActivity.addProperty(RDF.type, m.getResource(prov+"Activity"));
	  Resource cateringAgent =  m.createResource(pt+ "UniversityCatering");
	  cateringAgent.addProperty(RDF.type, m.getResource(prov+"Agent"));
	  
	  Resource qualifiedassociation =  m.createResource(pt+ UUID.randomUUID());
	  qualifiedassociation.addProperty(RDF.type, m.getResource(prov+"Association"));
	  qualifiedassociation.addProperty(m.getProperty(prov+"agent"), cateringAgent);
	  qualifiedassociation.addProperty(m.getProperty(prov+"hadPlan"), m.getResource(ucp+"UniversityCateringSandwichDeliveryPlan"));	  
	  
	  execActivity.addProperty(m.getProperty(prov+"wasAssociatedWith"), cateringAgent);
	  execActivity.addProperty(m.getProperty(prov+"qualifiedAssociation"), qualifiedassociation);
	  
	  linkresultsToPlanconstrains (execActivity,puckAgent, m, payload);
	  
	  Resource execBundle =  m.createResource(pt+ UUID.randomUUID());
	  execBundle.addProperty(RDF.type, m.getResource(epplan+"ExecutionTraceBundle"));
	  execBundle.addProperty(m.getProperty(prov+"wasInfluencedBy"), execActivity);
	  
	  createTrace  (execBundle, m, payload,puckAgent);
   }


private void createTrace(Resource execBundle, Model m, JsonObject payload, Resource puckAgent) {
	if (payload.getAsJsonArray("states")!= null) {
		// Iterator<JsonElement> it = payload.getAsJsonArray("states").iterator();
		 
		 Resource activity ;
		 Resource lastEntity = null ;
		 boolean startOfworkflow = true;
		 for (int i=0; i<payload.getAsJsonArray("states").size();i++) {
			
		        JsonElement element = payload.getAsJsonArray("states").get(i);
		        if (element.getAsJsonObject().get("state") != null) {
		        	String state = element.getAsJsonObject().get("state").getAsString();
		        	if (state.equals("fridge")) {
		        		activity = m.createResource(pt+ UUID.randomUUID());
		        		activity.addProperty(RDF.type, m.getResource(epplan+"Activity"));
		        		activity.addProperty(m.getProperty(epplan+"correspondsToStep"), m.getResource(ucp+"ColdStorage"));
		        		linkToTrace(execBundle,activity,m);
		        		
		        		//Continue adding provenance of the PUCK.js sensor evaluating constraints based on location and temperature readings
		        		Resource qualifiedEval = linktraceElementToConstraint (m.getResource(ucp+"ColdStorageConstraint"),  activity, puckAgent,  m,  element);
		        		
		        		if (startOfworkflow) {
		        			/*
		        			Resource entity = m.createResource(pt+ UUID.randomUUID());
		        			entity.addProperty(RDF.type, m.getResource(epplan+"Entity"));
		        			entity.addProperty(m.getProperty(epplan+"correspondsToVariable"), m.getResource(ucp+"PackedItem"));
		        			linkToTrace(execBundle,entity,m);
		        			activity.addProperty(m.getProperty(prov+"used"), entity);
		        			
		        			lastEntity = m.createResource(pt+ UUID.randomUUID());
		        			lastEntity.addProperty(RDF.type, m.getResource(epplan+"Entity"));
		        			lastEntity.addProperty(m.getProperty(epplan+"correspondsToVariable"), m.getResource(ucp+"ChilledItem"));
		        			lastEntity.addProperty(m.getProperty(prov+"wasGeneratedBy"), activity);
		        			
		        			addTimestamp (lastEntity,element,m);
		        			linkToTrace(execBundle,lastEntity,m);
		        			*/
		        			lastEntity = generateStartWorkflow (activity, m.getResource(ucp+"ChilledItem"),  execBundle, element, m);
		        			startOfworkflow = false;
		        		}
		        		
		        		else {
		        			//link to the last generated entity first!
		        			activity.addProperty(m.getProperty(prov+"used"), lastEntity);
		        			
		        			lastEntity = m.createResource(pt+ UUID.randomUUID());
		        			lastEntity.addProperty(RDF.type, m.getResource(epplan+"Entity"));
		        			lastEntity.addProperty(m.getProperty(epplan+"correspondsToVariable"), m.getResource(ucp+"ChilledItem"));
		        			
		        			addTimestamp (lastEntity,element,m);
		        			linkToTrace(execBundle,lastEntity,m);
		        			lastEntity.addProperty(m.getProperty(prov+"wasGeneratedBy"), activity);
		        		}
		        		
		        		
		        	}
		        	if (state.equals("outside")) {
		        		activity = m.createResource(pt+ UUID.randomUUID());
		        		activity.addProperty(RDF.type, m.getResource(epplan+"Activity"));
		        		activity.addProperty(m.getProperty(epplan+"correspondsToStep"), m.getResource(ucp+"AmbientTemperatureStroage"));
		        		
		        		//Continue adding provenance of the PUCK.js sensor evaluating constraints based on location and temperature readings
		        		
		        		Resource qualifiedEval = linktraceElementToConstraint (m.getResource(ucp+"AmbientStorageConstraint"),  activity, puckAgent,  m,  element);
		        		
		        		if (startOfworkflow) {
		        			
		        			lastEntity = generateStartWorkflow (activity, m.getResource(ucp+"OutOfColdStorageItem"), execBundle, element, m);
		        			
		        			
		        			startOfworkflow = false;
		        		}
		        		
		        		else {
		        			//link to the last generated entity first!
		        			activity.addProperty(m.getProperty(prov+"used"), lastEntity);
		        			
		        			lastEntity = m.createResource(pt+ UUID.randomUUID());
		        			lastEntity.addProperty(RDF.type, m.getResource(epplan+"Entity"));
		        			lastEntity.addProperty(m.getProperty(epplan+"correspondsToVariable"), m.getResource(ucp+"OutOfColdStorageItem"));
		        			
		        			addTimestamp (lastEntity,element,m);
		        			linkToTrace(execBundle,lastEntity,m);
		        			lastEntity.addProperty(m.getProperty(prov+"wasGeneratedBy"), activity);
		        		}
		        		
		        	}
		        }
		 }
		 
	}
	
}




private Resource  generateStartWorkflow (Resource activity, Resource generatedEntityCorrespondingStep, Resource execBundle, JsonElement element,  Model m )  {
	Resource entity = m.createResource(pt+ UUID.randomUUID());
	entity.addProperty(RDF.type, m.getResource(epplan+"Entity"));
	entity.addProperty(m.getProperty(epplan+"correspondsToVariable"), m.getResource(ucp+"PackedItem"));
	linkToTrace(execBundle,entity,m);
	activity.addProperty(m.getProperty(prov+"used"), entity);
	
	Resource lastEntity = m.createResource(pt+ UUID.randomUUID());
	lastEntity.addProperty(RDF.type, m.getResource(epplan+"Entity"));
	lastEntity.addProperty(m.getProperty(epplan+"correspondsToVariable"), generatedEntityCorrespondingStep);
	lastEntity.addProperty(m.getProperty(prov+"wasGeneratedBy"), activity);
	addTimestamp (lastEntity,element,m);
	linkToTrace(execBundle,lastEntity,m);
	
	return lastEntity;
}

private void addTimestamp (Resource entity, JsonElement element, Model m){
	
	  SimpleDateFormat formatter = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss");
      
	
	if (element.getAsJsonObject().get("timeStart") != null) {
	
	try {
		entity.addProperty(m.getProperty(prov+"generatedAtTime"), m.createTypedLiteral((formatter.parse(element.getAsJsonObject().get("timeStart").getAsString()).getTime())));
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	if (element.getAsJsonObject().get("timeEnd") != null) {
		
		try {
			entity.addProperty(m.getProperty(prov+"invalidatedAtTime"), m.createTypedLiteral((formatter.parse(element.getAsJsonObject().get("timeEnd").getAsString()).getTime())));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	
	
}

private void linkToTrace (Resource traceBundle, Resource element, Model m) {
	element.addProperty(m.getProperty(epplan+"isElementOfTrace"), traceBundle);
}


private Resource qualifyConstraintEval (Resource agent, Resource constraint, Resource activity, Model m) {
	Resource constraintEval = m.createResource(pt+ UUID.randomUUID());
	constraintEval.addProperty(RDF.type, m.getResource(epplan+"ConstraintEvaluation"));
	constraintEval.addProperty(m.getProperty(epplan+"evaluatedTraceElement"), activity);
	constraintEval.addProperty(m.getProperty(prov+"wasAttributedTo"), agent);
	constraint.addProperty(m.getProperty(epplan+"qualifiedEvaluation"), constraintEval);
	
	return constraintEval;
}

private Resource linktraceElementToConstraint (Resource constraint, Resource activity,Resource puckAgent, Model m, JsonElement element) {
	Resource constraintEval = null;  
	if (element.getAsJsonObject().get("assessment")!= null) {
	
	 if (element.getAsJsonObject().get("assessment").getAsString().equals("ok")) {
		 activity.addProperty(m.getProperty(epplan+"satisfied"), constraint); 
	 }
	 else {
		 activity.addProperty(m.getProperty(epplan+"violated"), constraint); 
	 }
		
		
	 constraintEval = qualifyConstraintEval (puckAgent, constraint,  activity,  m);
	
	}
	
	return constraintEval;
}

private void linkresultsToPlanconstrains(Resource execActivity, Resource puckAgent, Model m, JsonObject payload) {
	
	
	
	//check if any stage was non-compliant and if so then add the compliance as not satisfied
	if (payload.getAsJsonArray("states")!= null) {
		
	 Iterator<JsonElement> it = payload.getAsJsonArray("states").iterator();
	boolean compliant = true;		
	 while (it.hasNext()) {
		        JsonElement element = it.next();
				if (element.getAsJsonObject().get("assessment") != null) {
			     System.out.println (element.getAsJsonObject().get("assessment").getAsString());
			     if (!element.getAsJsonObject().get("assessment").getAsString().equals("ok")) {
			    	 compliant= false; 
			     }		     
				}
			 }
		
	    Resource constraint = m.getResource("https://github.com/proofd-it/Ontologies/deliveryCateringPlan1.ttl#OverallTemperatureComplianceConstraint");
	    qualifyConstraintEval (puckAgent, constraint, execActivity,  m);
	    
		if (compliant) {
			execActivity.addProperty(m.getProperty(epplan+"satisfied"),constraint );
		}
		else {
			execActivity.addProperty(m.getProperty(epplan+"violated"), constraint);
		}
		
		
			 
	}
	
	//handle out of cold storage constraints
	
  //  System.out.println(payload.getAsJsonObject("warning").get("code"));
  
    
	if (payload.getAsJsonArray("warnings")!= null) {
		
		Iterator<JsonElement>  it = payload.getAsJsonArray("warnings").iterator();
		
		boolean max_time_one_stage_outside = true;
		boolean max_number_of_times_outside = true;
		boolean max_combined_time_outside = true;
		
		while (it.hasNext()) {
		JsonElement element = it.next();
		if (((JsonObject) element).get("code") != null) {
		int code = ((JsonObject) element).get("code").getAsInt();
		
		switch (code){
        case 1:  max_time_one_stage_outside = false;
        break;
        case 2:  max_number_of_times_outside = false;
        break;
        case 3:  max_combined_time_outside = false;
        break;
        }
		
		}
		}
		
		Resource constraint = m.getResource("https://github.com/proofd-it/Ontologies/deliveryCateringPlan1.ttl#MaxSingleStageAmbientStorageTime");
		qualifyConstraintEval (puckAgent, constraint, execActivity,  m);
		
		if (max_time_one_stage_outside) {
			execActivity.addProperty(m.getProperty(epplan+"satisfied"),constraint);
		}
		else {
			execActivity.addProperty(m.getProperty(epplan+"violated"), constraint);
		}
		
		constraint = m.getResource("https://github.com/proofd-it/Ontologies/deliveryCateringPlan1.ttl#MaxNumberOfAmbientStorageStages");
		qualifyConstraintEval (puckAgent, constraint, execActivity,  m);
		
		if (max_number_of_times_outside) {
			execActivity.addProperty(m.getProperty(epplan+"satisfied"), constraint);
		}
		else {
			execActivity.addProperty(m.getProperty(epplan+"violated"), constraint);
		}
		
		constraint = m.getResource("https://github.com/proofd-it/Ontologies/deliveryCateringPlan1.ttl#MaxCombinedAmbientStorageTime");
		qualifyConstraintEval (puckAgent, constraint, execActivity,  m);
		
		if (max_combined_time_outside) {
			execActivity.addProperty(m.getProperty(epplan+"satisfied"), constraint);
		}
		else {
			execActivity.addProperty(m.getProperty(epplan+"violated"), constraint);
		}		
	}	
}


private void  loadPlanIntoModel (Model m) {		
		//m.read("ep-plan.ttl", "TTL") ;
		//m.read("fs-prov-ep-plan-mapping.ttl", "TTL") ;
		//m.read("fs-prov.ttl", "TTL") ;
		//m.read("prov-o.ttl", "TTL") ;
		
		m.setNsPrefix("ucp","https://github.com/proofd-it/Ontologies/deliveryCateringPlan1.ttl#");
		m.setNsPrefix("fs-prov-mapping","https://w3id.org/abdn/foodsafety/fs-prov_mapping_to_ep-plan#");
		
		m.setNsPrefix("prov-trace","https://github.com/proofd-it/prov-trace#");
		
		System.out.println(m.size());
		
		//model.write (System.out,"TTL");
		
	}



public Model getModel() {
	// TODO Auto-generated method stub
	return m;
}
	
}
