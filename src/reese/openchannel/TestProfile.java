package reese.openchannel;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class TestProfile extends Profile {

	public TestProfile(Flow newFlow, double start, double newLength) {
		super(newFlow,start,newLength);
	}

	@Override
	protected double slope (double depth, double x) {
		double gravity = (flow.isMetric) ? 9.81 : 32.2;
		TrapezoidChannel channel = (TrapezoidChannel) this.channel;
		return (getFlow(x)*depth*(inflow - outflow))
				/ (gravity*channel.bottomWidth*Math.pow(depth, 3.0) 
						- Math.pow(getFlow(x), 2.0));
	}
	
	@Override
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
			outFile = new FileWriter("test.txt");
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
			out.println(x.floatValue() + "	" + y.floatValue());
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
}
