import 'package:gethsemane/data/local/database.dart';

class WorshipsPagerState {
  final List<EventData> worshipEvents;
  final bool isError;
  final bool isInProgress;

  WorshipsPagerState({
    this.worshipEvents = const [],
    this.isError = false,
    this.isInProgress = false,
  });

  WorshipsPagerState copyWith({
    List<EventData>? worshipEvents,
    bool? isError,
    bool? isInProgress,
  }) =>
      WorshipsPagerState(
        worshipEvents: worshipEvents ?? this.worshipEvents,
        isError: isError ?? this.isError,
        isInProgress: isInProgress ?? this.isInProgress,
      );
}
