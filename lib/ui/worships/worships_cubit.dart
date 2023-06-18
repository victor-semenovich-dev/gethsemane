import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gethsemane/domain/repository/events.dart';

class WorshipsCubit extends Cubit<dynamic> {
  final EventsRepository eventsRepository;

  WorshipsCubit({
    required this.eventsRepository,
  }) : super(0) {
    eventsRepository.loadEvents();
  }
}
