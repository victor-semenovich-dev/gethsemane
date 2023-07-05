import 'package:gethsemane/domain/repository/authors_repository.dart';
import 'package:logging/logging.dart';

class LoadInitialDataUseCase {
  final AuthorsRepository authorsRepository;

  LoadInitialDataUseCase({
    required this.authorsRepository,
  });

  Future<void> invoke() async {
    try {
      await authorsRepository.loadAuthors();
    } catch (e) {
      Logger.root.log(Level.SEVERE, e);
    }
  }
}
