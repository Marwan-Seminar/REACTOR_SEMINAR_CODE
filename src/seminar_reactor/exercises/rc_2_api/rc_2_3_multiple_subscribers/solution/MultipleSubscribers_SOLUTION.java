package seminar_reactor.exercises.rc_2_api.rc_2_3_multiple_subscribers.solution;

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
public class MultipleSubscribers_SOLUTION {
	
	public static void main(String[] args) {
		MultipleSubscribers_SOLUTION instance = new MultipleSubscribers_SOLUTION();
		
		//instance.twoSubscribersNaive();
		
		instance.twoSubscribersParallel();
	}

	private void twoSubscribersNaive() {
		
		Flux<Integer> source = Flux.range(0, 100);
		
		source.subscribe(i ->{
			System.out.println("Subscriber 1 " + i + " " + Thread.currentThread());
		});
		
		source.subscribe(i ->{
			System.out.println("Subscriber 2 " + i + " " + Thread.currentThread());
		});
		
	}

	private void twoSubscribersParallel() {
		
		Flux<Integer> source = Flux.range(0, 100);
		
		source = source.subscribeOn(Schedulers.parallel());
		
		source.subscribe(i ->{
			System.out.println("Subscriber 1 " + i + " " + Thread.currentThread());
		});
		
		source.subscribe(i ->{
			System.out.println("Subscriber 2 " + i + " " + Thread.currentThread());
		});
		
		sleep(10000);
	}

	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
