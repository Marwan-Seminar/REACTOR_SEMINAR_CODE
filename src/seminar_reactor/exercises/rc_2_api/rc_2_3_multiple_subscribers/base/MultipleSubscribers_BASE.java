package seminar_reactor.exercises.rc_2_api.rc_2_3_multiple_subscribers.base;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/*
 * Baue einen Stream mit mehreren Subscribern und untersuche unter welchen Bedingungen diese 
 * gleichzeitig oder nacheinander mit Daten versorgt werden
 * 
 * Hinweise: 
 * - 	scheduleOn() parallelisiert die Ausfuehrung eines Streams.
 * - 	Falls das Programm sich beendet ohne dass etwas geschieht 
 * 		nutze sleep(3000) um kurz zu warten, und ueberlege, warum das notwendig ist.
 */
public class MultipleSubscribers_BASE {
	
	public static void main(String[] args) {
		MultipleSubscribers_BASE instance = new MultipleSubscribers_BASE();
		
		instance.twoSubscribersNaive();
		
		// TODO: warten, bis alle anderen Threads des Programms fertig sind
		
	}

	private void twoSubscribersNaive() {
		
		Flux<Integer> source = Flux.range(0, 100);
		
		
		// TODO: Die Source parallelisieren
	
		source.subscribe(i ->{
			System.out.println("Subscriber 1 " + i + " " + Thread.currentThread());
		});
		
		source.subscribe(i ->{
			System.out.println("Subscriber 2 " + i + " " + Thread.currentThread());
		});
		
	}

	
	/////////////// HELPER ////////////////
	

	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
