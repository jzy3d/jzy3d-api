package org.instantsvm.regression;

import java.io.IOException;

import libsvm.svm_model;

import org.instantsvm.Parameters;
import org.instantsvm.SVM;

public class RegressionSVM extends SVM {
	public RegressionSVM(){
		super();
	}
	
	public RegressionSVM(String filename) throws IOException{
		super(filename);
	}
	
	public RegressionSVM(svm_model model){
		super(model);
	}
	
	public void train(RegressionInputs inputs, Parameters parameters){
		train(inputs.getX(), inputs.getY(), parameters);
	}
	
	public void train(RegressionInputs inputs){
		train(inputs.getX(), inputs.getY());
	}
	
	@Override
    public Parameters getDefaultParameters(){
		return new RegressionParameters();
	}
}
