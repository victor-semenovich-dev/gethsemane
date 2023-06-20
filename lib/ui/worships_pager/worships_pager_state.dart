import 'package:gethsemane/data/local/database.dart';

class WorshipsPagerState {
  final List<EventData> worshipEvents;

  WorshipsPagerState({
    this.worshipEvents = const [],
  });

  WorshipsPagerState copyWith({
    List<EventData>? worshipEvents,
  }) =>
      WorshipsPagerState(
        worshipEvents: worshipEvents ?? this.worshipEvents,
      );
}
