package reese.openchannel;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import reese.functions.AnalyticFunction;

public class Profile {
	
	protected Channel channel;
	Flow flow;
	public double beginning;
	public double startingY;
	public double length;
	public double end;
	public double finalY;
	protected double inflow;
	protected double outflow;
	public double[] distances;
	public double[] depths;
	
	public void setInflow(double in) {
		inflow = in;
	}
	
	public void setOutflow(double out) {
		outflow = out;
	}
	
	public Profile() {
		super();
	}
	
	public Profile(Flow newFlow, double start, double newLength) {
		channel = newFlow.getChannel();
		flow = newFlow;
		beginning = start;
		startingY = flow.getDepth();
		length = newLength;
		end = beginning + length;
		inflow = 0;
		outflow = 0;
	}
	
	public Profile(Channel newChannel, double newFlow, double newDepth, double start, double newLength) {
		this(new Flow(newFlow,newChannel,newDepth),start,newLength);
	}
	
	public Profile(Channel newChannel, double newFlow, double newDepth, double newLength) {
		this(newChannel, newFlow, newDepth, 0, newLength);
	}
	
	public double[][] calculateProfile(double relAccuracy) {
		return calculateProfile(this.length/Math.abs(this.length), relAccuracy);
	}
	
	/**
	 * Calculates the water depth profile, using an adaptive step Runge-Kutta 4th order method,
	 * with a starting initial step and within the percent accuracy specified
	 * @param initialStep
	 * @param relAccuracy
	 * @return 2-D array of the water depths at the calculated distances along the profile
	 */
	
	public double[][] calculateProfile(double initialStep, double relAccuracy) {
		//returns an empty result if the problem is not setup to 
		//calculate in the right direction 
		if (this.length*initialStep < 0) {
			Main.write("error in setup");
			return new double[2][1]; 
		}
		
		//initialize all the variables
		ArrayList<Double> profileY = new ArrayList<Double>(2);
		ArrayList<Double> profileX = new ArrayList<Double>(2);
		Double y = this.startingY;
		Double x = this.beginning;
		profileY.add(y);
		profileX.add(x);
		Double stepSize = initialStep;
		Double desiredDelta;
		Double delta;
		Double yh;
		Double yh_2;
		Double accRatio;
		FileWriter outFile = null;
		PrintWriter out = null;
		try {
			outFile = new FileWriter("mine.txt");
			out = new PrintWriter(outFile);
		} catch (IOException e) {
			Main.write(e.getMessage());
			return new double[2][1];
		}
		out.println(x.floatValue() + "	" + y.floatValue());
		//start the computational loop
		do {
			
			//corrects the stepsize to make sure the profile ends at channel's end
			if ((x + stepSize - this.beginning) / this.length > 1) {
				stepSize = this.beginning + this.length - x;
			}
			
			
			/* Performs the rk4 adaptive step "sub-loop" to required accuracy:
			 * accuracy is recorded as accRatio which is the ratio of acceptible error 
			 * to current step error, which needs to be more than 1 (error < acceptable) 
			 */
			do {
				yh = rk4(y, x, stepSize);
				yh_2 = rk4(rk4(y, x, stepSize/2.0), x+stepSize/2.0, stepSize/2.0);
				
				/* Stability check for errors:
				 * if there are problems then the trial stepsize is cut in half or reset, since an
				 * adaptive step correction will not work for NaN or infinite 
				 */
				if (yh.isNaN() || yh_2.isNaN() || yh.isInfinite() || yh_2.isInfinite()) {
					while (yh.isNaN() || yh_2.isNaN() || yh.isInfinite() || yh_2.isInfinite()) {
						if (stepSize.isInfinite() || stepSize.isNaN()) {
							stepSize = initialStep;
							continue;
						}
						stepSize /= 2.0;
						yh = rk4(y, x, stepSize);
						yh_2 = rk4(rk4(y, x, stepSize/2.0), x+stepSize/2.0, stepSize/2.0);
					}
				}
				desiredDelta = relAccuracy*y;//euler(y, x, stepSize);
				delta = (yh_2*yh_2 - yh*yh)/(yh_2 + yh);
				accRatio = Math.abs(desiredDelta/delta);
				if (accRatio < .999) stepSize *= Math.pow(accRatio, 0.2);
			} while (accRatio < .999);
			
			y = yh_2 + delta/15;
			x += stepSize;
			
			//print result to file
			out.println(x.floatValue()+"	"+y.floatValue()+"	"
					+flow.froudeNumber(y, getFlow(x))+"	"+getFlow(x));
			profileY.add(y);
			profileX.add(x);
			
			//exit condition for if flow hits zero
			if(y < 0.0001) break;
			
			//expand stepSize as per adaptive step algorithm
			stepSize *= Math.pow(accRatio, 0.25);
			
			//'while' condition is set so profile can step forwards or backwards
		} while ((x - this.beginning) / this.length < 1);
		
		//close the file after writing is finished
		out.close();
		
		//after loop ends the data is recorded and output:
		//the final values are recorded to be used in composite profiles
		this.finalY	= y.doubleValue();
		this.end	= x.doubleValue();
		double[] profileDistances = new double[profileX.size()];
		double[] profileDepths = new double[profileY.size()];
		for (int i = 0 ; i < profileX.size() ; i++) {
			profileDistances[i] = profileX.get(i);
			profileDepths[i] = profileY.get(i);
		}
		distances = profileDistances;
		depths = profileDepths;
		double[][] results = {profileDistances,profileDepths};
		return results;
	}
	
