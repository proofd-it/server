package com.proofd.evaluate;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.ModelFactory;


public class RunEval {
	

	public static void main(String[] args) {
		
		
		OntModel m =  ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF); 
		
		m.read("src/main/resources/sample_non_compliant_trace.ttl");
		m.read("src/main/resources/ep-plan.ttl");
		m.read("src/main/resources/prov-o.ttl");
		m.read("src/main/resources/fs-prov-ep-plan-mapping.ttl");
		m.read("src/main/resources/fs-prov.ttl");
		m.read("src/main/resources/deliveryCateringPlan1.ttl");
		
		
		System.out.println (m.size());
		
		
		
		 runQuery (EvalQueries.Q1,m);
		 
		 runQuery (EvalQueries.Q2,m);
		 
		 runQuery (EvalQueries.Q3,m);
		 
		 runQuery (EvalQueries.Q4,m);
		 
		 runQuery (EvalQueries.Q5,m);
		 
		 runQuery (EvalQueries.Q6,m);
		 
		 runQuery (EvalQueries.Q7,m);
		
		  runQuery (EvalQueries.Q8,m);
		
		 runQuery (EvalQueries.Q9,m);
		
		 runQuery (EvalQueries.Q10,m);
		
		
		
		// TODO Auto-generated method stub

	}

	private static void runQuery (String queryString, OntModel m) {
		
		String prefixes = " PREFIX fs-prov:<https://w3id.org/abdn/foodsafety/fs-prov#> PREFIX sosa:<http://www.w3.org/ns/sosa/> PREFIX ep-plan:<https://w3id.org/ep-plan#> PREFIX prov:<http://www.w3.org/ns/prov#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX ucp:<https://github.com/proofd-it/Ontologies/deliveryCateringPlan1.ttl#> PREFIX example-trace:<https://github.com/proofd-it/prov-trace#> PREFIX proofdit:<https://w3id.org/proofd-it#>";
			
		Query query = QueryFactory.create(prefixes +  queryString);
		QueryExecution qExe = QueryExecutionFactory.create( query, m);
	    ResultSet results = qExe.execSelect();
	    ResultSetFormatter.out(System.out, results, query) ;
	}
	
private static void runAskQuery (String queryString, OntModel m) {
		
		String prefixes = " PREFIX fs-prov:<https://w3id.org/abdn/foodsafety/fs-prov#> PREFIX sosa:<http://www.w3.org/ns/sosa/> PREFIX ep-plan:<https://w3id.org/ep-plan#> PREFIX prov:<http://www.w3.org/ns/prov#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX ucp:<https://github.com/proofd-it/Ontologies/deliveryCateringPlan1.ttl#> PREFIX example-trace:<https://github.com/proofd-it/prov-trace#> PREFIX proofdit:<https://w3id.org/proofd-it#>";
		
		Query query = QueryFactory.create(prefixes +  queryString);
		QueryExecution qExe = QueryExecutionFactory.create( query, m);
	    boolean b = qExe.execAsk();
	    System.out.println("Ask result = " + ((b)?"TRUE":"FALSE"));
	}
	
}
