import 'package:gethsemane/domain/repository/authors_repository.dart';
import 'package:gethsemane/domain/repository/music_groups_repository.dart';
import 'package:logging/logging.dart';
import 'package:shared_preferences/shared_preferences.dart';

class LoadInitialDataUseCase {
  final AuthorsRepository authorsRepository;
  final MusicGroupsRepository musicGroupsRepository;

  LoadInitialDataUseCase({
    required this.authorsRepository,
    required this.musicGroupsRepository,
  });

  Future<void> perform() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      if (prefs.getBool('isInitialDataLoaded') != true) {
        await authorsRepository.loadAuthors();
        await musicGroupsRepository.loadMusicGroups();
        prefs.setBool('isInitialDataLoaded', true);
      }
    } catch (e) {
      Logger.root.log(Level.SEVERE, e);
    }
  }
}
