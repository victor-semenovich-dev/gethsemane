import 'package:gethsemane/domain/model/worship_extended.dart';

class WorshipState {
  final WorshipExtended? worship;
  final bool isError;
  final bool isInProgress;

  WorshipState({
    this.worship,
    this.isError = false,
    this.isInProgress = false,
  });

  WorshipState copyWith({
    WorshipExtended? worship,
    bool? isError,
    bool? isInProgress,
    DateTime? dateFrom,
  }) =>
      WorshipState(
        worship: worship ?? this.worship,
        isError: isError ?? this.isError,
        isInProgress: isInProgress ?? this.isInProgress,
      );
}
