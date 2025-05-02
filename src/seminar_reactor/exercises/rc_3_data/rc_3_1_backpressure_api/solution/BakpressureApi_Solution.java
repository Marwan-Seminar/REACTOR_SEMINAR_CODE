package seminar_reactor.exercises.rc_3_data.rc_3_1_backpressure_api.solution;

import org.reactivestreams.Subscription;


import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink.OverflowStrategy;
import reactor.core.scheduler.Schedulers;


/*

Der Stream in dieser Aufgabe versendet große Items aus einer Source, die 
nicht Backpressure aware ist. Das kann zu Problemen fuehren:

a ) Veraendere den Stream durch Backpressure so, dass es zu einem Absturz kommt
 	(OutOfMemory) indem Du im Subscriber Backpressure erzeugst.
	Hinweis: Subscription.request() in den Subscriber einbauen

b) 	Loese das Problem indem Du eine Backpressure-Stage in den Stream einbaust
	Hinweis: onBackpressureBuffer(100) 

c)	Loese das Problem, indem Du der Source eine OverflowStrategie mitgibst.
	Hinweis: create() mit OverflowStrategy.DROP

Lernziel: Notwendigkeit von Backpressure verstehen und API dafür kennenlernen 
 
 * 
 */
public class BakpressureApi_Solution {
	
	public static void main(String[] args) {
		BakpressureApi_Solution instance = new BakpressureApi_Solution();
		
	
		instance.memoryOverflowStream_Solution_a();
		
		//instance.memoryOverflowStream_Solution_b();
		
		//instance.memoryOverflowStream_Solution_c();
		
		sleep(120000);
	}

	
	/*
	 * Dieser Stream hat eine Source, die nicht Backpressure aware ist.
	 * Er erzeugt einen OutOfMEmoryError.
	 */
	void memoryOverflowStream_Solution_a(){
		
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
			
			
			// Log-Output
			.doOnRequest(request -> {
				System.out.println("Requested Demand by Subscriber: " + request);
			})


			// Backpressure-Subscriber
			.subscribe(new BaseSubscriber<int[]>(){
				@Override
				protected void hookOnSubscribe(Subscription subscription) {
					// TODO Backpressure erzeugen
					// Kein Backpressure: super.hookOnSubscribe(subscription) sorgt dafür, dass kein Backpressure entsteht. Es muss auskommentiert werden, um den Absturz zu erzeugen.
					//super.hookOnSubscribe(subscription);
					// Backpressure: Wenn diese Zeile einkommentiert wird, entsteht Backpressure, der zum Absturz fuehrt!
					subscription.request(20);
					
					
				}
				
				@Override
				protected void hookOnNext(int[] value) {
					System.out.println("Subscriber processed  " + value[0] + " " + Thread.currentThread());
				}
				
			});
		
	}
	
	/*
	 * Dieser Stream loest das Problem des OutOfMEmoryError indem 
	 * er eine Stream-Stufe einbaut, die ueberschuessige Elemente 
	 * verwirft
	 */
	void memoryOverflowStream_Solution_b(){
		
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
			
			// Thread-Grenze:  Entkoppelt die Stufen in separate Threads
			.publishOn(Schedulers.parallel())			
			
			
			// Backpressure-Subscriber
			.subscribe(new BaseSubscriber<int[]>(){
				@Override
				protected void hookOnSubscribe(Subscription subscription) {
					// Backpressure
					subscription.request(20);
					
				}
				
				@Override
				protected void hookOnNext(int[] value) {
					System.out.println("Subscriber processed  " + value[0] + " " + Thread.currentThread());
				}
				
			});
		
	}
	
	
	/*
	 * Dieser Stream loest das Problem, indem 
	 * dem Flux.create() mittels OverflowStrategy.DROP sagt
	 * dass die ueberschuessigen Items verworfen werden sollen
	 */
	void memoryOverflowStream_Solution_c(){

		// Source
		Flux<int[]> source = Flux.create(sink -> {
			for(int i = 0; i < 100_000; ++i) {
				System.out.println("Source emiting: " + i + " " + Thread.currentThread());
				int[] bigArray = new int[100_000];
				bigArray[0] = i;
				sink.next(bigArray);
			}
		}
		// TODO: die Overflow Strategy hier eintragen
		, OverflowStrategy.DROP);


		source

		// Log Output
		.map(bigArray -> { 
			// Zeige welche Daten durch den Stream fließen
			System.out.println("mapping : " + bigArray[0] + " " + Thread.currentThread());
			return bigArray;
		})

		
		// Thread-Grenze: Entkoppelt die Stufen in separate Threads
		.publishOn(Schedulers.parallel())			
 			

		
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

	
	
/////////////////// HELPER ///////////////////////
	
	static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
