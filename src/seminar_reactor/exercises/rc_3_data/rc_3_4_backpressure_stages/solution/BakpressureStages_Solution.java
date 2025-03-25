package seminar_reactor.exercises.rc_3_data.rc_3_4_backpressure_stages.solution;

import org.reactivestreams.Subscription;


import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/*
 * Der Stream in dieser Aufgabe erzeugt einen OutOfMemory Error, 
 * weil in der Source des Streams viele grosse Elemente entstehen,
 * die nicht schnell genug den Stream hinunter fliessen, weil der Subscriber
 * Backpressure signalisiert.
 * 
 *  
 * a)	Repariere den Stream indem Du eine weitere Stream-Stufe einfuehrst, die 
 * 		im Falle von Backpressure ueberschuessige Elemente verwirft.
 * 		HINWEIS: onBackpressureBuffer(100) oder onBackpressureDrop()
 * 
 * b) 	Zeige, dass onBackpressureBuffer(100) das Problem nicht loest, 
 * 		wenn es NACH dem publishOn() steht, und finde eine Erklaerung dafuer.
 * 		
 * Vereinfacht sieht der Stream so aus:
 * 
 * create()
 * .map()
 * .publishOn()
 * .subscribe()
 * 
 * Der Subscriber hat folgende Eigenschaften
 * 1. Er erzeugt Backpressure, indem er nur 20 Elemente anfordert.
 * 2. Er ist langsam
 * 
 */
public class BakpressureStages_Solution {
	
	public static void main(String[] args) {
		BakpressureStages_Solution instance = new BakpressureStages_Solution();
		
	
		instance.memoryOverflowStream_Base();
		
		//instance.memoryOverflowStream_Solaution_a();
		
		//instance.memoryOverflowStream_Solution_b();
		
		sleep(120000);
	}

	
	/*
	 * Dieser Stream hat eine Source, die nicht Backpressure aware ist.
	 * Er erzeugt einen OutOfMEmoryError.
	 */
	void memoryOverflowStream_Base(){
		
		// Source
		Flux<int[]> source = Flux.create(sink -> {
			for(int i = 0; i < 100_000; ++i) {
				System.out.println("Source emiting: " + i + " " + Thread.currentThread());
				int[] bigArray = new int[100_000];
				bigArray[0] = i;
				sink.next(bigArray);
			}
		});
		
	
		source
		
			// Log Output
			.map(bigArray -> { 
				// Zeige welche Daten durch den Stream fließen
				System.out.println("mapping : " + bigArray[0] + " " + Thread.currentThread());
				return bigArray;
			})
			
			
			// Thread-Grenze:  Entkoppelt die Stufen in separate Threads
			.publishOn(Schedulers.parallel())			
			
			
			// Backpressure-Subscriber, langsam
			.subscribe(new BaseSubscriber<int[]>(){
				@Override
				protected void hookOnSubscribe(Subscription subscription) {
					// Backpressure
					subscription.request(20);
					
				}
				
				@Override
				protected void hookOnNext(int[] value) {
					// langsam
					sleep(100);
					System.out.println("Subscriber processed  " + value[0] + " " + Thread.currentThread());
				}
				
			});
		
	}
	
	
	/*
	 * Loesung des Problems durch onBackpressureBuffer(100)
	 */
	void memoryOverflowStream_Solaution_a(){

		// Source
		Flux<int[]> source = Flux.create(sink -> {
			for(int i = 0; i < 100_000; ++i) {
				System.out.println("Source emiting: " + i + " " + Thread.currentThread());
				int[] bigArray = new int[100_000];
				bigArray[0] = i;
				sink.next(bigArray);
			}
		});


		source

		// Log Output
		.map(bigArray -> { 
			// Zeige welche Daten durch den Stream fließen
			System.out.println("mapping : " + bigArray[0] + " " + Thread.currentThread());
			return bigArray;
		})

		// TODO: Loesung onBackpressureDrop() oder auch onBackpressureBuffer()
		.onBackpressureBuffer(100) 
		//.onBackpressureDrop()

		// Thread-Grenze: Entkoppelt die Stufen in separate Threads
		.publishOn(Schedulers.parallel())			
 			

		// Log-Output
		.doOnRequest(request -> {
			System.out.println("Requested Demand by Subscriber: " + request);
		})


		// Backpressure-Subscriber
		.subscribe(new BaseSubscriber<int[]>(){
			@Override
			protected void hookOnSubscribe(Subscription subscription) {
				// Backpressure
				subscription.request(20);

			}

			@Override
			protected void hookOnNext(int[] value) {
				// langsam
				sleep(100);
				System.out.println("Subscriber processed  " + value[0] + " " + Thread.currentThread());
			}

		});

	}

	/*
	 * Keine Loseung des Problems, da onBackpaessureBuffer(100) an einer unguenstigen Stelle im Stream steht
	 */
	private void memoryOverflowStream_Solution_b() {

		// Source
		Flux<int[]> source = Flux.create(sink -> {
			for(int i = 0; i < 100_000; ++i) {
				System.out.println("Source emiting: " + i + " " + Thread.currentThread());
				int[] bigArray = new int[100_000];
				bigArray[0] = i;
				sink.next(bigArray);
			}
		});


		source

		// Log Output
		.map(bigArray -> { 
			// Zeige welche Daten durch den Stream fließen
			System.out.println("mapping : " + bigArray[0] + " " + Thread.currentThread());
			return bigArray;
		})


		// Thread-Grenze: Entkoppelt die Stufen in separate Threads
		.publishOn(Schedulers.parallel())			

		// Log Output
		.doOnRequest(request -> {
			System.out.println("Requested Demand at publishOn(): " + request);
		})
			

		// TODO 2
		// An dieser Position im Stream hilft Drop oder Buffer NICHT!!! Warum? 
		// Weil der Subscriber langsam ist, ist auch der Thread der die Stufen nach 
		// publishOn() bedient verlangsamt. Daher stauen sich die Daten vor dem publishOn()
		// im Buffer des FluxCreate, der unbegrenzt ist und in einem anderen Thread lauft.
		//.onBackpressureDrop()
		.onBackpressureBuffer(100) 

		// Backpressure-Subscriber, langsam
		.subscribe(new BaseSubscriber<int[]>(){
			@Override
			protected void hookOnSubscribe(Subscription subscription) {
				// Backpressure
				subscription.request(20);


			}

			@Override
			protected void hookOnNext(int[] value) {
				// langsam
				sleep(100);
				System.out.println("Subscriber processed  " + value[0] + " " + Thread.currentThread());
			}

		});


	}

	
/////////////////// HELPER ///////////////////////
	
	static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
