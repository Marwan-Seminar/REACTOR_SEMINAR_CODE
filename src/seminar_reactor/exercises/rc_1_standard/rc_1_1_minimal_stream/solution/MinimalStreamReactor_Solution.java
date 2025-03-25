package seminar_reactor.exercises.rc_1_standard.rc_1_1_minimal_stream.solution;

import reactor.core.publisher.Flux;

/*
 * Rc 1.1: Minimal Reactor-Stream
 * 
 * Einen ganz einfachen Reacor Stream bauen
 * 
 * 
 * Ein Flux versendet die Zahlen 1-100, ein Subscriber empfaengt sie, und schreibt sie auf die Shell.
 * 
 * Hinweise: 
 * 	- Flux.range(1, 100) gibt ein Observable zur�ck, das das die Daten emittiert
 * 	- Flux.subscribe(
 * 		i -> {
 * 			System.out.println(i);
 * 		}):
 * realieisert einen Subscriber, der die Daten auf die Shell schreibt.
 * 
 * Lernziel: Stream Programmierung kennenlernen
 */
public class MinimalStreamReactor_Solution {

	public static void main(String[] args) {
		
		MinimalStreamReactor_Solution instance = new MinimalStreamReactor_Solution();
		
		instance.simpleStreamRx();
	}

	private void simpleStreamRx() {
		
		Flux<Integer> source = Flux.range(1,  100);
		
		source.subscribe( value -> {
			System.out.println("Subscriber received next value: " + value);
		});
	}
}
