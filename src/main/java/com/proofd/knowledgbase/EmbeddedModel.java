package com.proofd.knowledgbase;

import java.io.InputStream;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;



public class EmbeddedModel {

	Model model;
	
	public EmbeddedModel () {	
		
		 model = ModelFactory.createDefaultModel ();
		 model.setNsPrefix("ep-plan", "https://w3id.org/ep-plan#");
		 model.setNsPrefix("fs-prov","https://w3id.org/abdn/foodsafety/fs-prov#");
		 model.setNsPrefix("prov","http://www.w3.org/ns/prov#");	
	}
	
//	public static void main(String[] args) {
//		 new EmbeddedModel ();
//	}

	public Model getModel() {
		return model;
	}
	
	
		
}
