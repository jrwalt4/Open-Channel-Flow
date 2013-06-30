package reese.openchannel;

import reese.functions.AnalyticFunction;

public class ManningRoot extends AnalyticFunction {
	
	double flow;
	Channel channel;
	
	public ManningRoot(Flow newFlow, Channel newChannel) {
		flow = newFlow.getFlow();
		channel = newChannel;
		functionName = "Manning's";
	}

	@Override
	public double f(double depth) {
		double constant;
		if (channel.isMetric) constant = 1;
		else constant = 1.49;
		return flow - constant/channel.roughness * channel.area(depth) *
				Math.pow(channel.hydRadius(depth), 2.0/3.0) * 
				Math.sqrt(channel.slope);
	}

}
