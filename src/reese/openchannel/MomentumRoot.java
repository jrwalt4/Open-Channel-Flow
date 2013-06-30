package reese.openchannel;

import reese.functions.AnalyticFunction;

public class MomentumRoot extends AnalyticFunction {
	
	double flow, momentum;
	Channel channel;
	
	public MomentumRoot(Flow newFlow, Channel newChannel) {
		flow = newFlow.getFlow();
		momentum = newFlow.momentum();
		channel = newChannel;
		functionName = "Momentum";
	}

	@Override
	public double f(double depth) {
		double gravity;
		if (channel.isMetric) gravity = 9.81;
		else gravity = 32.2;
		double val = (channel.centroid(depth)*channel.area(depth) - momentum)
				*gravity*channel.area(depth) + Math.pow(flow, 2.0);
		return val;
	}

}
