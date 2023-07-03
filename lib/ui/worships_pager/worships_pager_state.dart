import 'package:gethsemane/data/local/database.dart';

class WorshipsPagerState {
  final List<EventData> worshipEvents;
  final bool isError;
  final bool isInProgress;
  final DateTime? dateFrom;

  WorshipsPagerState({
    this.worshipEvents = const [],
    this.isError = false,
    this.isInProgress = false,
    this.dateFrom,
  });

  WorshipsPagerState copyWith({
    List<EventData>? worshipEvents,
    bool? isError,
    bool? isInProgress,
    DateTime? dateFrom,
  }) =>
      WorshipsPagerState(
        worshipEvents: worshipEvents ?? this.worshipEvents,
        isError: isError ?? this.isError,
        isInProgress: isInProgress ?? this.isInProgress,
        dateFrom: dateFrom ?? this.dateFrom,
      );
}
