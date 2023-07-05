import 'package:gethsemane/domain/repository/authors_repository.dart';
import 'package:gethsemane/domain/repository/music_groups_repository.dart';
import 'package:logging/logging.dart';

class LoadInitialDataUseCase {
  final AuthorsRepository authorsRepository;
  final MusicGroupsRepository musicGroupsRepository;

  LoadInitialDataUseCase({
    required this.authorsRepository,
    required this.musicGroupsRepository,
  });

  Future<void> invoke() async {
    try {
      await authorsRepository.loadAuthors();
      await musicGroupsRepository.loadMusicGroups();
    } catch (e) {
      Logger.root.log(Level.SEVERE, e);
    }
  }
}
