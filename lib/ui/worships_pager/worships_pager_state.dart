import 'package:gethsemane/data/local/database.dart';

class WorshipsPagerState {
  final List<EventData> worshipEvents;
  final bool isError;

  WorshipsPagerState({
    this.worshipEvents = const [],
    this.isError = false,
  });

  WorshipsPagerState copyWith({
    List<EventData>? worshipEvents,
    bool? isError,
  }) =>
      WorshipsPagerState(
        worshipEvents: worshipEvents ?? this.worshipEvents,
        isError: isError ?? this.isError,
      );
}
