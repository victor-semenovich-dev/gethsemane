import 'package:gethsemane/domain/model/worship_extended.dart';

abstract class WorshipsRepository {
  Future<void> loadWorship(int id);
  Stream<WorshipExtended> getWorship(int id);
}
