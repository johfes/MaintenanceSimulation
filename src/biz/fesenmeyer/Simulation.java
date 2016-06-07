package biz.fesenmeyer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;


public class Simulation {
	/**
	 * Future Event List.
	 */
	private static final SortedMap<Double, ArrayList<String>> FEL = new TreeMap<>();
	private static double simulationTime = 0.0;
	/**
	 * Mean Time Between Failure.
	 */
	private static double mtbf;
	private static final String FORMAT = "%.1f";
	private static double simulationDurance;
	private static double lastMaintenanceEnd = 0.0;
	
	public static double getSimulationTime() {
		return simulationTime;
	}

	public static void setSimulationTime(double simulationTime) {
		Simulation.simulationTime = simulationTime;
	}

	public static double getLastMaintenanceEnd() {
		return lastMaintenanceEnd;
	}

	public static void setLastMaintenanceEnd(double lastMaintenanceEnd) {
		Simulation.lastMaintenanceEnd = lastMaintenanceEnd;
	}

	public static double getMtbf() {
		return mtbf;
	}

	public static String getFormat() {
		return FORMAT;
	}

	public static void main(String[] args) {	
		Scanner s = new Scanner(System.in);
		System.out.println("Wie lange ist die MTBF in Tagen?");
		mtbf = s.nextDouble();
		System.out.println("Wieviele Tage soll die Simulation dauern?");
		simulationDurance = s.nextDouble();
		
		Machine machine = new Machine();
	    addToFel(0.1, "Ankunft");

	    System.out.println("Simulationsbeginn");
	    
		while(true){
			System.out.println("*****************************************");
			
		    for(Entry<Double, ArrayList<String>> entry : FEL.entrySet()) {
		    	  final Double key = entry.getKey();
		    	  final Object value = entry.getValue();
		    	  System.out.println(String.format(FORMAT, key) + " => " + value);
		    }
		    
	    	  System.out.println("WS" + " => " + machine.getCountWS());
	    	  System.out.println("Quality" + " => " + machine.getQualityQueue());
	    	  
	    	  simulationTime = FEL.firstKey();
	    	  if(simulationTime > simulationDurance){
	    		  break;
	    	  }
	    	  
			System.out.println("*****************************************");
		    
		    ArrayList<String> eventTypes = FEL.get(FEL.firstKey());
		    for (String eventType: eventTypes){
		    	execute(eventType, machine);
		    	removeFromFel(simulationTime, eventType);
		    }
		    
		}

		System.out.println("*****************************************");
		System.out.println("Simulationsende");
		System.out.println("*****************************************");
		System.out.println("Statistik:");
		System.out.println("Verfügbarkeit: "+
				String.format(FORMAT, calculateAvailability(machine.getDownTime()))+"%");
		System.out.println("durchschnittliche Warteschlangenlänge: "+
				String.format(FORMAT, getAverageWSLength(machine.getWSLengths())));		
	}
	
	private static void execute(String eventType, Machine machine) {
		switch(eventType){
		case "Ankunft":
			machine.arrival();
			break;
		case "Bearbeitungsende":
			machine.processingEnd();
			break;
		case "Wartungsende":
			machine.maintenanceEnd();
			break;
		default:
			throw new IllegalStateException("Unknown eventType: "+eventType);
	}
}
	
	public static void addToFel(Double time, String eventType){
		if(FEL.containsKey(time)){
			ArrayList<String> eventTypes= FEL.get(time);
			eventTypes.add(eventType);
		} else {
			ArrayList<String> eventTypes = new ArrayList<String>();
			eventTypes.add(eventType);
			FEL.put(time,eventTypes);
		}
	}
	
	public static void removeFromFel(Double time, String eventType){
  	  ArrayList<String> eventTypes = (ArrayList<String>) FEL.get(time);
  	  if(eventTypes.size() > 1){
  		eventTypes.remove(eventType);
  		FEL.replace(time, eventTypes);
  	  } else{
		  FEL.remove(time);
  	  }
	}
	
	public static double calculateAvailability(Double downTime){
		double availabilityTime = simulationDurance-downTime;
		return (availabilityTime/simulationDurance)*100;
	}

	public static int getQualitySum(Queue<Integer> qualityValues){
		int sum = 0;
		for(Integer quality : qualityValues){
			sum+=quality;
		}
		return sum;
	}
	
	public static double getAverageWSLength(SortedMap<Double, Integer> WSLengths){
		double sum = 0.0;
		SortedMap<Double, Integer> tmpMap = new TreeMap<Double, Integer>();
		
		for (Entry<Double, Integer> entry : WSLengths.entrySet()) {
			Double time = entry.getKey();
		    Integer wsCount = entry.getValue();
		    if(tmpMap.size() > 0 && !tmpMap.containsValue(wsCount)){
		    	sum+= (time-(Double)tmpMap.firstKey()) *(Integer) tmpMap.get(tmpMap.firstKey());
		    	tmpMap.clear();
		    }
		    tmpMap.put(time, wsCount);
		}

		return sum/simulationDurance;
	}
}
