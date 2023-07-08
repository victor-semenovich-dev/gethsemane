import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gethsemane/domain/repository/worships_repository.dart';

class WorshipCubit extends Cubit<void> {
  final int id;
  final WorshipsRepository worshipsRepository;

  WorshipCubit({
    required this.id,
    required this.worshipsRepository,
  }) : super(null) {
    worshipsRepository.getWorship(id);
  }
}
