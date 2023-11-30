import 'dart:async';

import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gethsemane/domain/repository/worships_repository.dart';
import 'package:gethsemane/ui/worship/worships_state.dart';

class WorshipCubit extends Cubit<WorshipState> {
  final int id;
  final WorshipsRepository worshipsRepository;

  late StreamSubscription _worshipSubscription;

  WorshipCubit({
    required this.id,
    required this.worshipsRepository,
  }) : super(WorshipState()) {
    loadWorship();
    _worshipSubscription = worshipsRepository.getWorship(id).listen((worship) {
      emit(state.copyWith(worship: worship));
    });
  }

  void loadWorship() async {
    try {
      emit(state.copyWith(isInProgress: true, isError: false));
      await worshipsRepository.loadWorship(id);
      if (!isClosed) emit(state.copyWith(isInProgress: false));
    } catch (e) {
      if (!isClosed) emit(state.copyWith(isInProgress: false, isError: true));
    }
  }

  @override
  Future<void> close() async {
    await _worshipSubscription.cancel();
    return super.close();
  }
}
