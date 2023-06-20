import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gethsemane/domain/repository/events.dart';
import 'package:gethsemane/ui/worships/worships_state.dart';

class WorshipsCubit extends Cubit<WorshipsState> {
  final EventsRepository eventsRepository;

  WorshipsCubit({
    required this.eventsRepository,
  }) : super(WorshipsState()) {
    eventsRepository.syncEvents();
  }
}
