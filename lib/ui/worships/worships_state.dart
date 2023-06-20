import 'package:gethsemane/data/local/database.dart';

class WorshipsState {
  final List<EventData> worshipEvents;

  WorshipsState({
    this.worshipEvents = const [],
  });

  WorshipsState copyWith({
    List<EventData>? worshipEvents,
  }) =>
      WorshipsState(
        worshipEvents: worshipEvents ?? this.worshipEvents,
      );
}
