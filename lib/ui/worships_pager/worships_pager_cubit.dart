import 'dart:async';

import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gethsemane/domain/repository/events.dart';
import 'package:gethsemane/ui/worships_pager/worships_pager_state.dart';
import 'package:logging/logging.dart';

class WorshipsPagerCubit extends Cubit<WorshipsPagerState> {
  final EventsRepository eventsRepository;

  late StreamSubscription _worshipEventsSubscription;

  WorshipsPagerCubit({
    required this.eventsRepository,
  }) : super(WorshipsPagerState()) {
    syncEvents();
    _worshipEventsSubscription =
        eventsRepository.getActualWorshipEvents().listen((worshipEvents) {
      emit(state.copyWith(worshipEvents: worshipEvents));
    });
  }

  void syncEvents() async {
    try {
      emit(state.copyWith(isError: false, isInProgress: true));
      await eventsRepository.syncEvents();
    } catch (e) {
      Logger.root.log(Level.SEVERE, e);
      final events = await eventsRepository.getActualWorshipEvents().first;
      emit(state.copyWith(
          isError: true, worshipEvents: events, isInProgress: false));
    }
  }

  @override
  Future<void> close() {
    _worshipEventsSubscription.cancel();
    return super.close();
  }
}
