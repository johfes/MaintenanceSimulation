package biz.fesenmeyer;
import java.util.Random;


public class RandomGenerator {
    static Random random = new Random();

	public static double generateNextMaintenanceEnd(){
		int number = generateRandomInt(1, 3);
		return (double)number/10;
	}

	public static double generateNextArrival(){
		return exponential(0.5)/10.0;
	}	
	
	public static int generateQuality(){
		double timeSinceLastMaintenance = Simulation.getSimulationTime()-
										Simulation.getLastMaintenanceEnd();
		double number =  generateRandomInt(0, 4)+timeSinceLastMaintenance;
		
		if(number < Simulation.getMtbf()){
			return 0;
		}else{
			return 1;
		}
	}
	
	public static double generateNextProcessingEnd(){
		int number = generateRandomInt(1, 2);
		return (double) number/10;
	}
	
	public static double exponential(double lambda){
		return (-1/lambda) * Math.log(1-random.nextDouble());
	}
	
	public static int generateRandomInt(int min, int max) {
	    int randomNum = random.nextInt((max - min) + 1) + min;
	    return randomNum;
	}

}