	/**
	 * Runge-Kutta 4th-order ODE step 
	 * @return profile depth after a step size of "h"
	 */
	public Double rk4(double initialDepth, double initialX, double stepSize) {
		
		/* The profile's flow returns its slope for a given depth
		 */
		double k1 = slope(initialDepth, initialX);
		double k2 = slope(initialDepth + stepSize/2.0 * k1, initialX + stepSize/2.0);
		double k3 = slope(initialDepth + stepSize/2.0 * k2, initialX + stepSize/2.0);
		double k4 = slope(initialDepth + stepSize * k3, initialX + stepSize);
		double beta = (k1 + 2.0*k2 + 2.0*k3 + k4) / 6.0;
		return initialDepth + beta*stepSize;
	}
	
	/**
	 * First-order Euler method, used in setting the y-scale of the adaptive step 
	 */
	
	protected Double euler(double Yn, double Xn, double h) {
		return Yn + slope(Yn, Xn)*h;
	}
	
	protected double slope (double depth, double x) {
		double units = (flow.isMetric) ? 1.0 : 1.49;
		double gravity = (flow.isMetric) ? 9.81 : 32.2;
		double Sf = Math.pow(getFlow(x) * channel.roughness
				/ units / channel.area(depth) 
				/ Math.pow(channel.hydRadius(depth), 2.0/3.0), 2.0);
		double num = (channel.slope - Sf
				- getFlow(x)/gravity/Math.pow(channel.area(depth),2.0) 
				* (2.0*inflow - outflow));
		double den = (1.0 - Math.pow(Flow.froudeNumber(channel,depth,getFlow(x),flow.isMetric), 2.0));
		return num/den;
	}

	protected double getFlow(double x) {
		double val = flow.flow + (x - beginning)*(inflow - outflow);
		return val;
	}
	
	/**
	 * Finds the section of the profile where the numerator of the slope is 0:
	 * this corresponds to the section where the flow is critical
	 * 
	 * The algorithm searches through the points output by calculateProfile for 
	 * a section where the slope is zero. It gets the depth at sections using a 
	 * linear interpolation between the points output from the profile 
	 * (since an adaptive step is used).  It searches by a generalized 
	 * root finder, finding a root for the numerator of the slope function.  
	 * @return
	 */
	public double[] findCritical() {
		AnalyticFunction fn = new AnalyticFunction("Find Critical Section") {
			@Override
			public double f(double x) {
			double units = (flow.isMetric) ? 1.0 : 1.49;
			double gravity = (flow.isMetric) ? 9.81 : 32.2;
			double Sf = Math.pow(getFlow(x) * 0.0000001//channel.roughness
				/ units / channel.area(depth(x)) 
				/ Math.pow(channel.hydRadius(depth(x)), 2.0/3.0), 2.0);
			double num = (channel.slope - Sf
				- getFlow(x)/gravity/Math.pow(channel.area(depth(x)),2.0) 
				* (2*inflow - outflow));
			return num;
			}
		};
		double x = fn.findRoot(beginning, end);
		double froude = flow.froudeNumber(depth(x), getFlow(x));
		return new double[] {x,froude};
	}
	
	protected double depth(double x) {
		///only works going forward
		if (x < this.beginning) return this.startingY;
		for (int i = 0 ; i < distances.length ; i++) {
			if (distances[i] > x) {
				
				return depths[i-1] + 
						(depths[i] - depths[i-1]) / (distances[i] - distances[i-1])
						*(x - distances[i-1]);
			}
		}
		return this.finalY;
	}
}
