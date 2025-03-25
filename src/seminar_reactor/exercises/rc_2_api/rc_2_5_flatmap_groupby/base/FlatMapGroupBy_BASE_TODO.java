package seminar_reactor.exercises.rc_2_api.rc_2_5_flatmap_groupby.base;

import java.util.function.Consumer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.scheduler.Schedulers;

/*
 * a) 	Zerlege die Zahlen 1..100 in vier Gruppen. 
 * 		Jede Gruppe enthält die Zaheln, die denselben Wert modulo 4 haben.
 * 		Zeige durch Shell-Output welche Zahl in welcher Gruppe ist.
 * 		Hinweis: groupBy()
 * 
 * b) 	Fuege die Gruppen aus a) so zusammen, so dass alle Zahlen wieder
 * 		in einen gemeinsamen Stream gemerged werden.
 * 		Hinweis: flatMap()
 * 
 * c) 	Parallelisiere die Loesung b) so dass die Elemente verschiedener
 * 		Gruppen parallel verarbeitet werden.
 * 		Zeige die Parallelität durch Shell Ausgaben
 * 		Hinweis: publishOn(Schedulers.parallel()) 
 * 
 */
public class FlatMapGroupBy_BASE_TODO {
	
	public static void main(String[] args) {
		
		FlatMapGroupBy_BASE_TODO instance = new FlatMapGroupBy_BASE_TODO();
		
		instance.a_groupingGroupBy();
		
		//instance.b_mergingFlatMap();
		
		//instance.c_parallelFlatMapGroupBy();
		
		sleep(3000);
	}

	/* 
	 * a) 	Zerlege die Zahlen 1..100 in vier Gruppen. 
	 * 		Jede Gruppe enthält die Zaheln, die denselben Wert modulo 4 haben.
	 * 		Zeige durch Shell-Output welche Zahl in welcher Gruppe ist.
	 * 		Hinweis: groupBy()
	 */
	private void a_groupingGroupBy() {
		
		// Emittiert Zahlen
		Flux<Integer> source = Flux.range(0, 100);
		
		// TODO: Gruppieren in vier Gruppen
		Flux<GroupedFlux<Object, Integer>> groupedFlux = null;
		
		// TODO: Subscribieren an GroupedFlux
		groupedFlux.subscribe((Consumer<? super GroupedFlux<Object, Integer>>)null

			// TODO  Subscribieren an jede Gruppe
				//System.out.println( "Group: " + group.key() + " Element " + val);
		);	
		
	}
	
	
	/* 
	 * b) 	Fuege die Gruppen aus a) so zusammen, so dass alle Zahlen wieder
	 * 		in einen gemeinsamen Stream gemerged werden.
	 * 		Hinweis: flatMap()
	 */
	private void b_mergingFlatMap() {
		
		// Emittiert Zahlen
		Flux<Integer> source =
				Flux.range(0, 100);
				
		// Gruppiert in vier Gruppen
		Flux<GroupedFlux<Object, Integer>> groupedFlux =
				source.groupBy(x -> x%4);
				
		
		// TODO Merging mit flatMap()
		//Flux<Integer> mergedStream 
		
			// TODO Gruppe bearbeiten
			
					// TODO Werte der Gruppe bearbeiten
					//System.out.println("Group: " + group.key() + " Element " + value);
		
		 
		
		// TODO  subcribieren
		//.subscribe( outValue -> {	System.out.println( "Subscriber: " + outValue);
		//});
		
	}

	 /* 
	 * c) 	Parallelisiere die Loesung b) so, dass die Elemente verschiedener
	 * 		Gruppen parallel verarbeitet werden können.
	 * 		Zeige die Parallelität durch Shell Ausgaben
	 * 		Hinweis: subscribeOn(Schedulers.parallel()) 
	 */
	private void c_parallelFlatMapGroupBy() {
		
		// Emittiert Zahlen
		Flux<Integer> source =
				Flux.range(0, 100);
				
		// Gruppiert in vier Gruppen
		Flux<GroupedFlux<Object, Integer>> groupedFlux =
				source.groupBy(x -> x%4);
				
		
		// Merging
		groupedFlux.flatMap( group -> {
			// Gruppe bearbeiten
			return 	
				group
				
				// TODO: PARALLEL EINSTIEGSPUNKT aktivieren
				
				.map( value -> {
				
					// Werte der Gruppe parallel bearbeiten
					System.out.println(
							"Value: " + value + " grp: " + group.key() + " " + Thread.currentThread().getName());
				return value;
			});			
		})
		
		.subscribe( outValue -> {
			System.out.println(
					"Subscriber: " + outValue);
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
