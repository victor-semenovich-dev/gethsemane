import 'dart:async';

import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gethsemane/domain/repository/events_repository.dart';
import 'package:gethsemane/domain/usecase/load_initial_data_usecase.dart';
import 'package:gethsemane/ui/worships_pager/worships_pager_state.dart';
import 'package:logging/logging.dart';

class WorshipsPagerCubit extends Cubit<WorshipsPagerState> {
  final LoadInitialDataUseCase loadInitialDataUseCase;
  final EventsRepository eventsRepository;

  late StreamSubscription _worshipEventsSubscription;

  WorshipsPagerCubit({
    required this.loadInitialDataUseCase,
    required this.eventsRepository,
  }) : super(WorshipsPagerState()) {
    loadInitialData();
    _worshipEventsSubscription =
        eventsRepository.getActualWorshipEvents().listen((worshipEvents) {
      emit(state.copyWith(worshipEvents: worshipEvents));
    });
  }

  void loadInitialData() async {
    emit(state.copyWith(isInProgress: true));
    await loadInitialDataUseCase.invoke();
    await loadEvents(updateProgress: false);
    emit(state.copyWith(isInProgress: false));
  }

  Future<void> loadEvents({bool updateProgress = true}) async {
    if (!updateProgress || !state.isInProgress) {
      try {
        emit(state.copyWith(
          isError: false,
          isInProgress: updateProgress ? true : null,
        ));
        final dateFrom = (state.dateFrom ?? DateTime.now())
            .subtract(const Duration(days: 30));
        await eventsRepository.loadEvents(dateFrom: dateFrom);
        final events = await eventsRepository.getActualWorshipEvents().first;
        emit(state.copyWith(
          worshipEvents: events,
          isInProgress: updateProgress ? false : null,
          dateFrom: dateFrom,
        ));
        if (events.length < 3) {
          loadEvents();
        }
      } catch (e) {
        Logger.root.log(Level.SEVERE, e);
        final events = await eventsRepository.getActualWorshipEvents().first;
        emit(state.copyWith(
          isError: true,
          worshipEvents: events,
          isInProgress: updateProgress ? false : null,
        ));
      }
    }
  }

  @override
  Future<void> close() {
    _worshipEventsSubscription.cancel();
    return super.close();
  }
}
