package paradigms.clase8;

import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReactiveStreamsLab {

  @Test
  void justEmmitOneValue() {
    final var singleEvent = "One event... whatever";
    final var observableSource = Observable.just(singleEvent);

    observableSource
            .subscribe(event -> assertEquals(singleEvent, event));
  }

  @Test
  void emittingMultiplesValues() {
    final var multiplesEvents = List.of("event1", "event2", "event3");
    final var observableSource = Observable.fromIterable(multiplesEvents);

    observableSource
        .map(event -> event.concat("-checked"))
        .subscribe(event -> assertTrue(event.contains("-checked")));
  }

  @Test
  void observablesLifeCicle() {
    final var multiplesEvents = List.of("a", "b", "c");
    final var observableSource = Observable.fromIterable(multiplesEvents);

    final var stringConsumer = new StringBuilder();

    observableSource.subscribe(
        stringConsumer::append, // onNext
        Throwable::printStackTrace, // onError
        () -> stringConsumer.append("_sarasa")); // onComplete

    assertEquals("abc_sarasa", stringConsumer.toString());
  }

  @Test
  void scanningObservables() {
    final var source = List.of("1", "2", "3", "4");

    final var observable = Observable.fromIterable(source);

    final var result = new StringBuilder();
    observable.scan("", (a, b) -> a + b).subscribe(result::append);

    assertEquals("1 12 123 1234", result.toString());
  }

  @Test
  void groupingByEvents() {
    final var source = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    final var observable = Observable.fromIterable(source);

    int[] PAR = {0};

    int[] IMPAR = {0};

    observable
            .groupBy(value -> value % 2 == 0 ? "PAR" : "IMPAR")
            .subscribe(
                    groupedObservable ->
                            groupedObservable.subscribe(
                                    value ->
                                            Optional.of(groupedObservable.getKey().equals("PAR"))
                                                    .filter(isEven -> isEven)
                                                    .map(even -> Observable.just(value))
                                                    .orElseGet(() -> Observable.fromIterable(Collections.nCopies(3, value)))
                                                    .subscribe(System.out::println)));

    assertEquals(30, PAR[0]);
    assertEquals(25, IMPAR[0]);
  }

  @Test
  void filteringEvents() {
    final var source = List.of(23, 12, 56, 28, 45);

    final var observable = Observable.fromIterable(source);

    final var accumulator = new AtomicInteger();

    observable
        .filter(value -> value < 20)
        .subscribe(System.out::println);

    assertEquals(63, accumulator.get());
  }
}
