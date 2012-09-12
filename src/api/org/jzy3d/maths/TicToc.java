package org.jzy3d.maths;

/**
 * {@link TicToc} allows measuring elapsed time between a call to {@link tic()}
 * and a call to {@link toc()}.
 * Retrieving elapsed time is done by calling either: {@link elapsedNanosecond()},
 * {@link elapsedMilisecond()} or {@link elapsedSecond()}.
 * 
 * @author Martin Pernollet
 *
 */
public class TicToc {
	public TicToc(){
		start = 0;
		stop  = 0;
	}
	
	public void tic(){
		start = System.nanoTime();
	}
	
	/** return time in second*/
	public double toc(){
		stop = System.nanoTime();
		return elapsedSecond();
	}
	
	public long elapsedNanosecond(){
		return stop-start;
	}
	
	public double elapsedMicrosecond(){
		return elapsedNanosecond()/1000;
	}
	
	public double elapsedMilisecond(){
		return elapsedMicrosecond()/1000;
	}
	
	public double elapsedSecond(){
		return elapsedMilisecond()/1000;
	}
	

	/**********************************************/
	
	protected long start;
	protected long stop;
}
