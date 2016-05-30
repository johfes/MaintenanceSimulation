package biz.fesenmeyer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;


public class Machine {
	private String status = "frei";
	private int countWS = 0;
	private double downTime = 0.0;
	private Queue<Integer> qualityQueue = new LinkedList<Integer>();
	private SortedMap<Double, Integer> WSLengths = new TreeMap<Double,Integer>();
	
	public int getCountWS() {
		return countWS;
	}

	public double getDownTime() {
		return downTime;
	}

	public Queue<Integer> getQualityQueue() {
		return qualityQueue;
	}

	public SortedMap<Double, Integer> getWSLengths() {
		return WSLengths;
	}

	public void arrival(){
		System.out.println("Ankunft "+ String.format(Simulation.getFormat(), Simulation.getSimulationTime()));
		double arrivalTime = Simulation.getSimulationTime() +RandomGenerator.generateNextArrival();
		Simulation.addToFel(arrivalTime, "Ankunft");
		System.out.println("Ankunftsereignis wurde geplant: "+String.format(Simulation.getFormat(), arrivalTime));
		if(countWS == 0 && status.equalsIgnoreCase("frei")){
			double processingEnd = Simulation.getSimulationTime()+RandomGenerator.generateNextProcessingEnd();
			status = "aktiv";
			Simulation.addToFel(processingEnd, "Bearbeitungsende");
			System.out.println("WS leer, Bearbeitung hat begonnen, Bearbeitungsendeereignis wurde geplant: "+String.format(Simulation.getFormat(), processingEnd));
		} else {
			countWS++;
			WSLengths.put(Simulation.getSimulationTime(), countWS);
			System.out.println("Das Teil wurde der Warteschlange hinzugefügt");
		}
	}

	public void processingEnd(){
		System.out.println("Bearbeitungsende "+ String.format(Simulation.getFormat(), Simulation.getSimulationTime()));
		int quality = RandomGenerator.generateQuality();
		
		if(qualityQueue.size() == 10){
			qualityQueue.remove();
		}
		qualityQueue.add(quality);
		
		if(Simulation.getQualitySum(qualityQueue) == 2){
			status = "wartung";
			double timeToMaintenanceEnd = RandomGenerator.generateNextMaintenanceEnd();
			downTime += timeToMaintenanceEnd;
			double maintenanceEndTime = Simulation.getSimulationTime() + timeToMaintenanceEnd;
			Simulation.addToFel(maintenanceEndTime, "Wartungsende");
			qualityQueue.clear();
			System.out.println("Wartung wird begonnen, Wartungsende wurde geplant: "+String.format(Simulation.getFormat(), maintenanceEndTime));
			return;
		}
		
			if(countWS > 0){
				countWS--;
				WSLengths.put(Simulation.getSimulationTime(), countWS);
				double processingEndTime = Simulation.getSimulationTime() +RandomGenerator.generateNextProcessingEnd();
				Simulation.addToFel(processingEndTime, "Bearbeitungsende");
				System.out.println("Teil wurde nachgezogen, Bearbeitungsende wurde geplant: "+String.format(Simulation.getFormat(), processingEndTime));
				status = "aktiv";
			} else {
				status = "frei";
			}
		
	}
	
	public void maintenanceEnd(){
		System.out.println("Wartungsende "+ String.format(Simulation.getFormat(), Simulation.getSimulationTime()));
		Simulation.setLastMaintenanceEnd(Simulation.getSimulationTime());
		
		if(countWS >= 1){
			countWS--;
			WSLengths.put(Simulation.getSimulationTime(), countWS);
			status = "aktiv";
			double processingEndTime = Simulation.getSimulationTime()+RandomGenerator.generateNextProcessingEnd();
			Simulation.addToFel(processingEndTime, "Bearbeitungsende");
			System.out.println("Teil wurde nachgezogen, Bearbeitungsende wurde geplant: "+String.format(Simulation.getFormat(), processingEndTime));
		} else {
			status = "frei";
		}
	}
}

