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
    loadEvents();
    _worshipEventsSubscription =
        eventsRepository.getActualWorshipEvents().listen((worshipEvents) {
      emit(state.copyWith(worshipEvents: worshipEvents));
    });
  }

  void loadEvents() async {
    if (!state.isInProgress) {
      try {
        emit(state.copyWith(isError: false, isInProgress: true));
        final dateFrom = (state.dateFrom ?? DateTime.now())
            .subtract(const Duration(days: 30));
        await eventsRepository.loadEvents(dateFrom: dateFrom);
        final events = await eventsRepository.getActualWorshipEvents().first;
        emit(state.copyWith(
            worshipEvents: events, isInProgress: false, dateFrom: dateFrom));
        if (events.length < 3) {
          loadEvents();
        }
      } catch (e) {
        Logger.root.log(Level.SEVERE, e);
        final events = await eventsRepository.getActualWorshipEvents().first;
        emit(state.copyWith(
            isError: true, worshipEvents: events, isInProgress: false));
      }
    }
  }

  @override
  Future<void> close() {
    _worshipEventsSubscription.cancel();
    return super.close();
  }
}
