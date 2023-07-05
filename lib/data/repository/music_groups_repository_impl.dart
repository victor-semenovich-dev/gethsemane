import 'package:gethsemane/data/local/database.dart';
import 'package:gethsemane/data/remote/service/api_gethsemane_service.dart';
import 'package:gethsemane/data/util/mappings.dart';
import 'package:gethsemane/domain/repository/music_groups_repository.dart';

class MusicGroupsRepositoryImpl extends MusicGroupsRepository {
  final AppDatabase database;
  final ApiGethsemaneService apiGethsemaneService;

  MusicGroupsRepositoryImpl({
    required this.database,
    required this.apiGethsemaneService,
  });

  @override
  Future<void> loadMusicGroups() async {
    final response = await apiGethsemaneService.getMusicGroups();
    if (response.isSuccessful) {
      final musicGroupDtoList = response.body;
      if (musicGroupDtoList != null) {
        database.batch((batch) {
          batch.deleteAll(database.musicGroup);
          batch.insertAllOnConflictUpdate(
            database.musicGroup,
            musicGroupDtoList.map((dto) => musicGroupDtoToDbEntity(dto)),
          );
        });
      }
    } else {
      throw response.error ?? 'An error occurred: ${response.statusCode}';
    }
  }
}
