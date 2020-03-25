package com.proofd.evaluate;

public class EvalQueries {

	//find all HACCP constraints associated with the plan and whether they were satisfied/ not satisfied
	public static String  Q1 = "Select ?plan ?const  ?result Where {?plan a ep-plan:Plan; ep-plan:hasConstraint ?const. ?const a fs-prov:HaccpConstraint.  ?trace prov:wasDerivedFrom ?plan; prov:wasInfluencedBy ?execActivity. ?execActivity a ep-plan:Activity; ?result ?const. FILTER (?result = ep-plan:violated ||?result = ep-plan:satisfied ) }";
	
	//find agent responsible for the delivery 
	public static String  Q2 = "Select ?deliveryAgent Where {?plan a ep-plan:Plan. ?trace prov:wasDerivedFrom ?plan; prov:wasInfluencedBy ?execActivity. ?execActivity a ep-plan:Activity; prov:wasAssociatedWith ?deliveryAgent.}";
	
	//find activities that breached the  HACCP constraints associated with HACCP steps
	public static String  Q3 = "Select ?step ?stepComment ?const ?act Where {?plan a ep-plan:Plan. ?const a fs-prov:HaccpConstraint. ?trace prov:wasDerivedFrom ?plan. ?step rdfs:comment ?stepComment; a fs-prov:HaccpStep; ep-plan:isStepOfPlan ?plan; ep-plan:hasConstraint ?const.  ?act ep-plan:correspondsToStep ?step; a ep-plan:Activity; ep-plan:isElementOfTrace ?trace; ep-plan:violated ?const.}";
	
	//find number of readings made, average values, and unit of measurement for each temperature controlled step 
	public static String  Q4 = "Select ?step ?const ?act ?unit ?avg ?numbReadings Where {?plan a ep-plan:Plan. ?trace prov:wasDerivedFrom ?plan. ?step ep-plan:isStepOfPlan ?plan; ep-plan:hasConstraint ?const.  ?act ep-plan:correspondsToStep ?step; a ep-plan:Activity; ep-plan:isElementOfTrace ?trace. ?constEval ep-plan:evaluatedTraceElement ?act; proofdit:basedOn ?coll. ?coll a proofdit:ObservationCollection; proofdit:average ?avg; proofdit:numberOfReadings ?numbReadings; proofdit:unit ?unit.  }";
	
	//find  observable property and feature of interest  that  was used by the sensor to produce temperature observations
	public static String  Q5 = "Select ?step ?const ?act ?featureOfInterest ?var ?observableProperty Where {?plan a ep-plan:Plan. ?trace prov:wasDerivedFrom ?plan. ?step ep-plan:isStepOfPlan ?plan; ep-plan:hasConstraint ?const.  ?act ep-plan:correspondsToStep ?step; a ep-plan:Activity; ep-plan:isElementOfTrace ?trace. ?constEval ep-plan:evaluatedTraceElement ?act; proofdit:basedOn ?coll. ?coll prov:wasGeneratedBy ?observation. ?observation a sosa:Observation; sosa:hasFeatureOfInterest ?featureOfInterest; sosa:hasObservableProperty ?observableProperty. ?featureOfInterest ep-plan:correspondsToVariable ?var.  }";
	
	
	//which steps were executed more than once ? 
	public static String  Q6 = "Select ?step (count(distinct ?act) as ?nubOfExecutions) Where {?plan a ep-plan:Plan. ?trace prov:wasDerivedFrom ?plan. ?step ep-plan:isStepOfPlan ?plan.  ?act ep-plan:correspondsToStep ?step; a ep-plan:Activity; ep-plan:isElementOfTrace ?trace. }GROUP BY ?step";
	
	//find sample readings that are associated with steps that broke temperature constraints
	
	public static String  Q7 = "Select ?step ?const ?act  ?readingValue Where {?plan a ep-plan:Plan. ?trace prov:wasDerivedFrom ?plan. ?step ep-plan:isStepOfPlan ?plan; ep-plan:hasConstraint ?const.  ?act ep-plan:correspondsToStep ?step; a ep-plan:Activity; ep-plan:violated ?const; ep-plan:isElementOfTrace ?trace. ?constEval ep-plan:evaluatedTraceElement ?act; proofdit:basedOn ?coll. ?coll a proofdit:ObservationCollection; prov:hadMember ?reading. ?reading a proofdit:SampleObservation; prov:value ?readingValue.  }";
	
	
	//how long was the item in the delivery process
	
	public static String  Q8 = "Select Distinct ?deliveryId  (min(?timeStart) as ?deliveryProcessStart)  (max(?timeEnd) as ?timeDelivered)  {?plan a ep-plan:Plan. ?trace prov:wasDerivedFrom ?plan. ?step ep-plan:isStepOfPlan ?plan.  ?ent proofdit:deliveryID ?deliveryId;  ep-plan:correspondsToVariable ?var; prov:generatedAtTime ?timeStart; prov:invalidatedAtTime ?timeEnd. ?var a fs-prov:PhysicalObject. {?var fs-prov:isResultOf ?step.} UNION {?var fs-prov:isRequiredResourceFor ?step.}} GROUP By ?deliveryId";
	
	//When was the item received by the customer
	
	public static String  Q9 = "Select ?var ?ent ?timeReceived {?plan a ep-plan:Plan. ?trace prov:wasDerivedFrom ?plan. ?var ep-plan:isVariableOfPlan ?plan; a fs-prov:Resource; a fs-prov:PhysicalObject. ?ent  ep-plan:correspondsToVariable ?var; prov:generatedAtTime ?timeReceived.   values(?var) {(ucp:DeliveredItem)}} ";
	
	//Was the item accepted/rejected by the custom    
	
	public static String  Q10 = "Select ?var ?ent ?result {?plan a ep-plan:Plan. ?trace prov:wasDerivedFrom ?plan. ?var ep-plan:isVariableOfPlan ?plan;a fs-prov:Resource.  ?ent  ep-plan:correspondsToVariable ?var; prov:value ?result.   values(?var) {(ucp:DeliveryResult)}} ";
	
}
