import 'dart:async';

import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gethsemane/domain/repository/events.dart';
import 'package:gethsemane/ui/worships/worships_state.dart';

class WorshipsCubit extends Cubit<WorshipsState> {
  final EventsRepository eventsRepository;

  late StreamSubscription _worshipEventsSubscription;

  WorshipsCubit({
    required this.eventsRepository,
  }) : super(WorshipsState()) {
    eventsRepository.syncEvents();
    _worshipEventsSubscription =
        eventsRepository.getActualWorshipEvents().listen((worshipEvents) {
      emit(state.copyWith(worshipEvents: worshipEvents));
    });
  }

  @override
  Future<void> close() {
    _worshipEventsSubscription.cancel();
    return super.close();
  }
}
