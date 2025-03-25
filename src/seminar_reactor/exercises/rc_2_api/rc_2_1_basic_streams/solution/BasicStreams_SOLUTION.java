package seminar_reactor.exercises.rc_2_api.rc_2_1_basic_streams.solution;


import java.util.concurrent.atomic.AtomicInteger;

import reactor.core.publisher.Flux;

/*
 * a)	Stream mit wenigen Stufen:
 * 		Baue einen einfachen Stream aus: range(), map(), subscribe().
 *  	Zeige druch Shell Ausgaben, was die einzelnen Stufen tun.
 * 		
 * 		Hinweise
 * 		map( i -> {
 *   		System.out.println(“mapping i ” + 1);
 *      	return i+1;
 *   	});
 * 		
 * 		.doOnNext(i -> {...})
 *
 *
 * b) 	In welchen Threads laufen die Stream-Stufen? Laufen sie parallel oder nacheinander
 *
 * 		Hinweis: .map( value -> {System.out.println( Thread.currentThread(); return value;}); 
 * 
 * c) 	Summe: Baue einen Stream aus den Stufen range(), filter(), map(), subscribe(), 
 * 		wobei der Subscriber die Quadrate der geraden Zahlen von 0-10 aufsummiert. 
 * 
 * 
 * Lernziel: Stream API benutzen können und mit Shell-Ausgaben debuggen.
 * 
 * 
 */
public class BasicStreams_SOLUTION {
	
	public static void main(String[] args) {
	
		System.out.println("BasicStreams_SOLUTION");
		
		
		BasicStreams_SOLUTION instance = new BasicStreams_SOLUTION();
		
		instance.a_fewStages();
		
		instance.b_whichThreads();
		
		instance.c_filterMapSum();
		
	}

	/*
	 *  a)	Stream mit wenigen Stufen:
	 * 		Baue einen einfachen Stream aus: range(), map(), subscribe().
	 *  	Zeige druch Shell Ausgaben, was die einzelnen Stufen tun.
	 * 		
	 * 		Hinweise
	 * 		map( i -> {
	 *   		System.out.println(“mapping i ” + 1);
	 *      	return i+1;
	 *   	});
	 * 		
	 * 		.doOnNext(i -> {...})
	 */
	private void a_fewStages() {
		

		System.out.println("\n a_fewStages \n");
		
		// Factory: erzeugt Daten und emittiert sie
		Flux<Integer> source = Flux.range(1, 10);
		
		source
			// doOnNext dient nur dem Debuggen, 
			// schreibt Items auf die Shell, die die Source emittiert
			.doOnNext(i -> {
				System.out.println("Source emited: " + i);
			
			})
		
		// map() bildetItems auf neue Items ab 
		// und gibt einen Stream mit den neuen Items zurueck 
		.map( i -> {
			int mapResult = i * 2;
			System.out.println("map() Stage maps " + i + " to " + mapResult);
			return mapResult;
		})
		
		// subscribe() empfaengt Items am Ende des Streams
		.subscribe(i -> {
			System.out.println("Subscriber got: " + i);
		}); 
		
	}

	/*
	 * b) In welchen Threads laufen die Stream-Stufen? Laufen sie parallel oder
	 * nacheinander
	 *
	 * Hinweis: .map( value -> {System.out.println( Thread.currentThread(); return
	 * value;});
	 */
	private void b_whichThreads() {
		
		System.out.println("\n b_whichThreads \n");
		
		// Factory: erzeugt Daten und emittiert sie
		Flux<Integer> source = Flux.range(1, 10);
		
		source
		.map( i -> {
			int mapResult = i * 2;
			System.out.println("map() Stage maps " + i + " to " + mapResult + " " + Thread.currentThread());
			return mapResult;
		})
		
		.subscribe(i -> {
			System.out.println("Subscriber got: " + i + " " + Thread.currentThread());
		}); 
		
	}
	
	private void c_filterMapSum() {
		
		System.out.println("\nc_filterMapSum\n");
		
		// Hierin summiert der Subscriber
		final AtomicInteger sum = new AtomicInteger();
		
		// Factory: erzeugt Daten und emittiert sie
		Flux<Integer> source = Flux.range(1, 10);
		
		source
		
		// Gerade Zahlen herausfiltern mit filter()
		.filter(i -> {
			System.out.println("filtering: " + i);
			return (i%2 ==0);
				
		})
		// Quadrate berechnen mit map()
		.map( i -> {
		
			int square = i*i;
			System.out.println("mapping: " + i + " to " + square);
			return square;
		})
		
		// Subscriber summiert in einer final Variable 
		.subscribe(i -> {
			
			System.out.println("Adding " + i + " to " + sum.get() + " result " +( i + sum.get()));
			
			sum.addAndGet(i);
		}); 
		
	}
}
