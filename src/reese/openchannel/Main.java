package reese.openchannel;
import reese.ioutil.MyReader;

/**
 * Main class for Open Channel Flow calculations 
 *
 * @author Reese
 */
@SuppressWarnings("unused")
public class Main {
	/*
	static double Q = 0.01;
	static double B = 3;
	static double m = 0.5;
	static double n = 0.0222;
	static double S0 = 0.15;
	static double Y0 = 0.01;
	static double X0 = 0;
	static double L = 75;
	static double initStep = 0.1;
	static double relAccuracy = 0.005;
	static double inflow = 2.72;
	static double outflow = 0.0;
	//*/
	
	//*
	static double Q = 750;
	static double B = 20;
	static double m = 0.0;
	static double n = 0.022;
	static double S0 = 0.0007;
	static double Y0 = 0.01;
	static double X0 = 0;
	static double L = 75;
	static double initStep = 0.1;
	static double relAccuracy = 0.0001;
	static double inflow = 0;
	static double outflow = .5;
	//*/
	
	public static void main(String[] args) {
		MyReader mr = new MyReader();
		Double d = mr.readDouble("Any Number: ");
		System.out.println(d*2.0);
		mr.close();
		test();
	}


	/**
	 * Calculates a water surface profile based using the later inflow and 
	 * outflow provided as static member variables in the main class
	 */
	public static void lateralInflow() {
		
		TrapezoidChannel trapChannel = new TrapezoidChannel(S0, true, n, B, m);

		Flow flow = new Flow(Q,trapChannel,Y0);
		flow.setDepth(flow.critDepth() + .1);
		Profile profile = new Profile(flow, X0, L);
		profile.setInflow(inflow);
		profile.setOutflow(outflow);
		
		double[][] prof = profile.calculateProfile(initStep, relAccuracy);
		write("Finished profile");
		/*
		double[] res = profile.findCritical();
		write("Crit at x	= "+res[0]);
		write("Froude		= "+res[1]);
		write("Num		= "+profile.function(res[0]));
		//*/
	}
	
	public static void test() {
		
		TrapezoidChannel trapChannel = new TrapezoidChannel(S0, false, n, B, m);

		Flow flow = new Flow(Q,trapChannel,Y0);
		TestProfile tProf = new TestProfile(flow, X0, L);
		tProf.setInflow(inflow);
		tProf.setOutflow(outflow);
		
		double[][] prof = tProf.calculateProfile(initStep, relAccuracy);
		write("Finished test");
	}
	
	static void write(Object string) {
		System.out.println(string);
	}
	
	static void blankLine() {
		System.out.println();
	}
	
}