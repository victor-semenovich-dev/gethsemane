import 'dart:async';

import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gethsemane/domain/repository/events_repository.dart';
import 'package:gethsemane/domain/usecase/load_initial_data_usecase.dart';
import 'package:gethsemane/ui/worships_pager/worships_pager_state.dart';
import 'package:logging/logging.dart';

class WorshipsPagerCubit extends Cubit<WorshipsPagerState> {
  final LoadInitialDataUseCase loadInitialDataUseCase;
  final EventsRepository eventsRepository;

  static const loadMoreEventsPeriod = Duration(days: 30);
  static const loadMoreEventsThreshold = 3;

  late StreamSubscription _worshipEventsSubscription;

  WorshipsPagerCubit({
    required this.loadInitialDataUseCase,
    required this.eventsRepository,
  }) : super(WorshipsPagerState()) {
    _loadInitialData();
    _worshipEventsSubscription =
        eventsRepository.getActualWorshipEvents().listen((worshipEvents) {
      emit(state.copyWith(worshipEvents: worshipEvents));
    });
  }

  void _loadInitialData() async {
    emit(state.copyWith(isInProgress: true));
    await loadInitialDataUseCase.perform();
    await _loadEventsFrom(DateTime.now().subtract(loadMoreEventsPeriod));
    emit(state.copyWith(isInProgress: false));
  }

  void reloadEvents() async {
    final dateFrom = state.dateFrom;
    if (dateFrom != null) {
      emit(state.copyWith(isInProgress: true));
      await _loadEventsFrom(dateFrom);
      emit(state.copyWith(isInProgress: false));
    }
  }

  void loadMoreEvents() async {
    final stateDateFrom = state.dateFrom;
    if (stateDateFrom != null) {
      emit(state.copyWith(isInProgress: true));
      final dateFrom = stateDateFrom.subtract(loadMoreEventsPeriod);
      await _loadEventsFrom(dateFrom);
      emit(state.copyWith(isInProgress: false));
    }
  }

  Future<void> _loadEventsFrom(DateTime dateFrom) async {
    try {
      emit(state.copyWith(isError: false));
      await eventsRepository.loadEvents(dateFrom: dateFrom);
      final events = await eventsRepository.getActualWorshipEvents().first;
      emit(state.copyWith(worshipEvents: events, dateFrom: dateFrom));
      if (events.length < loadMoreEventsThreshold) {
        await _loadEventsFrom(dateFrom.subtract(loadMoreEventsPeriod));
      }
    } catch (e) {
      Logger.root.log(Level.SEVERE, e);
      final events = await eventsRepository.getActualWorshipEvents().first;
      emit(state.copyWith(isError: true, worshipEvents: events));
    }
  }

  @override
  Future<void> close() {
    _worshipEventsSubscription.cancel();
    return super.close();
  }
}
